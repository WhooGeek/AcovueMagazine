package com.AcovueMagazine.Magazine.Service;


import com.AcovueMagazine.Magazine.DTO.MagazineReqDTO;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.Magazine.Specification.MagazineSpecification;
import com.AcovueMagazine.Member.Entity.UserRoll;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final UsersRepository usersRepository;

    // 매거진 전체 조회
    @Transactional
    public List<MagazineResDTO> getAllMagazines() {
        List<Magazine> magazines = magazineRepository.findAll();


        return magazines.stream()
                .map(magazine -> new MagazineResDTO(
                        magazine.getUser().getUserSeq(),
                        magazine.getUser().getUserName(),// 엔티티 필드에 맞춰서
                        magazine.getUser().getUserNickname(),
                        magazine.getUser().getUserEmail(),
                        magazine.getUser().getUserStatus(),
                        magazine.getMagazineSeq(),
                        magazine.getMagazineTitle(),
                        magazine.getMagazineContent(),
                        magazine.getRegDate(),
                        magazine.getModDate()
                ))
                .collect(Collectors.toList());
    }

    // 매거진 상세 조회
    @Transactional
    public MagazineResDTO getMagazine(Long magazineId) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        if (magazine.getUser().getUserStatus() == MemberStatus.INACTIVE) {
            throw new IllegalStateException("비활성화된 유저입니다.");
        }

        return MagazineResDTO.fromEntity(magazine);
    }

    // 매거진 생성 기능
    @Transactional
    public MagazineResDTO createMagazine(MagazineReqDTO magazineReqDTO) {

        Users users = usersRepository.findById(magazineReqDTO.getUserSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Magazine magazine = new Magazine(users, magazineReqDTO.getMagazine_title(), magazineReqDTO.getMagazine_content());

        magazine = magazineRepository.save(magazine);

        return MagazineResDTO.fromEntity(magazine);
    }

    // 매거진 수정 기능
    @Transactional
    public MagazineResDTO updateMagazine(MagazineReqDTO magazineReqDTO, Long magazineId) {

        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        Users users = usersRepository.findById(magazineReqDTO.getUserSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 권한 체크
        if (!magazine.getUser().getUserSeq().equals(users.getUserSeq()) &&
                users.getUserRoll() != UserRoll.ADMIN) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 제목 수정이 있으면 저장
        if (magazineReqDTO.getMagazine_title() != null && !magazineReqDTO.getMagazine_title().isBlank()) {
            magazine.updateTitle(magazineReqDTO.getMagazine_title());
        }


        // 내용 수정이 있으면 저장
        if (magazineReqDTO.getMagazine_content() != null && !magazineReqDTO.getMagazine_content().isBlank()) {
            magazine.updateContent(magazineReqDTO.getMagazine_content());
        }

        return MagazineResDTO.fromEntity(magazine);
    }

    /**
     * Deletes a magazine by ID if the requesting user is authorized and returns the deleted magazine as a DTO.
     *
     * If the current user is the magazine owner or has the ADMIN role, the magazine is removed from the repository.
     *
     * @param magazineId   ID of the magazine to delete
     * @param currentUsers the user performing the deletion; used for authorization (must be owner or ADMIN)
     * @return a MagazineResDTO representing the deleted magazine
     * @throws javax.persistence.EntityNotFoundException if no magazine exists with the given ID
     * @throws org.springframework.security.access.AccessDeniedException if the requesting user is not authorized to delete the magazine
     */
    @Transactional
    public MagazineResDTO deleteMagazine(Long magazineId, Users currentUsers) {
        Magazine magazine = magazineRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));


        if (magazine.getUser().getUserSeq().equals(currentUsers.getUserSeq()) ||
            currentUsers.getUserRoll() == UserRoll.ADMIN) {
            magazineRepository.delete(magazine);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("삭제 권한이 없습니다.");
        }


        return MagazineResDTO.fromEntity(magazine);
    }

    /**
     * Searches magazines by keyword and optional registration-date range, returning DTOs ordered by registration date.
     *
     * The search matches the keyword against title or content and filters by registration date between
     * `start` and `end` (if provided). Results are sorted by `regDate` descending when `newestFirst` is true,
     * otherwise ascending.
     *
     * @param keyword     text to match against magazine title or content; null or empty matches all
     * @param start       start of the registration-date range (inclusive); may be null to omit lower bound
     * @param end         end of the registration-date range (inclusive); may be null to omit upper bound
     * @param newestFirst if true, sort results by `regDate` descending; if false, sort ascending
     * @return a list of MagazineResDTOs representing matched magazines (may be empty)
     */
    public List<MagazineResDTO> searchMagzine(String keyword, LocalDateTime start, LocalDateTime end, boolean newestFirst) {
        Specification<Magazine> spec = Specification
                .where(MagazineSpecification.titleOrContentContains(keyword))
                .and(MagazineSpecification.regDateBetween(start, end));

        Sort sort = newestFirst ? Sort.by("regDate").descending() : Sort.by("regDate").ascending();

        List<Magazine> searchMagzines = magazineRepository.findAll(spec, sort);


        return searchMagzines.stream()
                .map(magazine -> new MagazineResDTO(
                        magazine.getUser().getUserSeq(),
                        magazine.getUser().getUserName(),// 엔티티 필드에 맞춰서
                        magazine.getUser().getUserNickname(),
                        magazine.getUser().getUserEmail(),
                        magazine.getUser().getUserStatus(),
                        magazine.getMagazineSeq(),
                        magazine.getMagazineTitle(),
                        magazine.getMagazineContent(),
                        magazine.getRegDate(),
                        magazine.getModDate()
                ))
                .collect(Collectors.toList());
    }
}
