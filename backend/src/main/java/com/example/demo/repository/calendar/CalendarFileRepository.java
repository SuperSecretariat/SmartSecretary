package com.example.demo.repository.calendar;

import com.example.demo.entity.calendar.CalendarFile;
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
    @Query("DELETE FROM CalendarFile cf WHERE cf.groups = ?1 AND cf.category = ?2")
    void deleteByGroupAndCategory(String groups, String category);

    List<CalendarFile> findAllByCategory(String category);

    @Transactional
    @Modifying
    @Query("DELETE FROM CalendarFile cf WHERE cf.category = ?1")
    void deleteByCategory(String Category);
}
