package com.gmail.alexei28.aws.docproc.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.infrastructure.messaging.SqsDocumentProcessingQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.net.URI;

/*
   Здесь нет AWS Access Key и Secret Key в коде.
    - Локально AWS SDK получает credentials через стандартную цепочку провайдеров.
    - В ECS используются временные credentials Task Role.
    - Lambda использует временные credentials Execution Role.
*/
@Configuration
public class AwsConfiguration {

    @Bean(destroyMethod = "close")
    public SqsClient sqsClient(Environment environment) {
        var region = Region.of(environment.getRequiredProperty("aws.sqs.region"));
        var builder = SqsClient.builder().region(region);
        var endpoint = environment.getProperty("aws.sqs.endpoint");
        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }
        return builder.build();
    }

    @Bean
    public SqsDocumentProcessingQueue documentProcessingQueue(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            Environment environment
    ) {
        return new SqsDocumentProcessingQueue(
                sqsClient,
                objectMapper,
                environment.getRequiredProperty("aws.sqs.queue-url")
        );
    }
}
