package com.example.demo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static com.example.demo.constants.LLMUrl.transcriptionUrl;

@RestController
@RequestMapping("/api/audio")
public class AudioTranscriptionController {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping("/transcribe")
    public ResponseEntity<String> transcribeAudio(@RequestParam("audio") MultipartFile audioFile) {
        try {
            if (audioFile.isEmpty()) {
                return ResponseEntity.badRequest().body("{\"error\":\"Audio file is empty\"}");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            ByteArrayResource audioResource = new ByteArrayResource(audioFile.getBytes()) {
                @Override
                public String getFilename() {
                    return audioFile.getOriginalFilename();
                }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("audio", audioResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(transcriptionUrl, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                JsonNode responseJson = mapper.readTree(response.getBody());

                if (responseJson.has("text")) {
                    String transcribedText = responseJson.get("text").asText();
                    return ResponseEntity.ok("{\"text\":\"" + transcribedText.replace("\"", "\\\"") + "\",\"status\":\"success\"}");
                } else {
                    return ResponseEntity.status(500).body("{\"error\":\"No transcription text received\"}");
                }
            } else {
                return ResponseEntity.status(503).body("{\"error\":\"Transcription service unavailable\"}");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"error\":\"Transcription failed: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(
                    transcriptionUrl.replace("/transcribe", "/health"), String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok("{\"status\":\"healthy\",\"transcription_service\":\"available\"}");
            } else {
                return ResponseEntity.status(503).body("{\"status\":\"unhealthy\",\"transcription_service\":\"unavailable\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(503).body("{\"status\":\"unhealthy\",\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}