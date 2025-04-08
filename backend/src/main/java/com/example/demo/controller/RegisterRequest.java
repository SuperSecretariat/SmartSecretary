package com.example.demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Prenume")
    private String firstName;

    @NotBlank(message = "Nume de familie")
    private String lastName;

    @NotBlank(message = "Numar matricol")
    private String idNumber;

    @NotBlank(message = "Email")
    private String email;

    @NotBlank(message = "Introdu parola")
    private String password;

    @NotBlank(message = "Confirma parola")
    private String confirmationPassword;

    public RegisterRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("idNumber") String idNumber,
            @JsonProperty("password") String password,
            @JsonProperty("confirmationPassword") String confirmationPassword)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.idNumber = idNumber;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmationPassword() { return confirmationPassword; }
    public void setConfirmationPassword(String confirmationPassword) { this.confirmationPassword = confirmationPassword; }
}
