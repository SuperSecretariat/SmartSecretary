package com.example.demo.util;

import com.example.demo.exceptions.FormCreationException;
import com.example.demo.exceptions.InvalidWordToPDFConversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WordFileUtil {
    private WordFileUtil() {}

    public static void convertDocxToPDF(String formTitle) throws IOException, InterruptedException, InvalidWordToPDFConversion  {
        String wordFilePath = "src/main/resources/uploaded.forms/" + formTitle + "/" + formTitle + ".docx";
        String pdfFilePath = "src/main/resources/uploaded.forms/" + formTitle;
        ProcessBuilder processBuilder = new ProcessBuilder(
                "soffice",
                "--headless",
                "--convert-to", "pdf",
                wordFilePath,
                "--outdir", pdfFilePath
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new InvalidWordToPDFConversion("File being converted: " + wordFilePath);
        }
    }
}
