package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.model.Message;
import com.example.demo.model.Ticket;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.tickets.MessageRepository;
import com.example.demo.repository.tickets.TicketRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private TicketRepository ticketRepository;
    private MessageRepository messageRepository;
    private UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
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

//    public Ticket createTicket(String subject, String messageContent, String type, String status, String createdBy) {
//        Ticket ticket = new Ticket();
//        ticket.setSubject(subject);
//        ticket.setType(type);
//        ticket.setStatus(status);
//        ticket.setTimestamp(java.sql.Timestamp.valueOf(LocalDateTime.now()));
//        ticket.setCreatedBy(createdBy);
//        ticket = ticketRepository.save(ticket);
//
//        Message message = new Message();
//        message.setTicket(ticket);
//        message.setContent(messageContent);
//        message.setTimestamp(LocalDateTime.now());
//        messageRepository.save(message);
//
//        return ticket;
//    }

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

//    public List<Message> getMessagesForTicket(Long ticketId) {
//        return messageRepository.findByTicketId(ticketId);
//    }
//
//    public Message sendMessageToTicket(Long ticketId, String content, String senderId) {
//        Ticket ticket = ticketRepository.findById(ticketId)
//                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + ticketId));
//
//        User sender = userRepository.findByRegNumber(senderId)
//                .orElseThrow(() -> new IllegalArgumentException("Sender (User) not found"));
//
//        Message message = new Message();
//        message.setTicket(ticket);
//        message.setSender(sender);
//        message.setContent(content);
//        message.setTimestamp(LocalDateTime.now());
//
//        return messageRepository.save(message);
//    }
}