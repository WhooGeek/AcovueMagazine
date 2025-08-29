package com.AcovueMagazine.Magazine.Repository;

import com.AcovueMagazine.Magazine.DTO.Magazine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {


}
