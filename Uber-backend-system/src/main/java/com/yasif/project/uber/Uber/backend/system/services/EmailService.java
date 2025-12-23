package com.yasif.project.uber.Uber.backend.system.services;

public interface EmailService {

     void sendEmail(String toEmail,String subject,String body);

     void sendEmail(String[] toEmail,String subject,String body);

}
