package com.merufureku.aromatica.review_service.services.impl;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import com.merufureku.aromatica.review_service.dto.responses.*;
import com.merufureku.aromatica.review_service.services.interfaces.IReviewService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl0 implements IReviewService {
    @Override
    public BaseResponse<ReviewsResponse> getReviewsByFragranceId(Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam) {
        return null;
    }

    @Override
    public BaseResponse<MyReviewsResponse> getMyReviews(Integer userId, Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam) {
        return null;
    }

    @Override
    public BaseResponse<PostReviewResponse> postReview(Integer userId, Long fragranceId, PostReviewParam param, BaseParam baseParam) {
        return null;
    }

    @Override
    public BaseResponse<UpdateReviewResponse> updateReview(Integer userId, Long reviewID, Long fragranceId, PostReviewParam param, BaseParam baseParam) {
        return null;
    }

    @Override
    public void deleteReview(Integer userId, Long reviewID, Long fragranceId, BaseParam baseParam) {

    }
}
