package com.example.demo.controller;

import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Prenume")
    private String firstName;

    @NotBlank(message = "Nume de familie")
    private String lastName;

    @NotBlank(message = "Numar matricol")
    private String idNumber;

    @NotBlank(message = "Email")
    private String email;

    @NotBlank(message = "Introdu parola")
    private String password;

    @NotBlank(message = "Confirma parola")
    private String confirmationPassword;

    public RegisterRequest(String firstName, String lastName, String email, String idNumber, String password, String confirmationPassword)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.idNumber = idNumber;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
    }
}
