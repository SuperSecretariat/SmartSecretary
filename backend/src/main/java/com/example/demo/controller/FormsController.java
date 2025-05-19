package com.example.demo.controller;

import com.example.demo.dto.FormResponse;
import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidFormIdException;
import com.example.demo.exceptions.NoFormFieldsFoundException;
import com.example.demo.entity.Form;
import com.example.demo.projection.FormFieldsProjection;
import com.example.demo.dto.FormCreationRequest;
import com.example.demo.service.FormService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormsController {
    private final Logger logger = LoggerFactory.getLogger(FormsController.class);

    private final FormService formService;

    public FormsController(FormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public ResponseEntity<List<FormResponse>> getActiveForms() {
        return ResponseEntity.ok(formService.getAllActiveForms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormResponse> getFormById(@PathVariable Long id) {
        try {
            FormResponse form = formService.getFormById(id);
            return ResponseEntity.ok(form);
        }
        catch (InvalidFormIdException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{id}/fields")
    public ResponseEntity<FormFieldsProjection> getFormFieldsOfFormWIthId(@PathVariable Long id) {
        try {
            FormFieldsProjection formFields = formService.getFormFieldsOfFormWithId(id);
            return ResponseEntity.ok(formFields);
        }
        catch (InvalidFormIdException | NoFormFieldsFoundException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<String> createForm(@Valid @RequestBody FormCreationRequest formCreationRequest) {
        try{
            Form form = formService.createForm(formCreationRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(form.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        catch (IOException | InterruptedException | FormCreationException e) {
            this.logger.error(e.getMessage());
            return ResponseEntity.status(503).body("Unable to create form. Please try again later.");
        }
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getFormImage(@PathVariable Long id) {
        try{
            byte[] imageBytes = formService.getFormImage(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // or IMAGE_PNG etc.
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
        catch (IOException | InvalidFormIdException e){
            this.logger.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
}
