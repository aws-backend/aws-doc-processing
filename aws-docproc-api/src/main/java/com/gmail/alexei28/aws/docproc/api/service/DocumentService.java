package com.gmail.alexei28.aws.docproc.api.service;

import com.gmail.alexei28.aws.docproc.api.dto.*;
import com.gmail.alexei28.aws.docproc.api.messaging.SqsDocumentPublisher;
import com.gmail.alexei28.aws.docproc.domain.model.*;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentStore;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class DocumentService {
    private final DocumentStore documentStore;
    private final SqsDocumentPublisher publisher;

    public DocumentService(DocumentStore documentStore, SqsDocumentPublisher publisher) {
        this.documentStore = documentStore;
        this.publisher = publisher;
    }

    public DocumentResponse create(CreateDocumentRequest request) {
        Instant now = Instant.now();
        Document document = new Document(
                UUID.randomUUID().toString(),
                request.content(),
                DocumentStatus.PENDING,
                null,
                now,
                now
        );

        documentStore.save(document);
        // In production here should use TransactionOutbox
        publisher.publish(document.id());
        return toResponse(document);
    }

    public DocumentResponse get(String id) {
        Document document = documentStore.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Document not found: " + id
                        )
                );
        return toResponse(document);
    }

    private DocumentResponse toResponse(Document document) {
        return new DocumentResponse(
                document.id(),
                document.status(),
                document.result(),
                document.createdAt()
        );
    }
}