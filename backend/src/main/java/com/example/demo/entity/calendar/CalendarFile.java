package com.example.demo.entity.calendar;

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

    @Column(name = "category", nullable = false)
    private String category;

    // Default constructor (required by JPA)
    public CalendarFile() {
    }

    // Constructor with parameters
    public CalendarFile(String name, String type, Float size, String groups, String category) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.groups = groups;
        this.category = category;
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

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
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