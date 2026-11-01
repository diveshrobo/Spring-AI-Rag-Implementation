package com.divesh.resumeoptimizer.ResumeAiOptimizer.config;

import org.springframework.ai.chat.client.ChatClientCustomizer;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class AiConfig {

    @Value("${rag.vector-store.file}")
    private String vectorStoreFile;

    @Bean
    public VectorStore vectorStore(
            EmbeddingModel embeddingModel) {

        SimpleVectorStore vectorStore =
                SimpleVectorStore.builder(embeddingModel)
                        .build();

        File file = new File(vectorStoreFile);

        if (file.exists()) {
            vectorStore.load(file);
        }

        return vectorStore;
    }
    
    @Bean
    public TokenTextSplitter textSplitter() {

    	return
                TokenTextSplitter.builder()
                        .withChunkSize(50)
                        .withMaxNumChunks(200)
                        .build();
    }
    
    @Bean
    public ChatClientCustomizer loggingCustomizer() {
        return builder -> builder.defaultAdvisors(
            new SimpleLoggerAdvisor()
        );
    }
}