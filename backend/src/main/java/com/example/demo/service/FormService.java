package com.example.demo.service;

import com.example.demo.model.Form;
import com.example.demo.model.FormRequest;
import com.example.demo.repository.FormRepository;
import org.springframework.stereotype.Service;
import com.example.demo.util.JwtUtil;

import java.util.List;

@Service
public class FormService {
    private FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public Form createForm(Form form) {
        return formRepository.save(form);
    }

    public List<Form> getAllActiveForms() {
        return this.formRepository.findByActive(true);
    }

    public void validateFormRequest(FormRequest formRequest) {

    }
}
