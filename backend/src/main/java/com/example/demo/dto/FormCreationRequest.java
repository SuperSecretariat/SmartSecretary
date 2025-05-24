package com.example.demo.dto;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public class FormCreationRequest {

    @NotBlank(message = ErrorMessage.MISSING_EMAIL)
    private final String title;

    private final boolean isActive;

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
