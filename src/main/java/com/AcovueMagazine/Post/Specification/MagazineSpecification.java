package com.AcovueMagazine.Post.Specification;

import com.AcovueMagazine.Post.Entity.Post;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MagazineSpecification {

    /**
     * Creates a Specification that matches magazines whose title or content contains the given keyword (case-insensitive).
     *
     * If {@code keyword} is {@code null} or empty, this method returns {@code null} (no predicate).
     *
     * @param keyword substring to search for in {@code magazineTitle} or {@code magazineContent}; case is ignored
     * @return a {@code Specification<Magazine>} performing a case-insensitive LIKE on title or content, or {@code null} when no predicate should be applied
     */
    public static Specification<Post> titleOrContentContains(String keyword){
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            return builder.or(
                    builder.like(builder.lower(root.get("magazineTitle")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("magazineContent")), "%" + keyword.toLowerCase() + "%")
            );
        };
    }

    /**
     * Builds a JPA Specification that filters Magazine.regDate to the given range.
     *
     * If both `start` and `end` are null, no predicate is produced (the method returns null).
     * If both are non-null, the predicate matches records with `regDate` between `start` and `end` (inclusive).
     * If only `start` is non-null, the predicate matches records with `regDate` >= `start`.
     * If only `end` is non-null, the predicate matches records with `regDate` <= `end`.
     *
     * @param start the lower bound of the registration date range (inclusive); may be null
     * @param end   the upper bound of the registration date range (inclusive); may be null
     * @return a Specification<Magazine> representing the date-range predicate, or null if neither bound is provided
     */
    public static Specification<Post> regDateBetween(LocalDateTime start, LocalDateTime end) {
        return(root, query, builder) -> {
            if(start == null && end == null) return null;
            if(start != null && end != null) return builder.between(root.get("regDate"), start, end);
            if(start != null) return builder.greaterThanOrEqualTo(root.get("regDate"), start);
            return builder.lessThanOrEqualTo(root.get("regDate"), end);
        };
    }
}
