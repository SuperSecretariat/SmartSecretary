package com.example.demo.util;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidWordToPDFConversion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PdfFileUtil {
    private static final String FORMS_DIRECTORY_PATH = "formDocuments/";
    private static final String FORM_REQUESTS_DIRECTORY_PATH = "src/main/resources/requests/";
    private static final String SCRIPTS_DIRECTORY_PATH = "src/main/resources/scripts/";
    private static final String PYTHON_SCRIPT_OUTPUT_FILE = "src/main/resources/scripts/script-output.json";
    private PdfFileUtil() {
        // Private constructor to prevent instantiation
    }

    public static String mapPdfInputFieldsToCssPercentages(String formTitle) throws IOException, InterruptedException, FormCreationException, InvalidWordToPDFConversion {
        String pdfOutputDirectoryPath = FORMS_DIRECTORY_PATH + formTitle;
        String docxFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".docx";
        WordFileUtil.convertDocxToPDF(docxFilePath, pdfOutputDirectoryPath);

        String pdfFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".pdf";

        String pythonScriptPath = SCRIPTS_DIRECTORY_PATH + "convert_points_to_percentages-v3.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, pdfFilePath);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            // Read the output from the script-output.json as the stdout was too little for some forms
            return Files.readString(Path.of(PYTHON_SCRIPT_OUTPUT_FILE));
        } else {
            // Read the errors from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line);
                message.append("\n");
            }
            throw new FormCreationException(message.toString());
        }
    }

    public static List<byte[]> getImagesOfPdfFile(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int numberOfPages = document.getNumberOfPages();
            List<byte[]> images = new ArrayList<>();

            for (int i = 0; i < numberOfPages; i++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(i, 300);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", byteArrayOutputStream);
                images.add(byteArrayOutputStream.toByteArray());
            }
            return images;
        }
    }

    public static void createPdfAndImageForSubmittedFormRequest(String formTitle, String userRegistrationNumber) throws IOException, InterruptedException, InvalidWordToPDFConversion {
        String pdfOutputDirectoryPath = FORM_REQUESTS_DIRECTORY_PATH + userRegistrationNumber;
        String docxFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".docx";
        WordFileUtil.convertDocxToPDF(docxFilePath, pdfOutputDirectoryPath);
    }
}
