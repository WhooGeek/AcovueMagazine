package com.AcovueMagazine.Magazine.Repository;

import com.AcovueMagazine.Magazine.DTO.MagazineResDTO;
import com.AcovueMagazine.Magazine.Entity.Magazine;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MagazineRepository extends JpaRepository<Magazine, Long> {


    /**
 * Retrieves all Magazine entities that match the given specification, applying the provided sort order.
 *
 * @param spec specification defining filtering criteria; may be {@code null} to match all entities
 * @param sort sort order to apply to the results; may be {@code null} or unsorted
 * @return a list of Magazine entities matching the specification in the requested order (empty if none)
 */
List<Magazine> findAll(Specification<Magazine> spec, Sort sort);

}
