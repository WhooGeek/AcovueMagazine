package com.AcovueMagazine.Comment.Respository;

import com.AcovueMagazine.Comment.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    // 최초 댓글만 조회
    @Query("SELECT c FROM Comment c WHERE c.parent IS NULL AND c.magazine.magazineSeq = :magazineId AND c.commentStatus = 'ACTIVE'")
    List<Comment> findTopCommentsByMagazine(@Param("magazineId") Long magazineId);

    // 최초 댓글 Seq 조회
    @Query("SELECT c FROM Comment c WHERE c.parent.commentSeq = :parentSeq AND c.commentStatus = 'ACTIVE' ")
    List<Comment> findByParent(@Param("parentSeq") Long parentSeq);


}
