package com.gmail.alexei28.aws.docproc.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record CreateDocumentRequest(
        @NotNull
        @NotEmpty
        Map<String, Object> content) {
}