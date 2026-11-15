package com.gmail.alexei28.aws.docproc.contracts;

import java.time.Instant;
import java.util.UUID;

// eventId нужен для идентификации события, а documentId — для поиска документа.
public record DocumentProcessingEvent(
        UUID eventId,
        String documentId,
        Instant createdAt
) {}