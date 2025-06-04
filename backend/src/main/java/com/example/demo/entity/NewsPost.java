package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
public class NewsPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(columnDefinition = "TEXT")
    private String body;

    @NotBlank
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "hidden", nullable = false)
    private Boolean hidden;

    @NotBlank
    @Column(name = "fileName")
    private String fileName;

    @Column(name = "fileType")
    @NotBlank
    private String fileType;

    @Lob
    @Column(name = "fileData")
    private byte[] fileData;

    // Constructors, getters, and setters

    public NewsPost() {
        this.createdAt = LocalDateTime.now();
    }

    public NewsPost(String title, String body, String fileName, String fileType, byte[] fileData) {
        this.title = title;
        this.body = body;
        this.createdAt = LocalDateTime.now();
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public byte[] getFileData() {
        return fileData;
    }
    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
    public Boolean getHidden() {
        return hidden;
    }
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
}