package com.example.demo.dto;

import java.sql.Date;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public record UserDetailsData(
        Long id,
        String registrationNumber,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String firstName,
        String lastName,
        String cnp,
        Date dateOfBirth,
        String university,
        String faculty
) {}
