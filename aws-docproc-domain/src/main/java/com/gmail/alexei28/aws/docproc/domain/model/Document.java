package com.gmail.alexei28.aws.docproc.domain.model;

import java.time.Instant;
import java.util.Map;

public record Document(
        String id,
        Map<String, Object> content,
        DocumentStatus status,
        Map<String, Object> result,
        Instant createdAt,
        Instant updatedAt
) {
}