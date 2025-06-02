package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.dto.response.UploadFileResponse;
import com.vdt_project1.loan_management.exception.AppException;
import com.vdt_project1.loan_management.exception.ErrorCode;
import com.vdt_project1.loan_management.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("files")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class FileController {
    FileService fileService;

    @PostMapping("/upload")
    public ApiResponse<UploadFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            UploadFileResponse response = fileService.storeFile(file);
            return ApiResponse.<UploadFileResponse>builder()
                    .data(response)
                    .build();
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new AppException(ErrorCode.SERVER_ERROR);
        }
    }
}
