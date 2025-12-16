package com.merufureku.aromatica.review_service.dto.responses;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;

import java.time.LocalDate;
import java.util.List;

public record GetAllReviews(List<Interactions> interactions){

    public record Interactions(int userId, Long fragranceId, int rating, LocalDate reviewDate){

        public Interactions(Reviews reviews){
            this(
                    reviews.getUserId(),
                    reviews.getFragranceId(),
                    reviews.getRating(),
                    reviews.getUpdatedAt()
            );
        }

    }
}
