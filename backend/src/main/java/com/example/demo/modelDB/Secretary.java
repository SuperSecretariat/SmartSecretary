package com.example.demo.modelDB;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@Entity
@Table( name = "secretaries",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "CNP"),
        })
public class Secretary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "email")
    private String email;

    @NotBlank
    @NotNull
    @Column(name = "authKey")
    private String authKey;


    public Secretary() {

    }
    public Secretary(String authKey, String email) {
        this.authKey = authKey;
        this.email = email;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAuthKey() {
        return authKey;
    }
    private static String generateRandomKey() {
        return UUID.randomUUID().toString();
    }
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
