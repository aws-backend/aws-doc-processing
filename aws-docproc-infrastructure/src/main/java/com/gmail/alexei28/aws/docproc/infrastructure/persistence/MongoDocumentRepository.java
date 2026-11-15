package com.gmail.alexei28.aws.docproc.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.domain.model.Document;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MongoDocumentRepository implements DocumentRepository {
    private final MongoCollection<org.bson.Document> collection;
    private final ObjectMapper mapper;

    public MongoDocumentRepository(
            MongoCollection<org.bson.Document> collection,
            ObjectMapper mapper
    ) {
        this.collection = collection;
        this.mapper = mapper;
    }

    @Override
    public Document save(Document document) {
        var mongoDocument = new org.bson.Document()
                .append("_id", document.id().toString())
                .append("type", document.type().name())
                .append("content", document.content())
                .append("status", document.status().name())
                .append("createdAt", toDate(document.createdAt()))
                .append("updatedAt", toDate(document.updatedAt()))
                .append(
                        "processingEventId",
                        document.processingEventId() == null
                                ? null
                                : document.processingEventId().toString()
                );

        collection.insertOne(mongoDocument);

        return document;
    }

    @Override
    public Optional<Document> findById(String id) {
        var result = collection.find(Filters.eq("_id", id)).first();

        if (result == null) {
            return Optional.empty();
        }

        var data = new HashMap<String, Object>(result);

        // MongoDB _id -> Domain id
        data.put("id", data.remove("_id"));

        // BSON Date -> Java Instant
        data.put("createdAt", toInstant(data.get("createdAt")));
        data.put("updatedAt", toInstant(data.get("updatedAt")));

        return Optional.of(mapper.convertValue(data, Document.class));
    }

    @Override
    public boolean markProcessing(String id) {
        var result = collection.updateOne(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.eq("status", "PENDING")
                ),
                Updates.combine(
                        Updates.set("status", "PROCESSING"),
                        Updates.set("updatedAt", new Date())
                )
        );

        return result.getModifiedCount() == 1;
    }

    @Override
    public void complete(String id, Map<String, Object> result) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("_id", id),
                        Filters.ne("status", "COMPLETED")
                ),
                Updates.combine(
                        Updates.set("status", "COMPLETED"),
                        Updates.set("result", result),
                        Updates.set("updatedAt", new Date())
                )
        );
    }

    @Override
    public void fail(String id) {
        collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                        Updates.set("status", "FAILED"),
                        Updates.set("updatedAt", new Date())
                )
        );
    }

    private Date toDate(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    private Instant toInstant(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Date date) {
            return date.toInstant();
        }

        throw new IllegalArgumentException(
                "Unsupported MongoDB date type: " + value.getClass()
        );
    }
}
