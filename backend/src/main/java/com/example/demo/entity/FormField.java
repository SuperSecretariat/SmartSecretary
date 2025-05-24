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
                '}';
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
}
