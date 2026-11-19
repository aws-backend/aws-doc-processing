package com.gmail.alexei28.aws.docproc.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSBatchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gmail.alexei28.aws.docproc.domain.model.DocumentMessage;
import com.gmail.alexei28.aws.docproc.domain.service.DocumentProcessor;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class DocumentLambdaHandler implements RequestHandler<SQSEvent, SQSBatchResponse> {

    private static final ConfigurableApplicationContext APPLICATION_CONTEXT =
            new SpringApplicationBuilder(LambdaApplication.class)
                    .web(WebApplicationType.NONE)
                    .profiles("aws")
                    .run();

    private static final DocumentProcessor PROCESSOR =
            APPLICATION_CONTEXT.getBean(DocumentProcessor.class);

    private static final ObjectMapper OBJECT_MAPPER =
            APPLICATION_CONTEXT.getBean(ObjectMapper.class);

    @Override
    public SQSBatchResponse handleRequest(
            SQSEvent event,
            Context context
    ) {

        List<SQSBatchResponse.BatchItemFailure> failures =
                new ArrayList<>();

        for (SQSEvent.SQSMessage message : event.getRecords()) {

            try {
                DocumentMessage documentMessage =
                        OBJECT_MAPPER.readValue(
                                message.getBody(),
                                DocumentMessage.class
                        );

                PROCESSOR.process(documentMessage.documentId());

            } catch (Exception exception) {

                failures.add(
                        new SQSBatchResponse.BatchItemFailure(
                                message.getMessageId()
                        )
                );

                context.getLogger().log(
                        "Processing failed: " + exception.getMessage()
                );
            }
        }
        return new SQSBatchResponse(failures);
    }
}