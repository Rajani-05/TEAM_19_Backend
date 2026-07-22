package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.*;
import com.eventmanagement.backend.service.AuthService;
import com.eventmanagement.backend.service.OtpService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Register success", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Login success", authService.login(request)));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpResponseDto>> sendOtp(@RequestBody OtpRequestDto request) {
        String otp = otpService.generateAndSendOtp(request.getEmail());
        OtpResponseDto resp = new OtpResponseDto();
        resp.setOtpHint(otp); // For demo only — remove in production
        resp.setMessage("OTP sent to " + request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", resp));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@RequestBody VerifyOtpDto request) {
        AuthResponse authResponse = otpService.verifyOtpAndLogin(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(ApiResponse.success("OTP verified, login successful", authResponse));
    }

    @Data
    public static class OtpRequestDto {
        private String email;
    }

    @Data
    public static class OtpResponseDto {
        private String otpHint;
        private String message;
    }

    @Data
    public static class VerifyOtpDto {
        private String email;
        private String otp;
    }
}
