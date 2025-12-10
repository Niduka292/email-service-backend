package com.emailapp.emailservice.service;


import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.View;

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

}
