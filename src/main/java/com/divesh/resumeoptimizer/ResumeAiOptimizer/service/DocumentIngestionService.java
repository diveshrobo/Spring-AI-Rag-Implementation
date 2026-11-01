package com.divesh.resumeoptimizer.ResumeAiOptimizer.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService
        implements CommandLineRunner {

    private final VectorStore vectorStore;

    private final Resource document;

    private final String vectorStoreFile;
    
    private final TokenTextSplitter textSplitter;
    
    private static final String DOCUMENT_PATH = "documents";

    public DocumentIngestionService(
            VectorStore vectorStore,
            @Value("${rag.document.location}")
            Resource document,
            @Value("${rag.vector-store.file}")
            String vectorStoreFile,
            TokenTextSplitter splitter) {

        this.vectorStore = vectorStore;
        this.document = document;
        this.vectorStoreFile = vectorStoreFile;
        this.textSplitter = splitter;
    }

    @Override
    public void run(String... args) throws Exception {
    	
    	File file = new File(vectorStoreFile);

        /*
         * Don't ingest again if vector store
         * already contains the document.
         */
    	if (file.exists() && file.length() > 0) {

            ((SimpleVectorStore) vectorStore).load(file);

            System.out.println(
                    "Vector store loaded from: "
                            + file.getAbsolutePath()
            );
        } else {
        	/*
             * VECTOR STORE DOES NOT EXIST
             * ---------------------------
             * Read → Chunk → Embed → Save
             */
            System.out.println(
                    "Vector store not found. Starting document ingestion..."
            );
            
        	ingest();
        }

    }

    private void ingest() throws IOException {
    	
    	
    	Resource[] resources =
                new PathMatchingResourcePatternResolver()
                        .getResources("classpath:" + DOCUMENT_PATH + "/*");

        List<Document> allChunks = new ArrayList<>();
        
        for (Resource resource : resources) {

            if (!resource.isFile()) {
                continue;
            }

            String filename = resource.getFilename();

            if (filename == null) {
                continue;
            }

            List<Document> documents = readDocument(resource);

            List<Document> chunks =
                    textSplitter.split(documents);

            allChunks.addAll(chunks);

            System.out.println(
                    "Ingested: " + filename +
                    " | Documents: " + documents.size() +
                    " | Chunks: " + chunks.size()
            );
        }
        
        if (!allChunks.isEmpty()) {
            vectorStore.add(allChunks);
            saveVectorStore();
        }

        System.out.println(
                "Total chunks added to vector store: "
                        + allChunks.size()
        );

//        TextReader reader =
//                new TextReader(document);
//
//        List<Document> documents =
//                reader.get();
//
//        TokenTextSplitter splitter =
//                TokenTextSplitter.builder()
//                        .withChunkSize(50)
//                        .withMaxNumChunks(200)
//                        .build();
//
//        List<Document> chunks =
//                splitter.split(documents);
//        
//        System.out.println("Original documents: " + documents.size());
//        System.out.println("Chunks created: " + chunks.size());
//
//        for (int i = 0; i < chunks.size(); i++) {
//            Document chunk = chunks.get(i);
//
//            System.out.println(
//                "CHUNK " + i +
//                " | ID: " + chunk.getId() +
//                " | LENGTH: " + chunk.getText().length()
//            );
//
//            System.out.println(chunk.getText());
//            System.out.println("--------------------------------");
//        }
//
//        vectorStore.add(chunks);
//
//        saveVectorStore();
//
//        System.out.println(
//                "RAG document ingestion completed. Chunks: "
//                        + chunks.size()
//        );
    }

    private void saveVectorStore() {

        if (vectorStore instanceof SimpleVectorStore simple) {

            File file =
                    new File(vectorStoreFile);

            file.getParentFile().mkdirs();

            simple.save(file);
        }
    }
    
    private List<Document> readDocument(Resource resource)
            throws IOException {

        String filename = resource.getFilename();

        String extension =
                filename.substring(filename.lastIndexOf('.') + 1)
                        .toLowerCase();

        return switch (extension) {

            case "txt" -> readTextFile(resource);

            case "pdf" -> readPdfFile(resource);

            case "docx" -> readDocxFile(resource);

            case "pptx" -> readPptxFile(resource);

            default -> {
                System.out.println(
                        "Skipping unsupported file: " + filename
                );
                yield Collections.emptyList();
            }
        };
    }
    
    private List<Document> readTextFile(Resource resource)
            throws IOException {

        String text = new String(
                resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8
        );

        return List.of(
                new Document(
                        text,
                        Map.of(
                                "source",
                                resource.getFilename(),
                                "type",
                                "txt"
                        )
                )
        );
    }
    
    private List<Document> readPdfFile(Resource resource)
            throws IOException {

        try (InputStream inputStream =
                     resource.getInputStream()) {

            try (PDDocument pdf =
                         Loader.loadPDF(inputStream.readAllBytes())) {

                PDFTextStripper stripper =
                        new PDFTextStripper();

                String text =
                        stripper.getText(pdf);

                return List.of(
                        new Document(
                                text,
                                Map.of(
                                        "source",
                                        resource.getFilename(),
                                        "type",
                                        "pdf"
                                )
                        )
                );
            }
        }
    }
    
    private List<Document> readDocxFile(Resource resource)
            throws IOException {

        try (InputStream inputStream =
                     resource.getInputStream();

             XWPFDocument document =
                     new XWPFDocument(inputStream)) {

            StringBuilder text = new StringBuilder();

            for (XWPFParagraph paragraph :
                    document.getParagraphs()) {

                text.append(paragraph.getText())
                        .append("\n");
            }

            return List.of(
                    new Document(
                            text.toString(),
                            Map.of(
                                    "source",
                                    resource.getFilename(),
                                    "type",
                                    "docx"
                            )
                    )
            );
        }
    }
    
    private List<Document> readPptxFile(Resource resource)
            throws IOException {

        try (InputStream inputStream =
                     resource.getInputStream();

             XMLSlideShow presentation =
                     new XMLSlideShow(inputStream)) {

            StringBuilder text = new StringBuilder();

            for (XSLFSlide slide :
                    presentation.getSlides()) {

                for (XSLFShape shape :
                        slide.getShapes()) {

                    if (shape instanceof XSLFTextShape textShape) {

                        text.append(textShape.getText())
                                .append("\n");
                    }
                }

                text.append("\n");
            }

            return List.of(
                    new Document(
                            text.toString(),
                            Map.of(
                                    "source",
                                    resource.getFilename(),
                                    "type",
                                    "pptx"
                            )
                    )
            );
        }
    }
}