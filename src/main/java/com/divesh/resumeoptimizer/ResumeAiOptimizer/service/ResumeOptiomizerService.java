package com.divesh.resumeoptimizer.ResumeAiOptimizer.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeRequestDTO;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeResponseDTO;

@Service
public interface ResumeOptiomizerService {

	public String getResponse(String prompt);
	
	public String extractText(MultipartFile file) throws IOException;
	
	public ResumeResponseDTO optimizeResume(ResumeRequestDTO resumeRequestDTO) throws Exception;
}
