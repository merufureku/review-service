package com.merufureku.aromatica.review_service.services.impl;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.GetAllReviews;
import com.merufureku.aromatica.review_service.services.interfaces.IInternalReviewService;
import org.springframework.stereotype.Service;

@Service
public class InternalReviewServiceImpl0 implements IInternalReviewService {
    @Override
    public BaseResponse<GetAllReviews> getReviews(Integer excludedUserId, GetFragranceBatchParam param, int minRating, BaseParam baseParam) {
        return null;
    }

    @Override
    public BaseResponse<GetAllReviews> getReviewsByUserId(int userId, int minRating, BaseParam baseParam) {
        return null;
    }
}
