package com.yasif.project.uber.Uber.backend.system.services.Impl;

import com.yasif.project.uber.Uber.backend.system.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            // Creates a simple email message object.
            // Used for sending plain text emails.
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            // Sets the recipient email address.
            simpleMailMessage.setTo(toEmail);

            // Sets the email subject line.
            simpleMailMessage.setSubject(subject);

            // Sets the email body content.
            simpleMailMessage.setText(body);

            // Sends the email using JavaMailSender.
            javaMailSender.send(simpleMailMessage);

            // Logs successful email delivery.
            log.info("Email sent successfully");

        } catch (Exception e) {

            // Handles any exception during email sending.
            // Prevents application crash due to email failures.
            log.info("Cannot send email: " + e.getMessage());
        }
    }


    @Override
    public void sendEmail(String[] toEmail, String subject, String body) {

        try {
            // Creates a simple email message object.
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            // Sets multiple recipient email addresses.
            simpleMailMessage.setTo(toEmail);

            // Adds a BCC recipient for monitoring or auditing purposes.
            simpleMailMessage.setBcc("callofcoder@gmail.com");

            // Sets the email subject line.
            simpleMailMessage.setSubject(subject);

            // Sets the email body content.
            simpleMailMessage.setText(body);

            // Sends the email using JavaMailSender.
            javaMailSender.send(simpleMailMessage);

            // Logs successful email delivery.
            log.info("Email sent successfully");

        } catch (Exception e) {

            // Handles any exception during email sending.
            // Ensures graceful degradation if mail server is unavailable.
            log.info("Cannot send email: " + e.getMessage());
        }
    }
}

