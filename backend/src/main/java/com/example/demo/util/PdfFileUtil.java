package com.example.demo.util;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidWordToPDFConversion;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfFileUtil {
    private PdfFileUtil() {
        // Private constructor to prevent instantiation
    }
    public static String mapPdfInputFieldsToCssPercentages(String formTitle) throws IOException, InterruptedException, FormCreationException, InvalidWordToPDFConversion {
        WordFileUtil.convertDocxToPDF(formTitle); // creates the pdf file from the docx
        PdfFileUtil.downloadImageOfPdfFile(formTitle); // creates the image file from the pdf
        Path pdfFilePath = Paths.get("src/main/resources/uploaded.forms/" + formTitle + "/" + formTitle + ".pdf");
        String pythonScriptPath = "src/main/resources/scripts/convert_points_to_percentages.py";
        ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, pdfFilePath.toString());
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

    public static byte[] getImageOfPdfFile(String formTitle) throws IOException {
        String pdfFilePath = "src/main/resources/uploaded.forms/" + formTitle + "/" + formTitle + ".pdf";
        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300); // Render the first page at 300 DPI

            // Write the image to a ByteArrayOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);

            // Get the byte array
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Example: Print the size of the byte array
            return imageBytes;
        }
    }

    public static void downloadImageOfPdfFile(String formTitle) throws IOException {
        byte[] imageBytes = getImageOfPdfFile(formTitle);
        String imageFilePath = "src/main/resources/uploaded.forms/" + formTitle + "/" + formTitle + ".png";
        try(FileOutputStream fos = new FileOutputStream(imageFilePath)) {
            fos.write(imageBytes);
        }
    }
}
