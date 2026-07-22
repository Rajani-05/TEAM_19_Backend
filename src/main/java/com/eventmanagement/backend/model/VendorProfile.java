package com.eventmanagement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vendors")
public class VendorProfile {
    @Id
    private String id;
    @Indexed(unique = true)
    private String userId;
    private String businessName;
    private Category category;
    private PriceRange priceRange;
    private String description;
    private String phoneNo;
    private String gender;
    private String location;
    private int experienceYears;
    private boolean available = true;
    private List<String> servicesOffered;
    private List<String> portfolioImages;
    private List<String> blockedDates; // ISO date strings like "2026-07-25"
    @Builder.Default
    private double averageRating = 0.0;
    @Builder.Default
    private Status status = Status.PENDING;
    @Builder.Default
    private Instant createdAt = Instant.now();

    public enum Category {
        VENUE, CATERING, DECOR, AV, OTHER
    }

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRange {
        private double min;
        private double max;
    }
}
