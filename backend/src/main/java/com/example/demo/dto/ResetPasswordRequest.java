package com.example.demo.dto;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public ResetPasswordRequest(){
        //SonarQube comment: constructor needed for JSON parsing
    }

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
