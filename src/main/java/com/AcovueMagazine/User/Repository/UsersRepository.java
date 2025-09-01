package com.AcovueMagazine.User.Repository;

import com.AcovueMagazine.User.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {
}
