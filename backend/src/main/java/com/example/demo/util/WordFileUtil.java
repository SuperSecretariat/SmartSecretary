package com.example.demo.util;
import org.apache.poi.xwpf.usermodel.*;
import java.io.*;
import java.util.List;
import java.util.regex.*;

import com.example.demo.exceptions.InvalidWordToPDFConversion;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WordFileUtil {
    private static final Logger logger = LoggerFactory.getLogger(WordFileUtil.class.getName());
    private static final String FORMS_DIRECTORY_PATH = "formDocuments/";
    private static final String REQUESTS_DIRECTORY_PATH = "src/main/resources/requests/";
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder message = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                message.append(line);
                message.append("\n");
            }
            throw new InvalidWordToPDFConversion("Error converting .docx (" + wordFilePath + ") to pdf. Error body: " + message);
        }
    }

    public static void fillPlaceholders(String formTitle, String regNumber, List<String> replacementWords) throws IOException {
        String docxOutputDirectoryPath = REQUESTS_DIRECTORY_PATH + regNumber;
        Path dir = Paths.get(docxOutputDirectoryPath);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        String docxFilePath = FORMS_DIRECTORY_PATH + formTitle + '/' + formTitle + ".docx";
        String docxOutputFilePath = docxOutputDirectoryPath + '/' + formTitle + ".docx";

        try (FileInputStream fis = new FileInputStream(docxFilePath);
             XWPFDocument doc = new XWPFDocument(fis)) {

            Pattern underscorePattern = Pattern.compile("_{2,}"); // match sequences of 2 or more underscores
            int wordIndex = 0;

            // Get all elements in top-down order
            List<IBodyElement> elements = doc.getBodyElements();

            for (IBodyElement element : elements) {
                if (element.getElementType() == BodyElementType.PARAGRAPH) {
                    XWPFParagraph para = (XWPFParagraph) element;
                    wordIndex = replaceInParagraph(para, replacementWords, wordIndex, underscorePattern);
                } else if (element.getElementType() == BodyElementType.TABLE) {
                    XWPFTable table = (XWPFTable) element;
                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            for (XWPFParagraph para : cell.getParagraphs()) {
                                wordIndex = replaceInParagraph(para, replacementWords, wordIndex, underscorePattern);
                            }
                        }
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(docxOutputFilePath)) {
                doc.write(fos);
            }

            logger.info("Saved the modified document with replaced placeholders as: {}", docxOutputFilePath);
        }
    }

    private static int replaceInParagraph(XWPFParagraph para, List<String> replacements, int wordIndex, Pattern pattern) {
        for (XWPFRun run : para.getRuns()) {
            String runText = run.getText(0);
            if (runText != null) {
                Matcher matcher = pattern.matcher(runText);
                StringBuilder updatedText = new StringBuilder();

                while (matcher.find() && wordIndex < replacements.size()) {
                    String replacement = replacements.get(wordIndex++);
                    matcher.appendReplacement(updatedText, Matcher.quoteReplacement(replacement));
                }
                matcher.appendTail(updatedText);
                run.setText(updatedText.toString(), 0);
            }
        }
        return wordIndex;
    }
}
