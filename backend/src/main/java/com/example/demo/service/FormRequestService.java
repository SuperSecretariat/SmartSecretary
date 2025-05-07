package com.example.demo.service;

import com.example.demo.model.FormRequest;
import com.example.demo.repository.FormRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormRequestService {
    private FormRequestRepository repository;

    public FormRequestService(FormRequestRepository repository) {
        this.repository = repository;
    }


//    public boolean createFormRequest(long formId, long userId /*, data*/) {
//        FormRequest formRequest = new FormRequest();
//        //check if the data given by the user fits to the desired form template
//        if (/*data fits the form template*/) {
//            formRequest.setFromId(formId);
//            formRequest.setUserId(userId);
//            this.addFormRequest(formRequest);
//            return true;
//        }
//        return false;
//    }

    private void addFormRequest(FormRequest formRequest) {
        repository.save(formRequest);
    }

    // Used to get all requests for a specific user
    // Can be used to display active formRequests in the dashboard
    public List<FormRequest> getFormRequestsByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    public void approveRequest(long requestId) {
        // secretary page
    }

    public void rejectRequest(long requestId) {
        // secretary page
    }

}
