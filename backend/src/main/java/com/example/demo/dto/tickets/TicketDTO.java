package com.example.demo.dto.tickets;

import com.example.demo.model.enums.TicketStatus;
import com.example.demo.model.enums.TicketType;

public class TicketDTO {
    private Long id;
    private String subject;
    private TicketType type;
    private TicketStatus status;
    private String creatorEmail;

    // Constructors
    public TicketDTO() {}

    public TicketDTO(Long id, String subject, TicketType type, TicketStatus status, String creatorEmail) {
        this.id = id;
        this.subject = subject;
        this.type = type;
        this.status = status;
        this.creatorEmail = creatorEmail;
    }

    // GETTERS & SETTERS
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getSubject() {
        return subject;
    }
    public void setSubject(String subject) {
        this.subject = subject;
    }
    public TicketType getType() {
        return type;
    }
    public void setType(TicketType type) {
        this.type = type;
    }
    public TicketStatus getStatus() {
        return status;
    }
    public void setStatus(TicketStatus status) {
        this.status = status;
    }
    public String getCreatorEmail() {
        return creatorEmail;
    }
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}
