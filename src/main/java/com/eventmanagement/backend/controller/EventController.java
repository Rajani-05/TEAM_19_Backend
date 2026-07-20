package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.Event;
import com.eventmanagement.backend.security.UserPrincipal;
import com.eventmanagement.backend.service.EventService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse<Event>> createEvent(
            @AuthenticationPrincipal UserPrincipal principal, @RequestBody EventCreateDto dto) {
        Event event = eventService.createEvent(principal.getId(), dto.getTitle(), dto.getTargetBudget(), dto.getClientEmail());
        return ResponseEntity.ok(ApiResponse.success("Event created", event));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Event>> getEvent(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Event found", eventService.getEventById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Event>>> getMyEvents(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.success("Events retrieved", eventService.getEventsByPlanner(principal.getId())));
    }

    @PatchMapping("/{id}/vendors")
    public ResponseEntity<ApiResponse<Event>> updateVendors(@PathVariable String id, @RequestBody VendorUpdateDto dto) {
        Event event = eventService.updateVendors(id, dto.getAction(), dto.getVendorId(), dto.getReplaceVendorId());
        return ResponseEntity.ok(ApiResponse.success("Vendors updated", event));
    }

    @PostMapping("/{id}/submit-for-approval")
    public ResponseEntity<ApiResponse<EventSubmitResponseDto>> submitForApproval(@PathVariable String id) {
        Event event = eventService.submitForApproval(id);
        EventSubmitResponseDto resp = new EventSubmitResponseDto();
        resp.setClientLinkToken(event.getClientLinkToken());
        return ResponseEntity.ok(ApiResponse.success("Proposal submitted", resp));
    }

    @GetMapping("/client-view/{clientLinkToken}")
    public ResponseEntity<ApiResponse<Event>> getClientView(@PathVariable String clientLinkToken) {
        return ResponseEntity.ok(ApiResponse.success("Proposal retrieved", eventService.getEventByClientLink(clientLinkToken)));
    }

    @PostMapping("/client-view/{clientLinkToken}/approve")
    public ResponseEntity<ApiResponse<Event>> approveProposal(@PathVariable String clientLinkToken) {
        return ResponseEntity.ok(ApiResponse.success("Proposal approved", eventService.approveProposal(clientLinkToken)));
    }

    @Data
    public static class EventCreateDto {
        private String title;
        private double targetBudget;
        private String clientEmail;
    }

    @Data
    public static class VendorUpdateDto {
        private String action;
        private String vendorId;
        private String replaceVendorId;
    }

    @Data
    public static class EventSubmitResponseDto {
        private String clientLinkToken;
    }
}
