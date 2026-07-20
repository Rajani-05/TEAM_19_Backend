package com.eventmanagement.backend.service;

import com.eventmanagement.backend.model.Review;
import com.eventmanagement.backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public Review createReview(String reviewerId, String eventId, String vendorId, int rating, String comment) {
        Review review = Review.builder()
                .reviewerId(reviewerId)
                .eventId(eventId)
                .vendorId(vendorId)
                .rating(rating)
                .comment(comment)
                .build();
        return reviewRepository.save(review);
    }

    public List<Review> getVendorReviews(String vendorId) {
        return reviewRepository.findByVendorId(vendorId);
    }
}
