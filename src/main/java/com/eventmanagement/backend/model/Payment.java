package com.eventmanagement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;
    private String eventId;
    private String payerId;
    private String payeeId;
    private double amount;
    private String type; // CLIENT_TO_PLATFORM | PLATFORM_TO_VENDOR
    private String status; // PENDING | SUCCESS | FAILED | REFUNDED
    private String gatewayOrderId;
    private String gatewayPaymentId;
    @Builder.Default
    private Instant createdAt = Instant.now();
}
