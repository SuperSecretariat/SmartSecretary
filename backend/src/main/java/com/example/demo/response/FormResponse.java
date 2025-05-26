package com.example.demo.response;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

public class FormResponse {

    private final Long id;

    @NotBlank(message = ErrorMessage.MISSING_FORM_TITLE)
    private final String title;

    private final int numberOfInputFields;

    public FormResponse(Long id, String title, int numberOfInputFields) {
        this.id = id;
        this.title = title;
        this.numberOfInputFields = numberOfInputFields;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getNumberOfInputFields() {
        return numberOfInputFields;
    }
}
