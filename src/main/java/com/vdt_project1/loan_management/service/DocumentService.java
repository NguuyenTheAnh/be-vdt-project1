package com.vdt_project1.loan_management.service;

import com.vdt_project1.loan_management.dto.request.DocumentRequest;
import com.vdt_project1.loan_management.dto.request.DocumentUpdateRequest;
import com.vdt_project1.loan_management.dto.response.DocumentResponse;
import com.vdt_project1.loan_management.entity.Document;
import com.vdt_project1.loan_management.entity.LoanApplication;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.mapper.DocumentMapper;
import com.vdt_project1.loan_management.repository.DocumentRepository;
import com.vdt_project1.loan_management.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class DocumentService {
    LoanApplicationRepository loanApplicationRepository;
    DocumentRepository documentRepository;
    DocumentMapper documentMapper;

    private LoanApplication findLoanApplicationById(Long id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_APPLICATION_NOT_FOUND));
    }

    @Transactional
    public DocumentResponse createDocument(DocumentRequest request){
        log.info("Creating new document for loan application ID: {}", request.getApplicationId());

        LoanApplication loanApplication = findLoanApplicationById(request.getApplicationId());

        Document document =  documentMapper.toEntity(request);
        document.setLoanApplication(loanApplication);
        document.setUploadedAt(LocalDateTime.now());

        document = documentRepository.save(document);
        return documentMapper.toResponse(document);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAllDocuments(Long applicationId ,Pageable pageable){
        if(applicationId == null) {
            log.info("Fetching all documents without application ID");
            return documentRepository.findAll(pageable).map(documentMapper::toResponse);
        } else {
            log.info("Fetching documents for loan application ID: {}", applicationId);
            return documentRepository.findByLoanApplicationId(applicationId, pageable).map(documentMapper::toResponse);
        }
    }

    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        log.info("Fetching document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        return documentMapper.toResponse(document);
    }

    @Transactional
    public void updateDocumentFile(DocumentUpdateRequest request){
        String fileName = request.getFileName();
        Long applicationId =  request.getApplicationId();
        String documentType = request.getDocumentType();

        Document document = documentRepository.findByLoanApplicationIdAndDocumentType(applicationId, documentType);
        document.setFileName(fileName);
        document.setUploadedAt(LocalDateTime.now());
        documentRepository.save(document);
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, DocumentRequest request) {
        log.info("Updating document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));

        documentMapper.updateEntityFromRequest(request, document);
        document = documentRepository.save(document);
        return documentMapper.toResponse(document);
    }

    @Transactional
    public void deleteDocumentById(Long id) {
        log.info("Deleting document with ID: {}", id);
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.DOCUMENT_NOT_FOUND));
        documentRepository.delete(document);
    }


}
