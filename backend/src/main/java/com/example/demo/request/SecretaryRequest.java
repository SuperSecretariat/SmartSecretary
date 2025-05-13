package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import static com.example.demo.util.AESUtil.encrypt;

public class SecretaryRequest {

    @NotBlank(message = ErrorMessage.MISSING_AUTHKEY)
    private String authKey;

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private String email;

    public SecretaryRequest(
            @JsonProperty("email") String email,
            @JsonProperty("authKey") String authKey){
        this.email = email;
        this.authKey = authKey;
    }

    public String getAuthKey() {
        return authKey;
    }
    public String getEmail() {
        return email;
    }
}
