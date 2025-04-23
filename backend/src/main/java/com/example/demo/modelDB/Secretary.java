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
    @Size(max = 20)
    @Column(name = "first_name")
    private  String firstName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "CNP")
    private String CNP;

    @NotBlank
    @NotNull
    @Column(name = "authKey")
    private String authKey;


    public Secretary() {

    }
    public Secretary(String firstName, String lastName, String CNP, String authKey) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.CNP = CNP;
        this.authKey = authKey;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getCNP() {
        return CNP;
    }
    public void setCNP(String CNP) {
        this.CNP = CNP;
    }
    public String getAuthKey() {
        return authKey;
    }
    public static Secretary withRandomKey(String firstName, String lastName, String CNP) {
        try {
            String key = generateRandomKey();
            String encrypted = encrypt(key);
            return new Secretary(firstName,lastName,CNP, encrypted);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate encrypted key", e);
        }
    }
    private static String generateRandomKey() {
        return UUID.randomUUID().toString();
    }
    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }
}
