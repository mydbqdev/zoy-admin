package com.integration.zoy.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.utils.Email;

@Service
public class ZoyEmailService {

	@Autowired
	EmailService emailService;

	@Value("${qa.signin.link}")
	private String qaSigninLink;
	
	@Value("${zoy.admin.toMail}")
	private String zoyAdminMail;

	public void sendForgotEmail(AdminUserLoginDetails user,String otp) {
		Email email = new Email();
		List<String> to=new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user.getUserEmail());
		email.setTo(to);
		email.setSubject("Change new password");
		String message = "<p>Hi,</p>"
			    + "<p>We received a request to reset your password for your Zoy Admin Portal account. "
			    + "Please use the following One-Time Password (OTP) to reset your password:</p>"
			    + "<p><strong>OTP: " + otp + "</strong></p>"
			    + "<p>This OTP is valid for the next 10 minutes. If you did not request this password reset, please ignore this email.</p>"
			    + "<p>Zoy Admin Portal Sign-in Link: <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a></p>"
			    + "<p class=\"footer\">Warm regards,<br>Team ZOY</p>";
		email.setBody(message);
		email.setContent("text/html");
		emailService.sendEmail(email,null);
	}

	public void sendZoyCode(String owneremail,String firstName,String lastName,String zoyCode) {
		Email email = new Email();
		email.setFrom(zoyAdminMail);
		List<String> to = new ArrayList<>();
		to.add(owneremail);
		email.setTo(to);
		email.setSubject("Welcome to ZOY! Unlock Your Journey – Verify & Register Today!");
		String message = "<p>Dear " + firstName + " " + lastName + ",</p>"
				+ "<p>We are excited to welcome you to ZOY, your trusted companion for hassle-free PG Management. To get started, we’ve made it quick and simple for you!</p>"
				+ "<p><strong>Your Invitation Code: </strong>" + zoyCode + "</p>"
				+ "<p>Please use this code to verify your account and complete your registration in the app.</p>"
				+ "<h4>Steps to Register:</h4>"
				+ "<ul>"
				+ "<li>Download ZOY Owner App from <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a>.</li>"
				+ "<li>Open the app and select “Register.”</li>"
				+ "<li>Enter the invitation code provided above and your PG details.</li>"
				+ "</ul>"
				+ "<p>Start exploring amazing functions tailored just for you!</p>"
				+ "<p>This verification ensures you receive the latest updates, offers, and a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at [support email/phone].</p>"
				+ "<p>Welcome aboard, and we can’t wait to make your experience amazing!</p>"
				+ "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";
 
		email.setBody(message);
		email.setContent("text/html");
		emailService.sendEmail(email, null);
	}
 
 
	public void resendPgOwnerDetails(String owneremail,String firstName,String lastName,String zoyCode) {
		Email email = new Email();
		email.setFrom("zoyAdmin@mydbq.com");
 
		List<String> to = new ArrayList<>();
		to.add(owneremail);
		email.setTo(to);
 
		email.setSubject("Welcome to ZOY! Unlock Your Journey – Verify & Register Today!");
 
		String message = "<p>Dear " + firstName + " " + lastName + ",</p>"
				+ "<p>We are excited to welcome you to ZOY, your trusted companion for hassle-free PG Management. To get started, we’ve made it quick and simple for you!</p>"
				+ "<p><strong>Your Invitation Code: </strong>" + zoyCode + "</p>"
				+ "<p>Please use this code to verify your account and complete your registration in the app.</p>"
				+ "<h4>Steps to Register:</h4>"
				+ "<ul>"
				+"<li>Download ZOY Owner App from <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a>.</li>"
				+ "<li>Open the app and select “Register.”</li>"
				+ "<li>Enter the invitation code provided above and your PG details.</li>"
				+ "</ul>"
				+ "<p>Start exploring amazing functions tailored just for you!</p>"
				+ "<p>This verification ensures you receive the latest updates, offers, and a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at [support email/phone].</p>"
				+ "<p>Welcome aboard, and we can’t wait to make your experience amazing!</p>"
				+ "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";
 
		email.setBody(message);
		email.setContent("text/html");
 
		emailService.sendEmail(email, null);
	}
	

}
