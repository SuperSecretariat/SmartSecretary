package com.example.demo.entity.calendar;

import jakarta.persistence.*;

@Entity
@Table(name = "year")
public class Year {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "start")
    private String start;

    @Column(name = "\"end\"")
    private String end;

    @Column(name = "type")
    private String type;

    public Year() {}

    public Year(String start, String end, String type) {
        this.start = start;
        this.end = end;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public String getEnd() {
        return end;
    }
    public void setEnd(String end) {
        this.end = end;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
