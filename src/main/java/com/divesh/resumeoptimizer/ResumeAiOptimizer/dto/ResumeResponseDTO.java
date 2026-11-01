package com.divesh.resumeoptimizer.ResumeAiOptimizer.dto;

import java.util.List;

public record ResumeResponseDTO (
		String candidateName,
        String jobTitle,
        Integer atsScore,
        List<String> matchedSkills,
        List<String> missingSkills,
        String experienceGap,
        String summarySuggestions,
        String recruiterFeedback,
        String recommendation
        ) {

}
