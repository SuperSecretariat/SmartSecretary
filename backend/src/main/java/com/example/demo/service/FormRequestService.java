package com.example.demo.service;

import com.example.demo.exceptions.FormRequestFieldDataException;
import com.example.demo.exceptions.InvalidFormRequestIdException;
import com.example.demo.model.FormRequest;
import com.example.demo.model.enums.FormRequestStatus;
import com.example.demo.repository.FormRepository;
import com.example.demo.repository.FormRequestRepository;
import com.example.demo.request.FormRequestRequest;
import com.example.demo.util.JwtUtil;
import org.springframework.stereotype.Service;

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
        // Uncomment the following line when the frontend is implemented
//        String userRegistrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(formRequestRequest.getJwtToken());
        //check if the data given by the user fits to the desired form template
        if (this.formRepository.findNumberOfInputFieldsById(formRequestRequest.getFormId()).getNumberOfInputFields()
                == formRequestRequest.getFields().size()) {
            FormRequest formRequest = new FormRequest(
                    formRequestRequest.getFormId(),
        // Uncomment the following line when the frontend is implemented
//                    userRegistrationNumber,
                    formRequestRequest.getJwtToken(), //only for testing, remove when frontend is done
                    FormRequestStatus.PENDING,
                    formRequestRequest.getFields()
            );

            formRequestRepository.save(formRequest);
            return formRequest;
        }
        else{
            throw new FormRequestFieldDataException("The number of fields in the form request does not match the number of fields in the form template.");
        }
    }

    // Used to get all requests for a specific user
    // Can be used to display active formRequests in the dashboard
    public List<FormRequest> getFormRequestsByUserRegistrationNumber(String sessionToken) {
        // Uncomment the following line when the frontend is implemented
//        String userRegistrationNumber = jwtUtil.getRegistrationNumberFromJwtToken(sessionToken);
        return formRequestRepository.findByUserRegistrationNumber(
        // Uncomment the following line when the frontend is implemented
//                userRegistrationNumber
                sessionToken //only for testing, remove when frontend is done
        );
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
