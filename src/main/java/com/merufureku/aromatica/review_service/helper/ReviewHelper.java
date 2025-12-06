package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ReviewHelper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    public Reviews updateReview(Reviews existingReview, PostReviewParam param) {
        String newComment = param.comment() != null ? param.comment() : existingReview.getComment();
        Integer newRating = param.rating();

        Reviews updated = Reviews.builder()
                .id(existingReview.getId())
                .fragranceId(existingReview.getFragranceId())
                .userId(existingReview.getUserId())
                .rating(newRating)
                .comment(newComment)
                .createdAt(existingReview.getCreatedAt())
                .updatedAt(LocalDate.now())
                .user(existingReview.getUser())
                .build();

        logger.info("Review has been updated: {}", updated);

        return updated;
    }
}
