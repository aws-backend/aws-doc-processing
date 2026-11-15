package com.gmail.alexei28.aws.docproc.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentRepository;
import com.gmail.alexei28.aws.docproc.infrastructure.persistence.MongoDocumentRepository;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.bson.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class MongoConfiguration {

    @Bean(destroyMethod = "close")
    public com.mongodb.client.MongoClient mongoClient(Environment environment) {
        var uri = environment.getRequiredProperty(
                "spring.data.mongodb.uri"
        );

        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(uri))
                .build();

        return MongoClients.create(settings);
    }

    @Bean
    public DocumentRepository documentRepository(
            com.mongodb.client.MongoClient mongoClient,
            ObjectMapper objectMapper,
            Environment environment
    ) {
        var databaseName = environment.getRequiredProperty(
                "spring.data.mongodb.database"
        );

        var collection = mongoClient
                .getDatabase(databaseName)
                .getCollection("documents", Document.class);

        return new MongoDocumentRepository(collection, objectMapper);
    }
}