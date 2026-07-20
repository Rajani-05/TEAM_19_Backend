package com.eventmanagement.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.*;

@Slf4j
@Service
public class GeminiService {
    @Value("${app.gemini.api-key}") private String apiKey;
    @Value("${app.gemini.api-url}") private String apiUrl;
    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateNegotiationSuggestion(String eventId, String vendorId, String goal) {
        String prompt = String.format(
            "Draft a polite, professional negotiation email / message to a vendor (id: %s) for event (id: %s) with the following goal: %s. " +
            "Be professional, clear, and collaborative. Return ONLY the drafted message content.",
            vendorId, eventId, goal
        );

        try {
            Map<String, Object> requestBody = new HashMap<>();
            List<Map<String, Object>> contents = new ArrayList<>();
            Map<String, Object> content = new HashMap<>();
            List<Map<String, String>> parts = new ArrayList<>();
            parts.add(Map.of("text", prompt));
            content.put("parts", parts);
            contents.add(content);
            requestBody.put("contents", contents);

            Map<?, ?> response = webClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("candidates")) {
                List<?> candidates = (List<?>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> contentMap = (Map<?, ?>) candidate.get("content");
                    List<?> partsList = (List<?>) contentMap.get("parts");
                    if (!partsList.isEmpty()) {
                        Map<?, ?> part = (Map<?, ?>) partsList.get(0);
                        return (String) part.get("text");
                    }
                }
            }
            return "Failed to generate AI suggestion. Please write a polite message manually.";
        } catch (Exception e) {
            log.error("Gemini API error: ", e);
            return "Polite reminder to align on the package budget and services as drafted.";
        }
    }
}
