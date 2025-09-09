package com.AcovueMagazine.Magazine.Specification;

import com.AcovueMagazine.Magazine.Entity.Magazine;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class MagazineSpecification {

    public static Specification<Magazine> titleOrContentContains(String keyword){
        return (root, query, builder) -> {
            if (keyword == null || keyword.isEmpty()) return null;
            return builder.or(
                    builder.like(builder.lower(root.get("magazineTitle")), "%" + keyword.toLowerCase() + "%"),
                    builder.like(builder.lower(root.get("magazineContent")), "%" + keyword.toLowerCase() + "%")
            );
        };
    }

    public static Specification<Magazine> regDateBetween(LocalDateTime start, LocalDateTime end) {
        return(root, query, builder) -> {
            if(start == null && end == null) return null;
            if(start != null && end != null) return builder.between(root.get("regDate"), start, end);
            if(start != null) return builder.greaterThanOrEqualTo(root.get("regDate"), start);
            return builder.lessThanOrEqualTo(root.get("regDate"), end);
        };
    }
}
