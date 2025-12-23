package com.yasif.project.uber.Uber.backend.system;

import com.yasif.project.uber.Uber.backend.system.services.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UberBackendSystemApplicationTests {

	@Autowired
// Injects the EmailService bean into the test class.
// Allows testing of email-sending functionality.
	private EmailService emailService;


	@Test
// Test case to verify email sending to a single recipient.
	void contextLoads() {

		// Sends a test email to one email address.
		// Used to validate basic email configuration and connectivity.
		emailService.sendEmail(
				"fefixep998@nctime.com",
				"This is sample subject of email",
				"This is sample body message of email"
		);
	}
	@Test
// Test case to verify email sending to multiple recipients.
	void sendEmailsToMultipleUsers() {

		// Array of email addresses to receive the email.
		String[] emails = {
				"yasiffkhan@gmail.com",
				"yasiffkhan4@gmail.com",
				"callofcoder@gmail.com",
				"cryptoorbitkaito@gmail.com",
				"fefixep998@nctime.com"
		};

		// Sends a single email to multiple recipients.
		// Ensures bulk email functionality works as expected.
		emailService.sendEmail(
				emails,
				"This is sample subject to send message to multiple users",
				"And this is the sample body message to multiple users"
		);
	}


}
