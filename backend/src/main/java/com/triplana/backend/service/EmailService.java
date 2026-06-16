package com.triplana.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendVerificationEmail(String toEmail, String token) {
        String link = baseUrl + "/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verify your Triplana account");
        message.setText("Click the link below to verify your account:\n\n" + link
            + "\n\n This link expires in 24 hours.");

        mailSender.send(message);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String link = baseUrl + "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Reset your Triplana password");
        message.setText("Click the link below to reset your password:\n\n" + link
            + "\n\n This link expires in 1 hour.\n\nIf you did not request this, ignore this email.");

        mailSender.send(message);
    }

    public void sendEmailChangeVerification(String toEmail, String token) {
        String link = baseUrl + "/change-email/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Changing your Triplana email address");
        message.setText("Click the link below to change your email address:\n\n" + link
            + "\n\n This link expires in 24 hours.\n\nIf you did not request this, ignore this email.");

        mailSender.send(message);
    }

    public void sendEmailChangeConfirmation(String toEmail, String token) {
        String link = baseUrl + "/change-email/confirm?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Confirming your new Triplana email address");
        message.setText("Click the link below to confirm your new email address:\n\n" + link
            + "\n\n This link expires in 24 hours.\n\nIf you did not request this, ignore this email.");

        mailSender.send(message);
    }
}
