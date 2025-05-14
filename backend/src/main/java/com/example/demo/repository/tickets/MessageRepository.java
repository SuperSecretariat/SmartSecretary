package com.example.demo.repository.tickets;

import com.example.demo.model.Message;
import com.example.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByTicketId(Long ticketId);
}