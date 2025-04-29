package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import static com.example.demo.util.AESUtil.encrypt;

public class SecretaryRequest {

    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME)
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME)
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_CNP)
    private String CNP;

    @NotBlank(message = ErrorMessage.MISSING_AUTHKEY)
    private String authKey;

    public SecretaryRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("CNP") String CNP,
            @JsonProperty("authKey") String authKey){
        this.firstName = firstName;
        this.lastName = lastName;
        this.CNP = CNP;
        try{
            this.authKey = encrypt(authKey);
        }
        catch (Exception e){
            throw new IllegalStateException("Encryption failed",e);
        }
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCNP() {
        return CNP;
    }

    public String getAuthKey() {
        return authKey;
    }
}
