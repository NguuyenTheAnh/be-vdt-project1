package com.vdt_project1.loan_management.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class EmailService {

    final JavaMailSender mailSender;
    final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.from:test.anhnguyenthe29112004@gmail.com}")
    String fromEmail;

    public void sendHtmlTemplateEmail(String to, String subject, String name, String verificationCode)
            throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Tạo nội dung HTML từ template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("verificationCode", verificationCode);
        String htmlContent = templateEngine.process("email-template", context);

        helper.setFrom(fromEmail); // Sử dụng email từ cấu hình
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }

    public void sendDisbursementEmail(String to, String name, String subject,
            Double amount, Double totalDisbursed, Double totalAmount,
            String transactionDate, Long applicationId, String notes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Tính toán các giá trị cần thiết
        Double remainingAmount = totalAmount - totalDisbursed;
        Double progressPercentage = (totalDisbursed / totalAmount) * 100;
        boolean isFullyDisbursed = remainingAmount <= 0.01; // Xử lý làm tròn

        // Tạo nội dung HTML từ template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("amount", amount);
        context.setVariable("totalDisbursed", totalDisbursed);
        context.setVariable("totalAmount", totalAmount);
        context.setVariable("remainingAmount", remainingAmount);
        context.setVariable("progressPercentage", Math.round(progressPercentage));
        context.setVariable("isFullyDisbursed", isFullyDisbursed);
        context.setVariable("transactionDate", transactionDate);
        context.setVariable("applicationId", applicationId);
        context.setVariable("notes", notes);
        String htmlContent = templateEngine.process("disburse-template", context);

        helper.setFrom(fromEmail); // Sử dụng email từ cấu hình
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }

    public void sendApplicationResultEmail(String to, String name, String subject,
            String status, String statusText, Long applicationId, Long amount,
            String productName, String internalNotes) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Tạo nội dung HTML từ template
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("status", status);
        context.setVariable("statusText", statusText);
        context.setVariable("applicationId", applicationId);
        context.setVariable("amount", amount);
        context.setVariable("productName", productName);
        context.setVariable("internalNotes", internalNotes);
        String htmlContent = templateEngine.process("application-result-template", context);

        helper.setFrom(fromEmail); // Sử dụng email từ cấu hình
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true = HTML

        mailSender.send(message);
    }
}
