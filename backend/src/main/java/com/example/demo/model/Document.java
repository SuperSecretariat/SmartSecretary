package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @NotBlank
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    private String type;
    @NotBlank
    private String name;
    public Document(){
    }

    public Document(Integer id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public String getType() {
        return type;
    }
    public String getName() {
        return name;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setName(String name) {
        this.name = name;
    }
}
