package com.example.demo.util;

import com.example.demo.exceptions.FormCreationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PdfFileUtil {
    public static String mapPdfInputFieldsToCssPercentages(String pdfFilePath) throws IOException, InterruptedException, FormCreationException {
        String pythonScriptPath = "src/main/resources/scripts/convert_points_to_percentages.py";
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
}
