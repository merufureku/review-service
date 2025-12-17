package com.merufureku.aromatica.review_service.services;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dao.entity.Users;
import com.merufureku.aromatica.review_service.dao.repository.FragrancesRepository;
import com.merufureku.aromatica.review_service.dao.repository.ReviewsRepository;
import com.merufureku.aromatica.review_service.dao.repository.UsersRepository;
import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import com.merufureku.aromatica.review_service.dto.responses.*;
import com.merufureku.aromatica.review_service.enums.CustomStatusEnums;
import com.merufureku.aromatica.review_service.exceptions.ServiceException;
import com.merufureku.aromatica.review_service.helper.ReviewHelper;
import com.merufureku.aromatica.review_service.helper.SpecificationHelper;
import com.merufureku.aromatica.review_service.services.impl.ReviewServiceImpl1;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImpl1Test {

    @InjectMocks
    private ReviewServiceImpl1 reviewServiceImpl1;

    @Mock
    private FragrancesRepository fragrancesRepository;

    @Mock
    private ReviewsRepository reviewsRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private SpecificationHelper specificationHelper;

    @Mock
    private ReviewHelper reviewHelper;

    private BaseParam baseParam;
    private Pageable pageable;
    private Reviews review;
    private PostReviewParam postReviewParam;

    private final Long fragranceId = 1L;
    private final Integer userId = 1;
    private final Long reviewId = 10L;

    @BeforeEach
    void setUp() {
        baseParam = new BaseParam(1, "tester");
        pageable = PageRequest.of(0, 10);

        postReviewParam = new PostReviewParam(5, "Great fragrance");
        Users users = Users.builder()
                .id(userId)
                .username("username")
                .build();

        review = Reviews.builder()
                .id(reviewId)
                .fragranceId(fragranceId)
                .userId(userId)
                .rating(5)
                .comment("Great fragrance")
                .createdAt(LocalDate.now())
                .updatedAt(LocalDate.now())
                .user(users)
                .build();
    }

    @Test
    void testGetReviewsByFragranceId_thenReturnReviews() {
        var reviews = Collections.singletonList(review);
        var reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(specificationHelper.buildReviewsSpecification(null, fragranceId, null))
                .thenReturn(mock(Specification.class));
        when(reviewsRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(reviewPage);

        BaseResponse<ReviewsResponse> response = reviewServiceImpl1
                .getReviewsByFragranceId(fragranceId, null, pageable, baseParam);

        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals(fragranceId, response.data().fragranceId());
        assertEquals(1, response.data().reviews().size());

        verify(fragrancesRepository, times(1)).existsById(fragranceId);
        verify(reviewsRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetReviewsByFragranceId_whenFragranceNotFound_thenThrowException() {
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.getReviewsByFragranceId(fragranceId, null, pageable, baseParam));

        assertEquals(CustomStatusEnums.FRAGRANCE_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetMyReviews_thenReturnReviews() {
        var reviews = Collections.singletonList(review);
        var reviewPage = new PageImpl<>(reviews, pageable, reviews.size());

        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(specificationHelper.buildReviewsSpecification(userId, fragranceId, null))
                .thenReturn(mock(Specification.class));
        when(reviewsRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(reviewPage);

        BaseResponse<MyReviewsResponse> response = reviewServiceImpl1
                .getMyReviews(userId, fragranceId, null, pageable, baseParam);

        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals(1, response.data().reviews().size());

        verify(usersRepository, times(1)).existsById(userId);
        verify(fragrancesRepository, times(1)).existsById(fragranceId);
        verify(reviewsRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetMyReviews_whenUserNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.getMyReviews(userId, fragranceId, null, pageable, baseParam));

        assertEquals(CustomStatusEnums.NO_USER_FOUND, exception.getCustomStatusEnums());
        verify(fragrancesRepository, never()).existsById(fragranceId);
        verify(reviewsRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testGetMyReviews_whenFragranceNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.getMyReviews(userId, fragranceId, null, pageable, baseParam));

        assertEquals(CustomStatusEnums.FRAGRANCE_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testPostReview_thenReturnSuccess() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.existsByUserIdAndFragranceId(userId, fragranceId)).thenReturn(false);
        when(reviewsRepository.save(any(Reviews.class))).thenReturn(review);

        BaseResponse<PostReviewResponse> response = reviewServiceImpl1
                .postReview(userId, fragranceId, postReviewParam, baseParam);

        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(HttpStatus.CREATED.value(), response.status());
        assertEquals(reviewId, response.data().reviewId());

        verify(reviewsRepository, times(1)).existsByUserIdAndFragranceId(userId, fragranceId);
        verify(reviewsRepository, times(1)).save(any(Reviews.class));
    }

    @Test
    void testPostReview_whenUserNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.postReview(userId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.NO_USER_FOUND, exception.getCustomStatusEnums());
        verify(fragrancesRepository, never()).existsById(fragranceId);
        verify(reviewsRepository, never()).existsByUserIdAndFragranceId(userId, fragranceId);
        verify(reviewsRepository, never()).save(any(Reviews.class));
    }

    @Test
    void testPostReview_whenFragranceNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.postReview(userId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.FRAGRANCE_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).existsByUserIdAndFragranceId(userId, fragranceId);
        verify(reviewsRepository, never()).save(any(Reviews.class));
    }

    @Test
    void testPostReview_whenReviewAlreadyExists_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.existsByUserIdAndFragranceId(userId, fragranceId)).thenReturn(true);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.postReview(userId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.REVIEW_ALREADY_EXISTS, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).save(any(Reviews.class));
    }

    @Test
    void testUpdateReview_thenReturnSuccess() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId))
                .thenReturn(Optional.of(review));

        Reviews updatedReview = Reviews.builder()
                .id(reviewId)
                .fragranceId(fragranceId)
                .userId(userId)
                .rating(4)
                .comment("Updated comment")
                .createdAt(review.getCreatedAt())
                .updatedAt(LocalDate.now())
                .build();

        when(reviewHelper.updateReview(review, postReviewParam)).thenReturn(updatedReview);
        when(reviewsRepository.save(updatedReview)).thenReturn(updatedReview);

        BaseResponse<UpdateReviewResponse> response = reviewServiceImpl1
                .updateReview(userId, reviewId, fragranceId, postReviewParam, baseParam);

        assertNotNull(response);
        assertNotNull(response.data());
        assertEquals(HttpStatus.OK.value(), response.status());
        assertEquals(updatedReview.getRating(), response.data().rating());
        assertEquals(updatedReview.getComment(), response.data().comment());

        verify(reviewsRepository, times(1))
                .findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId);
        verify(reviewHelper, times(1)).updateReview(review, postReviewParam);
        verify(reviewsRepository, times(1)).save(updatedReview);
    }

    @Test
    void testUpdateReview_whenUserNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.updateReview(userId, reviewId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.NO_USER_FOUND, exception.getCustomStatusEnums());
        verify(fragrancesRepository, never()).existsById(fragranceId);
        verify(reviewsRepository, never()).findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId);
    }

    @Test
    void testUpdateReview_whenFragranceNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.updateReview(userId, reviewId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.FRAGRANCE_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId);
    }

    @Test
    void testUpdateReview_whenReviewNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId))
                .thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.updateReview(userId, reviewId, fragranceId, postReviewParam, baseParam));

        assertEquals(CustomStatusEnums.REVIEW_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewHelper, never()).updateReview(any(), any());
        verify(reviewsRepository, never()).save(any(Reviews.class));
    }

    @Test
    void testDeleteReview_thenSuccess() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId))
                .thenReturn(Optional.of(review));

        reviewServiceImpl1.deleteReview(userId, reviewId, fragranceId, baseParam);

        verify(reviewsRepository, times(1)).delete(review);
    }

    @Test
    void testDeleteReview_whenUserNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.deleteReview(userId, reviewId, fragranceId, baseParam));

        assertEquals(CustomStatusEnums.NO_USER_FOUND, exception.getCustomStatusEnums());
        verify(fragrancesRepository, never()).existsById(fragranceId);
        verify(reviewsRepository, never()).findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId);
        verify(reviewsRepository, never()).delete(any(Reviews.class));
    }

    @Test
    void testDeleteReview_whenFragranceNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(false);

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.deleteReview(userId, reviewId, fragranceId, baseParam));

        assertEquals(CustomStatusEnums.FRAGRANCE_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId);
        verify(reviewsRepository, never()).delete(any(Reviews.class));
    }

    @Test
    void testDeleteReview_whenReviewNotFound_thenThrowException() {
        when(usersRepository.existsById(userId)).thenReturn(true);
        when(fragrancesRepository.existsById(fragranceId)).thenReturn(true);
        when(reviewsRepository.findByIdAndUserIdAndFragranceId(reviewId, userId, fragranceId))
                .thenReturn(Optional.empty());

        ServiceException exception = assertThrows(ServiceException.class, () ->
                reviewServiceImpl1.deleteReview(userId, reviewId, fragranceId, baseParam));

        assertEquals(CustomStatusEnums.REVIEW_NOT_FOUND, exception.getCustomStatusEnums());
        verify(reviewsRepository, never()).delete(any(Reviews.class));
    }
}
