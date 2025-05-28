package com.example.demo.repository.tickets;

import com.example.demo.entity.TicketMessage;
import org.springframework.data.jpa.repository.*;

import java.util.List;

public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {
    List<TicketMessage> findByTicketIdOrderByTimestampAsc(Long ticketId);
}
