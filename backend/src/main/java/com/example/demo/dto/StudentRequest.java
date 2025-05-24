package com.example.demo.dto;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class StudentRequest {

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String registrationNumber;

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private String email;

    public StudentRequest(
            @JsonProperty("email") String email,
            @JsonProperty("registrationNumber") String registrationNumber)
{
        this.registrationNumber = registrationNumber;
        this.email = email;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
    public String getEmail() {
        return email;
    }
}
