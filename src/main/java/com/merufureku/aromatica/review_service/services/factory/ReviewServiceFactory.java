package com.merufureku.aromatica.review_service.services.factory;

import com.merufureku.aromatica.review_service.services.impl.ReviewServiceImpl0;
import com.merufureku.aromatica.review_service.services.impl.ReviewServiceImpl1;
import com.merufureku.aromatica.review_service.services.interfaces.IReviewService;
import org.springframework.stereotype.Component;

@Component
public class ReviewServiceFactory {

    private final ReviewServiceImpl0 reviewServiceImpl0;
    private final ReviewServiceImpl1 reviewServiceImpl1;

    public ReviewServiceFactory(ReviewServiceImpl0 reviewServiceImpl0, ReviewServiceImpl1 reviewServiceImpl1) {
        this.reviewServiceImpl0 = reviewServiceImpl0;
        this.reviewServiceImpl1 = reviewServiceImpl1;
    }

    public IReviewService getService(int version) {
        return switch (version) {
            case 0 -> reviewServiceImpl0;
            case 1 -> reviewServiceImpl1;
            default -> throw new IllegalArgumentException("Unsupported service version: " + version);
        };
    }
}
