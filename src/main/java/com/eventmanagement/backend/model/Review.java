package com.eventmanagement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {
    @Id
    private String id;
    private String eventId;
    private String vendorId;
    private String reviewerId;
    private int rating;
    private String comment;
    @Builder.Default
    private Instant createdAt = Instant.now();
}
