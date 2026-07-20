package com.eventmanagement.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class Message {
    @Id
    private String id;
    private String eventId;
    private String senderId;
    private String receiverId;
    private String content;
    @Builder.Default
    private boolean aiGenerated = false;
    @Builder.Default
    private Instant sentAt = Instant.now();
}
