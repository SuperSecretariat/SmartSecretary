package com.example.demo.response;

import com.example.demo.model.enums.FormRequestStatus;

public class FormRequestResponse {
    private final Long id;
    private final Long formId;
    private final FormRequestStatus status;

    public FormRequestResponse(Long id, Long formId, FormRequestStatus status) {
        this.id = id;
        this.formId = formId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getFormId() {
        return formId;
    }

    public FormRequestStatus getStatus() {
        return status;
    }
}
