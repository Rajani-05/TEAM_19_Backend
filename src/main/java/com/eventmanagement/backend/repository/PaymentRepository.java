package com.eventmanagement.backend.repository;

import com.eventmanagement.backend.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);
    List<Payment> findByEventId(String eventId);
}
