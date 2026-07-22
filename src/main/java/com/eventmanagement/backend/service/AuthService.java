package com.eventmanagement.backend.service;

import com.eventmanagement.backend.dto.*;
import com.eventmanagement.backend.exception.BadRequestException;
import com.eventmanagement.backend.model.User;
import com.eventmanagement.backend.repository.UserRepository;
import com.eventmanagement.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if ("ADMIN".equalsIgnoreCase(request.getRole())) {
            throw new BadRequestException("Administrator registration is not permitted.");
        }
        User.Role mappedRole = User.Role.CLIENT;
        if (request.getRole() != null) {
            String r = request.getRole().toUpperCase();
            if ("PLANNER".equals(r)) mappedRole = User.Role.PLANNER;
            else if ("VENDOR".equals(r)) mappedRole = User.Role.VENDOR;
            else if ("ADMIN".equals(r)) {
                throw new BadRequestException("Administrator registration is not permitted.");
            } else {
                mappedRole = User.Role.CLIENT;
            }
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(mappedRole)
                .phoneNo(request.getPhoneNo())
                .gender(request.getGender())
                .build();
        user = userRepository.save(user);

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

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid email or password"));
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
