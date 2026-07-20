package com.eventmanagement.backend.service;

import com.eventmanagement.backend.model.User;
import com.eventmanagement.backend.model.VendorProfile;
import com.eventmanagement.backend.repository.UserRepository;
import com.eventmanagement.backend.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final VendorProfileRepository vendorProfileRepository;

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<VendorProfile> getPendingVendors() { return vendorProfileRepository.findByStatus(VendorProfile.Status.PENDING); }
    public VendorProfile updateVendorStatus(String id, String status) {
        VendorProfile vendor = vendorProfileRepository.findById(id).orElseThrow();
        vendor.setStatus(VendorProfile.Status.valueOf(status.toUpperCase()));
        return vendorProfileRepository.save(vendor);
    }
}
