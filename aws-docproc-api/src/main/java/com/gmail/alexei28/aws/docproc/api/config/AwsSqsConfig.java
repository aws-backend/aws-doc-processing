package com.gmail.alexei28.aws.docproc.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

import java.net.URI;

/*
    В local используем тестовые credentials LocalStack.
    В aws — IAM Role ECS Task Execution Role здесь не подходит для вызовов SQS: приложению нужна именно ECS Task Role
    с разрешением sqs:SendMessage.
*/
@Configuration
public class AwsSqsConfig {

    @Bean
    public SqsClient sqsClient(
            @Value("${app.aws.region}") String region,
            @Value("${app.aws.endpoint:}") String endpoint
    ) {
        SqsClientBuilder builder = SqsClient.builder()
                .region(Region.of(region));
        if (!endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(
                                            "test",
                                            "test"
                                    )
                            )
                    );
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        return builder.build();
    }
}