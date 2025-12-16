package com.merufureku.aromatica.review_service.services.impl;

import com.merufureku.aromatica.review_service.dao.repository.ReviewsRepository;
import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.GetAllReviews;
import com.merufureku.aromatica.review_service.services.interfaces.IInternalReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class InternalReviewServiceImpl implements IInternalReviewService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final ReviewsRepository reviewsRepository;

    public InternalReviewServiceImpl(ReviewsRepository reviewsRepository) {
        this.reviewsRepository = reviewsRepository;
    }


    @Override
    public BaseResponse<GetAllReviews> getReviews(Integer excludedUserId, GetFragranceBatchParam param, int minRating, BaseParam baseParam) {

        logger.info("Get All reviews for fragrance IDs: {}", param.fragranceIds());

        var reviews = reviewsRepository.findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(param.fragranceIds(), minRating, excludedUserId);

        var interactions = reviews.stream().
                map(GetAllReviews.Interactions::new)
                .toList();

        return new BaseResponse<>(HttpStatus.OK.value(), "Get All Reviews Success",
                new GetAllReviews(interactions));
    }

    @Override
    public BaseResponse<GetAllReviews> getReviewsByUserId(int userId, int minRating, BaseParam baseParam) {

        logger.info("Get All reviews by user ID: {}", userId);

        var reviews = reviewsRepository.findByUserIdAndRatingGreaterThanOrderByFragranceId(userId, minRating);

        var interactions = reviews.stream().
                map(GetAllReviews.Interactions::new)
                .toList();

        return new BaseResponse<>(HttpStatus.OK.value(), "Get All Reviews from User Success",
                new GetAllReviews(interactions));
    }
}
