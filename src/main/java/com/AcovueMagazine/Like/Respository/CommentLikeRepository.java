package com.AcovueMagazine.Like.Respository;

import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Like.Entity.CommentLike;
import com.AcovueMagazine.Member.Entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 유저 + 댓글로 좋아요 있는지 검증
    Optional<CommentLike> findByMembersAndComment(Members members, Comment comment);

    Long countByComment_CommentSeq(Long commentSeq);
}
