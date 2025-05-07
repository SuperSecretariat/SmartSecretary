package com.example.demo.model;

import com.example.demo.modelDB.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String status = "OPEN"; // TODO: CHANGE TO ENUM

    @ManyToOne
    private User createdBy;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<Message> messages = new ArrayList<>();

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

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
    /* |||||||||| GETTERS AND SETTERS |||||||||| */
}
