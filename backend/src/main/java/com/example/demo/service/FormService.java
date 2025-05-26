package com.example.demo.service;

import com.example.demo.exceptions.InvalidWordToPDFConversion;
import com.example.demo.response.FormResponse;
import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidFormIdException;
import com.example.demo.exceptions.NoFormFieldsFoundException;
import com.example.demo.entity.Form;
import com.example.demo.entity.FormField;
import com.example.demo.dto.FormFieldJsonObject;
import com.example.demo.projection.FormFieldsProjection;
import com.example.demo.repository.FormRepository;
import com.example.demo.dto.FormCreationRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Service;
import com.example.demo.util.PdfFileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FormService {
    private final Logger logger = LoggerFactory.getLogger(FormService.class);
    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public Form createForm(FormCreationRequest formCreationRequest) throws IOException, InterruptedException, FormCreationException, InvalidWordToPDFConversion {
        String jsonString = PdfFileUtil.mapPdfInputFieldsToCssPercentages(formCreationRequest.getTitle());

        ObjectMapper mapper = new ObjectMapper();
        // Deserialize the JSON string into a list of FormFieldJsonObject objects
        List<FormFieldJsonObject> formFieldsAsJsonObjects = mapper.readValue(jsonString, new TypeReference<>() {});

        // Create and save the new form in the db
        Form form = new Form();
        form.setActive(formCreationRequest.getIsActive());
        form.setTitle(formCreationRequest.getTitle());
        form.setNumberOfInputFields(formFieldsAsJsonObjects.size());

        List<FormField> formFields = formFieldsAsJsonObjects.stream()
                .map(formFieldJsonObject -> new FormField(
                        formFieldJsonObject.getPage(),
                        formFieldJsonObject.getTop(),
                        formFieldJsonObject.getLeft(),
                        formFieldJsonObject.getWidth(),
                        formFieldJsonObject.getHeight(),
                        formFieldJsonObject.getText(),
                        formFieldJsonObject.getPreviousWord()
                        )
                )
                .toList();

        form.addFields(formFields);

        this.formRepository.save(form);

        return form;
        //save files in resources/uploaded.forms directory
        //compute size of the pdf file in points     ---
        //compute coordinates of the input fields      | --> the python script does this
        //convert the coordinates to % for css       ---
        //store the % into the database
        //store the file name, number of input fields, ?size of the pdf file in the database
    }

    public List<FormResponse> getAllActiveForms() {
        List<Form> activeForms = this.formRepository.findByActive(true);
        List<FormResponse> formsResponse = new ArrayList<>();
        for (Form form : activeForms) {
            formsResponse.add(new FormResponse(form.getId(), form.getTitle(), form.getNumberOfInputFields()));
        }
        return formsResponse;
    }

    public FormResponse getFormById(Long id) throws InvalidFormIdException {
        Optional<Form> form = this.formRepository.findById(id);
        if (form.isEmpty()) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        return new FormResponse(form.get().getId(), form.get().getTitle(), form.get().getNumberOfInputFields());
    }

    public byte[] getFormImage(Long id) throws IOException, InvalidFormIdException {
        Optional<Form> form = this.formRepository.findById(id);
        if (form.isEmpty()) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        String title = form.get().getTitle();
        this.logger.info("Getting form image for form with title: {}", title);
        String imageFilePath = "src/main/resources/uploaded.forms/" + title + '/' + title + ".png";
        return Files.readAllBytes(Paths.get(imageFilePath));
    }

    public FormFieldsProjection getFormFieldsOfFormWithId(Long id) throws InvalidFormIdException, NoFormFieldsFoundException {
        if (!doesFormExist(id)) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        FormFieldsProjection formFieldsProjection = this.formRepository.findFieldsById(id);
        if (formFieldsProjection.getFields().isEmpty()) {
            throw new NoFormFieldsFoundException("No form fields found for the given form ID.");
        }
        return formFieldsProjection;
    }

    private boolean doesFormExist(Long id) {
        return this.formRepository.existsById(id);
    }
}
