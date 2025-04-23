package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class RegisterRequest {

    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME)
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME)
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String registrationNumber;

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

    @NotBlank(message = ErrorMessage.MISSING_DATE_OF_BIRTH)
    private LocalDate dateOfBirth;

    @NotBlank(message = ErrorMessage.MISSING_CNP)
    private String CNP;

    public RegisterRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("email") String email,
            @JsonProperty("registrationNumber") String registrationNumber,
            @JsonProperty("university") String university,
            @JsonProperty("faculty") String faculty,
            @JsonProperty("password") String password,
            @JsonProperty("confirmationPassword") String confirmationPassword,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
            @JsonProperty("CNP") String CNP)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationNumber = registrationNumber;
        this.university = university;
        this.faculty = faculty;
        this.password = password;
        this.confirmationPassword = confirmationPassword;
        this.dateOfBirth = dateOfBirth;
        this.CNP = CNP;
    }


    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCNP() {
        return CNP;
    }
    public void setCNP(String CNP) {
        this.CNP = CNP;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmationPassword() { return confirmationPassword; }
    public void setConfirmationPassword(String confirmationPassword) { this.confirmationPassword = confirmationPassword; }
}
