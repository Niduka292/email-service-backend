package com.emailapp.emailservice.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

import java.util.List;

@Service
public class AIService {

    private final Client geminiClient;
    private static final String MODEL_NAME = "gemini-2.5-flash";

    public AIService(@Value("${gemini.api.key}") String apiKey, View error) {
        this.geminiClient = Client.builder().apiKey(apiKey).build();
    }

    public String summarizeMail(String emailContent){

        String prompt = String.format(
                "Summarize the following email for key action items and main topic. " +
                        "Respond only with a concise, easy-to-read paragraph. Email content: \n\n%s",
                emailContent
        );

        try{

            GenerateContentResponse response = geminiClient.models.
                    generateContent(
                            MODEL_NAME,
                            prompt,
                            null
                    );

            if(response != null && response.text() != null){
                return response.text().trim();
            }else{
                return "AI summary failed to generate content";
            }
        }catch(Exception e){
            System.err.println("Gemini API error "+e.getMessage());
            return "Failed to generate summary due to an internal API error.";
        }
    }

    public List<String> generateSmartReplies(String emailContent){
        String prompt = String.format(
                "Based on the following email, generate exactly 3 short, helpful, and professional " +
                        "reply options (max 10 words each). " +
                        "Return the response ONLY as a JSON array of strings. " +
                        "Example: [\"Sounds good!\", \"I'll check and let you know.\", \"Can we reschedule?\"] " +
                        "Email content: \n\n%s",
                emailContent
        );

        try{
            GenerateContentResponse response = geminiClient.models.generateContent(MODEL_NAME,prompt,null);
            String rawText = response.text().trim();

            String cleanJson = rawText.replace("```json", "").replace("```", "").trim();

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(cleanJson, new TypeReference<List<String>>() {});
        }catch(Exception e){
            System.err.println("Smart reply error: "+e.getMessage());
            return List.of("reply 1", "reply 2", "reply 3"); // Fallback
        }
    }

}
