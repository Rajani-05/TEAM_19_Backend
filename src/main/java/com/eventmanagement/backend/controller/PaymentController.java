package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.Payment;
import com.eventmanagement.backend.security.UserPrincipal;
import com.eventmanagement.backend.service.PaymentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> initiatePayment(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody InitiateDto dto) {
        String payerId = principal != null ? principal.getId() : null;
        Map<String, Object> order = paymentService.initiatePayment(dto.getEventId(), dto.getAmount(), dto.getType(), payerId);
        return ResponseEntity.ok(ApiResponse.success("Order initiated", order));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyPayment(@RequestBody VerifyDto dto) {
        paymentService.verifyPayment(dto.getOrderId(), dto.getPaymentId(), dto.getSignature());
        return ResponseEntity.ok(ApiResponse.success("Payment verified"));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<ApiResponse<List<Payment>>> getHistory(@PathVariable String eventId) {
        return ResponseEntity.ok(ApiResponse.success("History retrieved", paymentService.getPaymentHistory(eventId)));
    }

    @Data
    public static class InitiateDto {
        private String eventId;
        private double amount;
        private String type;
    }

    @Data
    public static class VerifyDto {
        private String orderId;
        private String paymentId;
        private String signature;
    }
}
