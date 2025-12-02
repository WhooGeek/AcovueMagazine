package com.AcovueMagazine.Post.Service;


import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Repository.PostRepository;
import com.AcovueMagazine.Post.Specification.MagazineSpecification;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Repository.MembersRepository;
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
public class PostService {

    private final PostRepository postRepository;
    private final MembersRepository membersRepository;

    // 매거진 전체 조회
    @Transactional
    public List<PostResDto> getAllMagazines() {
        List<Post> magazines = postRepository.findAll();


        return magazines.stream()
                .map(magazine -> new PostResDto(
                        magazine.getMembers().getMember_seq(),
                        magazine.getMembers().getMemberName(),// 엔티티 필드에 맞춰서
                        magazine.getMembers().getMemberNickname(),
                        magazine.getMembers().getMemberEmail(),
                        magazine.getMembers().getMemberStatus(),
                        magazine.getMagazineSeq(),
                        magazine.getMagazineTitle(),
                        magazine.getMagazineContent(),
                        magazine.getMagazineCategory(),
                        magazine.getRegDate(),
                        magazine.getModDate()
                ))
                .collect(Collectors.toList());
    }

    // 매거진 상세 조회
    @Transactional
    public PostResDto getMagazine(Long magazineId) {
        Post magazine = postRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        if (magazine.getMembers().getMemberStatus() == MemberStatus.INACTIVE) {
            throw new IllegalStateException("비활성화된 유저입니다.");
        }

        return PostResDto.fromEntity(magazine);
    }

    // 매거진 생성 기능
    @Transactional
    public PostResDto createMagazine(PostReqDto magazineReqDTO) {

        Members members = membersRepository.findById(magazineReqDTO.getMemberSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Post magazine = new Post(members, magazineReqDTO.getMagazine_title(), magazineReqDTO.getMagazine_content());

        magazine = postRepository.save(magazine);

        return PostResDto.fromEntity(magazine);
    }

    // 매거진 수정 기능
    @Transactional
    public PostResDto updateMagazine(PostReqDto magazineReqDTO, Long magazineId) {

        Post magazine = postRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));

        Members members = membersRepository.findById(magazineReqDTO.getMemberSeq())
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 권한 체크
        if (!magazine.getMembers().getMember_seq().equals(members.getMember_seq()) &&
                members.getMemberRole() != MemberRole.ADMIN) {
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

        return PostResDto.fromEntity(magazine);
    }


    @Transactional
    public PostResDto deleteMagazine(Long magazineId, Members currentMembers) {
        Post magazine = postRepository.findById(magazineId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + magazineId));


        if (magazine.getMembers().getMember_seq().equals(currentMembers.getMember_seq()) ||
                currentMembers.getMemberRole() == MemberRole.ADMIN) {
            postRepository.delete(magazine);
        } else {
            throw new org.springframework.security.access.AccessDeniedException("삭제 권한이 없습니다.");
        }


        return PostResDto.fromEntity(magazine);
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
    public List<PostResDto> searchMagzine(String keyword, LocalDateTime start, LocalDateTime end, boolean newestFirst) {
        Specification<Post> spec = Specification
                .where(MagazineSpecification.titleOrContentContains(keyword))
                .and(MagazineSpecification.regDateBetween(start, end));

        Sort sort = newestFirst ? Sort.by("regDate").descending() : Sort.by("regDate").ascending();

        List<Post> searchMagazines = postRepository.findAll(spec, sort);


        return searchMagazines.stream()
                .map(magazine -> new PostResDto(
                        magazine.getMembers().getMember_seq(),
                        magazine.getMembers().getMemberName(),// 엔티티 필드에 맞춰서
                        magazine.getMembers().getMemberNickname(),
                        magazine.getMembers().getMemberEmail(),
                        magazine.getMembers().getMemberStatus(),
                        magazine.getMagazineSeq(),
                        magazine.getMagazineTitle(),
                        magazine.getMagazineContent(),
                        magazine.getMagazineCategory(),
                        magazine.getRegDate(),
                        magazine.getModDate()
                ))
                .collect(Collectors.toList());
    }
}
