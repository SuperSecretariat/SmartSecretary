package com.example.demo.repository;

import com.example.demo.model.Form;
import com.example.demo.projection.FormNumberOfInputFieldsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.projection.FormFieldsProjection;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    List<Form> findByActive(boolean active);
    FormFieldsProjection findFieldsById(Long id);
    FormNumberOfInputFieldsProjection findNumberOfInputFieldsById(Long id);
}
