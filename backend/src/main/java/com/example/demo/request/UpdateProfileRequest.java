package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.fasterxml.jackson.core.ErrorReportConfiguration;
import jakarta.validation.constraints.NotBlank;

import java.sql.Date;
import java.time.LocalDate;

public class UpdateProfileRequest {
    @NotBlank(message = ErrorMessage.MISSING_ID)
    private Long id;

    @NotBlank(message = ErrorMessage.MISSING_UNIVERSITY)
    private String university;

    @NotBlank(message = ErrorMessage.MISSING_FACULTY)
    private String faculty;

    @NotBlank(message = ErrorMessage.MISSING_DATE_OF_BIRTH)
    private Date dateOfBirth;

    @NotBlank(message = ErrorMessage.MISSING_CNP)
    private String cnp;

    public UpdateProfileRequest(Long id, String university, String faculty,Date dateOfBirth, String cnp){
        this.id = id;
        this.university = university;
        this.faculty = faculty;
        this.dateOfBirth = dateOfBirth;
        this.cnp = cnp;
    }

    public String getUniversity() {
        return university;
    }

    public String getFaculty() {
        return faculty;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCnp() {
        return cnp;
    }

    public Long getId() {
        return id;
    }
}
