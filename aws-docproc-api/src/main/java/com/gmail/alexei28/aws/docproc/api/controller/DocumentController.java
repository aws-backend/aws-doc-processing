package com.gmail.alexei28.aws.docproc.api.controller;

import com.gmail.alexei28.aws.docproc.api.dto.*;
import com.gmail.alexei28.aws.docproc.api.service.DocumentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DocumentResponse create(@Valid @RequestBody CreateDocumentRequest request) {
        return documentService.create(request);
    }

    @GetMapping("/{id}")
    public DocumentResponse get(@PathVariable String id) {
        return documentService.get(id);
    }
}