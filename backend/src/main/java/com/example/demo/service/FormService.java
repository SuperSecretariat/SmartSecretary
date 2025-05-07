package com.example.demo.service;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidFormIdException;
import com.example.demo.exceptions.NoFormFieldsFoundException;
import com.example.demo.model.Form;
import com.example.demo.model.FormField;
import com.example.demo.model.FormFieldJsonObject;
import com.example.demo.model.FormRequest;
import com.example.demo.repository.FormFieldRepository;
import com.example.demo.repository.FormRepository;
import com.example.demo.request.FormCreationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Service;
import com.example.demo.util.PdfFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FormService {
    private final FormRepository formRepository;

    private final FormFieldRepository formFieldRepository;

    public FormService(FormRepository formRepository, FormFieldRepository formFieldRepository) {
        this.formRepository = formRepository;
        this.formFieldRepository = formFieldRepository;
    }

    public Form createForm(FormCreationRequest formCreationRequest) throws IOException, InterruptedException, FormCreationException {
        String jsonString = PdfFileUtil.mapPdfInputFieldsToCssPercentages(formCreationRequest.getTitle());

        ObjectMapper mapper = new ObjectMapper();
        // Deserialize the JSON string into a list of FormFieldJsonObject objects
        List<FormFieldJsonObject> formFieldsAsJsonObjects = mapper.readValue(jsonString, new TypeReference<>() {});

        // Create and save the new form in the db
        Form form = new Form();
        form.setActive(formCreationRequest.getIsActive());
        form.setTitle(formCreationRequest.getTitle());
        form.setNumberOfInputFields(formFieldsAsJsonObjects.size());
        this.formRepository.save(form);

        // Create and save new form fields in the db and assign them the newly created form
        List<FormField> formFields = formFieldsAsJsonObjects.stream()
                .map(formFieldJsonObject -> new FormField(
                        form.getId(),
                        formFieldJsonObject.getPage(),
                        formFieldJsonObject.getTop(),
                        formFieldJsonObject.getLeft(),
                        formFieldJsonObject.getWidth(),
                        formFieldJsonObject.getHeight())
                )
                .toList();
        this.formFieldRepository.saveAll(formFields);

        return form;
        //save files in resources/uploaded.forms directory
        //compute size of the pdf file in points     ---
        //compute coordinates of the input fields      | --> the python script does this
        //convert the coordinates to % for css       ---
        //store the % into the database
        //store the file name, number of input fields, ?size of the pdf file in the database
    }

    public List<Form> getAllActiveForms() {
        return this.formRepository.findByActive(true);
    }

    public Form getFormById(Long id) throws InvalidFormIdException {
        Optional<Form> form = this.formRepository.findById(id);
        if (form.isEmpty()) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        return form.get();
    }

    public void validateFormRequest(FormRequest formRequest) {}

    public byte[] getFormImage(Long id) throws IOException, InvalidFormIdException {
        Optional<Form> form = this.formRepository.findById(id);
        if (form.isEmpty()) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        String title = form.get().getTitle();
        Path path = Paths.get("src/main/resources/uploaded.forms/" + title + "/" + title + ".jpg");
        System.out.println(path);
        return Files.readAllBytes(path);
    }

    public List<FormFieldJsonObject> getFormFieldsOfFormWithId(Long id) throws InvalidFormIdException, NoFormFieldsFoundException {
        if (!doesFormExist(id)) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        List<FormField> formFields = this.formFieldRepository.findByFormId(id);
        if (formFields.isEmpty()) {
            throw new NoFormFieldsFoundException("No form fields found for the given form ID.");
        }
        return formFields.stream()
                .map(formField -> new FormFieldJsonObject(
                        formField.getPage(),
                        formField.getTop(),
                        formField.getLeft(),
                        formField.getWidth(),
                        formField.getHeight()
                ))
                .toList();
    }

    private boolean doesFormExist(Long id) {
        return this.formRepository.existsById(id);
    }
}
