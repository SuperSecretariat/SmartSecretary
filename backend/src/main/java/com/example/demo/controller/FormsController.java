package com.example.demo.controller;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.model.Form;
import com.example.demo.repository.FormRepository;
import com.example.demo.request.FormCreationRequest;
import com.example.demo.service.FormService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
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

    @GetMapping("/{id}")
    public ResponseEntity<Form> getFormById(@PathVariable Long id) {
        Form form = formService.getFormById(id);
        if (form == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(form);
    }

    @PostMapping
    public ResponseEntity<String> createForm(@RequestBody FormCreationRequest formCreationRequest) {
        try{
            Form form = formService.createForm(formCreationRequest);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(form.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        }
        catch (IOException | InterruptedException | FormCreationException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(503).body("Unable to create form. Please try again later.");
        }
    }

    @GetMapping("/{title}/image")
    public ResponseEntity<?> getFormImage(@PathVariable String title) {
        try{
            byte[] imageBytes = formService.getFormImage(title);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // or IMAGE_PNG etc.
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        }
        catch (IOException e){
            return ResponseEntity.status(503).body("Unable to load form. Please try again later.");
        }
    }
}
