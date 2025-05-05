package com.example.demo.repository;

import com.example.demo.model.FormRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRequestRepository extends JpaRepository<FormRequest, Long> {
    List<FormRequest> findByUserId(long userId);

    Optional<FormRequest> findById(long id);
}
