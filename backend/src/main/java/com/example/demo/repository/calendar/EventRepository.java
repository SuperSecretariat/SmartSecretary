package com.example.demo.repository.calendar;

import com.example.demo.entity.calendar.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByGroup(String group);
    @Transactional
    @Modifying
    @Query("DELETE FROM Event e where e.group = ?1")
    void deleteByGroup(@Param("group") String group);
}

