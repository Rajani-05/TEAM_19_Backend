package com.eventmanagement.backend.service;

import com.eventmanagement.backend.dto.AuthResponse;
import com.eventmanagement.backend.exception.BadRequestException;
import com.eventmanagement.backend.model.User;
import com.eventmanagement.backend.repository.UserRepository;
import com.eventmanagement.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpService {
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    // In-memory OTP store (email -> otp). In production, use Redis with TTL.
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public String generateAndSendOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("No account found with this email address."));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStore.put(email, otp);

        // In production, send via email/SMS service. For demo, log to console.
        log.info("========================================");
        log.info("  OTP for {} : {}", email, otp);
        log.info("========================================");

        return otp; // Return OTP in response for demo/testing purposes
    }

    public AuthResponse verifyOtpAndLogin(String email, String otp) {
        String storedOtp = otpStore.get(email);
        if (storedOtp == null) {
            throw new BadRequestException("OTP expired or not requested. Please request a new OTP.");
        }
        if (!storedOtp.equals(otp)) {
            throw new BadRequestException("Invalid OTP. Please check and try again.");
        }

        // OTP valid — remove it and issue JWT
        otpStore.remove(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User account not found."));

        String token = tokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .role(user.getRole().name())
                        .phoneNo(user.getPhoneNo())
                        .gender(user.getGender())
                        .build())
                .build();
    }
}
