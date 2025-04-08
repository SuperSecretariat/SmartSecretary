package com.example.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("api/auth")
public class Register {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest)
    {
        User newUser = new User(registerRequest.getFirstName(), registerRequest.getLastName(),registerRequest.getEmail(), registerRequest.getIdNumber(), registerRequest.getPassword());
        userRepository.save(newUser);

        return ResponseEntity.ok("Cont create cu succes!");
    }


}
