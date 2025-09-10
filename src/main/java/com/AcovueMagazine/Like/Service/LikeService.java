package com.AcovueMagazine.Like.Service;

import com.AcovueMagazine.Comment.DTO.CommentResDTO;
import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Respository.CommentRepository;
import com.AcovueMagazine.Like.DTO.CommentLikeCountResDTO;
import com.AcovueMagazine.Like.DTO.CommentLikeResDTO;
import com.AcovueMagazine.Like.DTO.MagazineLikeResDTO;
import com.AcovueMagazine.Like.Entity.CommentLike;
import com.AcovueMagazine.Like.Entity.MagazineLike;
import com.AcovueMagazine.Like.Respository.CommentLikeRepository;
import com.AcovueMagazine.Like.Respository.MagazineLikeRepository;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.Magazine.Repository.MagazineRepository;
import com.AcovueMagazine.User.Entity.Users;
import com.AcovueMagazine.User.Repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final UsersRepository usersRepository;
    private final MagazineLikeRepository magazineLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MagazineRepository magazineRepository;
    private final CommentRepository commentRepository;

    // 매거진 좋아요 등록
    @Transactional
    public MagazineLikeResDTO toggleMagazineLike(Long magazineSeq, Long userSeq) {

        Users user = usersRepository.findById(userSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다." + userSeq));

        Magazine magazine = magazineRepository.findById(magazineSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 매거진을 찾을 수 없습니다." + magazineSeq));

        Optional<MagazineLike> existing = magazineLikeRepository.findByUserAndMagazine(user, magazine);

        MagazineLike like;
        if(existing.isPresent()){
            // 좋아요 취소
            magazineLikeRepository.delete(existing.get());
            like = null;
        }else{
            // 좋아요 등록
            like = magazineLikeRepository.save(MagazineLike.create(user, magazine));
        }

        return MagazineLikeResDTO.fromEntity(like);
    }


    // 댓글 좋아요 토글 기능
    @Transactional
    public CommentLikeResDTO toggleCommentLike(Long commentSeq, Long userSeq) {

        Users user = usersRepository.findById(userSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 유저를 찾을 수 없습니다." + userSeq));

        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다." + commentSeq));

        Optional<CommentLike> existing = commentLikeRepository.findByUserAndComment(user, comment);

        CommentLike like;
        if(existing.isPresent()){
            // 좋아요 취소
            commentLikeRepository.delete(existing.get());

            like = null;
        }else{
            // 좋아요 등록
            like = commentLikeRepository.save(CommentLike.create(user, comment));
        }

        return CommentLikeResDTO.fromEntity(like);
    }

    // 댓글 좋아요 조회 기능
    @Transactional
    public CommentLikeCountResDTO commentLikeCount(Long commentSeq) {

        Comment comment = commentRepository.findById(commentSeq)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다." + commentSeq));

        Long likeCount = commentLikeRepository.countByComment_CommentSeq(commentSeq);

        return CommentLikeCountResDTO.from(commentSeq, likeCount);
    }
}
