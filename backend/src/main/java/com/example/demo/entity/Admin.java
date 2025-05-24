package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static com.example.demo.util.AESUtil.encrypt;

@Entity
@Table( name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
        })
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "email")
    private String email;

    @NotBlank
    @NotNull
    @Column(name = "authKey")
    private String authKey;

    public Admin() {

    }
    public Admin(String authKey, String email) {
        this.email = email;
        this.authKey = authKey;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAuthKey() {
       return authKey;
    }
    public void setAuthKey(String authKey) {
       this.authKey = authKey;
    }
    public static Admin withRandomKey(String email) {
        try {
            String key = generateRandomKey();
            String encrypted = encrypt(key);
            return new Admin(email, encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate encrypted key", e);
        }
    }
    private static String generateRandomKey() {
        return UUID.randomUUID().toString();
    }

}
