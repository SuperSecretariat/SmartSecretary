package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class FormRequestField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String data;

    public FormRequestField() {}

    public FormRequestField(String data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FormRequestField{" +
                "id=" + id +
                ", data='" + data + '\'' +
                '}';
    }
}
