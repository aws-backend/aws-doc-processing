package com.gmail.alexei28.aws.docproc.lambda;

import com.gmail.alexei28.aws.docproc.domain.port.DocumentStore;
import com.gmail.alexei28.aws.docproc.domain.service.DocumentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentProcessorService {

    @Bean
    public DocumentProcessor documentProcessor(DocumentStore documentStore) {
        return new DocumentProcessor(documentStore);
    }
}