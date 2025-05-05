package com.example.demo.model;

import com.example.demo.model.enums.FieldType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "form_id")
    private long formId;

    @NotBlank
    private String name;

    private FieldType type;

    private boolean required;

    public FormField() {
    }

    public FormField(long formId, String name, FieldType type) {
        this.formId = formId;
        this.name = name;
        this.type = type;
    }
}
