package com.example.demo.entity;

import com.example.demo.model.enums.FormRequestStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;

import java.util.List;

@Entity
public class FormRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Positive()
    private long formId;

    @Positive
    private String userRegistrationNumber;

    private FormRequestStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_request_id")
    private List<FormRequestField> fields;

    public FormRequest() {}

    public FormRequest(long formId, String userRegistrationNumber, FormRequestStatus status, List<FormRequestField> fields) {
        this.formId = formId;
        this.userRegistrationNumber = userRegistrationNumber;
        this.status = status;
        this.fields = fields;
    }

    public long getId() {
        return id;
    }

    public long getFormId() {
        return formId;
    }

    public String getUserRegistrationNumber() {
        return userRegistrationNumber;
    }

    public FormRequestStatus getStatus() {
        return status;
    }

    public List<FormRequestField> getFields() {
        return fields;
    }

    public void setStatus(FormRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "FormRequest{" +
                "id=" + id +
                ", formId=" + formId +
                ", userRegistrationNumber='" + userRegistrationNumber + '\'' +
                ", status=" + status +
                ", fields=" + fields +
                '}';
    }
}
