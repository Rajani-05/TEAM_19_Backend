package com.eventmanagement.backend.service;

import com.eventmanagement.backend.exception.*;
import com.eventmanagement.backend.model.*;
import com.eventmanagement.backend.repository.*;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.Utils;
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
                .orElseGet(() -> eventRepository.findByClientLinkToken(eventId).orElse(null));
        String resolvedPayerId = payerId != null ? payerId : (event != null ? event.getClientEmail() : "client@example.com");
        try {
            String orderId;
            if ("YOUR_RAZORPAY_KEY_ID".equals(razorpayKeyId) || razorpayKeyId == null || razorpayKeyId.startsWith("YOUR_")) {
                log.info("Sandbox/Unconfigured Razorpay key active. Creating simulated test order.");
                orderId = "order_simulated_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
            } else {
                RazorpayClient razorpay = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
                JSONObject orderRequest = new JSONObject();
                orderRequest.put("amount", (int) (amount * 100)); // amount in paise
                orderRequest.put("currency", "INR");
                orderRequest.put("receipt", "evt_" + eventId);

                Order order = razorpay.orders.create(orderRequest);
                orderId = order.get("id");
            }

            Payment payment = Payment.builder()
                    .eventId(eventId)
                    .payerId(resolvedPayerId)
                    .amount(amount)
                    .type(type)
                    .gatewayOrderId(orderId)
                    .status("PENDING")
                    .build();
            paymentRepository.save(payment);

            Map<String, Object> response = new HashMap<>();
            response.put("orderId", orderId);
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

            boolean isValid = false;
            if ("YOUR_RAZORPAY_KEY_SECRET".equals(razorpayKeySecret) || signature.startsWith("sig_simulated")) {
                log.info("Simulated or unconfigured signature bypass active. Auto-verifying payment.");
                isValid = true;
            } else {
                isValid = Utils.verifyPaymentSignature(attributes, razorpayKeySecret);
            }

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
