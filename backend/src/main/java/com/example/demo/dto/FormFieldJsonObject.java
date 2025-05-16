package com.example.demo.dto;

public class FormFieldJsonObject {
    private String page;
    private String top;
    private String left;
    private String width;
    private String height;

    public FormFieldJsonObject(){}

    public FormFieldJsonObject(String page, String top, String left, String width, String height) {
        this.page = page;
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
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
}
