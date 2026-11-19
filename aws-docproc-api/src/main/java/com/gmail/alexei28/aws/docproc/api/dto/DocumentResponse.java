package com.gmail.alexei28.aws.docproc.api.dto;

import com.gmail.alexei28.aws.docproc.domain.model.DocumentStatus;

import java.time.Instant;
import java.util.Map;

public record DocumentResponse(
        String id,
        DocumentStatus status,
        Map<String, Object> result,
        Instant createdAt
) {
}