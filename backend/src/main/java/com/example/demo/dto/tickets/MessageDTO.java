package com.example.demo.dto.tickets;

public class MessageDTO {
    private Long senderId;
    private String message;

    // GETTERS AND SETTERS
    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
