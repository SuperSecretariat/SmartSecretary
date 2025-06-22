package com.example.demo.repository;

import com.example.demo.entity.FormRequest;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.projection.FormRequestFieldsProjection;
import com.example.demo.projection.RegistrationNumberProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface FormRequestRepository extends JpaRepository<FormRequest, Long> {
    List<FormRequest> findByUserRegistrationNumber(String userRegistrationNumber);
    List<FormRequest> findByStatus(FormRequestStatus status);
    FormRequestFieldsProjection findFormRequestFieldsById(Long id);
    RegistrationNumberProjection findRegistrationNumberById(Long id);
}
