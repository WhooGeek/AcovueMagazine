package com.AcovueMagazine.Post.Repository;

import com.AcovueMagazine.Post.Entity.Post;
import com.AcovueMagazine.Post.Entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {


    /**
 * Retrieves all Magazine entities that match the given specification, applying the provided sort order.
 *
 * @param spec specification defining filtering criteria; may be {@code null} to match all entities
 * @param sort sort order to apply to the results; may be {@code null} or unsorted
 * @return a list of Magazine entities matching the specification in the requested order (empty if none)
 */
    List<Post> findAll(Specification<Post> spec, Sort sort);

    Page<Post> findByPostCategory(PostType postType, Pageable pageable);
}
