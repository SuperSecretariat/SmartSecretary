package com.example.demo.service;

import com.example.demo.model.Ticket;
import com.example.demo.repository.tickets.TicketRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Ticket createTicket(String subject, String message, String type, String status, String createdBy) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setMessage(message);
        ticket.setType(type);
        ticket.setStatus(status);
        ticket.setTimestamp(java.sql.Timestamp.valueOf(LocalDateTime.now()));
        ticket.setCreatedBy(createdBy); // assumes Ticket has a field 'createdBy'
        ticket = ticketRepository.save(ticket);
        return ticket;
    }

    public void closeTicket(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            ticket.get().setStatus("CLOSED");
            ticketRepository.save(ticket.get());
        }
    }

    public void finishTicket(Long ticketId) {
        Optional<Ticket> ticket = ticketRepository.findById(ticketId);
        if (ticket.isPresent()) {
            ticket.get().setStatus("FINISHED");
            ticketRepository.save(ticket.get());
        }
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

}
