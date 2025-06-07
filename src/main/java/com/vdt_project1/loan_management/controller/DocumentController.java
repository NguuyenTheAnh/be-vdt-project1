package com.vdt_project1.loan_management.controller;


import com.vdt_project1.loan_management.dto.request.DocumentRequest;
import com.vdt_project1.loan_management.dto.request.DocumentUpdateRequest;
import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.DocumentResponse;
import com.vdt_project1.loan_management.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("documents")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DocumentController {
    DocumentService documentService;

    @PostMapping
    @PreAuthorize("hasAuthority('POST_DOCUMENTS_CREATE')")
    public ApiResponse<DocumentResponse> createDocument(@Valid @RequestBody DocumentRequest request) {
        log.info("Creating a new document");
        DocumentResponse response = documentService.createDocument(request);
        return ApiResponse.<DocumentResponse>builder()
                .data(response)
                .build();
    }

    @GetMapping("/application")
    @PreAuthorize("hasAuthority('GET_DOCUMENTS_BY_APPLICATION_ID')")
    public ApiResponse<Page<DocumentResponse>> getDocument(
            @RequestParam Long applicationId,
            Pageable pageable
    ) {
      return ApiResponse.<Page<DocumentResponse>>builder()
                .data(documentService.getAllDocuments(applicationId, pageable))
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('GET_DOCUMENTS_BY_ID')")
    public ApiResponse<DocumentResponse> getDocumentById(@PathVariable Long id) {
        log.info("Fetching document with ID: {}", id);
        DocumentResponse response = documentService.getDocumentById(id);
        return ApiResponse.<DocumentResponse>builder()
                .data(response)
                .build();
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('PATCH_DOCUMENTS_UPDATE_FILE_CONTENT')")
    public ApiResponse<Void> updateDocumentFle(@RequestBody DocumentUpdateRequest request) {
        log.info("Updating document file with request: {}", request);
        documentService.updateDocumentFile(request);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('PATCH_DOCUMENTS_UPDATE_METADATA_BY_ID')")
    public ApiResponse<DocumentResponse> updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody DocumentRequest request
    ) {
        log.info("Updating document with ID: {}", id);
        DocumentResponse response = documentService.updateDocument(id, request);
        return ApiResponse.<DocumentResponse>builder()
                .data(response)
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_DOCUMENTS_BY_ID')")
    public ApiResponse<Void> deleteDocument(@PathVariable Long id) {
        log.info("Deleting document with ID: {}", id);
        documentService.deleteDocumentById(id);
        return ApiResponse.<Void>builder()
                .data(null)
                .build();
    }

}
