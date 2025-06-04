package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Form {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @PositiveOrZero
    private int numberOfPages;

    @NotBlank
    private String title;

    private boolean active;

    @PositiveOrZero
    private int numberOfInputFields;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_id")
    private List<FormField> fields = new ArrayList<>();


    public Form(){ /* Constructor used by Spring Data JPA */ }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setNumberOfInputFields(int numberOfInputFields) {
        this.numberOfInputFields = numberOfInputFields;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public boolean isActive() {
        return active;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void addFields(List<FormField> fields) {
        this.fields.clear();
        this.fields.addAll(fields);
    }

    public int getNumberOfInputFields() {
        return numberOfInputFields;
    }
}