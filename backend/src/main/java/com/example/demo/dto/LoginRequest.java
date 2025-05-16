package com.example.demo.dto;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String registrationNumber;

    @NotBlank(message = ErrorMessage.MISSING_PASSWORD)
    private String password;

    public LoginRequest(String registrationNumber, String password) {
        this.registrationNumber = registrationNumber;
        this.password = password;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}