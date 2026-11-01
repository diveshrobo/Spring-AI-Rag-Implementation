package com.divesh.resumeoptimizer.ResumeAiOptimizer.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeRequestDTO;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeResponseDTO;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.service.ResumeOptiomizerService;


@RestController
@RequestMapping("/api")
public class ResumeOptimizationController {
	
	public final ResumeOptiomizerService resumeOptiomizerService;
	
	public ResumeOptimizationController(ResumeOptiomizerService resumeOptiomizerService) {
		this.resumeOptiomizerService = resumeOptiomizerService;
	}
	

	@GetMapping("/getResponse")
	public ResponseEntity<String> getResponse(@RequestParam("prompt") String propmt) {
		String response = this.resumeOptiomizerService.getResponse(propmt);
		return new ResponseEntity<String>(response,HttpStatus.OK);
	}
	
	 @PostMapping("/uploadResume")
	    public ResponseEntity<String> parseResume(@RequestParam("file") MultipartFile file) {
	        try {
	            String text = resumeOptiomizerService.extractText(file);
	            return ResponseEntity.ok(text);
	        } catch (Exception e) {
	            return ResponseEntity.internalServerError().body("Parsing failed");
	        }
	    }
	 
	 @PostMapping("/optimize")
	    public ResponseEntity<ResumeResponseDTO> optimizeResume(
	            @Valid @RequestBody ResumeRequestDTO request) throws Exception {

		 ResumeResponseDTO response =
				 resumeOptiomizerService.optimizeResume(request);

	        return ResponseEntity.status(HttpStatus.OK).body(response);
	    }
}
