package com.AcovueMagazine.Like.Respository;

import com.AcovueMagazine.Like.Entity.PostLike;
import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Member.Entity.Members;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 유저 + 매거진으로 좋아요 있는지 검증
    Optional<PostLike> findByMembersAndPost(Members members, Post post);

    // 매거진seq로 매거진 좋아요 개수 조회
    Long countByPost_PostSeq(Long postSeq);
}
