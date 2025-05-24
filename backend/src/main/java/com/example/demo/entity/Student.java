package com.example.demo.entity;

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
    @Column(name = "registration_number", nullable = false)
    private String regNumber;

    @NotBlank
    @Size(max = 50)
    @Column(name = "email")
    private String email;


    public Student() {

    }
    public Student(String regNumber, String email) {
    this.regNumber = regNumber;
    this.email = email;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRegNumber() {
        return regNumber;
    }
    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
