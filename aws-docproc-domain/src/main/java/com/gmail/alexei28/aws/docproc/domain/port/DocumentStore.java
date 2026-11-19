package com.gmail.alexei28.aws.docproc.domain.port;

import com.gmail.alexei28.aws.docproc.domain.model.Document;

import java.util.Optional;

public interface DocumentStore {

    Document save(Document document);

    Optional<Document> findById(String id);

    void update(Document document);
}