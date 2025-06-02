package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "calendar_files")
public class CalendarFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "size", nullable = false)
    private Float size;

    @Column(name = "groups", nullable = false)
    private String groups;

    // Default constructor (required by JPA)
    public CalendarFile() {
    }

    // Constructor with parameters
    public CalendarFile(String name, String type, Float size, String groups) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.groups = groups;
    }

    // Getters and Setters (REQUIRED by JPA/Hibernate)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Float getSize() {
        return size;
    }

    public void setSize(Float size) {
        this.size = size;
    }

    public String getGroups() {
        return groups;
    }

    public void setGroups(String groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "CalendarFile{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", groups='" + groups + '\'' +
                '}';
    }
}