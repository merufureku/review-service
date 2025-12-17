package com.merufureku.aromatica.review_service.services.factory;

import com.merufureku.aromatica.review_service.services.impl.InternalReviewServiceImpl0;
import com.merufureku.aromatica.review_service.services.impl.InternalReviewServiceImpl1;
import com.merufureku.aromatica.review_service.services.impl.ReviewServiceImpl0;
import com.merufureku.aromatica.review_service.services.impl.ReviewServiceImpl1;
import com.merufureku.aromatica.review_service.services.interfaces.IInternalReviewService;
import com.merufureku.aromatica.review_service.services.interfaces.IReviewService;
import org.springframework.stereotype.Component;

@Component
public class InternalReviewServiceFactory {

    private final InternalReviewServiceImpl0 internalReviewServiceImpl0;
    private final InternalReviewServiceImpl1 internalReviewServiceImpl1;

    public InternalReviewServiceFactory(InternalReviewServiceImpl0 internalReviewServiceImpl0, InternalReviewServiceImpl1 internalReviewServiceImpl1) {
        this.internalReviewServiceImpl0 = internalReviewServiceImpl0;
        this.internalReviewServiceImpl1 = internalReviewServiceImpl1;
    }


    public IInternalReviewService getService(int version) {
        return switch (version) {
            case 0 -> internalReviewServiceImpl0;
            case 1 -> internalReviewServiceImpl1;
            default -> throw new IllegalArgumentException("Unsupported recommendation service version: " + version);
        };
    }
}
