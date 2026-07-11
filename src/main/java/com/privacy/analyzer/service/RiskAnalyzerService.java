package com.privacy.analyzer.service;

import org.springframework.stereotype.Service;

@Service
public class RiskAnalyzerService {

    public String analyzeRisk(String text) {

        int score = 0;
        StringBuilder result = new StringBuilder();

        text = text.toLowerCase();

        // High risk checks
        if (text.contains("share your data") || text.contains("third parties")) {
            score += 30;
            result.append("❌ High Risk: Shares data with third parties\n");
        }

        if (text.contains("sell personal information")) {
            score += 40;
            result.append("❌ High Risk: Sells personal data\n");
        }

        if (text.contains("location")) {
            score += 20;
            result.append("⚠ Medium Risk: Tracks location\n");
        }

        if (text.contains("advertising partners")) {
            score += 20;
            result.append("⚠ Medium Risk: Shares with advertisers\n");
        }

        // Positive checks
        if (text.contains("delete your account")) {
            score -= 10;
            result.append("✅ Positive: User can delete account\n");
        }

        if (text.contains("encrypted")) {
            score -= 10;
            result.append("✅ Positive: Data encryption available\n");
        }

        // Final status
        String riskLevel;

        if (score <= 20) {
            riskLevel = "SAFE";
        } else if (score <= 50) {
            riskLevel = "MODERATE";
        } else {
            riskLevel = "RISKY";
        }

        return "Risk Score: " + score + "/100\n"
                + "Status: " + riskLevel + "\n\n"
                + result.toString();
    }
}