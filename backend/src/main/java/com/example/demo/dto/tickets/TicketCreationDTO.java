package com.example.demo.dto.tickets;

import com.example.demo.model.enums.TicketType;

public class TicketCreationDTO {
    private Long userId;
    private String subject;
    private TicketType type;
    private String message;

    // GETTERS AND SETTERS
    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public TicketType getType() {
        return this.type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
