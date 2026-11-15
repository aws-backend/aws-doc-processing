package com.gmail.alexei28.aws.docproc.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record Document(
        String id,
        DocumentType type,
        Map<String, Object> content,
        DocumentStatus status,
        Map<String, Object> result,
        Instant createdAt,
        Instant updatedAt,
        UUID processingEventId
) {}
