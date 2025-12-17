package com.merufureku.aromatica.review_service.services;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dao.repository.ReviewsRepository;
import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.GetAllReviews;
import com.merufureku.aromatica.review_service.services.impl.InternalReviewServiceImpl1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalReviewServiceImpl1Test {

    @InjectMocks
    private InternalReviewServiceImpl1 internalReviewServiceImpl1;

    @Mock
    private ReviewsRepository reviewsRepository;

    private BaseParam baseParam;
    private Reviews review1;
    private Reviews review2;
    private Set<Long> fragranceIds;
    private int minRating;
    private Integer excludedUserId;
    private int userId;

    @BeforeEach
    void setUp() {
        baseParam = new BaseParam(1, "test-correlation-id");
        minRating = 3;
        excludedUserId = 1;
        userId = 5;
        fragranceIds = Set.of(10L, 20L);

        review1 = Reviews.builder()
                .id(100L)
                .fragranceId(10L)
                .userId(2)
                .rating(4)
                .comment("Nice fragrance")
                .createdAt(LocalDate.now().minusDays(2))
                .updatedAt(LocalDate.now().minusDays(1))
                .build();

        review2 = Reviews.builder()
                .id(101L)
                .fragranceId(20L)
                .userId(3)
                .rating(5)
                .comment("Excellent fragrance")
                .createdAt(LocalDate.now().minusDays(3))
                .updatedAt(LocalDate.now())
                .build();
    }

    @Test
    void getReviews_whenReviewsExist_returnsInteractions() {
        GetFragranceBatchParam param = new GetFragranceBatchParam(fragranceIds);
        List<Reviews> reviews = List.of(review1, review2);

        when(reviewsRepository.findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, excludedUserId)).thenReturn(reviews);

        BaseResponse<GetAllReviews> response = internalReviewServiceImpl1.getReviews(
                excludedUserId, param, minRating, baseParam);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertNotNull(response.data().interactions());
        assertEquals(2, response.data().interactions().size());

        GetAllReviews.Interactions interaction1 = response.data().interactions().getFirst();
        assertEquals(review1.getUserId(), interaction1.userId());
        assertEquals(review1.getFragranceId(), interaction1.fragranceId());
        assertEquals(review1.getRating(), interaction1.rating());
        assertEquals(review1.getUpdatedAt(), interaction1.reviewDate());

        verify(reviewsRepository, times(1)).findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, excludedUserId);
    }

    @Test
    void getReviews_whenNoReviews_returnsEmptyInteractions() {
        GetFragranceBatchParam param = new GetFragranceBatchParam(fragranceIds);

        when(reviewsRepository.findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, excludedUserId)).thenReturn(Collections.emptyList());

        BaseResponse<GetAllReviews> response = internalReviewServiceImpl1.getReviews(
                excludedUserId, param, minRating, baseParam);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertNotNull(response.data().interactions());
        assertTrue(response.data().interactions().isEmpty());

        verify(reviewsRepository, times(1)).findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, excludedUserId);
    }

    @Test
    void getReviews_whenExcludedUserIsNull_passesNullToRepository() {
        GetFragranceBatchParam param = new GetFragranceBatchParam(fragranceIds);

        when(reviewsRepository.findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, null)).thenReturn(List.of(review1));

        BaseResponse<GetAllReviews> response = internalReviewServiceImpl1.getReviews(
                null, param, minRating, baseParam);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertEquals(1, response.data().interactions().size());

        verify(reviewsRepository, times(1)).findAllByFragranceIdInAndRatingGreaterThanOrEqualExcludingUser(
                fragranceIds, minRating, null);
    }

    @Test
    void getReviewsByUserId_whenReviewsExist_returnsInteractions() {
        List<Reviews> reviews = List.of(review1, review2);

        when(reviewsRepository.findByUserIdAndRatingGreaterThanOrderByFragranceId(userId, minRating))
                .thenReturn(reviews);

        BaseResponse<GetAllReviews> response = internalReviewServiceImpl1.getReviewsByUserId(
                userId, minRating, baseParam);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertNotNull(response.data().interactions());
        assertEquals(2, response.data().interactions().size());

        verify(reviewsRepository, times(1)).findByUserIdAndRatingGreaterThanOrderByFragranceId(userId, minRating);
    }

    @Test
    void getReviewsByUserId_whenNoReviews_returnsEmptyInteractions() {
        when(reviewsRepository.findByUserIdAndRatingGreaterThanOrderByFragranceId(userId, minRating))
                .thenReturn(Collections.emptyList());

        BaseResponse<GetAllReviews> response = internalReviewServiceImpl1.getReviewsByUserId(
                userId, minRating, baseParam);

        assertNotNull(response);
        assertEquals(HttpStatus.OK.value(), response.status());
        assertNotNull(response.data());
        assertNotNull(response.data().interactions());
        assertTrue(response.data().interactions().isEmpty());

        verify(reviewsRepository, times(1)).findByUserIdAndRatingGreaterThanOrderByFragranceId(userId, minRating);
    }
}

