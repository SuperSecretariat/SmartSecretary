package com.example.demo.dto.tickets;

import com.example.demo.model.enums.TicketType;

public class TicketCreationRequestDTO {
    private String subject;
    private TicketType type;
    private String firstMessage;

    // GETTERS & SETTERS
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
    public String getFirstMessage() {
        return firstMessage;
    }
    public void setFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }
}
