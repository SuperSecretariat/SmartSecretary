package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class StudentRequest {

    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME)
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME)
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER)
    private String registrationNumber;

    @NotBlank(message = ErrorMessage.MISSING_DATE_OF_BIRTH)
    private LocalDate dateOfBirth;

    @NotBlank(message = ErrorMessage.MISSING_CNP)
    private String CNP;

    public StudentRequest(
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("registrationNumber") String registrationNumber,
            @JsonProperty("dateOfBirth") LocalDate dateOfBirth,
            @JsonProperty("CNP") String CNP){
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationNumber = registrationNumber;
        this.dateOfBirth = dateOfBirth;
        this.CNP = CNP;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCNP() {
        return CNP;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }
}
