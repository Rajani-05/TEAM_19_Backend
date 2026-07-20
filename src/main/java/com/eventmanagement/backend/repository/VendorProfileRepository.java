package com.eventmanagement.backend.repository;

import com.eventmanagement.backend.model.VendorProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface VendorProfileRepository extends MongoRepository<VendorProfile, String> {
    Optional<VendorProfile> findByUserId(String userId);
    List<VendorProfile> findByCategoryAndStatus(VendorProfile.Category category, VendorProfile.Status status);
    List<VendorProfile> findByStatus(VendorProfile.Status status);
}
