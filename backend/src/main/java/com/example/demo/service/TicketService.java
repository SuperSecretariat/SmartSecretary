package com.example.demo.service;

import com.example.demo.dto.tickets.TicketDTO;
import com.example.demo.dto.tickets.TicketMessageDTO;
import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketMessage;
import com.example.demo.entity.User;
import com.example.demo.model.enums.TicketStatus;
import com.example.demo.model.enums.TicketType;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.tickets.TicketMessageRepository;
import com.example.demo.repository.tickets.TicketRepository;
import com.example.demo.util.JwtUtil;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepo;

    @Autowired
    private TicketMessageRepository messageRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JwtUtil jwtUtil;

    public Ticket createTicketFromJwt(String jwtToken, String subject, TicketType type, String firstMessage) {
        User user = getUserFromJwt(jwtToken);

        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setType(type);
        ticket.setUser(user);

        TicketMessage msg = new TicketMessage();
        msg.setTicket(ticket);
        msg.setSender(user);
        msg.setMessage(firstMessage);

        ticket.getMessages().add(msg);

        return ticketRepo.save(ticket);
    }

    public List<Ticket> getTicketsByUser(User user) {
        return ticketRepo.findByUserId(user.getId());
    }

    public List<Ticket> getTickets(String jwtToken, boolean getOnlyForUser) {
        User user = getUserFromJwt(jwtToken);

        if (getOnlyForUser) {
            return getTicketsByUser(user);
        }

        return ticketRepo.findAll();
    }

    public Ticket updateTicketStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        ticket.setStatus(status);
        return ticketRepo.save(ticket);
    }

    public List<TicketMessage> getMessagesOfTicket(Long ticketId) {
        return messageRepo.findByTicketIdOrderByTimestampAsc(ticketId);
    }

    public TicketMessage addMessageToTicket(Long ticketId, String token, String message) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        User sender = getUserFromJwt(token);

        TicketMessage msg = new TicketMessage();
        msg.setTicket(ticket);
        msg.setSender(sender);
        msg.setMessage(message);

        return messageRepo.save(msg);
    }

    public User getUserFromJwt(String jwtToken) {
        String regNumber = jwtUtil.getRegistrationNumberFromJwtToken(jwtToken);
        return userRepo.findByRegNumber(regNumber)
                .orElseThrow(() -> new RuntimeException("User not found for registration number: " + regNumber));
    }

    public TicketDTO toTicketDTO(Ticket ticket) {
        return new TicketDTO(
                ticket.getId(),
                ticket.getSubject(),
                ticket.getType(),
                ticket.getStatus(),
                ticket.getUser().getEmail()
        );
    }

    public TicketMessageDTO toTicketMessageDTO(TicketMessage message) {
        TicketMessageDTO dto = new TicketMessageDTO();
        dto.setId(message.getId());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setMessage(message.getMessage());
        dto.setTimestamp(message.getTimestamp());
        return dto;
    }
}

