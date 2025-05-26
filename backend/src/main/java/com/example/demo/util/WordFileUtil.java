package com.example.demo.util;

import com.example.demo.exceptions.InvalidWordToPDFConversion;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WordFileUtil {
    private static final String FORMS_DIRECTORY_PATH = "formDocuments/";
    private WordFileUtil() {}

    public static void convertDocxToPDF(String wordFilePath, String pdfOutputDirectoryPath) throws IOException, InterruptedException, InvalidWordToPDFConversion  {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "soffice",
                "--headless",
                "--convert-to", "pdf",
                wordFilePath,
                "--outdir", pdfOutputDirectoryPath
        );
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new InvalidWordToPDFConversion("File being converted: " + wordFilePath);
        }
    }

    public static void fillPlaceholders(String formTitle, String regNumber, List<String> replacementWords) throws IOException {
        String docxOutputDirectoryPath = "src/main/resources/requests/" + regNumber;
        Path dir = Paths.get(docxOutputDirectoryPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        String docxFilePath = FORMS_DIRECTORY_PATH + formTitle + '/' + formTitle+ ".docx";
        String docxOutputFilePath = docxOutputDirectoryPath + '/' + formTitle + ".docx";
        try (FileInputStream fis = new FileInputStream(docxFilePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            int wordIndex = 0; // To track the current replacement word
            for (XWPFParagraph para : doc.getParagraphs()) {
                for (XWPFRun run : para.getRuns()) {
                    String runText = run.getText(0);
                    if (runText != null) {
                        String[] words = runText.split(" "); // Split the run text into words
                        StringBuilder updatedText = new StringBuilder();

                        for (String word : words) {
                            if (word.startsWith("_") && wordIndex < replacementWords.size()) {
                                if (!word.endsWith("_")){
                                    replacementWords.set(wordIndex, replacementWords.get(wordIndex) + word.charAt(word.length() - 1));
                                }
                                updatedText.append(replacementWords.get(wordIndex)).append(" ");
                                wordIndex++;
                            } else {
                                updatedText.append(word).append(" ");
                            }
                        }

                        // Update the run with the modified text
                        run.setText(updatedText.toString().trim(), 0);
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(docxOutputFilePath)) {
                doc.write(fos);
            }
            System.out.println("Saved the modified document with replaced placeholders as: " + docxOutputFilePath);
        }
    }
}
