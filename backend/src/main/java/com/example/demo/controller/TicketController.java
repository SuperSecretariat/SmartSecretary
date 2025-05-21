package com.example.demo.controller;

import com.example.demo.dto.tickets.MessageDTO;
import com.example.demo.dto.tickets.TicketCreationDTO;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketMessage;
import com.example.demo.model.enums.TicketStatus;
import com.example.demo.model.enums.TicketType;
import com.example.demo.service.TicketService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    // Create a new ticket
    @PostMapping
    public ResponseEntity<Ticket> createTicket(@RequestBody TicketCreationDTO request) {
        Ticket ticket = ticketService.createTicket(request.getUserId(), request.getSubject(), request.getType(), request.getMessage());
        return ResponseEntity.ok(ticket);
    }

    // Get all tickets (for admins, with optional filters)
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketType type,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String subject
    ) {
        List<Ticket> tickets = ticketService.getAllTickets(status, type, userId, subject);
        return ResponseEntity.ok(tickets);
    }

    // Get tickets for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getUserTickets(@PathVariable Long userId) {
        return ResponseEntity.ok(ticketService.getTicketsByUser(userId));
    }

    // Update ticket status (admin only)
    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<Ticket> updateStatus(@PathVariable Long ticketId, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(ticketId, status));
    }

    // Add message to ticket
    @PostMapping("/{ticketId}/messages")
    public ResponseEntity<TicketMessage> addMessage(@PathVariable Long ticketId, @RequestBody MessageDTO request) {
        return ResponseEntity.ok(ticketService.addMessageToTicket(ticketId, request.getSenderId(), request.getMessage()));
    }
}
