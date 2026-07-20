package com.eventmanagement.backend.repository;

import com.eventmanagement.backend.model.Review;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByVendorId(String vendorId);
}
