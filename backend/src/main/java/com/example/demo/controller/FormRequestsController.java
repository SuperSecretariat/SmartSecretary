package com.example.demo.controller;

import com.example.demo.exceptions.FormRequestFieldDataException;
import com.example.demo.exceptions.InvalidFormRequestIdException;
import com.example.demo.entity.FormRequest;
import com.example.demo.dto.FormRequestRequest;
import com.example.demo.service.FormRequestService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public FormRequestsController(FormRequestService formRequestService) {
        this.formRequestService = formRequestService;
    }

    @GetMapping
    public ResponseEntity<List<FormRequest>> getAllFormsForUserWithId(String sessionToken) {
        // Logic to retrieve all forms
        return ResponseEntity.ok(formRequestService.getFormRequestsByUserRegistrationNumber(sessionToken));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormRequest> getFormRequestById(@PathVariable Long id) {
        try {
            FormRequest formRequest = this.formRequestService.getFormRequestById(id);
            System.out.println(formRequest);
            return ResponseEntity.ok(formRequest);
        }
        catch (InvalidFormRequestIdException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createFormRequest(@Valid @RequestBody FormRequestRequest formRequestRequest) {
        try {
            FormRequest formRequest = this.formRequestService.createFormRequest(formRequestRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(formRequest.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        catch (FormRequestFieldDataException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().body("The number of fields in the form request does not match the number of fields in the form template.");
        }
    }
}
