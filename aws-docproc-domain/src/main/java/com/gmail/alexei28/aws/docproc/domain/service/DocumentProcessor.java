package com.gmail.alexei28.aws.docproc.domain.service;

import com.gmail.alexei28.aws.docproc.domain.model.*;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentStore;

import java.time.Instant;
import java.util.Map;

public class DocumentProcessor {
    private final DocumentStore documentStore;

    public DocumentProcessor(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    public void process(String documentId) {

        Document document = documentStore.findById(documentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Document not found: " + documentId
                        )
                );

        // Idempotency: повторно завершенный документ не обрабатываем.
        if (document.status() == DocumentStatus.COMPLETED) {
            return;
        }

        documentStore.update(new Document(
                document.id(),
                document.content(),
                DocumentStatus.PROCESSING,
                document.result(),
                document.createdAt(),
                Instant.now()
        ));

        try {
            Map<String, Object> result = processContent(document.content());

            documentStore.update(new Document(
                    document.id(),
                    document.content(),
                    DocumentStatus.COMPLETED,
                    result,
                    document.createdAt(),
                    Instant.now()
            ));

        } catch (Exception exception) {

            documentStore.update(new Document(
                    document.id(),
                    document.content(),
                    DocumentStatus.FAILED,
                    Map.of("error", exception.getMessage() == null
                            ? "Processing failed"
                            : exception.getMessage()),
                    document.createdAt(),
                    Instant.now()
            ));

            throw new RuntimeException(
                    "Document processing failed: " + documentId,
                    exception
            );
        }
    }

    private Map<String, Object> processContent(
            Map<String, Object> content
    ) {
        // Здесь будет реальная бизнес-логика.

        return Map.of(
                "valid", true,
                "processed", true
        );
    }
}