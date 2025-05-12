package com.example.demo.controller;

import com.example.demo.dto.MessageDTO;
import com.example.demo.dto.TicketRequestDTO;
import com.example.demo.model.Message;
import com.example.demo.model.Ticket;
import com.example.demo.modelDB.User;
import com.example.demo.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService; // MAKE TICKET SERVCIE

    @PostMapping
    public Ticket createTicket(@RequestBody TicketRequestDTO dto, @AuthenticationPrincipal User user) {
        return ticketService.createTicket(dto.getSubject(), dto.getMessage(), user);
    }

    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAll();
    }

    @PostMapping("/{ticketId}/messages")
    public Message addMessage(@PathVariable Long ticketId, @RequestBody MessageDTO dto, @AuthenticationPrincipal User user) {
        return ticketService.addMessage(ticketId, dto.getContent(), user);
    }
}
