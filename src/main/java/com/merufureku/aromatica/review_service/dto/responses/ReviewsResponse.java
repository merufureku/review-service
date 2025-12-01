package com.merufureku.aromatica.review_service.dto.responses;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;

import java.util.List;

public record ReviewsResponse(Long fragranceId, double averageRating, List<ReviewDetail> reviews,
                              int page, int size, long totalElement, int totalPage,
                              boolean last) {


    public record ReviewDetail(Long reviewId, String username, int rating,
                               String comment, String createdAt) {

        public ReviewDetail(Reviews reviews) {
            this(
                    reviews.getId(),
                    reviews.getUser().getUsername(),
                    reviews.getRating(),
                    reviews.getComment(),
                    reviews.getCreatedAt().toString()
            );
        }
    }

}
