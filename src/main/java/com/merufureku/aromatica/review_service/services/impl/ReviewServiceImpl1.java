package com.merufureku.aromatica.review_service.services.impl;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dao.repository.FragrancesRepository;
import com.merufureku.aromatica.review_service.dao.repository.ReviewsRepository;
import com.merufureku.aromatica.review_service.dao.repository.UsersRepository;
import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import com.merufureku.aromatica.review_service.dto.responses.*;
import com.merufureku.aromatica.review_service.exceptions.ServiceException;
import com.merufureku.aromatica.review_service.helper.ReviewHelper;
import com.merufureku.aromatica.review_service.helper.SpecificationHelper;
import com.merufureku.aromatica.review_service.services.interfaces.IReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.merufureku.aromatica.review_service.enums.CustomStatusEnums.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReviewServiceImpl1 implements IReviewService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final FragrancesRepository fragrancesRepository;
    private final ReviewsRepository reviewsRepository;
    private final UsersRepository usersRepository;
    private final SpecificationHelper specificationHelper;
    private final ReviewHelper reviewHelper;

    public ReviewServiceImpl1(FragrancesRepository fragrancesRepository, ReviewsRepository reviewsRepository, UsersRepository usersRepository, SpecificationHelper specificationHelper, ReviewHelper reviewHelper) {
        this.fragrancesRepository = fragrancesRepository;
        this.reviewsRepository = reviewsRepository;
        this.usersRepository = usersRepository;
        this.specificationHelper = specificationHelper;
        this.reviewHelper = reviewHelper;
    }

    @Override
    public BaseResponse<ReviewsResponse> getReviewsByFragranceId(Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam) {

        logger.info("Fetching reviews for fragrance ID: {}", fragranceId);

        if (!fragrancesRepository.existsById(fragranceId)){
            throw new ServiceException(FRAGRANCE_NOT_FOUND);
        }

        var reviewsPage = reviewsRepository.findAll(
                specificationHelper.buildReviewsSpecification(null, fragranceId, ratings),
                pageable
        );

        var reviewDetails = reviewsPage.getContent().stream()
                .map(ReviewsResponse.ReviewDetail::new)
                .toList();

        var averageRating = reviewsPage.getContent().stream().mapToInt(Reviews::getRating)
                .average().orElse(0.0);

        var response = new ReviewsResponse(fragranceId,
                averageRating,
                reviewDetails,
                reviewsPage.getNumber(),
                reviewsPage.getSize(),
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages(),
                reviewsPage.isLast());

        return new BaseResponse<>(HttpStatus.OK.value(), "Get Reviews Success",
                response);
    }

    @Override
    public BaseResponse<MyReviewsResponse> getMyReviews(Integer userId, Long fragranceId, List<Integer> ratings, Pageable pageable, BaseParam baseParam) {

        logger.info("Fetching reviews for user ID: {} and fragrance ID: {}", userId, fragranceId);

        if (!usersRepository.existsById(userId)){
            throw new ServiceException(NO_USER_FOUND);
        }

        if (fragranceId != null && !fragrancesRepository.existsById(fragranceId)){
            throw new ServiceException(FRAGRANCE_NOT_FOUND);
        }

        var reviewsPage = reviewsRepository.findAll(
                specificationHelper.buildReviewsSpecification(userId, fragranceId, ratings),
                pageable
        );

        logger.info("Found {} reviews for user ID: {}", reviewsPage.getTotalElements(), userId);

        var reviewDetails = reviewsPage.getContent().stream()
                .map(MyReviewsResponse.ReviewDetail::new)
                .toList();

        var response = new MyReviewsResponse(
                reviewDetails,
                reviewsPage.getNumber(),
                reviewsPage.getSize(),
                reviewsPage.getTotalElements(),
                reviewsPage.getTotalPages(),
                reviewsPage.isLast());

        return new BaseResponse<>(HttpStatus.OK.value(), "Get My Reviews Success",
                response);
    }

    @Override
    public BaseResponse<PostReviewResponse> postReview(Integer userId, Long fragranceId, PostReviewParam param, BaseParam baseParam) {

        logger.info("User ID: {} posting review for fragrance ID: {}", userId, fragranceId);

        if (!usersRepository.existsById(userId)){
            throw new ServiceException(NO_USER_FOUND);
        }

        if (!fragrancesRepository.existsById(fragranceId)){
            throw new ServiceException(FRAGRANCE_NOT_FOUND);
        }

        if (reviewsRepository.existsByUserIdAndFragranceId(userId, fragranceId)){
            throw new ServiceException(REVIEW_ALREADY_EXISTS);
        }

        var now = LocalDate.now();

        var newReview = Reviews.builder()
                .fragranceId(fragranceId)
                .userId(userId)
                .rating(param.rating())
                .comment(param.comment())
                .createdAt(now)
                .updatedAt(now)
                .build();

        var savedReview = reviewsRepository.save(newReview);

        return new BaseResponse<>(HttpStatus.CREATED.value(), "Post Review Success",
                new PostReviewResponse(savedReview.getId(), savedReview.getFragranceId(),
                        savedReview.getRating(), savedReview.getComment(), savedReview.getCreatedAt()));
    }

    @Override
    public BaseResponse<UpdateReviewResponse> updateReview(Integer userId, Long reviewID, Long fragranceId, PostReviewParam param, BaseParam baseParam) {

        if (!usersRepository.existsById(userId)){
            throw new ServiceException(NO_USER_FOUND);
        }

        if (!fragrancesRepository.existsById(fragranceId)){
            throw new ServiceException(FRAGRANCE_NOT_FOUND);
        }

        var review = reviewsRepository.findByIdAndUserIdAndFragranceId(reviewID, userId, fragranceId)
                .orElseThrow(() -> new ServiceException(REVIEW_NOT_FOUND));

        var updatedReview = reviewHelper.updateReview(review, param);

        reviewsRepository.save(updatedReview);

        return new BaseResponse<>(HttpStatus.OK.value(), "Update Review Success",
                new UpdateReviewResponse(updatedReview.getId(), updatedReview.getFragranceId(),
                        updatedReview.getRating(), updatedReview.getComment(), updatedReview.getUpdatedAt()));
    }

    @Override
    public void deleteReview(Integer userId, Long reviewID, Long fragranceId, BaseParam baseParam) {

        logger.info("User ID: {} deleting review ID: {} for fragrance ID: {}", userId, reviewID, fragranceId);

        if (!usersRepository.existsById(userId)){
            throw new ServiceException(NO_USER_FOUND);
        }

        if (!fragrancesRepository.existsById(fragranceId)){
            throw new ServiceException(FRAGRANCE_NOT_FOUND);
        }

        var review = reviewsRepository.findByIdAndUserIdAndFragranceId(reviewID, userId, fragranceId)
                .orElseThrow(() -> new ServiceException(REVIEW_NOT_FOUND));

        reviewsRepository.delete(review);

        logger.info("Review ID: {} deleted successfully", reviewID);
    }
}
