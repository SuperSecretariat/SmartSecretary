package com.example.demo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class Login {
    private Cryptography encryption = new Encryption();
    private Cryptography decryption = new Decryption();
    private String idNumber;
    private String password;

    public Login(String idNumber, String password)
    {
        this.idNumber = idNumber;
        this.password = password;
        verifyLoginRequest();
    }

    private void verifyLoginRequest()
    {
        // cautam in baza de date daca exista un cont deja logat
    }
}
