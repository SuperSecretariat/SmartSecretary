package com.example.demo.controller;

import com.example.demo.constants.ValidationMessage;
import com.example.demo.dto.MessageDTO;
import com.example.demo.dto.TicketRequestDTO;
import com.example.demo.model.Message;
import com.example.demo.model.Ticket;
import com.example.demo.response.JwtResponse;
import com.example.demo.service.TicketService;
import com.example.demo.service.UserDetailsServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    private TicketService ticketService;
    private UserDetailsServiceImpl userService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> createTicket(@RequestBody TicketRequestDTO dto) {
        ticketService.createTicket(dto.getSubject(), dto.getMessage(), dto.getType(), dto.getStatus(), dto.getRegistrationNumber());
        return ResponseEntity.ok("Created ticket successfully.");
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAll());
    }

//    @PostMapping("/{ticketId}")
//    public Message addMessage(@PathVariable Long ticketId, @RequestBody MessageDTO dto, @AuthenticationPrincipal User user) {
//        return ticketService.addMessage(ticketId, dto.getContent(), user);
//    }

    @PostMapping("/close")
    public ResponseEntity<String> closeTicket(@RequestBody Map<String, Long> payload) {
        Long ticketId = payload.get("ticketId");
        ticketService.closeTicket(ticketId);
        return ResponseEntity.ok("Closed ticket successfully.");
    }
    @PostMapping("/done")
    public ResponseEntity<String> finishTicket(@RequestBody Map<String, Long> payload) {
        Long ticketId = payload.get("ticketId");
        ticketService.finishTicket(ticketId);
        return ResponseEntity.ok("Finished ticket successfully.");
    }

}
