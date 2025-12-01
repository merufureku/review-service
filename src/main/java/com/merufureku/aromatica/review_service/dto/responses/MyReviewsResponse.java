package com.merufureku.aromatica.review_service.dto.responses;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;

import java.time.LocalDate;
import java.util.List;

public record MyReviewsResponse(List<ReviewDetail> reviews,
                                int page, int size, long totalElement, int totalPage,
                                boolean last) {


    public record ReviewDetail(Long reviewId, Long fragranceId, int rating,
                               String comment, LocalDate createdAt, LocalDate updatedAt) {

        public ReviewDetail(Reviews reviews) {
            this(
                    reviews.getId(),
                    reviews.getFragranceId(),
                    reviews.getRating(),
                    reviews.getComment(),
                    reviews.getCreatedAt(),
                    reviews.getUpdatedAt()
            );
        }
    }

}
