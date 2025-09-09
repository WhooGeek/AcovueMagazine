package com.AcovueMagazine.Magazine.Repository;

import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {


    List<Magazine> findAll(Specification<Magazine> spec, Sort sort);

}
