package com.merufureku.aromatica.review_service.dto.responses;

import java.time.LocalDate;

public record PostReviewResponse(Long reviewId, Long fragranceId, int rating,
                                 String comment, LocalDate createdAt) {}
