package com.example.demo.repository;

import com.example.demo.model.Form;
import com.example.demo.model.FormField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FormFieldRepository extends JpaRepository<FormField, Integer> {
    List<FormField> findByFormId(Long formId);
}
