package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "form_id")
    private long formId;

    @NotBlank
    private String page;

    @NotBlank
    private String top;

    @NotBlank
    @Column(name = "\"left\"")
    private String left;

    @NotBlank
    private String width;

    @NotBlank
    private String height;

    public FormField() {}

    public FormField(long formId, String page, String top, String left, String width, String height) {
        this.formId = formId;
        this.page = page;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }
}
