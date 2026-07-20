package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.Review;
import com.eventmanagement.backend.security.UserPrincipal;
import com.eventmanagement.backend.service.ReviewService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> createReview(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody ReviewDto dto) {
        Review review = reviewService.createReview(principal.getId(), dto.getEventId(), dto.getVendorId(), dto.getRating(), dto.getComment());
        return ResponseEntity.ok(ApiResponse.success("Review created", review));
    }

    @Data
    public static class ReviewDto {
        private String eventId;
        private String vendorId;
        private int rating;
        private String comment;
    }
}
