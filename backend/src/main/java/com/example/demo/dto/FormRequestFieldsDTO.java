package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;

public class FormRequestFieldsDTO {
    List<String> fieldsData = new ArrayList<>();;

    public FormRequestFieldsDTO() {}

    public List<String> getFieldsData() {
        return fieldsData;
    }

    public void setFieldsData(List<String> fieldsData) {
        this.fieldsData = fieldsData;
    }

    public void addFieldData(String fieldData) {
        this.fieldsData.add(fieldData);
    }
}
