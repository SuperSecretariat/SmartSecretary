package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Numarul matricol nu poate sa lipseasca")
    private String idNumber;

    @NotBlank(message = "Nu te poti conecta fara parola")
    private String password;

    public LoginRequest(String idNumber, String password) {
        this.idNumber = idNumber;
        this.password = password;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}