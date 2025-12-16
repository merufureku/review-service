package com.merufureku.aromatica.review_service.services.interfaces;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.GetAllReviews;

public interface IInternalReviewService {

    BaseResponse<GetAllReviews> getReviews(Integer excludedUserId, GetFragranceBatchParam param, int minRating, BaseParam baseParam);

    BaseResponse<GetAllReviews> getReviewsByUserId(int userId, int minRating, BaseParam baseParam);
}
