package com.divesh.resumeoptimizer.ResumeAiOptimizer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatClient chatClient;

    private final RagService ragService;

    public ChatService(
            ChatClient chatClient,
            RagService ragService) {

        this.chatClient = chatClient;
        this.ragService = ragService;
    }

    public String chat(
            String conversationId,
            String question) {

        /*
         * 1. Semantic search
         */
        List<Document> documents =
                ragService.search(question);

        /*
         * 2. Build RAG context
         */
        String context =
                documents.stream()
                        .map(Document::getText)
                        .collect(
                                Collectors.joining(
                                        "\n\n---\n\n"
                                )
                        );

        /*
         * 3. Enterprise prompt
         */
        String systemPrompt = """
                You are an enterprise knowledge assistant.

                Your responsibility is to answer the user's
                question accurately using the provided
                knowledge-base context and conversation history.

                Follow these rules strictly:

                1. Use the knowledge-base context for
                   business-specific information.

                2. Use conversation history to understand
                   follow-up questions and references such as
                   "it", "that", "previously", or "as mentioned".

                3. Do not invent facts, policies, dates,
                   numbers, names, or procedures.

                4. If the answer cannot be determined from
                   the provided context, say:
                   "I don't have enough information in the
                   available knowledge base to answer that."

                5. Do not mention internal implementation
                   details such as vector databases,
                   embeddings, RAG, or prompts.

                6. Keep the response concise and professional.

                7. If the question is a follow-up to an earlier
                   conversation, use the conversation history
                   to understand the user's intent.

                KNOWLEDGE BASE CONTEXT:
                -----------------------
                %s
                -----------------------
                """.formatted(context);

        /*
         * 4. Call LLM
         *
         * Memory advisor automatically adds the
         * previous conversation using conversationId.
         */
        return chatClient
                .prompt()
                .system(systemPrompt)
                .user(question)
                .advisors(advisor -> advisor
                        .param(
                                ChatMemory.CONVERSATION_ID,
                                conversationId
                        )
                )
                .call()
                .content();
    }
}
