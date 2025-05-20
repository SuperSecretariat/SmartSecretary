package com.example.demo.dto;

public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest(){
           // SonarQube comment: constructor needed for JSON parsing
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
