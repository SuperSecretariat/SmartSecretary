package com.example.demo.dto;

public class FormFieldJsonObject {
    private String page;
    private String top;
    private String left;
    private String width;
    private String height;
    private String text;
    private String previousWord;

    public FormFieldJsonObject(){}

    public FormFieldJsonObject(String page, String top, String left, String width, String height, String text, String previousWord) {
        this.page = page;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
        this.text = text;
        this.previousWord = previousWord;
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
}
