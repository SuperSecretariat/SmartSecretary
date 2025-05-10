package com.example.demo.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResetPasswordRequest {
    private String token;
    private String newPassword;

    public ResetPasswordRequest(){}

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
