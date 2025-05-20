package com.example.demo.controller;

import com.example.demo.dto.ChatRequest;
import com.example.demo.response.ChatResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

import static com.example.demo.constants.LLMUrl.*;

/*
 Copy pasted from the LLM team's repo: https://github.com/AndreiBalan98/ArtificialGeneralIntelligence/tree/main
 Without the setup done locally this will not work when called
 This needs to be refactored as it's literally just hardcoded strings and nothing else
 !!! When doing the setup from the README, for setting up Python:
 - at point a. run venv\Scripts\Activate.ps1 instead
 - at point b. replace \ with ' in your terminal as it won't work with Powershell like that.
 */

@RestController
@RequestMapping("/api/pubble")
public class PubbleChatController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String userPrompt = request.getMessage();
        String provider = request.getProvider();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ObjectNode augmentPayload = mapper.createObjectNode();
            augmentPayload.put("prompt", userPrompt);
            HttpEntity<String> augmentRequest = new HttpEntity<>(mapper.writeValueAsString(augmentPayload), headers);

            ResponseEntity<String> augmentResponse = restTemplate.postForEntity(augmentUrl, augmentRequest, String.class);
            JsonNode augmentJson = mapper.readTree(augmentResponse.getBody());

            if (augmentJson.has("error")) {
                return new ChatResponse("RAG service error: " + augmentJson.get("error").asText(), Collections.emptyList(), Collections.emptyList());
            }

            String augmentedPrompt = augmentJson.get("augmented_prompt").asText();
            JsonNode chunksNode = augmentJson.get("chunks");
            JsonNode sourcesNode = augmentJson.get("sources");

            List<String> chunks = chunksNode != null ? mapper.readValue(chunksNode.toString(), List.class) : Collections.emptyList();
            List<String> sources = sourcesNode != null ? mapper.readValue(sourcesNode.toString(), List.class) : Collections.emptyList();

            if (provider.equals("microsoft")) {
                headers.setBearerAuth(openRouterApiKey);

                ObjectNode openRouterPayload = mapper.createObjectNode();
                openRouterPayload.put("model", "microsoft/mai-ds-r1:free");
                openRouterPayload.put("temperature", 0.7);

                ObjectNode message = mapper.createObjectNode();
                message.put("role", "user");
                message.put("content", augmentedPrompt);

                openRouterPayload.putArray("messages").add(message);

                HttpEntity<String> openRouterRequest = new HttpEntity<>(mapper.writeValueAsString(openRouterPayload), headers);


                ResponseEntity<String> openRouterResponse = restTemplate.postForEntity(microsoftUrl, openRouterRequest, String.class);
                JsonNode openRouterJson = mapper.readTree(openRouterResponse.getBody());

                JsonNode microsoftAnswerNode = openRouterJson.at("/choices/0/message/content");
                String microsoftAnswer = microsoftAnswerNode.isTextual() ? microsoftAnswerNode.asText() : "Invalid response from OpenRouter";

                return new ChatResponse(microsoftAnswer, chunks, sources);

            } else if (provider.equals("mistral")) {
                ObjectNode ollamaPayload = mapper.createObjectNode();
                ollamaPayload.put("model", "mistral");
                ollamaPayload.put("prompt", augmentedPrompt);
                ollamaPayload.put("stream", false);

                HttpEntity<String> ollamaRequest = new HttpEntity<>(mapper.writeValueAsString(ollamaPayload), headers);


                ResponseEntity<String> ollamaResponse = restTemplate.postForEntity(ollamaUrl, ollamaRequest, String.class);
                JsonNode ollamaJson = mapper.readTree(ollamaResponse.getBody());
                String ollamaAnswer = ollamaJson.get("response").asText();

                return new ChatResponse(ollamaAnswer, chunks, sources);
            }
            else {
                return new ChatResponse("No provider selected", Collections.emptyList(), Collections.emptyList());
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse("Error when processing message: " + e.getMessage(), Collections.emptyList(), Collections.emptyList());
        }
    }
}