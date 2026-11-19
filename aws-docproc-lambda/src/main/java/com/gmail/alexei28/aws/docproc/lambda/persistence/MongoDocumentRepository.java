package com.gmail.alexei28.aws.docproc.lambda.persistence;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoDocumentRepository extends MongoRepository<MongoDocument, String> {
}