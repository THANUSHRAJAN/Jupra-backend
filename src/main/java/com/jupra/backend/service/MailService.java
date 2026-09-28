package com.jupra.backend.service;

import com.jupra.backend.exception.ApiException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtpEmail(String to, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Your JUPRA admin verification code");
            helper.setText(
                    "<div style=\"font-family:Arial,sans-serif;font-size:15px;color:#0b1230;\">"
                            + "<p>Use the code below to continue signing in to the JUPRA founder dashboard.</p>"
                            + "<p style=\"font-size:32px;font-weight:700;letter-spacing:8px;color:#0A2FC0;margin:20px 0;\">" + otp + "</p>"
                            + "<p>This code expires in 10 minutes. If you did not request this, you can safely ignore this email.</p>"
                            + "</div>",
                    true
            );
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new ApiException(500, "Failed to send the verification email. Please try again.");
        }
    }
}
