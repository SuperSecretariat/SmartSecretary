package com.example.chat.controller;

import com.example.chat.dto.ChatRequest;
import com.example.chat.dto.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
@CrossOrigin(origins = "*")
public class ChatController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String userPrompt = request.getMessage();
        String provider = "mistral";
        String augmentUrl = "http://localhost:8000/rag";
        String ollamaUrl = "http://localhost:11434/api/generate";
        String microsoftUrl = "https://openrouter.ai/api/v1/chat/completions";
        String openRouterApiKey = "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";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String augmentPayload = String.format("{\"prompt\": \"%s\"}", escapeJson(userPrompt));
            HttpEntity<String> augmentRequest = new HttpEntity<>(augmentPayload, headers);

            ResponseEntity<String> augmentResponse = restTemplate.postForEntity(augmentUrl, augmentRequest, String.class);
            JsonNode augmentJson = mapper.readTree(augmentResponse.getBody());

            if (augmentJson.has("error")) {
                return new ChatResponse("Eroare de la serviciul RAG: " + augmentJson.get("error").asText(), Collections.emptyList(), Collections.emptyList());
            }

            String augmentedPrompt = augmentJson.get("augmented_prompt").asText();

            if (provider.equals("microsoft")) {
                headers.setBearerAuth(openRouterApiKey);

                ObjectNode payload = mapper.createObjectNode();
                payload.put("model", "microsoft/mai-ds-r1:free");
                payload.put("temperature", 0.7);

                ArrayNode messages = mapper.createArrayNode();
                ObjectNode message = mapper.createObjectNode();
                message.put("role", "user");
                message.put("content", augmentedPrompt);
                messages.add(message);

                payload.set("messages", messages);

                HttpEntity<String> openRouterRequest = new HttpEntity<>(mapper.writeValueAsString(payload), headers);

                ResponseEntity<String> openRouterResponse = restTemplate.postForEntity(microsoftUrl, openRouterRequest, String.class);
                System.out.println("OpenRouter response: " + openRouterResponse.getBody());
                JsonNode openRouterJson = mapper.readTree(openRouterResponse.getBody());

                if (openRouterJson.has("error")) {
                    String errMsg = openRouterJson.get("error").get("message").asText();
                    return new ChatResponse("Eroare OpenRouter: " + errMsg, Collections.emptyList(), Collections.emptyList());
                }

                JsonNode choices = openRouterJson.get("choices");
                if (choices != null && choices.isArray() && !choices.isEmpty()) {
                    JsonNode Jmessage = choices.get(0).get("message");
                    if (Jmessage != null && message.has("content")) {
                        String microsoftAnswer = Jmessage.get("content").asText();
                        return new ChatResponse(microsoftAnswer, Collections.emptyList(), Collections.emptyList());
                    }
                }
                return new ChatResponse("Răspuns invalid de la OpenRouter.", Collections.emptyList(), Collections.emptyList());

            } else {
                String ollamaPayload = String.format("{\"model\": \"mistral\", \"prompt\": \"%s\", \"stream\": false}", escapeJson(augmentedPrompt));
                HttpEntity<String> ollamaRequest = new HttpEntity<>(ollamaPayload, headers);

                ResponseEntity<String> ollamaResponse = restTemplate.postForEntity(ollamaUrl, ollamaRequest, String.class);
                JsonNode ollamaJson = mapper.readTree(ollamaResponse.getBody());
                String ollamaAnswer = ollamaJson.get("response").asText();

                return new ChatResponse(ollamaAnswer, Collections.emptyList(), Collections.emptyList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Eroare la procesarea mesajului: " + e.getMessage(), Collections.emptyList(), Collections.emptyList());
        }
    }

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}