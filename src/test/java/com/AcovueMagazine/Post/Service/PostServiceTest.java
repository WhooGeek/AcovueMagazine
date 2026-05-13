package com.AcovueMagazine.Post.Service;

import com.AcovueMagazine.Comment.Entity.CommentStatus;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Like.Respository.PostLikeRepository;
import com.AcovueMagazine.Member.Entity.MemberLoginStatus;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Post.Dto.PostResDto;
import com.AcovueMagazine.Post.Entity.CommunityCategory;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Entity.PostStatus;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MembersRepository membersRepository;

    @InjectMocks
    private PostService postService;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostLikeRepository postLikeRepository;

    @Test
    void 게시글_작성자는_삭제_요청_시_게시글이_INACTIVE_상태가_된다(){
        //givne
        Members writer = Members.builder()
                .memberName("후")
                .memberNickname("whoo")
                .memberEmail("test@test.com")
                .memberPassword("test!@#123")
                .memberRole(MemberRole.USER)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();

        ReflectionTestUtils.setField(writer, "memberSeq", 1L);

        Post post = new Post(
                writer,
                "테스트 제목",
                "테스트 내용",
                PostType.CONCERT_NEWS,
                "thumb.jpg",
                null,
                false
        );

        Long postId = 1L;
        Long memberSeq = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(membersRepository.findById(memberSeq)).thenReturn(Optional.of(writer));

        //when
        postService.deletePost(postId, memberSeq);

        //then
        assertEquals(PostStatus.INACTIVE, post.getPostStatus());
    }

    @Test
    void 게시글_작성자가_아닌_일반_사용자는_삭제할_수_없다() {
        //given
        Members writer = Members.builder()
                .memberName("작성자")
                .memberNickname("writer")
                .memberEmail("writer@test.com")
                .memberPassword("encoded")
                .memberRole(MemberRole.USER)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();
        ReflectionTestUtils.setField(writer, "memberSeq", 1L);

        Members otherUser = Members.builder()
                .memberName("다른유저")
                .memberNickname("other")
                .memberEmail("other@test.com")
                .memberPassword("encoded")
                .memberRole(MemberRole.USER)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();
        ReflectionTestUtils.setField(otherUser, "memberSeq", 2L);

        Post post = new Post(
                writer,
                "테스트 제목",
                "테스트 내용",
                PostType.CONCERT_NEWS,
                "thumb.jpg",
                null,
                false
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(membersRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        //when & then
        assertThrows(AccessDeniedException.class, () -> postService.deletePost(1L, 2L));
        assertEquals(PostStatus.ACTIVE, post.getPostStatus());
    }

    @Test
    void 관리자는_다른_사용자의_게시글도_삭제할_수_있다() {
        // given
        Members writer = Members.builder()
                .memberName("작성자")
                .memberNickname("writer")
                .memberEmail("writer@test.com")
                .memberPassword("encoded")
                .memberRole(MemberRole.USER)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();
        ReflectionTestUtils.setField(writer, "memberSeq", 1L);

        Members admin = Members.builder()
                .memberName("관리자")
                .memberNickname("admin")
                .memberEmail("admin@test.com")
                .memberPassword("encoded")
                .memberRole(MemberRole.ADMIN)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();
        ReflectionTestUtils.setField(admin, "memberSeq", 99L);

        Post post = new Post(
                writer,
                "테스트 제목",
                "테스트 내용",
                PostType.CONCERT_NEWS,
                "thumb.jpg",
                null,
                false
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(membersRepository.findById(99L)).thenReturn(Optional.of(admin));

        // when
        postService.deletePost(1L, 99L);

        // then
        assertEquals(PostStatus.INACTIVE, post.getPostStatus());
    }

    @Test
    void 게시글_목록_조회_시_댓글수와_좋아요수가_포함된다() {
        Members writer = Members.builder()
                .memberName("작성자")
                .memberNickname("writer")
                .memberEmail("writer@test.com")
                .memberPassword("encoded")
                .memberRole(MemberRole.USER)
                .memberLoginStatus(MemberLoginStatus.LOGOUT)
                .provider(null)
                .providerId(null)
                .build();
        ReflectionTestUtils.setField(writer, "memberSeq", 1L);

        Post post = new Post(
                writer,
                "테스트 제목",
                "테스트 내용",
                PostType.COMMUNITY,
                "thumb.jpg",
                CommunityCategory.REVIEW,
                false
        );
        ReflectionTestUtils.setField(post, "postSeq", 10L);

        when(postRepository.findByPostCategoryAndCommunityCategoryAndPostStatus(
                org.mockito.ArgumentMatchers.eq(PostType.COMMUNITY),
                org.mockito.ArgumentMatchers.eq(CommunityCategory.REVIEW),
                org.mockito.ArgumentMatchers.eq(PostStatus.ACTIVE),
                org.mockito.ArgumentMatchers.any(Pageable.class)
        )).thenReturn(new PageImpl<>(List.of(post)));
        when(commentRepository.countCommentsByPostSeqs(List.of(10L), CommentStatus.ACTIVE))
                .thenReturn(List.<Object[]>of(new Object[]{10L, 3L}));
        when(postLikeRepository.countPostLikesByPostSeqs(List.of(10L)))
                .thenReturn(List.<Object[]>of(new Object[]{10L, 2L}));

        List<PostResDto> posts = postService.getAllPosts(20, 1, PostType.COMMUNITY, CommunityCategory.REVIEW);

        assertEquals(1, posts.size());
        assertEquals(3L, posts.get(0).getCommentCount());
        assertEquals(2L, posts.get(0).getPostLikeCount());
    }


}
