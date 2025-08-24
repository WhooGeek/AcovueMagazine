package com.AcovueMagazine.User.Repository;

import com.AcovueMagazine.User.Aggregate.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> findByUserId(String userId);
}
