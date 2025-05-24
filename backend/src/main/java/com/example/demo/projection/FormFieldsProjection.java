package com.example.demo.projection;

import com.example.demo.entity.FormField;

import java.util.List;

/**
 * Projection interface for FormField.
 * This interface is used to retrieve a list of FormField objects from the formRepository
 * automatically using JPA conventions.
 */
public interface FormFieldsProjection {
    List<FormField> getFields();
}
