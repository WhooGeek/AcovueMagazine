package com.AcovueMagazine.Comment.Respository;

import com.AcovueMagazine.Comment.Entity.Comment;
import com.AcovueMagazine.Comment.Entity.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment,Long> {

    // 최초 댓글만 조회
    @Query("SELECT c FROM Comment c WHERE c.parent IS NULL AND c.post.postSeq = :postId AND c.commentStatus = 'ACTIVE'")
    List<Comment> findTopCommentsByPost(@Param("postId") Long postId);

    // 최초 댓글 Seq 조회
    @Query("SELECT c FROM Comment c WHERE c.parent.commentSeq = :parentSeq AND c.commentStatus = 'ACTIVE' ")
    List<Comment> findByParent(@Param("parentSeq") Long parentSeq);

    Long countByPost_PostSeqAndCommentStatus(Long postSeq, CommentStatus commentStatus);

    @Query("""
            SELECT c.post.postSeq, COUNT(c)
            FROM Comment c
            WHERE c.post.postSeq IN :postSeqs
              AND c.commentStatus = :commentStatus
            GROUP BY c.post.postSeq
            """)
    List<Object[]> countCommentsByPostSeqs(
            @Param("postSeqs") List<Long> postSeqs,
            @Param("commentStatus") CommentStatus commentStatus
    );
}
