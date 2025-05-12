package com.example.demo.service;

import com.example.demo.model.Message;
import com.example.demo.model.Ticket;
import com.example.demo.modelDB.User;
import com.example.demo.repository.tickets.MessageRepository;
import com.example.demo.repository.tickets.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MessageRepository messageRepository;

    public Ticket createTicket(String subject, String messageContent, User creator) {
        Ticket ticket = new Ticket();
        ticket.setSubject(subject);
        ticket.setCreatedBy(creator); // assumes Ticket has a field 'createdBy'
        ticket = ticketRepository.save(ticket);

        Message message = new Message();
        message.setContent(messageContent);
        message.setTicket(ticket);
        message.setSender(creator); // assumes Message has a 'sender' field
        messageRepository.save(message);

        return ticket;
    }

    public List<Ticket> getAll() {
        return ticketRepository.findAll();
    }

    public Message addMessage(Long ticketId, String content, User sender) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Message message = new Message();
        message.setContent(content);
        message.setTicket(ticket);
        message.setSender(sender);

        return messageRepository.save(message);
    }
}
