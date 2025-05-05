package com.example.demo.controller;

import com.example.demo.model.Form;
import com.example.demo.repository.FormRepository;
import com.example.demo.service.FormService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormsController {

    private final FormService formService;

    public FormsController(FormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public ResponseEntity<List<Form>> getActiveForms() {
        return ResponseEntity.ok(formService.getAllActiveForms());
    }
}
