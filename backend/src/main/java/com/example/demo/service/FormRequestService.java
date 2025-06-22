package com.example.demo.service;

import com.example.demo.dto.FormRequestFieldsDTO;
import com.example.demo.exceptions.*;
import com.example.demo.response.FormRequestResponse;
import com.example.demo.entity.FormRequestField;
import com.example.demo.entity.FormRequest;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.repository.FormRepository;
import com.example.demo.repository.FormRequestRepository;
import com.example.demo.dto.FormRequestDTO;
import com.example.demo.util.JwtUtil;
import com.example.demo.util.PdfFileUtil;
import com.example.demo.util.WordFileUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.util.AESUtil.decrypt;
import static com.example.demo.util.AESUtil.encrypt;

@Service
public class FormRequestService {
    private static final String FORM_REQUESTS_DIRECTORY_PATH = "src/main/resources/requests/";
    private final FormRequestRepository formRequestRepository;
    private final FormRepository formRepository;
    private final JwtUtil jwtUtil;

    public FormRequestService(FormRequestRepository repository, FormRepository formRepository, JwtUtil jwtUtil) {
        this.formRequestRepository = repository;
        this.formRepository = formRepository;
        this.jwtUtil = jwtUtil;
    }


    public FormRequest createFormRequest(FormRequestDTO formRequestRequest) throws FormRequestFieldDataException, IOException, InvalidWordToPDFConversion, InterruptedException, EncryptionException {
        // Get the userRegistrationNumber from the JWT token
        String userRegistrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(formRequestRequest.getJwtToken());
        if (this.formRepository.findNumberOfInputFieldsById(formRequestRequest.getFormId()).getNumberOfInputFields()
                == formRequestRequest.getFieldsData().size()) {
            FormRequest formRequest = new FormRequest(
                    formRequestRequest.getFormId(),
                    formRepository.findTitleById(formRequestRequest.getFormId()).getTitle(),
                    userRegistrationNumber,
                    FormRequestStatus.PENDING,
                    createFormRequestFields(formRequestRequest.getFieldsData())
            );

//            save the request in the db
            formRequestRepository.save(formRequest);

//            create files for the formRequest view
            String formTitle = formRequest.getFormTitle();
            WordFileUtil.fillPlaceholders(formTitle, userRegistrationNumber, formRequestRequest.getFieldsData());
            PdfFileUtil.createPdfAndImageForSubmittedFormRequest(formTitle, userRegistrationNumber);

            return formRequest;
        }
        throw new FormRequestFieldDataException("The number of fields in the form request does not match the number of fields in the form template.");
    }

    private List<FormRequestField> createFormRequestFields(List<String> fieldsData) throws EncryptionException {
            List<FormRequestField> formRequestFields = new ArrayList<>();
            for (String fieldData : fieldsData) {
                formRequestFields.add(new FormRequestField(encrypt(fieldData)));
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
            formRequestsResponse.add(new FormRequestResponse(formRequest.getId(), formRequest.getFormTitle(), formRequest.getStatus(), formRequest.getFormId()));
        }
        return formRequestsResponse;
    }

    //Used to get requests based on status
    //Can also be used to filter requests by status (for students combined with user filtering)
    public List<FormRequestResponse> getFormRequestsByStatus(FormRequestStatus status) {
        List<FormRequest> formRequests = formRequestRepository.findByStatus(status);

        List<FormRequestResponse> formRequestsResponse = new ArrayList<>();
        for (FormRequest formRequest : formRequests) {
            formRequestsResponse.add(new FormRequestResponse(formRequest.getId(), formRequest.getFormTitle(), formRequest.getStatus(), formRequest.getFormId()));
        }
        return formRequestsResponse;
    }

    public FormRequest getFormRequestById(Long id) throws InvalidFormRequestIdException {
        Optional<FormRequest> formRequest = this.formRequestRepository.findById(id);
        if (formRequest.isPresent()) {
            return formRequest.get();
        }
        throw new InvalidFormRequestIdException("The form request with the given ID does not exist.");
    }

    // provides a way to update the status of an existing form request
    public void updateFormRequestStatus(Long id, String status) throws InvalidFormRequestIdException, InvalidFormRequestStatusException {
        Optional<FormRequest> formRequest = this.formRequestRepository.findById(id);
        if (formRequest.isPresent()) {
            formRequest.get().setStatus(FormRequestStatus.getStatusFromString(status));
            this.formRequestRepository.save(formRequest.get());
            return;
        }
        throw new InvalidFormRequestIdException("The form request with the given ID does not exist.");
    }

    // provides the image of the completed form request with the given id
    public List<byte[]> getFormRequestImages(Long id) throws IOException, InvalidFormIdException {
        Optional<FormRequest> formRequest = this.formRequestRepository.findById(id);
        if (formRequest.isEmpty()) {
            throw new InvalidFormIdException("The form request with the given ID does not exist.");
        }
        String registrationNumber = this.formRequestRepository.findRegistrationNumberById(id).getUserRegistrationNumber();
        String title = formRequest.get().getFormTitle();
        String pdfFilePath = FORM_REQUESTS_DIRECTORY_PATH + registrationNumber + '/' + title + ".pdf";
        return PdfFileUtil.getImagesOfPdfFile(pdfFilePath);
    }

    public FormRequestFieldsDTO getFormRequestFieldsById(Long id) throws InvalidFormRequestIdException, DecryptionException {
            if (doesFormRequestNotExist(id)) {
                throw new InvalidFormRequestIdException("The form with the given ID does not exist.");
            }
            FormRequestFieldsDTO formRequestFieldsDTO = new FormRequestFieldsDTO();
            List<FormRequestField> formRequestFields = this.formRequestRepository.findFormRequestFieldsById(id).getFields();
            for (FormRequestField formRequestField : formRequestFields) {
                formRequestFieldsDTO.addFieldData(decrypt(formRequestField.getData()));
            }
            return formRequestFieldsDTO;
    }

    private boolean doesFormRequestNotExist(Long id) {
        return !this.formRequestRepository.existsById(id);
    }

    public void deleteFormRequestById(Long id) throws InvalidFormRequestIdException {
        if (doesFormRequestNotExist(id)) {
            throw new InvalidFormRequestIdException("The form request with ID: " + id + "does not exist.");
        }
        this.formRequestRepository.deleteById(id);
    }
}
