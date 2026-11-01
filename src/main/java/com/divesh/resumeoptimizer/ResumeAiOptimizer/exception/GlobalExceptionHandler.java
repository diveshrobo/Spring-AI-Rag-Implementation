package com.divesh.resumeoptimizer.ResumeAiOptimizer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ErrorResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {

	    ErrorResponseDTO response = new ErrorResponseDTO(
	            HttpStatus.INTERNAL_SERVER_ERROR.value(),
	            "INTERNAL_SERVER_ERROR",
	            ex.getMessage(),
	            System.currentTimeMillis()
	    );

	    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
