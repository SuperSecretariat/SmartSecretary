package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


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
