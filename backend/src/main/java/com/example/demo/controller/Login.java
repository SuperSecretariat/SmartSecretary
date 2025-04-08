package com.example.demo.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class Login {

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        String idNumber = loginRequest.getIdNumber();
        String password = loginRequest.getPassword();
        if(idNumber.equals("admin") && password.equals("123"))
        {
            return ResponseEntity.ok("Logarea s-a finalizat cu succes!");
        }
        else
        {
            return ResponseEntity.status(401).body("Date invalide");
        }
    }
}
