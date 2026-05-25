package com.AcovueMagazine.Like.Service;

import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Common.Response.ErrorCode;
import com.AcovueMagazine.Common.Response.RestApiException;
import com.AcovueMagazine.Like.Dto.CommentLikeCountResDTO;
import com.AcovueMagazine.Like.Dto.CommentLikeResDTO;
import com.AcovueMagazine.Like.Dto.PostLikeCountResDTO;
import com.AcovueMagazine.Like.Dto.PostLikeResDTO;
import com.AcovueMagazine.Like.Entity.CommentLike;
import com.AcovueMagazine.Like.Entity.PostLike;
import com.AcovueMagazine.Like.Respository.CommentLikeRepository;
import com.AcovueMagazine.Like.Respository.PostLikeRepository;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Repository.PostRepository;
import com.AcovueMagazine.Member.Entity.Members;
import com.AcovueMagazine.Member.Repository.MembersRepository;
import com.AcovueMagazine.Member.Util.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostLikeRepository magazineLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository magazineRepository;
    private final CommentRepository commentRepository;
    private final MembersRepository membersRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 매거진 좋아요 등록
    @Transactional
    public PostLikeResDTO togglePostLike(Long postSeq) {

        Members members = getCurrentMember();

        Post post = magazineRepository.findById(postSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Optional<PostLike> existing = magazineLikeRepository.findByMembersAndPost(members, post);

        PostLike like;
        if(existing.isPresent()){
            // 좋아요 취소
            magazineLikeRepository.delete(existing.get());
            like = null;
        }else{
            // 좋아요 등록
            like = magazineLikeRepository.save(PostLike.create(members, post));
        }

        return PostLikeResDTO.fromEntity(like);
    }


    // 댓글 좋아요 토글 기능
    @Transactional
    public CommentLikeResDTO toggleCommentLike(Long commentSeq) {

        Members members = getCurrentMember();

        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.COMMENT_NOT_FOUND));

        Optional<CommentLike> existing = commentLikeRepository.findByMembersAndComment(members, comment);

        CommentLike like;
        if(existing.isPresent()){
            // 좋아요 취소
            commentLikeRepository.delete(existing.get());

            like = null;
        }else{
            // 좋아요 등록
            like = commentLikeRepository.save(CommentLike.create(members, comment));
        }

        return CommentLikeResDTO.fromEntity(like);
    }

    // 댓글 좋아요 조회 기능
    @Transactional
    public CommentLikeCountResDTO commentLikeCount(Long commentSeq) {

        Optional<Comment> comment = commentRepository.findById(commentSeq);

        if (comment.isEmpty()){
            Long likeCount = 0L;
            return CommentLikeCountResDTO.from(commentSeq, likeCount);
        }

        Long likeCount = commentLikeRepository.countByComment_CommentSeq(commentSeq);

        return CommentLikeCountResDTO.from(commentSeq, likeCount);
    }

    // 매거진 좋아요 조회 기능
    @Transactional
    public PostLikeCountResDTO postLikeCount(Long postSeq) {

        Post magazine = magazineRepository.findById(postSeq)
                .orElseThrow(() -> new RestApiException(ErrorCode.POST_NOT_FOUND));

        Long magazineLikeCount = magazineLikeRepository.countByPost_PostSeq(postSeq);

        return PostLikeCountResDTO.from(postSeq, magazineLikeCount);

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
}
