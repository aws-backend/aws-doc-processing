package com.gmail.alexei28.aws.docproc.domain.service;


import com.gmail.alexei28.aws.docproc.contracts.DocumentProcessingEvent;
import com.gmail.alexei28.aws.docproc.domain.model.Document;
import com.gmail.alexei28.aws.docproc.domain.model.DocumentStatus;
import com.gmail.alexei28.aws.docproc.domain.model.DocumentType;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentProcessingQueue;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentRepository;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class DocumentService {
    private final DocumentRepository repository;
    private final DocumentProcessingQueue queue;

    public DocumentService(DocumentRepository repository, DocumentProcessingQueue queue) {
        this.repository = repository;
        this.queue = queue;
    }

    public Document create(DocumentType type, Map<String, Object> content) {
        var now = Instant.now();
        var id = UUID.randomUUID().toString();
        var eventId = UUID.randomUUID();
        var document = new Document(id, type, content, DocumentStatus.PENDING, null, now, now, eventId);

        repository.save(document);
        // Между repository.save() и queue.publish() существует окно отказа.
        // Если процесс завершится после сохранения документа, но до отправки сообщения, документ останется в PENDING.
        // In production should use pattern TransactionOutbox.
        queue.publish(new DocumentProcessingEvent(eventId, id, now));

        return document;
    }
}
