package com.example.demo.response;

import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String registrationNumber;
    private List<String> roles;

    public JwtResponse(String token, Long id, String registrationNumber, List<String> roles){
        this.token = token;
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public List<String> getRoles() {
        return roles;
    }
}
