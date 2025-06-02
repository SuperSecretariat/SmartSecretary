package com.example.demo.response;

import com.example.demo.model.enums.FormRequestStatus;

public class FormRequestResponse {
    private final Long id;
    private final String formTitle;
    private final FormRequestStatus status;
    private final Long formId;

    public FormRequestResponse(Long id, String formTitle, FormRequestStatus status, Long formId) {
        this.id = id;
        this.formTitle = formTitle;
        this.status = status;
        this.formId = formId;
    }

    public Long getId() {
        return id;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public FormRequestStatus getStatus() {
        return status;
    }

    public Long getFormId() {
        return formId;
    }
}
