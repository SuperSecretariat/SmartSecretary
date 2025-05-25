package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String group;
    private String type;
    private String day;
    private String time;
    private String title;
    private String professor;

    // Constructors, getters, and setters
    public Event(String group, String type, String day, String time, String title, String professor) {
        this.group = group;
        this.type = type;
        this.day = day;
        this.time = time;
        this.title = title;
        this.professor = professor;
    }

    public Event() {}
}

