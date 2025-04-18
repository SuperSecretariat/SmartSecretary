package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String idNumber;

    @NotBlank(message = ErrorMessage.MISSING_PASSWORD)
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