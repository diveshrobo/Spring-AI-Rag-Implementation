package com.divesh.resumeoptimizer.ResumeAiOptimizer.config;

import org.springframework.stereotype.Component;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ResumeRequestDTO;

@Component
public class ResumePromptBuilder {
	
	public String buildPrompt(ResumeRequestDTO request) {

        return """
                Analyze the candidate resume against the job description.

                Job Title:
                %s

                Company:
                %s

                Job Description:
                %s

                Candidate Resume:
                %s

                Instructions:
                - Compare technical skills
                - Compare experience relevance
                - Compare project relevance
                - Identify ATS keyword gaps
                - Rewrite professional summary
                - Give recruiter recommendation
                """
                .formatted(
                        request.jobTitle(),
                        request.company(),
                        request.jobDescription(),
                        request.candidateResume()
                );
    }
	
	public static final String SYSTEM_PROMPT = """
            You are an expert ATS resume analyzer and senior technical recruiter.

            Your task is to compare a candidate resume against a job description.

            STRICT RULES:
            1. Return ONLY valid JSON
            2. No markdown
            3. No explanation outside JSON
            4. ATS score must be between 0 and 100
            5. Match skills exactly
            6. Missing information should be null

            REQUIRED JSON FORMAT:
            {
              "candidateName": "string",
              "jobTitle": "string",
              "atsScore": 0,
              "matchedSkills": ["string"],
              "missingSkills": ["string"],
              "experienceGap": "string",
              "summarySuggestions": "string",
              "recruiterFeedback": "string",
              "recommendation": "SHORTLIST | UPSKILL | REJECT"
            }
            """;

}
