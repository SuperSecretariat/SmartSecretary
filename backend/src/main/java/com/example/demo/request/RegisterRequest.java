package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.constants.ValidationGroup;
import jakarta.validation.constraints.NotBlank;

import java.sql.Date;
import java.time.LocalDate;

public class RegisterRequest {


    @NotBlank(message = ErrorMessage.MISSING_FIRST_NAME, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class})
    private String firstName;

    @NotBlank(message = ErrorMessage.MISSING_LAST_NAME, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class})
    private String lastName;

    @NotBlank(message = ErrorMessage.MISSING_REGISTRATION_NUMBER, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class, ValidationGroup.AdminGroup.class})
    private String registrationNumber;

    @NotBlank(message = ErrorMessage.MISSING_UNIVERSITY, groups = ValidationGroup.StudentGroup.class)
    private String university;

    @NotBlank(message = ErrorMessage.MISSING_FACULTY, groups = ValidationGroup.StudentGroup.class)
    private String faculty;

    @NotBlank(message = ErrorMessage.MISSING_EMAIL, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class, ValidationGroup.AdminGroup.class})
    private String email;

    @NotBlank(message = ErrorMessage.MISSING_PASSWORD, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class, ValidationGroup.AdminGroup.class})
    private String password;

    @NotBlank(message = ErrorMessage.MISSING_CONFIRMATION_PASSWORD, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class, ValidationGroup.AdminGroup.class})
    private String confirmationPassword;

    @NotBlank(message = ErrorMessage.MISSING_DATE_OF_BIRTH, groups = ValidationGroup.StudentGroup.class)
    private LocalDate dateOfBirth;

    @NotBlank(message = ErrorMessage.MISSING_CNP, groups = {ValidationGroup.StudentGroup.class, ValidationGroup.SecretaryGroup.class})
    private String cnp;

    public RegisterRequest(){}

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

    public Date getDateOfBirth() {
        return Date.valueOf(dateOfBirth);
    }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCnp() {
        return this.cnp;
    }
    public void setCnp(String cnp) {
        this.cnp = cnp;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmationPassword() { return confirmationPassword; }
    public void setConfirmationPassword(String confirmationPassword) { this.confirmationPassword = confirmationPassword; }
}
