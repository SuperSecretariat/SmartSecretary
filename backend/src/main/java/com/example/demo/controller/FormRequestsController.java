package com.example.demo.controller;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.response.FormRequestResponse;
import com.example.demo.exceptions.FormRequestFieldDataException;
import com.example.demo.exceptions.InvalidFormRequestIdException;
import com.example.demo.entity.FormRequest;
import com.example.demo.dto.FormRequestRequest;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.FormRequestService;
import com.example.demo.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity<JwtResponse> getAllFormsForUserWithId(
            @RequestHeader("Authorization") String authorizationHeader) {

            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            String registrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(token);
            List<FormRequestResponse> forms = formRequestService.getFormRequestsByUserRegistrationNumber(registrationNumber);
            return ResponseEntity.ok(new JwtResponse("Form requests retrieved successfully", forms));

    }

    @GetMapping("/{id}")
    public ResponseEntity<JwtResponse> getFormRequestById(@PathVariable Long id,
                                                          @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            FormRequest formRequest = formRequestService.getFormRequestById(id);
            return ResponseEntity.ok(new JwtResponse("Form request retrieved successfully", formRequest));
        } catch (InvalidFormRequestIdException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.status(400).body(new JwtResponse(ErrorMessage.INVALID_DATA));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<JwtResponse> createFormRequest(@Valid @RequestBody FormRequestRequest formRequestRequest,
                                               @RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.substring(7);
            if (!jwtUtil.validateJwtToken(token)) {
                return ResponseEntity.status(401).body(new JwtResponse(ErrorMessage.INVALID_DATA));
            }

            FormRequest formRequest = formRequestService.createFormRequest(formRequestRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(formRequest.getId())
                    .toUri();
            JwtResponse response = new JwtResponse("Form request created successfully", formRequest.getId());
            return ResponseEntity.created(location).body(response);
        } catch (FormRequestFieldDataException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.status(400).body(new JwtResponse("The number of fields in the form request does not match the number of fields in the form template."));
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.status(500).body(new JwtResponse(ErrorMessage.UNKNOWN_ERROR));
        }
    }

}
