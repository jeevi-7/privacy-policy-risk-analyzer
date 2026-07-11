package com.privacy.analyzer.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.List;

@Service
public class OpenRouterService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.create("https://openrouter.ai");

    public String analyzeWithAI(String pdfText) {

        String prompt = """
You are a Privacy Policy Risk Analyzer AI.

Analyze the privacy policy and give output in this EXACT format:

Risk Score: (0 to 100)

Status:
SAFE / MODERATE / HIGH RISK

Pros:
- short points only

Cons:
- short points only

Hidden Risks:
- Mention things companies may hide
- Mention third-party sharing
- Mention public data sharing
- Mention tracking/cookies
- Mention unclear legal language

Sensitive Permissions Detected:
- Location
- Contacts
- Camera
- Microphone
- Personal Data
- Financial Data

Recommendation:
One short final recommendation

Privacy Policy Text:
""" + pdfText;
        Map<String, Object> requestBody = Map.of(
                "model", "openai/gpt-oss-20b:free",
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        try {
            Map response = webClient.post()
                    .uri("/api/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List choices = (List) response.get("choices");
            Map firstChoice = (Map) choices.get(0);
            Map message = (Map) firstChoice.get("message");

            return message.get("content").toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "OpenRouter Error: " + e.toString();
        }
    }
}