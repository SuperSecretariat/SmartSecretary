package com.example.demo.dto;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {


    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME)
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME)
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String registrationNumber;

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.MISSING_PASSWORD)
    private String password;

    @NotBlank(message = ErrorMessage.MISSING_CONFIRMATION_PASSWORD)
    private String confirmationPassword;

    public RegisterRequest(){
        //SonarQube comment: constructor needed for JSON parsing
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmationPassword() { return confirmationPassword; }
    public void setConfirmationPassword(String confirmationPassword) { this.confirmationPassword = confirmationPassword; }
}
