package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.VendorProfile;
import com.eventmanagement.backend.model.Review;
import com.eventmanagement.backend.security.UserPrincipal;
import com.eventmanagement.backend.service.VendorService;
import com.eventmanagement.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VendorProfile>>> getVendors(
            @RequestParam(required = false) String category, @RequestParam(required = false) String search) {
        return ResponseEntity.ok(ApiResponse.success("Vendors retrieved", vendorService.getVendors(category, search)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorProfile>> getVendorById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Vendor profile found", vendorService.getVendorById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VendorProfile>> createProfile(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody VendorProfile profile) {
        return ResponseEntity.ok(ApiResponse.success("Profile created", vendorService.createProfile(principal.getId(), profile)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<VendorProfile>> updateProfile(@PathVariable String id, @RequestBody VendorProfile profile) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", vendorService.updateProfile(id, profile)));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> getReviews(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Reviews retrieved", reviewService.getVendorReviews(id)));
    }
}
