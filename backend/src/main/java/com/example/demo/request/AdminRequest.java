package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import static com.example.demo.util.AESUtil.encrypt;

public class AdminRequest {

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.MISSING_AUTHKEY)
    private String authKey;

    public AdminRequest(
            @JsonProperty("email") String email,
            @JsonProperty("authKey") String authKey){
        this.email = email;
        try{
            String key = authKey;
            String encrypteddKey = encrypt(key);
            this.authKey = encrypteddKey;
        }
        catch (Exception e){
            throw new IllegalStateException("Encryption failed",e);
        }
    }

    public String getAuthKey() {
        return authKey;
    }

    public String getEmail() {
        return email;
    }
}
