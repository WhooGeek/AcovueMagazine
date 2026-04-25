package com.AcovueMagazine.Post.Service;

import com.AcovueMagazine.Member.Entity.MemberLoginStatus;
import com.AcovueMagazine.Member.Entity.MemberRole;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Entity.PostStatus;
import com.AcovueMagazine.Post.Entity.PostType;
import com.AcovueMagazine.Post.Repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.security.access.AccessDeniedException;
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
                PostType.NEWS,
                "thumb.jpg"
        );

        Long postId = 1L;
        Long memberSeq = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(membersRepository.findById(memberSeq)).thenReturn(Optional.of(writer));

        //when
        postService.deleteMagazine(postId, memberSeq);

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
                PostType.NEWS,
                "thumb.jpg"
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(membersRepository.findById(2L)).thenReturn(Optional.of(otherUser));

        //when & then
        assertThrows(AccessDeniedException.class, () -> postService.deleteMagazine(1L, 2L));
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
                PostType.NEWS,
                "thumb.jpg"
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(membersRepository.findById(99L)).thenReturn(Optional.of(admin));

        // when
        postService.deleteMagazine(1L, 99L);

        // then
        assertEquals(PostStatus.INACTIVE, post.getPostStatus());
    }


}