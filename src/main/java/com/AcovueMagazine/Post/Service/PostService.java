package com.AcovueMagazine.Post.Service;

import com.AcovueMagazine.Comment.Entity.CommentStatus;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import com.AcovueMagazine.Like.Respository.PostLikeRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Post.Dto.PostNavigateDto;
import com.AcovueMagazine.Post.Dto.PostNavigationResDto;
import com.AcovueMagazine.Post.Dto.PostReqDto;
import com.AcovueMagazine.Post.Entity.*;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Repository.PostRepository;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Entity.MemberStatus;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Post.Specification.PostSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public List<PostResDto> getAllPosts(Integer limit, Integer page, PostType postType, CommunityCategory communityCategory) {

        Sort sort = postType == PostType.COMMUNITY
                ? Sort.by(Sort.Order.desc("notice"), Sort.Order.desc("regDate"))
                : Sort.by(Sort.Direction.DESC, "regDate");

        Pageable pageable = PageRequest.of(page - 1, limit, sort);

        Page<Post> postPage;

        if (postType == PostType.COMMUNITY && communityCategory != null) {
            postPage = postRepository.findByPostCategoryAndCommunityCategoryAndPostStatus(
                    PostType.COMMUNITY,
                    communityCategory,
                    PostStatus.ACTIVE,
                    pageable
            );
        } else if (postType != null) {
            postPage = postRepository.findByPostCategoryAndPostStatus(
                    postType,
                    PostStatus.ACTIVE,
                    pageable
            );
        } else {
            postPage = postRepository.findByPostStatus(
                    PostStatus.ACTIVE,
                    pageable
            );
        }

        List<Post> posts = postPage.getContent();

        Map<Long, Long> commentCountMap = getCommentCountMap(posts);
        Map<Long, Long> postLikeCountMap = getPostLikeCountMap(posts);

        return posts.stream()
                .map(post -> PostResDto.fromEntity(
                        post,
                        commentCountMap.getOrDefault(post.getPostSeq(), 0L),
                        postLikeCountMap.getOrDefault(post.getPostSeq(), 0L)
                ))
                .toList();
    }

    private Map<Long, Long> getCommentCountMap(List<Post> posts) {
        List<Long> postSeqs = posts.stream()
                .map(Post::getPostSeq)
                .toList();

        if (postSeqs.isEmpty()) {
            return Map.of();
        }

        return commentRepository.countCommentsByPostSeqs(postSeqs, CommentStatus.ACTIVE).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    private Map<Long, Long> getPostLikeCountMap(List<Post> posts) {
        List<Long> postSeqs = posts.stream()
                .map(Post::getPostSeq)
                .toList();

        if (postSeqs.isEmpty()) {
            return Map.of();
        }

        return postLikeRepository.countPostLikesByPostSeqs(postSeqs).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    // 매거진 상세 조회
    @Transactional
    public PostResDto getPost(Long postSeq) {
        Post post = postRepository.findByPostSeqAndPostStatus(postSeq, PostStatus.ACTIVE)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        if (post.getMembers().getMemberStatus() == MemberStatus.INACTIVE) {
            throw new RestApiException(ErrorCode.INACTIVE_USER);
        }

        return PostResDto.fromEntity(post);
    }

    // 매거진 생성 기능
    @Transactional
    public PostResDto createPost(PostReqDto postReqDTO) {

        Members members = getCurrentMember();

        boolean requestedNotice = Boolean.TRUE.equals(postReqDTO.getNotice());

        if(requestedNotice && members.getMemberRole() != MemberRole.ADMIN){
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        Post post = new Post(
                members,
                postReqDTO.getPost_title(),
                postReqDTO.getPost_content(),
                postReqDTO.getPost_category(),
                postReqDTO.getThumbnail_url(),
                postReqDTO.getCommunityCategory(),
                requestedNotice
        );

        if(postReqDTO.getImageUrls() != null && !postReqDTO.getImageUrls().isEmpty()){
            for(String url : postReqDTO.getImageUrls()){
                PostImage postImage = PostImage.builder()
                        .imageUrl(url)
                        .post(post)
                        .build();
                post.addImage(postImage);
            }

        }

        post = postRepository.save(post);

        return PostResDto.fromEntity(post);
    }

    // 매거진 수정 기능
    @Transactional
    public PostResDto updatePost(PostReqDto postReqDTO, Long postSeq) {

        Post post = postRepository.findById(postSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Members members = getCurrentMember();

        boolean isWriter = post.getMembers().getMemberSeq().equals(members.getMemberSeq());
        boolean isAdmin = (members.getMemberRole() == MemberRole.ADMIN);

        if (!isWriter && !isAdmin) {
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        if (postReqDTO.getNotice() != null && !isAdmin){
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        // 제목 수정이 있으면 저장
        if (postReqDTO.getPost_title() != null && !postReqDTO.getPost_title().isBlank()) {
            post.updateTitle(postReqDTO.getPost_title());
        }

        // 내용 수정이 있으면 저장
        if (postReqDTO.getPost_content() != null && !postReqDTO.getPost_content().isBlank()) {
            post.updateContent(postReqDTO.getPost_content());
        }

        // 썸네일 이미지 수정 있으면 저장
        if (postReqDTO.getThumbnail_url() != null) {
            post.updateThumbnailUrl(postReqDTO.getThumbnail_url());
        }

        if (postReqDTO.getCommunityCategory() != null) {
            post.updateCommunityCategory(postReqDTO.getCommunityCategory());
        }

        if (postReqDTO.getNotice() != null) {
            post.updateNotice(postReqDTO.getNotice());
        }

        return PostResDto.fromEntity(post);
    }

    // 게시글 삭제 기능
    @Transactional
    public PostResDto deletePost(Long postSeq) {

        Post post = postRepository.findById(postSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Members currentMember = getCurrentMember();

        boolean isWrtter = post.getMembers().getMemberSeq().equals(currentMember.getMemberSeq());
        boolean isAdmin = currentMember.getMemberRole() == MemberRole.ADMIN;

        if(!isWrtter && !isAdmin){
            throw new RestApiException(ErrorCode.FORBIDDEN);
        }

        // 게시물 소프트 삭제
        post.inActivate();

        return PostResDto.fromEntity(post);
    }

    private Members getCurrentMember() {
        String accessToken = jwtTokenProvider.resolveToken();

        if (accessToken == null || accessToken.isEmpty()) {
            throw new RestApiException(ErrorCode.ACCESS_TOKEN_NULL);
        }

        Long memberSeq = jwtTokenProvider.getMemberSeqFromToken(accessToken);

        if (memberSeq == null) {
            throw new RestApiException(ErrorCode.INVALID_TOKEN);
        }

        return membersRepository.findById(memberSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.USER_NOT_FOUND));
    }

    // 게시물 검색 기능
    public List<PostResDto> searchPost(String keyword, LocalDateTime start, LocalDateTime end, boolean newestFirst) {
        Specification<Post> spec = Specification
                .where(PostSpecification.titleOrContentContains(keyword))
                .and(PostSpecification.regDateBetween(start, end))
                .and(PostSpecification.isActive());

        Sort sort = newestFirst ? Sort.by("regDate").descending() : Sort.by("regDate").ascending();

        List<Post> searchMagazines = postRepository.findAll(spec, sort);


        return searchMagazines.stream()
                .map(PostResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 이전, 다음 게시물 조회
    @Transactional
    public PostNavigationResDto getNavigation(Long postSeq) {
        Post currentPost = getPostEntity(postSeq);

        Optional<Post> prevPost = postRepository.findFirstByPostSeqLessThanAndPostStatusAndNoticeFalseAndPostCategoryOrderByPostSeqDesc(
                currentPost.getPostSeq(),
                PostStatus.ACTIVE,
                currentPost.getPostCategory()
        );

        Optional<Post> nextPost = postRepository.findFirstByPostSeqGreaterThanAndPostStatusAndNoticeFalseAndPostCategoryOrderByPostSeqAsc(
                currentPost.getPostSeq(),
                PostStatus.ACTIVE,
                currentPost.getPostCategory()
        );

        return new PostNavigationResDto(
                prevPost.map(PostNavigateDto::fromEntity).orElse(null),
                nextPost.map(PostNavigateDto::fromEntity).orElse(null)
        );




    }

    private Post getPostEntity(Long postSeq) {
        return postRepository.findById(postSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));
    }
}
