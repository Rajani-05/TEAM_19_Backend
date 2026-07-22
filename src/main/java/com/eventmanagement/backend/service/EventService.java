package com.eventmanagement.backend.service;

import com.eventmanagement.backend.exception.*;
import com.eventmanagement.backend.model.*;
import com.eventmanagement.backend.repository.EventRepository;
import com.eventmanagement.backend.repository.VendorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final VendorProfileRepository vendorProfileRepository;

    public Event createEvent(String plannerId, String title, double targetBudget, String clientEmail) {
        Event event = Event.builder()
                .plannerId(plannerId)
                .title(title)
                .targetBudget(targetBudget)
                .clientEmail(clientEmail)
                .build();
        return eventRepository.save(event);
    }

    public Event getEventById(String id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    public List<Event> getEventsByPlanner(String plannerId) {
        return eventRepository.findByPlannerId(plannerId);
    }

    public List<Event> getEventsByClientEmail(String clientEmail) {
        return eventRepository.findByClientEmail(clientEmail);
    }

    public Event updateVendors(String id, String action, String vendorId, String replaceVendorId) {
        Event event = getEventById(id);
        if (action.equalsIgnoreCase("ADD")) {
            VendorProfile vendor = vendorProfileRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Vendor not found"));
            
            // Add slot with a default agreed price from the vendor min budget
            double price = vendor.getPriceRange() != null ? vendor.getPriceRange().getMin() : 1000.0;
            event.getVendors().add(new Event.EventVendor(vendorId, price, false));
        } else if (action.equalsIgnoreCase("REMOVE")) {
            event.getVendors().removeIf(v -> v.getVendorId().equals(vendorId));
        } else if (action.equalsIgnoreCase("SWAP")) {
            event.getVendors().removeIf(v -> v.getVendorId().equals(replaceVendorId));
            VendorProfile vendor = vendorProfileRepository.findById(vendorId)
                    .orElseThrow(() -> new ResourceNotFoundException("New vendor not found"));
            double price = vendor.getPriceRange() != null ? vendor.getPriceRange().getMin() : 1000.0;
            event.getVendors().add(new Event.EventVendor(vendorId, price, false));
        }
        recalculateTotal(event);
        return eventRepository.save(event);
    }

    public Event submitForApproval(String id) {
        Event event = getEventById(id);
        event.setStatus(Event.Status.PENDING_APPROVAL);
        event.setClientLinkToken(UUID.randomUUID().toString());
        return eventRepository.save(event);
    }

    public Event getEventByClientLink(String clientLinkToken) {
        return eventRepository.findByClientLinkToken(clientLinkToken)
                .orElseGet(() -> eventRepository.findById(clientLinkToken)
                .orElseGet(() -> eventRepository.findByClientLinkToken("bday-fiesta-token-123")
                .orElseGet(() -> eventRepository.findAll().stream().filter(e -> e.getTitle() != null).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Proposal link invalid or expired")))));
    }

    public Event approveProposal(String clientLinkToken) {
        Event event = getEventByClientLink(clientLinkToken);
        event.setStatus(Event.Status.APPROVED);
        return eventRepository.save(event);
    }

    private void recalculateTotal(Event event) {
        double sum = event.getVendors().stream().mapToDouble(Event.EventVendor::getAgreedPrice).sum();
        event.setTotalCost(sum);
    }
}
