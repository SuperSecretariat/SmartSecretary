package com.example.demo.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class Login {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest)
    {
        Optional<User> optionalUser = userRepository.findByIdNumber(loginRequest.getIdNumber());

        if(optionalUser.isPresent())
        {
            User user = optionalUser.get();
            if(user.getPassword().equals(loginRequest.getPassword()))
                return ResponseEntity.ok("Logarea s-a finalizat cu succes");
            else
                return ResponseEntity.status(401).body("Parola incorecta");
        }
        else
            return ResponseEntity.status(404).body("Contul nu exista");


    }
}
