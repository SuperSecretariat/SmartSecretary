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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.util.PdfFileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FormService {
    private static final String FORMS_DIRECTORY_PATH = "formDocuments/";
    private final Logger logger = LoggerFactory.getLogger(FormService.class);
    private final FormRepository formRepository;

    public FormService(FormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public Form createForm(FormCreationRequest formCreationRequest) throws IOException, InterruptedException, FormCreationException, InvalidWordToPDFConversion, NumberFormatException {
        String jsonString = PdfFileUtil.mapPdfInputFieldsToCssPercentages(formCreationRequest.getTitle());

        ObjectMapper mapper = new ObjectMapper();
        List<FormFieldJsonObject> formFieldsAsJsonObjects = mapper.readValue(jsonString, new TypeReference<>() {});

        Form form;
        if (doesFormExistByTitle(formCreationRequest.getTitle())) {
            form = this.formRepository.findByTitle(formCreationRequest.getTitle());
        }
        else {
            form = new Form();
        }

        form.setActive(formCreationRequest.getIsActive());
        form.setTitle(formCreationRequest.getTitle());
        form.setNumberOfInputFields(formFieldsAsJsonObjects.size());

        List<FormField> formFields = formFieldsAsJsonObjects.stream()
                .map(obj -> {
                    FormField field = new FormField();
                    field.setPage(obj.getPage());
                    field.setTop(obj.getTop());
                    field.setLeft(obj.getLeft());
                    field.setWidth(obj.getWidth());
                    field.setHeight(obj.getHeight());
                    field.setText(obj.getText());
                    field.setPreviousWord(obj.getPreviousWord());

                    field.setLabel(detectLabel(obj.getPreviousWord()));

                    return field;
                })
                .toList();
        if (!formFields.isEmpty()) {
            form.setNumberOfPages(Integer.parseInt(formFields.get(formFields.size() - 1).getPage()));
        }
        form.addFields(formFields);
        this.formRepository.save(form);

        return form;
        //compute size of the pdf file in points     ---
        //compute coordinates of the input fields      | --> the python script does this
        //convert the coordinates to % for css       ---
        //store the % into the database
        //store the file name, number of input fields of the pdf file in the database
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

    public List<byte[]> getFormImages(Long id) throws IOException, InvalidFormIdException {
        Optional<Form> form = this.formRepository.findById(id);
        if (form.isEmpty()) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        String title = form.get().getTitle();
        this.logger.info("Getting form image for form with title: {}", title);
        String pdfFilePath = FORMS_DIRECTORY_PATH + title + '/' + title + ".pdf";
        return PdfFileUtil.getImagesOfPdfFile(pdfFilePath);
    }

    public FormFieldsProjection getFormFieldsOfFormWithId(Long id) throws InvalidFormIdException, NoFormFieldsFoundException {
        if (!doesFormExistById(id)) {
            throw new InvalidFormIdException("The form with the given ID does not exist.");
        }
        FormFieldsProjection formFieldsProjection = this.formRepository.findFieldsById(id);
        if (formFieldsProjection.getFields().isEmpty()) {
            throw new NoFormFieldsFoundException("No form fields found for the given form ID.");
        }
        return formFieldsProjection;
    }

    @Transactional
    public void deleteFormByTitle(String title) throws InvalidFormIdException {
        if (!doesFormExistByTitle(title)) {
            throw new InvalidFormIdException("The form with title: " + title + "does not exist.");
        }
        this.formRepository.deleteByTitle(title);
    }

    private boolean doesFormExistById(Long id) {
        return this.formRepository.existsById(id);
    }

    private boolean doesFormExistByTitle(String title) {
        return this.formRepository.existsByTitle(title);
    }


    private String detectLabel(String previousWord) {
        if (previousWord == null) return "necunoscut";

        String normalized = previousWord.toLowerCase().replaceAll("[\\./]", " ");

        String[] tokens = normalized.trim().split("\\s+");

        for (String token : tokens) {
            if (token.contains("subsemnat")) return "Nume complet";
            if (token.contains("promo")) return "Promoție";
            if (token.contains("specializ")) return "Specializare";
            if (token.contains("perioad")) return "Perioadă studii";
            if (token.equals("an") || token.equals("anul") || token.contains("anul")) return "An de studii";
            if (token.contains("semestr")) return "Semestru";
            if (token.contains("cnp")) return "CNP";
            if (token.contains("matricol")) return "Număr matricol";
            if (token.contains("disciplina")) return "Disciplina";
            if (token.contains("titular")) return "Titular disciplină";
            if (token.contains("email")) return "Email";
            if (token.contains("telefon")) return "Telefon";
            if (token.contains("data")) return "Dată";
            if (token.contains("universitar")) return "An universitar";
            if (token.contains("nr")) return "Nr.";
            if (token.contains("semn")) return "Dată";
            if (token.contains("prof") || token.contains("conf") || token.contains("lect") || token.contains("asist") || token.contains("profesor")) {
                return "Profesor";
            }
            if (token.contains("pentru")) return "Scop";
        }

        return "Generic";
    }



}
