package com.divesh.resumeoptimizer.ResumeAiOptimizer.dto;

public record ErrorResponseDTO(
		 int status,
	     String error,
	     String message,
	     long timestamp) {

}
