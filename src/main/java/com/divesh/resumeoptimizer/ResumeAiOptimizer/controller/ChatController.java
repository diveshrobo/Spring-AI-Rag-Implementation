package com.divesh.resumeoptimizer.ResumeAiOptimizer.controller;


import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.divesh.resumeoptimizer.ResumeAiOptimizer.dto.ChatRequest;
import com.divesh.resumeoptimizer.ResumeAiOptimizer.service.ChatService;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(
            ChatService chatService) {

        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<String> chat(
            @Valid @RequestBody ChatRequest request) {

        String response =
                chatService.chat(
                        request.conversationId(),
                        request.message()
                );

        return ResponseEntity.ok(response);
    }
}
