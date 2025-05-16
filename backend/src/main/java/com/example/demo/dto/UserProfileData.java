package com.example.demo.dto;

import java.sql.Date;
import java.util.List;

public record UserProfileData(
        String token,
        String registrationNumber,
        String firstName,
        String lastName,
        String email,
        String cnp,
        Date dateOfBirth,
        String university,
        String faculty,
        List<String> roles
) {}