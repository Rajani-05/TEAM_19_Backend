package com.eventmanagement.backend.service;

import com.eventmanagement.backend.exception.*;
import com.eventmanagement.backend.model.*;
import com.eventmanagement.backend.repository.*;
import com.razorpay.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
public class PaymentService {
    @Value("${app.razorpay.key-id}") private String razorpayKeyId;
    @Value("${app.razorpay.key-secret}") private String razorpayKeySecret;
    private final PaymentRepository paymentRepository;
    private final EventRepository eventRepository;

    public PaymentService(PaymentRepository paymentRepository, EventRepository eventRepository) {
        this.paymentRepository = paymentRepository;
        this.eventRepository = eventRepository;
    }

    public Map<String, Object> initiatePayment(String eventId, double amount, String type, String payerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        String resolvedPayerId = payerId != null ? payerId : event.getClientEmail();
        try {
            RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int) (amount * 100)); // amount in paise
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "evt_" + eventId);

            Order order = razorpay.orders.create(orderRequest);

            Payment payment = Payment.builder()
                    .eventId(eventId)
                    .payerId(resolvedPayerId)
                    .amount(amount)
                    .type(type)
                    .gatewayOrderId(order.get("id"))
                    .status("PENDING")
                    .build();
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", order.get("id"));
            response.put("amount", amount);
            response.put("currency", "INR");
            response.put("razorpayKeyId", razorpayKeyId);
            return response;
        } catch (RazorpayException e) {
            log.error("Razorpay initiation error: ", e);
            throw new BadRequestException("Razorpay error: " + e.getMessage());
        }
    }

    public void verifyPayment(String orderId, String paymentId, String signature) {
        Payment payment = paymentRepository.findByGatewayOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found"));
        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", orderId);
            attributes.put("razorpay_payment_id", paymentId);
            attributes.put("razorpay_signature", signature);

            boolean isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            if (isValid) {
                payment.setGatewayPaymentId(paymentId);
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);

                Event event = eventRepository.findById(payment.getEventId()).orElse(null);
                if (event != null) {
                    event.setStatus(Event.Status.COMPLETED); // Fully booked/settled
                    eventRepository.save(event);
                }
            } else {
                payment.setStatus("FAILED");
                paymentRepository.save(payment);
                throw new BadRequestException("Invalid payment signature");
            }
        } catch (RazorpayException e) {
            throw new BadRequestException("Verification error: " + e.getMessage());
        }
    }

    public List<Payment> getPaymentHistory(String eventId) {
        return paymentRepository.findByEventId(eventId);
    }
}
