package com.gmail.alexei28.aws.docproc.lambda.persistence;

import com.gmail.alexei28.aws.docproc.domain.model.DocumentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "documents")
public record MongoDocument(
        @Id String id,
        Map<String, Object> content,
        DocumentStatus status,
        Map<String, Object> result,
        Instant createdAt,
        Instant updatedAt
) {
}