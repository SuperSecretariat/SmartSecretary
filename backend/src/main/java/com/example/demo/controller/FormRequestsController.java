package com.example.demo.controller;

import com.example.demo.model.FormRequest;
import com.example.demo.service.FormRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("api/form-requests")
public class FormRequestsController {

    private final FormRequestService formRequestService;

    public FormRequestsController(FormRequestService formRequestService) {
        this.formRequestService = formRequestService;
    }

    @GetMapping
    public ResponseEntity<List<FormRequest>> getAllFormsForUserWithId(long userId/*session token*/) {
        // Logic to retrieve all forms
        return ResponseEntity.ok(formRequestService.getFormRequestsByUserId(userId/*session token*/));
    }
}
