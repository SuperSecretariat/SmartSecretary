package com.example.demo.response;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String registrationNumber;
    private String email;
    private String firstName;
    private String lastName;
    private String cnp;
    private Date dateOfBirth;
    private String university;
    private String faculty;
    private List<String> roles;

    public JwtResponse(String token, Long id, String registrationNumber, List<String> roles){
        this.token = token;
        this.id = id;
        this.registrationNumber = registrationNumber;
        this.roles = roles;
    }

    public JwtResponse(
            String token,
            String registrationNumber,
            String firstName,
            String lastName,
            String email,
            String cnp,
            Date dateOfBirth,
            String university,
            String faculty,
            List<String> roles){
        this.token = token;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.cnp = cnp;
        this.dateOfBirth = dateOfBirth;
        this.university = university;
        this.faculty = faculty;
        this.roles = roles;

    }

    public String getToken() {
        return token;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getUniversity() {
        return university;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getCnp() {
        return cnp;
    }

    public Date getDateOfBirth(){ return dateOfBirth; }


    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public List<String> getRoles() {
        return roles;
    }
}
