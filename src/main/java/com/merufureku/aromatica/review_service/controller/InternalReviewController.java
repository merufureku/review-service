package com.merufureku.aromatica.review_service.controller;

import com.merufureku.aromatica.review_service.dto.params.BaseParam;
import com.merufureku.aromatica.review_service.dto.params.GetFragranceBatchParam;
import com.merufureku.aromatica.review_service.dto.responses.BaseResponse;
import com.merufureku.aromatica.review_service.dto.responses.GetAllReviews;
import com.merufureku.aromatica.review_service.services.factory.InternalReviewServiceFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("internal/reviews")
public class InternalReviewController {

    private final InternalReviewServiceFactory internalReviewServiceFactory;

    public InternalReviewController(InternalReviewServiceFactory internalReviewServiceFactory) {
        this.internalReviewServiceFactory = internalReviewServiceFactory;
    }

    @PostMapping
    @Operation(summary = "Get all reviews from specific perfumes")
    public ResponseEntity<BaseResponse<GetAllReviews>> getReviews(@RequestBody GetFragranceBatchParam param,
                                                  @RequestParam int minRating,
                                                  @RequestParam(required = false) Integer excludedUserId,
                                                  @RequestParam(required = false, defaultValue = "1") int version,
                                                  @RequestParam(required = false, defaultValue = "") String correlationId){

        var response = internalReviewServiceFactory.getService(version)
                .getReviews(excludedUserId, param, minRating, new BaseParam(version, correlationId));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get all reviews")
    public ResponseEntity<BaseResponse<GetAllReviews>> getReviewsByUserId(@PathVariable int userId,
                                                          @RequestParam int minRating,
                                                          @RequestParam(required = false, defaultValue = "1") int version,
                                                          @RequestParam(required = false, defaultValue = "") String correlationId){

        var response = internalReviewServiceFactory.getService(version)
                .getReviewsByUserId(userId, minRating, new BaseParam(version, correlationId));

        return ResponseEntity.ok(response);
    }

}
