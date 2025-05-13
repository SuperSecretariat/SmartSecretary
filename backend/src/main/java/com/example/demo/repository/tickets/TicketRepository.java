package com.example.demo.repository.tickets;

import com.example.demo.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findById(Long id);
    boolean existsById(Long id);

    List<Ticket> getAllByType(String type);
}
