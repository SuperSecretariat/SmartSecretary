package com.example.demo.repository.tickets;

import com.example.demo.entity.Ticket;
import com.example.demo.model.enums.TicketStatus;
import com.example.demo.model.enums.TicketType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {
    List<Ticket> findByUserId(Long userId);

    @Query("SELECT t FROM Ticket t WHERE " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:type IS NULL OR t.type = :type) AND " +
            "(:userId IS NULL OR t.user.id = :userId) AND " +
            "(:subject IS NULL OR LOWER(t.subject) LIKE LOWER(CONCAT('%', :subject, '%')))")
    List<Ticket> findByOptionalFilters(@Param("status") TicketStatus status,
                                       @Param("type") TicketType type,
                                       @Param("userId") Long userId,
                                       @Param("subject") String subject);
}
