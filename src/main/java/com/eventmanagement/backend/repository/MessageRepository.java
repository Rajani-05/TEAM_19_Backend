package com.eventmanagement.backend.repository;

import com.eventmanagement.backend.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByEventIdOrderBySentAtAsc(String eventId);
}
