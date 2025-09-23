package com.AcovueMagazine.Like.Respository;

import com.AcovueMagazine.Like.Entity.MagazineLike;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MagazineLikeRepository extends JpaRepository<MagazineLike, Long> {

    // 유저 + 매거진으로 좋아요 있는지 검증
    Optional<MagazineLike> findByUserAndMagazine(Users user, Magazine magazine);

    // 매거진seq로 매거진 좋아요 개수 조회
    Long countByMagazine_MagazineSeq(Long magazineSeq);
}
