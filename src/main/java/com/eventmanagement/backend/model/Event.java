package com.eventmanagement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String plannerId;
    private String clientEmail;
    private String title;
    private double targetBudget;
    @Builder.Default
    private double totalCost = 0.0;
    @Builder.Default
    private Status status = Status.DRAFT;
    @Builder.Default
    private List<EventVendor> vendors = new ArrayList<>();
    private String clientLinkToken;
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum Status {
        DRAFT, PENDING_APPROVAL, APPROVED, COMPLETED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventVendor {
        private String vendorId;
        private double agreedPrice;
        @Builder.Default
        private boolean locked = false;
    }
}
