package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME)
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME)
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String idNumber;

    @NotBlank(message = ErrorMessage.MISSING_UNIVERSITY)
    private String university;

    @NotBlank(message = ErrorMessage.MISSING_FACULTY)
    private String faculty;

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.MISSING_PASSWORD)
    private String password;

    @NotBlank(message = ErrorMessage.MISSING_CONFIRMATION_PASSWORD)
    private String confirmationPassword;

    public RegisterRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("idNumber") String idNumber,
            @JsonProperty("university") String university,
            @JsonProperty("faculty") String faculty,
            @JsonProperty("password") String password,
            @JsonProperty("confirmationPassword") String confirmationPassword)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.idNumber = idNumber;
        this.university = university;
        this.faculty = faculty;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getFaculty() {
        return faculty;
    }
    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getUniversity() {
        return university;
    }
    public void setUniversity(String university) {
        this.university = university;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmationPassword() { return confirmationPassword; }
    public void setConfirmationPassword(String confirmationPassword) { this.confirmationPassword = confirmationPassword; }
}
