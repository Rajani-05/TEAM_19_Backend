package com.eventmanagement.backend.repository;

import com.eventmanagement.backend.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findByPlannerId(String plannerId);
    Optional<Event> findByClientLinkToken(String clientLinkToken);
}
