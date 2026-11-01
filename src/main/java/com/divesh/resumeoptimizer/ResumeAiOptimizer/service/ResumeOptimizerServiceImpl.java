package com.divesh.resumeoptimizer.ResumeAiOptimizer.service;

import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.config.ResumePromptBuilder;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeRequestDTO;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeResponseDTO;




@Component
public class ResumeOptimizerServiceImpl implements ResumeOptiomizerService{
	
	private final ChatClient chatClient;
	private final ResumePromptBuilder promptBuilder;
	
	public ResumeOptimizerServiceImpl (ChatClient.Builder chatClientBuilder , ResumePromptBuilder promptBuilder) {
		this.chatClient = chatClientBuilder.build();
		this.promptBuilder = promptBuilder;
	}

	@Override
	public String getResponse(String prompt) {
		return chatClient
                .prompt(prompt)
                .call()
                .content();
	}
	
	public String extractText(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            
            return text
                    .replace("\r", "")
                    .replace("\n", "\\n")
                    .replace("\t", " ")
                    .replace("\"", "\\\"")
                    .trim();
        }
    }
	
	
	 public ResumeResponseDTO optimizeResume(ResumeRequestDTO request) throws Exception {

		 try {
		        String userPrompt = promptBuilder.buildPrompt(request);

		        ResumeResponseDTO response =
		                chatClient
		                        .prompt()
		                        .system(ResumePromptBuilder.SYSTEM_PROMPT)
		                        .user(userPrompt)
		                        .call()
		                        .entity(ResumeResponseDTO.class);

		        if (response == null) {
		            throw new Exception("Empty response received from AI");
		        }

		        return response;

		    } catch (Exception ex) {
		        throw new Exception(
		                "Failed to process resume optimization request",
		                ex
		        );
		    }
	    }
	
	


}
