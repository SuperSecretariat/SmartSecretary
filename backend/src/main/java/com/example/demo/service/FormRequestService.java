package com.example.demo.service;

import com.example.demo.entity.Form;
import com.example.demo.projection.FormTitleProjection;
import com.example.demo.response.FormRequestResponse;
import com.example.demo.entity.FormRequestField;
import com.example.demo.exceptions.FormRequestFieldDataException;
import com.example.demo.exceptions.InvalidFormRequestIdException;
import com.example.demo.entity.FormRequest;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.repository.FormRepository;
import com.example.demo.repository.FormRequestRepository;
import com.example.demo.dto.FormRequestRequest;
import com.example.demo.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FormRequestService {
    private final FormRequestRepository formRequestRepository;
    private final FormRepository formRepository;
    private final JwtUtil jwtUtil;

    public FormRequestService(FormRequestRepository repository, FormRepository formRepository, JwtUtil jwtUtil) {
        this.formRequestRepository = repository;
        this.formRepository = formRepository;
        this.jwtUtil = jwtUtil;
    }


    public FormRequest createFormRequest(FormRequestRequest formRequestRequest) throws FormRequestFieldDataException {
        // Get the userRegistrationNumber from the JWT token
        String userRegistrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(formRequestRequest.getJwtToken());
        if (this.formRepository.findNumberOfInputFieldsById(formRequestRequest.getFormId()).getNumberOfInputFields()
                == formRequestRequest.getFieldsData().size()) {
            FormRequest formRequest = new FormRequest(
                    formRequestRequest.getFormId(),
                    userRegistrationNumber,
                    FormRequestStatus.PENDING,
                    createFormRequestFields(formRequestRequest.getFieldsData())
            );

            formRequestRepository.save(formRequest);
            return formRequest;
        }
        else{
            throw new FormRequestFieldDataException("The number of fields in the form request does not match the number of fields in the form template.");
        }
    }

    private List<FormRequestField> createFormRequestFields(List<String> fieldsData) {
        List<FormRequestField> formRequestFields = new ArrayList<>();
        for (String fieldData : fieldsData) {
            formRequestFields.add(new FormRequestField(fieldData));
        }
        return formRequestFields;
    }

    // Used to get all requests for a specific user
    // Can be used to display active formRequests in the dashboard
    public List<FormRequestResponse> getFormRequestsByUserRegistrationNumber(String jwtToken) {
        String userRegistrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(jwtToken);
        List<FormRequest> formRequests = formRequestRepository.findByUserRegistrationNumber(
                userRegistrationNumber
        );

        List<FormRequestResponse> formRequestsResponse = new ArrayList<>();
        for (FormRequest formRequest : formRequests) {
            String formTitle = formRepository.findTitleById(formRequest.getFormId()).getTitle();
            formRequestsResponse.add(new FormRequestResponse(formRequest.getId(), formTitle, formRequest.getStatus()));
        }
        return formRequestsResponse;
    }

    public void approveRequest(long requestId) {
        // secretary page
    }

    public void rejectRequest(long requestId) {
        // secretary page
    }

    public FormRequest getFormRequestById(Long id) throws InvalidFormRequestIdException {
        Optional<FormRequest> formRequest = this.formRequestRepository.findById(id);
        if (formRequest.isPresent()) {
            System.out.println(formRequest.get());
            return formRequest.get();
        }
        else{
            throw new InvalidFormRequestIdException("The form request with the given ID does not exist.");
        }
    }
}
