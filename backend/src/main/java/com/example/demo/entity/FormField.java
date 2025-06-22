package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
public class FormField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

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

    @NotBlank
    private String text;

    @NotBlank
    private String previousWord;

    private String label;

    public FormField() {}

    public FormField(String page, String top, String left, String width, String height, String text, String previousWord) {
        this.page = page;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.text = text;
        this.previousWord = previousWord;
    }


    @Override
    public String toString() {
        return "FormField{" +
                "id=" + id +
                ", page='" + page + '\'' +
                ", top='" + top + '\'' +
                ", left='" + left + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                ", text='" + text + '\'' +
                ", previousWord='" + previousWord + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getPage() {
        return page;
    }

    public String getTop() {
        return top;
    }

    public String getLeft() {
        return left;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getText() {
        return text;
    }

    public String getPreviousWord() {
        return previousWord;
    }

    public String getLabel() {
        return label;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setTop(String top) {
        this.top = top;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPreviousWord(String previousWord) {
        this.previousWord = previousWord;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
