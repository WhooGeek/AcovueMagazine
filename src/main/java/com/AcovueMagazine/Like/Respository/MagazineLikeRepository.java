package com.AcovueMagazine.Like.Respository;

import com.AcovueMagazine.Like.Entity.MagazineLike;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import com.AcovueMagazine.User.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MagazineLikeRepository extends JpaRepository<MagazineLike, Long> {

    // 유저 + 매거진으로 좋아요 있는지 검증
    Optional<MagazineLike> findByUserAndMagazine(Users user, Magazine magazine);
    
}
