package com.vdt_project1.loan_management.mapper;

import com.vdt_project1.loan_management.dto.request.DocumentRequest;
import com.vdt_project1.loan_management.dto.response.DocumentLoanApplicationDto;
import com.vdt_project1.loan_management.dto.response.DocumentResponse;
import com.vdt_project1.loan_management.entity.Document;
import com.vdt_project1.loan_management.entity.LoanApplication;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DocumentMapper {
    Document toEntity(DocumentRequest documentRequest);

    void updateEntityFromRequest(DocumentRequest documentRequest, @MappingTarget Document document);

    // Custom mapping logic to handle the conversion of Document to DocumentResponse
    @Mapping(target = "loanApplication", source = "document", qualifiedByName = "toLoanApplicationDto")
    DocumentResponse toResponse(Document document);

    /**
     * Maps a Document entity to a simplified LoanApplicationDto to prevent circular
     * references.
     */
    @Named("toLoanApplicationDto")
    default DocumentLoanApplicationDto toLoanApplicationDto(Document document) {
        if (document == null || document.getLoanApplication() == null) {
            return null;
        }

        LoanApplication loanApp = document.getLoanApplication();

        return DocumentLoanApplicationDto.builder()
                .id(loanApp.getId())
                .status(loanApp.getStatus() != null ? loanApp.getStatus().name() : null)
                .userId(loanApp.getUser() != null ? loanApp.getUser().getId() : null)
                .userEmail(loanApp.getUser() != null ? loanApp.getUser().getEmail() : null)
                .productId(loanApp.getLoanProduct() != null ? loanApp.getLoanProduct().getId() : null)
                .productName(loanApp.getLoanProduct() != null ? loanApp.getLoanProduct().getName() : null)
                .build();
    }
}
