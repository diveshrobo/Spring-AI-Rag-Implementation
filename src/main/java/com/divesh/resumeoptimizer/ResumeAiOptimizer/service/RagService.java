package com.divesh.resumeoptimizer.ResumeAiOptimizer.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RagService {

    private final VectorStore vectorStore;

    private final int topK;

    private final double similarityThreshold;

    public RagService(
            VectorStore vectorStore,
            @Value("${rag.search.top-k}") int topK,
            @Value("${rag.search.similarity-threshold}")
            double similarityThreshold) {

        this.vectorStore = vectorStore;
        this.topK = topK;
        this.similarityThreshold =
                similarityThreshold;
    }

    public List<Document> search(
            String question) {

        SearchRequest request =
                SearchRequest.builder()
                        .query(question)
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build();

        return vectorStore.similaritySearch(
                request
        );
    }
}
