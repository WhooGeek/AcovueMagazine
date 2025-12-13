package com.AcovueMagazine.Post.Service;

import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Repository.PostRepository;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Post.Specification.PostSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final JwtTokenProvider jwtTokenProvider;

    // Post 조회 ( Limit, page, PostType )
    @Transactional
    public List<PostResDto> getAllPosts(Integer limit, Integer page, PostType postType) {

        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Post> postPage;

        if(postType != null){
            // 특정 카테고리 지정 된 케이스
            postPage = postRepository.findByPostCategory(postType, pageable);
        } else {
            // 카테고리 지정 안된 케이스
            postPage = postRepository.findAll(pageable);
        }

        return postPage.getContent().stream()
                .map(post -> new PostResDto(
                        post.getMembers().getMember_seq(),
                        post.getMembers().getMemberName(),
                        post.getMembers().getMemberNickname(),
                        post.getMembers().getMemberEmail(),
                        post.getMembers().getMemberStatus(),
                        post.getPostSeq(),
                        post.getPostTitle(),
                        post.getPostContent(),
                        post.getPostCategory(),
                        post.getRegDate(),
                        post.getModDate()
                )).collect(Collectors.toList());
    }

    // 매거진 상세 조회
    @Transactional
    public PostResDto getMagazine(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + postId));

        if (post.getMembers().getMemberStatus() == MemberStatus.INACTIVE) {
            throw new IllegalStateException("비활성화된 유저입니다.");
        }

        return PostResDto.fromEntity(post);
    }

    // 매거진 생성 기능
    @Transactional
    public PostResDto createMagazine(PostReqDto postReqDTO) {

        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {

            // 추후 Custom Reaction으로 리펙토링 예정
            throw new NullPointerException("토큰이 조회되지 않습니다.");
        }

        // memberSeq 꺼내기
        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new NullPointerException("해당 MemberSeq가 조회되지 않습니다.");
        }

        Members members = membersRepository.findById(memberSeq)
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        Post post = new Post(members, postReqDTO.getPost_title(), postReqDTO.getPost_content(), postReqDTO.getPost_category());

        post = postRepository.save(post);

        return PostResDto.fromEntity(post);
    }

    // 매거진 수정 기능
    @Transactional
    public PostResDto updateMagazine(PostReqDto postReqDTO, Long postId) {

        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {

            // 추후 Custom Reaction으로 리펙토링 예정
            throw new NullPointerException("토큰이 조회되지 않습니다.");
        }

        // memberSeq 꺼내기
        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new NullPointerException("해당 MemberSeq가 조회되지 않습니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + postId));

        Members members = membersRepository.findById(memberSeq)
                .orElseThrow(()-> new EntityNotFoundException("해당 유저를 찾을 수 없습니다."));

        // 권한 체크
        if (!post.getMembers().getMember_seq().equals(members.getMember_seq())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        if (!post.getMembers().getMember_seq().equals(memberSeq)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        // 제목 수정이 있으면 저장
        if (postReqDTO.getPost_title() != null && !postReqDTO.getPost_title().isBlank()) {
            post.updateTitle(postReqDTO.getPost_title());
        }

        // 내용 수정이 있으면 저장
        if (postReqDTO.getPost_content() != null && !postReqDTO.getPost_content().isBlank()) {
            post.updateContent(postReqDTO.getPost_content());
        }

        return PostResDto.fromEntity(post);
    }


    @Transactional
    public PostResDto deleteMagazine(Long postId, Members currentMembers) {
        Post magazine = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다. ID = " + postId));


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
    public List<PostResDto> searchPost(String keyword, LocalDateTime start, LocalDateTime end, boolean newestFirst) {
        Specification<Post> spec = Specification
                .where(PostSpecification.titleOrContentContains(keyword))
                .and(PostSpecification.regDateBetween(start, end));

        Sort sort = newestFirst ? Sort.by("regDate").descending() : Sort.by("regDate").ascending();

        List<Post> searchMagazines = postRepository.findAll(spec, sort);


        return searchMagazines.stream()
                .map(magazine -> new PostResDto(
                        magazine.getMembers().getMember_seq(),
                        magazine.getMembers().getMemberName(),// 엔티티 필드에 맞춰서
                        magazine.getMembers().getMemberNickname(),
                        magazine.getMembers().getMemberEmail(),
                        magazine.getMembers().getMemberStatus(),
                        magazine.getPostSeq(),
                        magazine.getPostTitle(),
                        magazine.getPostContent(),
                        magazine.getPostCategory(),
                        magazine.getRegDate(),
                        magazine.getModDate()
                ))
                .collect(Collectors.toList());
    }
}
