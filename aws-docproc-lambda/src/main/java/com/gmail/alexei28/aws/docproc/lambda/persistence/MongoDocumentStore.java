package com.gmail.alexei28.aws.docproc.lambda.persistence;

import com.gmail.alexei28.aws.docproc.domain.model.Document;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentStore;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MongoDocumentStore implements DocumentStore {
    private final MongoDocumentRepository repository;

    public MongoDocumentStore(MongoDocumentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Document save(Document document) {
        return toDomain(repository.save(toEntity(document)));
    }

    @Override
    public Optional<Document> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public void update(Document document) {
        repository.save(toEntity(document));
    }

    private MongoDocument toEntity(Document document) {
        return new MongoDocument(
                document.id(),
                document.content(),
                document.status(),
                document.result(),
                document.createdAt(),
                document.updatedAt()
        );
    }

    private Document toDomain(MongoDocument entity) {
        return new Document(
                entity.id(),
                entity.content(),
                entity.status(),
                entity.result(),
                entity.createdAt(),
                entity.updatedAt()
        );
    }
}