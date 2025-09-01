package com.AcovueMagazine.Magazine.Repository;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {


}
