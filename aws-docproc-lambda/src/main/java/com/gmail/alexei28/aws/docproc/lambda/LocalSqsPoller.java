package com.gmail.alexei28.aws.docproc.lambda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.domain.model.DocumentMessage;
import com.gmail.alexei28.aws.docproc.domain.service.DocumentProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

@Component
@Profile("local")
public class LocalSqsPoller implements CommandLineRunner {
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;
    private final DocumentProcessor processor;
    private final String queueUrl;

    public LocalSqsPoller(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            DocumentProcessor processor,
            @Value("${app.sqs.queue-url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.processor = processor;
        this.queueUrl = queueUrl;
    }

    @Override
    public void run(String... args) {
        while (!Thread.currentThread().isInterrupted()) {
            ReceiveMessageResponse response = sqsClient.receiveMessage(
                    ReceiveMessageRequest.builder()
                            .queueUrl(queueUrl)
                            .maxNumberOfMessages(10)
                            .waitTimeSeconds(10)
                            .visibilityTimeout(30)
                            .build()
            );

            for (Message message : response.messages()) {
                try {
                    DocumentMessage documentMessage =
                            objectMapper.readValue(
                                    message.body(),
                                    DocumentMessage.class
                            );

                    processor.process(documentMessage.documentId());

                    sqsClient.deleteMessage(
                            DeleteMessageRequest.builder()
                                    .queueUrl(queueUrl)
                                    .receiptHandle(message.receiptHandle())
                                    .build()
                    );

                } catch (Exception exception) {
                    System.err.println("Document processing failed: " + exception.getMessage());
                    // Не удаляем сообщение.
                    // SQS повторит доставку после Visibility Timeout.
                }
            }
        }
    }
}