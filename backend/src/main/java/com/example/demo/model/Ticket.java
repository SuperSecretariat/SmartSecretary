package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Tickets")
public class Ticket {
    @Id
    @NotBlank
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "subject")
    private String subject;

    @NotBlank
    @Column(name = "message")
    private String message;

    @NotBlank
    @Column(name = "type")
    private String type;

    @NotBlank
    @Column(name = "status")
    private String status; // TODO: CHANGE TO ENUM

    @NotBlank
    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "dd-MM-yyyy:HH-mm-ss")
    private Timestamp timestamp;

    @NotBlank
    @Column(name = "createdByRegNumber")
    private String createdBy;

//    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Message> messages = new ArrayList<>();

    /* |||||||||| GETTERS AND SETTERS |||||||||| */
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

//    public List<Message> getMessages() {
//        return messages;
//    }
//
//    public void setMessages(List<Message> messages) {
//        this.messages = messages;
//    }

    /* |||||||||| GETTERS AND SETTERS |||||||||| */
}