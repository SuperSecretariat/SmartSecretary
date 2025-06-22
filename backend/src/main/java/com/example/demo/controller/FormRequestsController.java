package com.example.demo.controller;

import com.example.demo.dto.FormRequestFieldsDTO;
import com.example.demo.exceptions.*;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.response.FormRequestResponse;
import com.example.demo.entity.FormRequest;
import com.example.demo.dto.FormRequestDTO;
import com.example.demo.service.FormRequestService;
import com.example.demo.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("api/form-requests")
public class FormRequestsController {
    private final Logger logger = LoggerFactory.getLogger(FormRequestsController.class);
    private final FormRequestService formRequestService;
    private final JwtUtil jwtUtil;

    @Autowired
    public FormRequestsController(FormRequestService formRequestService,
                                  JwtUtil jwtUtil) {
        this.formRequestService = formRequestService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/submitted")
    public ResponseEntity<List<FormRequestResponse>> getAllFormsForUserWithId(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(formRequestService.getFormRequestsByUserRegistrationNumber(token));
    }

    @GetMapping("/review-requests")
    public ResponseEntity<List<FormRequestResponse>> getAllFormsForReview(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateJwtToken(token)) {
            return ResponseEntity.status(401).build();
        }
        List<FormRequestResponse> pendingForms = formRequestService.getFormRequestsByStatus(FormRequestStatus.PENDING);
        List<FormRequestResponse> inReviewForms = formRequestService.getFormRequestsByStatus(FormRequestStatus.IN_REVIEW);
        List<FormRequestResponse> secForms = new ArrayList<>(pendingForms);
        secForms.addAll(inReviewForms);
        return ResponseEntity.ok(secForms);
    }


    @GetMapping("/{id}")
    public ResponseEntity<FormRequest> getFormRequestById(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).build();
            }
            return ResponseEntity.ok(formRequestService.getFormRequestById(id));
        } catch (InvalidFormRequestIdException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Void> createFormRequest(@Valid @RequestBody FormRequestDTO formRequestRequest,
                                               @RequestHeader("Authorization") String authorizationHeader) throws InterruptedException{
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).build();
            }

            FormRequest formRequest = formRequestService.createFormRequest(formRequestRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(formRequest.getId())
                    .toUri();

            return ResponseEntity.created(location).build();
        } catch (EncryptionException | FormRequestFieldDataException | IOException | InvalidWordToPDFConversion e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateFormRequestStatus(@PathVariable Long id,
                                                          @Valid @RequestBody String status)
    {
        try{
            this.formRequestService.updateFormRequestStatus(id, status);
            return ResponseEntity.status(204).body("The status of the form request has been updated successfully.");
        } catch (InvalidFormRequestIdException | InvalidFormRequestStatusException e){
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<List<String>> getFormImages(@PathVariable Long id,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        try{
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).build();
            }

            List<byte[]> imagesBytes = formRequestService.getFormRequestImages(id);
            List<String> imageAsBase64 = imagesBytes.stream().map(Base64.getEncoder()::encodeToString).toList();
            return ResponseEntity.ok(imageAsBase64);
        }
        catch (IOException | InvalidFormIdException e){
            this.logger.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<FormRequestFieldsDTO> getFormRequestFieldsById(@PathVariable Long id) {
        try {
            FormRequestFieldsDTO formRequestFields = formRequestService.getFormRequestFieldsById(id);
            return ResponseEntity.ok(formRequestFields);
        }
        catch (InvalidFormRequestIdException | DecryptionException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormRequestById(@PathVariable Long id) {
        try{
            formRequestService.deleteFormRequestById(id);
            return ResponseEntity.noContent().build();
        } catch (InvalidFormRequestIdException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

}
