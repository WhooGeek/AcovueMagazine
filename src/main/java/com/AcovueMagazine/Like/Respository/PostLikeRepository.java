package com.AcovueMagazine.Like.Respository;

import com.AcovueMagazine.Like.Entity.PostLike;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Member.Entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 유저 + 매거진으로 좋아요 있는지 검증
    Optional<PostLike> findByMembersAndPost(Members members, Post post);

    // 매거진seq로 매거진 좋아요 개수 조회
    Long countByPost_PostSeq(Long postSeq);

    @Query("""
            SELECT pl.post.postSeq, COUNT(pl)
            FROM PostLike pl
            WHERE pl.post.postSeq IN :postSeqs
            GROUP BY pl.post.postSeq
            """)
    List<Object[]> countPostLikesByPostSeqs(@Param("postSeqs") List<Long> postSeqs);
}
