package com.eventmanagement.backend.service;

import com.eventmanagement.backend.model.Message;
import com.eventmanagement.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final MessageRepository messageRepository;

    public List<Message> getMessages(String eventId) {
        return messageRepository.findByEventIdOrderBySentAtAsc(eventId);
    }

    public Message sendMessage(String eventId, String senderId, String receiverId, String content, boolean aiGenerated) {
        Message message = Message.builder()
                .eventId(eventId)
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .aiGenerated(aiGenerated)
                .build();
        return messageRepository.save(message);
    }
}
