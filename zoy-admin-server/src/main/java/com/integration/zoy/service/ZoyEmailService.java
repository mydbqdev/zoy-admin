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

import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.utils.Email;

@Service
public class ZoyEmailService {

	@Autowired
	EmailService emailService;

	@Value("${app.zoy.image}")
	private String zoyImagesPath;

	@Value("${app.zoy.logo}")
	private String zoyLogoPath;

	@Value("${app.zoy.banner.30}")
	private String zoyBanner30Path;

	@Value("${app.zoy.banner.31}")
	private String zoyBanner31Path;

	@Value("${app.zoy.forgot.password.url}")
	String forgotPasswordUrl;

	@Value("${app.zoy.email.verification.url}")
	String emailVerificationUrl;


	public void sendRegistrationMail(UserProfile user) {
		Email email=new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo=new ArrayList<>();
		sendTo.add(user.getEmailId());
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY – Let's Boost Your Bookings!");
		String body="<p>Dear "+user.getPropertyOwnerName()+",</p>"
				+ "<p>We are thrilled to welcome you to ZOY, your new partner in maximizing your property’s online bookings and enhancing your guests’ experiences. We are excited to have you on board and are here to help you every step of the way.</p>"
				+ "<p>As a valued member of our platform, you now have access to a range of features designed to simplify your booking process and increase your visibility to potential guests.</p>"
				+ "<p>To help you get started, here are a few steps you can take right now:</p>"
				+ "<ol>"
				+ "<li><strong>Log in to Your Account:</strong> Access your dashboard through ZOY owner application using your credentials.</li>"
				+ "<li><strong>Complete Your Profile:</strong> Ensure your hotel information is up-to-date and add any attractive photos or descriptions to catch the eye of potential guests.</li>"
				+ "<li><strong>Set Your Rates and Availability:</strong> Adjust your rates and availability to reflect your current offerings.</li>"
				+ "</ol>"
				+ "<p>We believe in building strong partnerships and are committed to your success. Our goal is to make your experience as seamless and rewarding as possible. Should you need any assistance, please do not hesitate to reach out to our support team at [support email/phone number].</p>"
				+ "<p>Once again, welcome to ZOY. We look forward to a fruitful partnership!</p>"
				+ "<p>Best regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		//ClassPathResource imageResource = new ClassPathResource("static/images/30.png");
		Path path =Paths.get(zoyBanner30Path);
		byte[] content = null;
		try {
			content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(), path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email,multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendForgotEmail(UserProfile user,String otp) {
		Email email = new Email();
		List<String> to=new ArrayList<>();
		email.setFrom("zoyPg@mydbq.com");
		to.add(user.getEmailId());
		email.setTo(to);
		email.setSubject("Change new password");
		String verifyLink=forgotPasswordUrl + user.getVerifyToken();
		String message = "Click the link to change new password: " + verifyLink + "\n" + "Otp: " + otp;
		email.setBody(message);
		email.setContent("text/html");
		emailService.sendEmail(email,null);
	}

	public void sendConfirmRegistrationMail(UserProfile user,String token) {
		Email email=new Email();
		List<String> to=new ArrayList<>();
		email.setFrom("zoyPg@mydbq.com");
		to.add(user.getEmailId());
		email.setTo(to);
		email.setSubject("Email Verification");
		String verifyLink=emailVerificationUrl + token;
		String message = "Click the link to verify your email: " + verifyLink;
		email.setBody(message);
		email.setContent("text/html");
		emailService.sendEmail(email,null);

	}

}
