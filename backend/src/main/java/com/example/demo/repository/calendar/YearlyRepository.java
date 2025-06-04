package com.example.demo.repository.calendar;

import com.example.demo.entity.calendar.Year;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YearlyRepository extends JpaRepository<Year, Long> {
}
