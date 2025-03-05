package org.example.meniar.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TranslationService {
    private static final String API_URL = "https://api.mymemory.translated.net/get";

    /**
     * Translates text from one language to another using MyMemory Translation API
     * @param text Text to translate
     * @param targetLang Target language code (e.g., "en", "fr", "ar", etc.)
     * @return Translated text
     * @throws IOException If translation fails
     */
    public static String translate(String text, String targetLang) throws IOException {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // For debugging
        System.out.println("Translating text: " + text);
        System.out.println("Target language: " + targetLang);

        // Detect source language (using first few characters)
        String sourceLang = detectLanguage(text);
        
        // Construct URL with parameters
        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String urlStr = String.format("%s?q=%s&langpair=%s|%s", API_URL, encodedText, sourceLang, targetLang);
        
        // For debugging
        System.out.println("API URL: " + urlStr);

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        // Read response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
        }

        // For debugging
        System.out.println("API Response: " + response.toString());

        try {
            // Parse JSON response
            JSONObject jsonResponse = new JSONObject(response.toString());
            
            // Check if there's an error
            if (jsonResponse.has("responseStatus") && jsonResponse.getInt("responseStatus") != 200) {
                throw new IOException("Translation failed: " + jsonResponse.optString("responseDetails", "Unknown error"));
            }

            // Get the translated text
            JSONObject responseData = jsonResponse.getJSONObject("responseData");
            String translatedText = responseData.getString("translatedText");

            // For debugging
            System.out.println("Translated text: " + translatedText);

            return translatedText;
        } catch (Exception e) {
            System.err.println("Error parsing translation response: " + e.getMessage());
            System.err.println("Raw response: " + response.toString());
            throw new IOException("Translation failed: " + e.getMessage());
        }
    }

    /**
     * Simple language detection based on character analysis
     * @param text Text to analyze
     * @return ISO 639-1 language code
     */
    private static String detectLanguage(String text) {
        // Default to French if we can't detect
        if (text == null || text.trim().isEmpty()) {
            return "fr";
        }

        // Count character frequencies
        int latinChars = 0;
        int cyrillicChars = 0;
        int arabicChars = 0;
        int cjkChars = 0;

        for (char c : text.toCharArray()) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                latinChars++;
            } else if (c >= '\u0400' && c <= '\u04FF') {
                cyrillicChars++;
            } else if (c >= '\u0600' && c <= '\u06FF') {
                arabicChars++;
            } else if (c >= '\u4E00' && c <= '\u9FFF') {
                cjkChars++;
            }
        }

        // Determine most frequent script
        int max = Math.max(Math.max(latinChars, cyrillicChars), Math.max(arabicChars, cjkChars));
        
        if (max == latinChars) {
            // For Latin script, try to differentiate between French and English
            return containsFrenchCharacters(text) ? "fr" : "en";
        } else if (max == cyrillicChars) {
            return "ru";
        } else if (max == arabicChars) {
            return "ar";
        } else if (max == cjkChars) {
            return "zh";
        }

        // Default to French if no clear winner
        return "fr";
    }

    /**
     * Check if text contains French-specific characters
     */
    private static boolean containsFrenchCharacters(String text) {
        return text.toLowerCase().matches(".*[éèêëàâäôöûüùïîç].*");
    }

    /**
     * Gets list of supported languages
     * @return Array of supported language codes
     */
    public static String[] getSupportedLanguages() {
        return new String[] {
            "en", // English
            "ar", // Arabic
            "fr", // French
            "de", // German
            "es", // Spanish
            "it", // Italian
            "pt", // Portuguese
            "ru", // Russian
            "zh", // Chinese
            "ja", // Japanese
            "ko", // Korean
            "nl", // Dutch
            "pl", // Polish
            "tr", // Turkish
            "vi"  // Vietnamese
        };
    }

    /**
     * Gets the language names map
     * @return Map of language codes to their full names
     */
    public static Map<String, String> getLanguageNames() {
        Map<String, String> languageNames = new HashMap<>();
        languageNames.put("en", "English");
        languageNames.put("ar", "Arabic");
        languageNames.put("fr", "French");
        languageNames.put("de", "German");
        languageNames.put("es", "Spanish");
        languageNames.put("it", "Italian");
        languageNames.put("pt", "Portuguese");
        languageNames.put("ru", "Russian");
        languageNames.put("zh", "Chinese");
        languageNames.put("ja", "Japanese");
        languageNames.put("ko", "Korean");
        languageNames.put("nl", "Dutch");
        languageNames.put("pl", "Polish");
        languageNames.put("tr", "Turkish");
        languageNames.put("vi", "Vietnamese");
        return languageNames;
    }
} 