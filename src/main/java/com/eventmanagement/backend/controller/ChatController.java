package com.eventmanagement.backend.controller;

import com.eventmanagement.backend.dto.ApiResponse;
import com.eventmanagement.backend.model.Message;
import com.eventmanagement.backend.security.UserPrincipal;
import com.eventmanagement.backend.service.ChatService;
import com.eventmanagement.backend.service.GeminiService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final GeminiService geminiService;

    @GetMapping("/api/chat/{eventId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> getMessages(@PathVariable String eventId) {
        return ResponseEntity.ok(ApiResponse.success("Messages retrieved", chatService.getMessages(eventId)));
    }

    @PostMapping("/api/chat/{eventId}/messages")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @AuthenticationPrincipal UserPrincipal principal, @PathVariable String eventId, @RequestBody SendMsgDto dto) {
        Message msg = chatService.sendMessage(eventId, principal.getId(), dto.getReceiverId(), dto.getContent(), dto.isAiGenerated());
        return ResponseEntity.ok(ApiResponse.success("Message sent", msg));
    }

    @PostMapping("/api/ai/draft-message")
    public ResponseEntity<ApiResponse<String>> draftMessage(@RequestBody DraftMsgDto dto) {
        String draft = geminiService.generateNegotiationSuggestion(dto.getEventId(), dto.getVendorId(), dto.getGoal());
        return ResponseEntity.ok(ApiResponse.success("AI draft generated", draft));
    }

    @Data
    public static class SendMsgDto {
        private String receiverId;
        private String content;
        private boolean aiGenerated;
    }

    @Data
    public static class DraftMsgDto {
        private String eventId;
        private String vendorId;
        private String goal;
    }
}
