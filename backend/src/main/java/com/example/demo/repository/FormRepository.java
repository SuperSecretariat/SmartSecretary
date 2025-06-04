package com.example.demo.repository;

import com.example.demo.entity.Form;
import com.example.demo.projection.FormNumberOfInputFieldsProjection;
import com.example.demo.projection.FormTitleProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.projection.FormFieldsProjection;

import java.util.List;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {
    boolean existsByTitle(String title);
    void deleteByTitle(String title);
    Form findByTitle(String title);
    List<Form> findByActive(boolean active);
    FormFieldsProjection findFieldsById(Long id);
    FormNumberOfInputFieldsProjection findNumberOfInputFieldsById(Long id);
    FormTitleProjection findTitleById(Long id);
}
