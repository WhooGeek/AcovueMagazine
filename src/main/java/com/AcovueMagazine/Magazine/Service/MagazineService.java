package com.AcovueMagazine.Magazine.Service;


import com.AcovueMagazine.Magazine.DTO.MagazineReqDTO;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.User.Entity.UserRoll;
import com.AcovueMagazine.User.Entity.UserStatus;
import com.AcovueMagazine.User.Entity.Users;
import com.AcovueMagazine.User.Repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

        if (magazine.getUser().getUserStatus() == UserStatus.INACTIVE) {
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


    // 매거진 삭제 기능
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


}
