package com.gmail.alexei28.aws.docproc.domain.port;

import com.gmail.alexei28.aws.docproc.domain.model.Document;

import java.util.Map;
import java.util.Optional;

public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(String id);
    boolean markProcessing(String id);
    void complete(String id, Map<String, Object> result);
    void fail(String id);
}