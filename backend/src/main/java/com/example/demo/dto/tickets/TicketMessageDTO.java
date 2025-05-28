// TicketMessageDTO.java
package com.example.demo.dto.tickets;

import java.time.LocalDateTime;

public class TicketMessageDTO {
    private Long id;
    private String senderEmail;  // <-- sender email
    private String message;
    private LocalDateTime timestamp;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String senderEmail) { this.senderEmail = senderEmail; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
