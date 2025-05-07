package com.example.demo.request;

public class FormCreationRequest {
    private String title;
    private boolean isActive;

    FormCreationRequest(String title, boolean isActive) {
        this.title = title;
        this.isActive = isActive;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
