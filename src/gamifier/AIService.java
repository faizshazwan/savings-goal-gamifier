package gamifier;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * AI Service for getting savings advice from FreeLLMAPI (OpenAI-compatible endpoint).
 * Uses Java's built-in HttpClient (JDK 11+).
 */
public class AIService {
    // FreeLLMAPI configuration - easily changeable
    private static final String API_BASE_URL = "http://127.0.0.1:31415/v1";
    private static final String API_KEY = "freellmapi-01285609308905d8776ad2370c224945908dbdd54541eda6";
    private static final String MODEL = "auto";
    
    private static final String CHAT_COMPLETIONS_URL = API_BASE_URL + "/chat/completions";
    
    private final HttpClient httpClient;

    public AIService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Get AI savings advice for a specific goal.
     * 
     * @param goalName the name of the goal
     * @param target the target amount
     * @param current the current amount saved
     * @return AI advice as a String
     */
    public String getSavingsAdvice(String goalName, double target, double current) {
        try {
            String prompt = buildPrompt(goalName, target, current);
            String jsonPayload = buildJsonPayload(prompt);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHAT_COMPLETIONS_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return extractTextFromResponse(response.body());
            } else {
                return "Error: API returned status " + response.statusCode() + " - " + response.body();
            }
        } catch (IOException | InterruptedException e) {
            return "Error connecting to AI service: " + e.getMessage();
        }
    }

    /**
     * Build the prompt for the AI.
     */
    private String buildPrompt(String goalName, double target, double current) {
        double progress = target > 0 ? (current / target) * 100 : 0;
        double remaining = target - current;
        
        return String.format(
            "You are a friendly personal finance coach. Give concise, encouraging savings advice for this goal:\n\n" +
            "Goal: %s\n" +
            "Target: $%.2f\n" +
            "Current: $%.2f\n" +
            "Progress: %.1f%%\n" +
            "Remaining: $%.2f\n\n" +
            "Provide 3 specific, actionable tips to help reach this goal faster. " +
            "Keep it under 200 words. Be encouraging and practical.",
            goalName, target, current, progress, remaining
        );
    }

    /**
     * Build JSON payload for OpenAI-compatible chat completions API.
     */
    private String buildJsonPayload(String prompt) {
        return String.format(
            "{\"model\":\"%s\",\"messages\":[{\"role\":\"user\",\"content\":\"%s\"}]}",
            MODEL, escapeJson(prompt)
        );
    }

    /**
     * Escape special characters for JSON.
     */
    private String escapeJson(String input) {
        return input
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }

    /**
     * Extract text from OpenAI-compatible API JSON response.
     * Expected format: {"choices":[{"message":{"content":"..."}}]}
     * Simple string parsing without external JSON libraries.
     */
    private String extractTextFromResponse(String jsonResponse) {
        try {
            // Look for "content":"..." pattern in choices array
            String searchPattern = "\"content\":\"";
            int startIndex = jsonResponse.indexOf(searchPattern);
            
            if (startIndex == -1) {
                // Try alternative pattern with space
                searchPattern = "\"content\": \"";
                startIndex = jsonResponse.indexOf(searchPattern);
            }
            
            if (startIndex == -1) {
                return "Could not parse AI response.";
            }
            
            startIndex += searchPattern.length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            
            // Handle escaped quotes in the text
            while (endIndex > 0 && jsonResponse.charAt(endIndex - 1) == '\\') {
                endIndex = jsonResponse.indexOf("\"", endIndex + 1);
            }
            
            if (endIndex == -1) {
                return "Could not parse AI response.";
            }
            
            String extracted = jsonResponse.substring(startIndex, endIndex);
            
            // Unescape JSON characters
            return unescapeJson(extracted);
            
        } catch (Exception e) {
            return "Error parsing AI response: " + e.getMessage();
        }
    }

    /**
     * Unescape JSON characters.
     */
    private String unescapeJson(String input) {
        return input
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
            .replace("\\\\", "\\")
            .replace("\\t", "\t")
            .replace("\\r", "\r");
    }
}