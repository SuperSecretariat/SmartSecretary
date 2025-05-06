package com.example.demo.model;

import com.example.demo.model.enums.FormRequestStatus;
import jakarta.persistence.*;

@Entity
public class FormRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long formId;

    private long userId;

    private FormRequestStatus status;

//    @ElementCollection
//    private Map<Integer, String> data;

    public FormRequest() {}

    public void submit(){}

    public void approve(){}

    public void reject(String reason){}

    public void generatePdf() {}

    public void saveAsDraft(){}
}
