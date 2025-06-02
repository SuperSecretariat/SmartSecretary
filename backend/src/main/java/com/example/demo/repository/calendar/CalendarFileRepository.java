package com.example.demo.repository.calendar;

import com.example.demo.entity.CalendarFile;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CalendarFileRepository extends JpaRepository<CalendarFile, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM CalendarFile cf WHERE cf.groups = ?1")
    void deleteByGroups(String groups);
}
