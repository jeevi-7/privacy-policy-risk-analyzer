package com.privacy.analyzer.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PdfService {

    public String extractText(String filePath) {

        String text = "";

        try {
            File file = new File(filePath);

            PDDocument document = PDDocument.load(file);

            PDFTextStripper pdfStripper = new PDFTextStripper();

            text = pdfStripper.getText(document);

            document.close();

        } catch (IOException e) {
            e.printStackTrace();
            text = "Error reading PDF.";
        }

        return text;
    }
}