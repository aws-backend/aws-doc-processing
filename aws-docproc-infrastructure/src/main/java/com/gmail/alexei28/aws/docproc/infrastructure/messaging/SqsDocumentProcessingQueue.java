package com.gmail.alexei28.aws.docproc.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.contracts.DocumentProcessingEvent;
import com.gmail.alexei28.aws.docproc.domain.port.DocumentProcessingQueue;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

public class SqsDocumentProcessingQueue implements DocumentProcessingQueue {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final String queueUrl;

    public SqsDocumentProcessingQueue(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }

    @Override
    public void publish(DocumentProcessingEvent event) {
        try {
            var message = objectMapper.writeValueAsString(event);
            var request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(message)
                    .build();
            sqsClient.sendMessage(request);
        } catch (Exception exception) {
            throw new IllegalStateException("Failed to publish document processing event", exception);
        }
    }
}
