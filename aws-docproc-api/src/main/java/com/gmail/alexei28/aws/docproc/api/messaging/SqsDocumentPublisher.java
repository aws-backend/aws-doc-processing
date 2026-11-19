package com.gmail.alexei28.aws.docproc.api.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.domain.model.DocumentMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Component
public class SqsDocumentPublisher {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsDocumentPublisher(SqsClient sqsClient, ObjectMapper objectMapper, @Value("${app.sqs.queue-url}") String queueUrl) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    public void publish(String documentId) {
        try {
            String message = objectMapper.writeValueAsString(
                    new DocumentMessage(documentId)
            );

            sqsClient.sendMessage(
                    SendMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .messageBody(message)
                            .build()
            );
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Cannot serialize document message", exception);
        }
    }
}