package com.example.demo.modelDB;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table( name = "students",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "registration_number"),
        })
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(name = "first_name")
    private  String firstName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Size(max = 20)
    @Column(name = "registration_number", nullable = false)
    private String regNumber;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "CNP")
    private String CNP;

    public Student() {

    }
    public Student(String firstName, String lastName, String regNumber, String dateOfBirth, String CNP) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.regNumber = regNumber;
    this.dateOfBirth = dateOfBirth;
    this.CNP = CNP;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getRegNumber() {
        return regNumber;
    }
    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public String getCNP() {
        return CNP;
    }
    public void setCNP(String CNP) {
        this.CNP = CNP;
    }



}
