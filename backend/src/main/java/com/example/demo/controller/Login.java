package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class Login {
    private Cryptography encryption = new Encryption();
    private Cryptography decryption = new Decryption();
}
