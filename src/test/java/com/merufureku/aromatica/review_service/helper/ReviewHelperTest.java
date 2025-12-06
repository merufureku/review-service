package com.merufureku.aromatica.review_service.helper;

import com.merufureku.aromatica.review_service.dao.entity.Reviews;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewHelperTest {

    private ReviewHelper reviewHelper;

    private Reviews existingReview;

    @BeforeEach
    void setUp() {
        reviewHelper = new ReviewHelper();
        existingReview = Reviews.builder()
                .id(1L)
                .fragranceId(1L)
                .userId(1)
                .rating(3)
                .comment("Old comment")
                .createdAt(LocalDate.now().minusDays(2))
                .updatedAt(LocalDate.now().minusDays(1))
                .build();
    }

    @Test
    void testUpdateReview_whenUpdateBothFields_thenSuccess() {
        PostReviewParam param = new PostReviewParam(5, "New comment");

        Reviews updated = reviewHelper.updateReview(existingReview, param);

        assertEquals(5, updated.getRating());
        assertEquals("New comment", updated.getComment());
        assertNotNull(updated.getUpdatedAt());
        assertNotEquals(existingReview.getUpdatedAt(), updated.getUpdatedAt());
    }

    @Test
    void testUpdateReview_whenUpdateOnlyComment_thenSuccess() {
        PostReviewParam param = new PostReviewParam(3, "Only comment updated");

        Reviews updated = reviewHelper.updateReview(existingReview, param);

        assertEquals(3, updated.getRating());
        assertEquals("Only comment updated", updated.getComment());
        assertNotNull(updated.getUpdatedAt());
        assertNotEquals(existingReview.getUpdatedAt(), updated.getUpdatedAt());
    }

    @Test
    void testUpdateReview_whenUpdateOnlyRating_thenSuccess() {
        PostReviewParam param = new PostReviewParam(4, null);

        Reviews updated = reviewHelper.updateReview(existingReview, param);

        assertEquals(4, updated.getRating());
        assertEquals("Old comment", updated.getComment());
        assertNotNull(updated.getUpdatedAt());
        assertNotEquals(existingReview.getUpdatedAt(), updated.getUpdatedAt());
    }

    @Test
    void testUpdateReview_whenNoFieldsUpdated_thenOnlyDateChanges() {
        PostReviewParam param = new PostReviewParam(3, "Old comment");

        Reviews updated = reviewHelper.updateReview(existingReview, param);

        assertEquals(3, updated.getRating());
        assertEquals("Old comment", updated.getComment());
        assertNotNull(updated.getUpdatedAt());
        assertNotEquals(existingReview.getUpdatedAt(), updated.getUpdatedAt());
    }
}
