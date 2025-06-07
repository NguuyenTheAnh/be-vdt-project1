package com.vdt_project1.loan_management.controller;

import com.vdt_project1.loan_management.dto.response.ApiResponse;
import com.vdt_project1.loan_management.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("email")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class EmailController {

     EmailService emailService;

    @PostMapping("/send")
    public ApiResponse<String> sendEmail() throws MessagingException {
        emailService.sendHtmlTemplateEmail(
                "anhnguyenthe29112004@gmail.com",
                "Xác thực tài khoản - LoanConv",
                "Nguyễn Thế Anh",
                "123456"
        );
        return ApiResponse.<String>builder()
                .data("Email sent successfully")
                .build();
    }
}
