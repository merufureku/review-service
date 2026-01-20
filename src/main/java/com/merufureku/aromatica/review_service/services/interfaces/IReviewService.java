package com.merufureku.aromatica.review_service.services.interfaces;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import com.merufureku.aromatica.review_service.dto.responses.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReviewService {

    BaseResponse<ReviewsResponse> getReviewsByFragranceId(Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam);

    BaseResponse<MyReviewsResponse> getMyReviews(Integer userId, Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam);

    BaseResponse<PostReviewResponse> postReview(Integer userId, Long fragranceId, PostReviewParam param, BaseParam baseParam);

    BaseResponse<UpdateReviewResponse> updateReview(Integer userId, Long reviewID, Long fragranceId, PostReviewParam param, BaseParam baseParam);

    void deleteReview(Long reviewID, Long fragranceId, BaseParam baseParam);

    void deleteReview(Integer userId, Long reviewID, Long fragranceId, BaseParam baseParam);
}
