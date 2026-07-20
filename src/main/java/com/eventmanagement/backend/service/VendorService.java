package com.eventmanagement.backend.service;

import com.eventmanagement.backend.exception.ResourceNotFoundException;
import com.eventmanagement.backend.model.VendorProfile;
import com.eventmanagement.backend.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VendorService {
    private final VendorProfileRepository vendorProfileRepository;

    public List<VendorProfile> getVendors(String category, String search) {
        if (category != null && !category.isEmpty()) {
            return vendorProfileRepository.findByCategoryAndStatus(VendorProfile.Category.valueOf(category.toUpperCase()), VendorProfile.Status.APPROVED);
        }
        return vendorProfileRepository.findByStatus(VendorProfile.Status.APPROVED);
    }

    public VendorProfile getVendorById(String id) {
        return vendorProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found"));
    }

    public VendorProfile getVendorByUserId(String userId) {
        return vendorProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor profile not found for user: " + userId));
    }

    public VendorProfile createProfile(String userId, VendorProfile profile) {
        profile.setUserId(userId);
        profile.setStatus(VendorProfile.Status.APPROVED); // Auto-approve locally for seamless testing
        return vendorProfileRepository.save(profile);
    }

    public VendorProfile updateProfile(String id, VendorProfile profile) {
        VendorProfile existing = getVendorById(id);
        existing.setBusinessName(profile.getBusinessName());
        existing.setCategory(profile.getCategory());
        existing.setPriceRange(profile.getPriceRange());
        existing.setDescription(profile.getDescription());
        existing.setPortfolioImages(profile.getPortfolioImages());
        return vendorProfileRepository.save(existing);
    }
}
