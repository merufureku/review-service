package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dto.specifications.ReviewSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpecificationHelper {

    public Specification<Reviews> buildReviewsSpecification(Integer userId, Long perfumeId, List<Integer> ratings) {
        Specification<Reviews> specification = Specification
                .allOf(ReviewSpecification.byPerfumeId(perfumeId))
                .and(ReviewSpecification.byUserId(userId));

        if (ratings != null && !ratings.isEmpty()) {
            specification = specification.and(ReviewSpecification.withRatings(ratings));
        }

        return specification;
    }
}
