package com.example.demo.request;

import com.example.demo.constants.ErrorMessage;
import com.example.demo.model.FormRequestField;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class FormRequestRequest {

    @NotBlank(message = ErrorMessage.MISSING_JWT_TOKEN)
    String jwtToken;

    long formId;

    List<FormRequestField> fields;

    public FormRequestRequest(String jwtToken, long formId, List<FormRequestField> fields) {
        this.jwtToken = jwtToken;
        this.formId = formId;
        this.fields = fields;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public long getFormId() {
        return formId;
    }

    public List<FormRequestField> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "FormRequestRequest{" +
                "jwtToken='" + jwtToken + '\'' +
                ", formId=" + formId +
                ", fields=" + fields +
                '}';
    }
}
