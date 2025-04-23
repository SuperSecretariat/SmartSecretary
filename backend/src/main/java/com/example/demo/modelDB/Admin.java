package com.example.demo.modelDB;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.demo.util.AESUtil;
import java.util.UUID;

import static com.example.demo.util.AESUtil.decrypt;
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
    public Admin(String email, String authKey) {
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
        try{
            String key = this.authKey;
            String decrypt = decrypt(key);
            return decrypt;

        }
        catch (Exception e){
            throw new IllegalStateException("Cannot decrypt authKey",e);
        }
    }
    public void setAuthKey(String authKey) {
        try {
            String key = authKey;
            String encrypted = encrypt(key);
            this.authKey = encrypted;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate encrypted key", e);
        }
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
