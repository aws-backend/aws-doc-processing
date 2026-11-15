package com.gmail.alexei28.aws.docproc.domain.port;

import com.gmail.alexei28.aws.docproc.contracts.DocumentProcessingEvent;

public interface DocumentProcessingQueue {

    void publish(DocumentProcessingEvent event);
}
