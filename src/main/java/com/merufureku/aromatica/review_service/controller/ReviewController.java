package com.merufureku.aromatica.review_service.controller;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.PostReviewParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.PostReviewResponse;
import com.merufureku.aromatica.review_service.dto.responses.ReviewsResponse;
import com.merufureku.aromatica.review_service.dto.responses.UpdateReviewResponse;
import com.merufureku.aromatica.review_service.services.interfaces.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewController {

    private final IReviewService reviewService;

    public ReviewController(IReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews/public/{fragranceId}")
    @Operation(summary = "Get Reviews by Fragrance ID")
    public ResponseEntity<BaseResponse<ReviewsResponse>> getReviewsByFragranceId(@PathVariable ("fragranceId") Long fragranceId,
                                                                                 @RequestParam(required = false) List<Integer> ratings,
                                                                                 @RequestParam(required = false, defaultValue = "1") int version,
                                                                                 @RequestParam(required = false, defaultValue = "") String correlationId,
                                                                                 Pageable pageable) {

        var baseParam = new BaseParam(version, correlationId);
        var response = reviewService.getReviewsByFragranceId(fragranceId, ratings, pageable, baseParam);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reviews/{fragranceId}")
    @Operation(summary = "Post new review for a fragrance")
    public ResponseEntity<BaseResponse<PostReviewResponse>> postNewReview(@PathVariable ("fragranceId") Long fragranceId,
                                                                         @RequestBody PostReviewParam param,
                                                                         @RequestParam(required = false, defaultValue = "1") int version,
                                                                         @RequestParam(required = false, defaultValue = "") String correlationId) {

        var baseParam = new BaseParam(version, correlationId);
        var response = reviewService.postReview(getUserId(), fragranceId, param, baseParam);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/reviews/{fragranceId}/{reviewId}")
    @Operation(summary = "Update review for a fragrance")
    public ResponseEntity<BaseResponse<UpdateReviewResponse>> updateReview(@PathVariable ("fragranceId") Long fragranceId,
                                                                           @PathVariable ("reviewId") Long reviewId,
                                                                           @RequestBody PostReviewParam param,
                                                                           @RequestParam(required = false, defaultValue = "1") int version,
                                                                           @RequestParam(required = false, defaultValue = "") String correlationId) {

        var baseParam = new BaseParam(version, correlationId);
        var response = reviewService.updateReview(getUserId(), reviewId, fragranceId, param, baseParam);

        return ResponseEntity.ok(response);
    }

    private Integer getUserId(){

        return (Integer) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }
}
