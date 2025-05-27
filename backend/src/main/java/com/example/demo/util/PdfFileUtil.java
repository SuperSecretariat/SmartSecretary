package com.example.demo.util;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidWordToPDFConversion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class PdfFileUtil {
    private static final String FORMS_DIRECTORY_PATH = "formDocuments/";
    private static final String FORM_REQUESTS_DIRECTORY_PATH = "src/main/resources/requests/";
    private static final String SCRIPTS_DIRECTORY_PATH = "src/main/resources/scripts/";
    private PdfFileUtil() {
        // Private constructor to prevent instantiation
    }

    public static String mapPdfInputFieldsToCssPercentages(String formTitle) throws IOException, InterruptedException, FormCreationException, InvalidWordToPDFConversion {
        String pdfOutputDirectoryPath = FORMS_DIRECTORY_PATH + formTitle;
        String docxFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".docx";
        WordFileUtil.convertDocxToPDF(docxFilePath, pdfOutputDirectoryPath);

        String pdfFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".pdf";
        PdfFileUtil.downloadImageOfPdfFile(pdfFilePath);

        String pythonScriptPath = SCRIPTS_DIRECTORY_PATH + "convert_points_to_percentages-v3.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, pdfFilePath);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            // Read the output from the Python script
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }
            return jsonString.toString();
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

    public static byte[] getImageOfPdfFile(String pdfFilePath) throws IOException {
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // Render the first page at 300 DPI

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }
    }

    public static void downloadImageOfPdfFile(String pdfFilePath) throws IOException {
        byte[] imageBytes = getImageOfPdfFile(pdfFilePath);
        String imageFilePath = pdfFilePath.substring(0, pdfFilePath.length() - 3) + "png";
        try(FileOutputStream fos = new FileOutputStream(imageFilePath)) {
            fos.write(imageBytes);
        }
    }

    public static void createPdfAndImageFromDocx(String docxFilePath, String pdfOutputDirectoryPath, String pdfFilePath) throws IOException, InterruptedException, InvalidWordToPDFConversion {
        WordFileUtil.convertDocxToPDF(docxFilePath, pdfOutputDirectoryPath); // creates the pdf file from the docx
        PdfFileUtil.downloadImageOfPdfFile(pdfFilePath); // creates the image file from the pdf
    }

    public static void createPdfAndImageForSubmittedFormRequest(String formTitle, String userRegistrationNumber) throws IOException, InterruptedException, InvalidWordToPDFConversion {
        String pdfOutputDirectoryPath = FORM_REQUESTS_DIRECTORY_PATH + userRegistrationNumber;
        String docxFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".docx";
        WordFileUtil.convertDocxToPDF(docxFilePath, pdfOutputDirectoryPath);

        String pdfFilePath = pdfOutputDirectoryPath + '/' + formTitle + ".pdf";
        PdfFileUtil.downloadImageOfPdfFile(pdfFilePath);
    }
}
