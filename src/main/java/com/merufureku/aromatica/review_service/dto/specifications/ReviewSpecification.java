package com.merufureku.aromatica.review_service.dto.specifications;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ReviewSpecification {

    public static Specification<Reviews> byPerfumeId(Long perfumeId) {
        return (root, query, cb) ->
                perfumeId == null ? null :
                cb.equal(root.get("fragranceId"), perfumeId);
    }

    public static Specification<Reviews> byUserId(Integer userId) {
        return (root, query, cb) ->
                userId == null ? null :
                        cb.equal(root.get("userId"), userId);
    }

    public static Specification<Reviews> withRatings(List<Integer> ratings) {
        return (root, query, cb) ->
                root.get("rating").in(ratings);
    }
}
