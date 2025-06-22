package com.example.demo.dto;

import com.example.demo.constants.ErrorMessage;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class FormRequestDTO {

    @NotBlank(message = ErrorMessage.MISSING_JWT_TOKEN)
    String jwtToken;

    long formId;

    List<String> fieldsData;

    public FormRequestDTO(String jwtToken, long formId, List<String> fields) {
        this.jwtToken = jwtToken;
        this.formId = formId;
        this.fieldsData = fields;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public long getFormId() {
        return formId;
    }

    public List<String> getFieldsData() {
        return fieldsData;
    }

    @Override
    public String toString() {
        return "FormRequestRequest{" +
                "jwtToken='" + jwtToken + '\'' +
                ", formId=" + formId +
                ", fieldsData=" + fieldsData +
                '}';
    }
}
