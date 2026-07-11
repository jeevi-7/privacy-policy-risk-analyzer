package com.privacy.analyzer.controller;


import com.privacy.analyzer.model.PrivacyPolicy;
import com.privacy.analyzer.repository.PrivacyPolicyRepository;
import com.privacy.analyzer.service.OpenRouterService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Controller
public class UploadController {

    @Autowired
    private PrivacyPolicyRepository repository;

    @Autowired
    private OpenRouterService openRouterService;

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {

        try {
            // Create uploads folder if not exists
            File uploadDir = new File("uploads");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save uploaded file
            String filePath = uploadDir.getAbsolutePath() + File.separator + file.getOriginalFilename();
            file.transferTo(new File(filePath));

            // Extract PDF text
            PDDocument document = PDDocument.load(new File(filePath));
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String extractedText = pdfStripper.getText(document);
            document.close();

            // AI analysis
            String aiResult;
            try {
                aiResult = openRouterService.analyzeWithAI(extractedText);
            } catch (Exception e) {
                aiResult = "AI analysis failed: " + e.getMessage();
            }

            // Save to database
            PrivacyPolicy policy = new PrivacyPolicy();
            policy.setFileName(file.getOriginalFilename());
            policy.setExtractedText(extractedText);
            policy.setAnalysisResult(aiResult);
            policy.setRiskScore(75); // temporary fixed score
            policy.setStatus("Analyzed");
            policy.setUploadDate(java.time.LocalDateTime.now());

            repository.save(policy);

            // Send result to webpage
            model.addAttribute("message", "Upload successful!");
            model.addAttribute("extractedText", extractedText);
            model.addAttribute("analysisResult", aiResult);

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Upload failed!");
            model.addAttribute("analysisResult", e.getMessage());
        }

        return "result";
    }
}