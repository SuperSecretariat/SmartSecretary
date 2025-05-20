package com.example.demo.repository;

import com.example.demo.entity.FormRequest;
import com.example.demo.projection.FormRequestFieldsProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FormRequestRepository extends JpaRepository<FormRequest, Long> {
    List<FormRequest> findByUserRegistrationNumber(String userRegistrationNumber);
    FormRequestFieldsProjection findFormRequestFieldsById(Long id);
    Optional<FormRequest> findById(long id);
}
