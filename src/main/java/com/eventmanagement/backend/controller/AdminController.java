package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.User;
import com.eventmanagement.backend.model.VendorProfile;
import com.eventmanagement.backend.service.AdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users list", adminService.getAllUsers()));
    }

    @GetMapping("/vendors/pending")
    public ResponseEntity<ApiResponse<List<VendorProfile>>> getPending() {
        return ResponseEntity.ok(ApiResponse.success("Pending applications", adminService.getPendingVendors()));
    }

    @PatchMapping("/vendors/{id}/status")
    public ResponseEntity<ApiResponse<VendorProfile>> updateStatus(@PathVariable String id, @RequestBody StatusDto dto) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", adminService.updateVendorStatus(id, dto.getStatus())));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", id));
    }

    @Data
    public static class StatusDto {
        private String status;
    }
}
