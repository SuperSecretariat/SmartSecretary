package com.example.demo.controller;

import com.example.demo.dto.tickets.TicketCreationRequestDTO;
import com.example.demo.dto.tickets.TicketDTO;
import com.example.demo.dto.tickets.TicketMessageDTO;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {
    @Autowired
    private TicketService ticketService;

    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(
            @RequestBody TicketCreationRequestDTO request,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ?
                authorizationHeader.substring(7) : authorizationHeader;

        Ticket ticket = ticketService.createTicketFromJwt(jwtToken, request.getSubject(), request.getType(), request.getFirstMessage());
        return ResponseEntity.ok(ticketService.toTicketDTO(ticket));
    }

    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets(
            @RequestParam(defaultValue = "false") boolean getOnlyForUser,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        String jwtToken = authorizationHeader.startsWith("Bearer ") ?
                authorizationHeader.substring(7) : authorizationHeader;

        List<TicketDTO> tickets = ticketService
                .getTickets(jwtToken, getOnlyForUser)
                .stream()
                .map(ticketService::toTicketDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tickets);
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<TicketDTO> updateStatus(@PathVariable Long ticketId, @RequestParam TicketStatus status) {
        var updatedTicket = ticketService.updateTicketStatus(ticketId, status);
        return ResponseEntity.ok(ticketService.toTicketDTO(updatedTicket));
    }

//    @PostMapping("/{ticketId}/messages")
//    public ResponseEntity<TicketMessageDTO> addMessage(@PathVariable Long ticketId, @RequestBody TicketMessageDTO request) {
//        TicketMessage msg = ticketService.addMessageToTicket(ticketId, request.getSenderId(), request.getMessage());
//        return ResponseEntity.ok(ticketService.toTicketMessageDTO(msg));
//    }
}
