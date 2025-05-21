package com.example.demo.service;

import com.example.demo.entity.Ticket;
import com.example.demo.entity.TicketMessage;
import com.example.demo.entity.User;
import com.example.demo.model.enums.TicketStatus;
import com.example.demo.model.enums.TicketType;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.tickets.TicketMessageRepository;
import com.example.demo.repository.tickets.TicketRepository;
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

    public Ticket createTicket(Long userId, String subject, TicketType type, String message) {
        User user = userRepo.findById(userId).orElseThrow();

        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setType(type);
        ticket.setUser(user);

        TicketMessage msg = new TicketMessage();
        msg.setTicket(ticket);
        msg.setSender(user);
        msg.setMessage(message);

        ticket.getMessages().add(msg);

        return ticketRepo.save(ticket);
    }

    public List<Ticket> getTicketsByUser(Long userId) {
        return ticketRepo.findByUserId(userId);
    }

    public List<Ticket> getAllTickets(TicketStatus status, TicketType type, Long userId, String subject) {
        return ticketRepo.findByOptionalFilters(status, type, userId, subject);
    }

    public Ticket updateTicketStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        ticket.setStatus(status);
        return ticketRepo.save(ticket);
    }

    public TicketMessage addMessageToTicket(Long ticketId, Long senderId, String message) {
        Ticket ticket = ticketRepo.findById(ticketId).orElseThrow();
        User sender = userRepo.findById(senderId).orElseThrow();

        TicketMessage msg = new TicketMessage();
        msg.setTicket(ticket);
        msg.setSender(sender);
        msg.setMessage(message);

        return messageRepo.save(msg);
    }
}

