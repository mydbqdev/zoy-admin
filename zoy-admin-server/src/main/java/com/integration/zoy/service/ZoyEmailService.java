package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.entity.ZoyPgPropertyDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.RegisterUser;
import com.integration.zoy.repository.BulkUploadDetailsRepository;
import com.integration.zoy.utils.AdminUserList;
import com.integration.zoy.utils.Email;

@Service
public class ZoyEmailService {

	@Autowired
	EmailService emailService;
	
	@Autowired
	PdfGenerateService pdfGenerateService;
	
	@Autowired
	BulkUploadDetailsRepository bulkUploadDetailsRepository;
	
	@Value("${qa.signin.link}")
	private String qaSigninLink;

	@Value("${zoy.owner.app}")
	private String ownerAppLink;

	@Value("${zoy.admin.toMail}")
	private String zoyAdminMail;

	@Value("${zoy.support.toMail}")
	private String zoySupportMail;
	
	@Value("${zoy.support.phone.number}")
	private String zoySupportPhoneNumber;

	@Value("${app.zoy.email.verification.url}")
	String emailVerificationUrl;

	@Value("${app.zoy.banner.30}")
	private String zoyBanner30Path;

	@Value("${app.zoy.term.doc}")
	private String zoyTermDoc;
	

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private static final Gson gson = new GsonBuilder().create();

	public void sendForgotEmail(AdminUserLoginDetails user,String otp) throws WebServiceException {
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
		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the registration email to " + zoyAdminMail + ": " + e.getMessage(), e);
	    }
	}
	public void sendZoyCode(String owneremail,String firstName,String lastName,String zoyCode,String token) {
		String verifyLink=emailVerificationUrl + token;
		Email email = new Email();
		email.setFrom(zoyAdminMail);
		List<String> to = new ArrayList<>();
		to.add(owneremail);
		email.setTo(to);
		email.setSubject("Welcome to ZOY! Unlock Your Journey – Verify & Register Today!");
		String message = "<p>Dear " + firstName + " " + lastName + ",</p>"
				+ "<p>We are excited to welcome you to ZOY, your trusted companion for hassle-free PG Management. To get started, we've made it quick and simple for you!</p>"
				+ "<p>Click the link to verify your email:<a href=\"" + verifyLink + "\">Verify Email</a></p>"
				+ "<p><strong>(Verifying Email is mandatory to register on ZOY)</strong></p>"
				+ "<p><strong>Your Invitation Code: </strong>" + zoyCode + "</p>"
				+ "<p>Please use this code to verify your account and complete your registration in the app.</p>"
				+ "<h4><strong>Steps to Register:</strong></h4>"
				+ "<ul>"
				+ "<li>Download ZOY Owner App from <a href='" + ownerAppLink + "'>" + ownerAppLink + "</a>.</li>"
				+ "<li>Open the app and select &quot;Register&quot;.</li>"
				+ "<li>Enter your invitation code provided above to fetch your details.</li>"
				+ "<li>Enter your desired Password</li>"
				+ "<li>Start exploring amazing functions tailored just for you!</li>"
				+ "</ul>"
				+ "<p>This verification ensures you a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at <a href='mailto:" + zoySupportMail + "'>" + zoySupportMail + "</a>.</p>"
				+ "<p>Welcome aboard, and we can't wait to make your experience amazing!</p>"
				+ "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");
		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the registration email to " + owneremail + ": " + e.getMessage(), e);
	    }
	}


	public void resendPgOwnerDetails(String owneremail,String firstName,String lastName,String zoyCode,String token) {
		String verifyLink=emailVerificationUrl + token;
		Email email = new Email();
		email.setFrom(zoyAdminMail);

		List<String> to = new ArrayList<>();
		to.add(owneremail);
		email.setTo(to);

		email.setSubject("Welcome to ZOY! Unlock Your Journey – Verify & Register Today!");

		String message = "<p>Dear " + firstName + " " + lastName + ",</p>"
				+ "<p>We are excited to welcome you to ZOY, your trusted companion for hassle-free PG Management. To get started, we've made it quick and simple for you!</p>"
				+ "<p>Click the link to verify your email:<a href=\"" + verifyLink + "\">Verify Email</a></p>"
				+ "<p><strong>(Verifying Email is mandatory to register on ZOY)</strong></p>"
				+ "<p><strong>Your Invitation Code: </strong>" + zoyCode + "</p>"
				+ "<p>Please use this code to verify your account and complete your registration in the app.</p>"
				+ "<h4><strong>Steps to Register:</strong></h4>"
				+ "<ul>"
				+ "<li>Download ZOY Owner App from <a href='" + ownerAppLink + "'>" + ownerAppLink + "</a>.</li>"
				+ "<li>Open the app and select &quot;Register&quot;.</li>"
				+ "<li>Enter your invitation code provided above to fetch your details.</li>"
				+ "<li>Enter your desired Password</li>"
				+ "<li>Start exploring amazing functions tailored just for you!</li>"
				+ "</ul>"
				+ "<p>This verification ensures you a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at <a href='mailto:" + zoySupportMail + "'>" + zoySupportMail + "</a>.</p>"
				+ "<p>Welcome aboard, and we can't wait to make your experience amazing!</p>"
				+ "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";
		
		email.setBody(message);
		email.setContent("text/html");

		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the registration email to " + owneremail + ": " + e.getMessage(), e);
	    }
	}
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

	public void sendUserWelcomeMail(RegisterUser registerUser) {
		Email email=new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo=new ArrayList<>();
		sendTo.add(registerUser.getEmail());
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY - Your happy stay Starts Here!");
		String body="<h1>Dear "+ registerUser.getFirstName()+",</h1>"
				+ "<p>We are delighted to welcome you to ZOY!</p>"
				+ "<p>Thank you for choosing us to manage your bookings and enhance your stay experience...</p>"
				+ "<p>Regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		//ClassPathResource imageResource = new ClassPathResource("static/images/30.png");
		Path path =Paths.get(zoyBanner30Path);
		try {
			byte[] content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(), path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email,multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBookingEmail(String toEmail, ZoyPgOwnerBookingDetails saveMyBookings, ZoyPgPropertyDetails propertyDetail,
			ZoyPgOwnerDetails zoyPgOwnerDetails,ZoyPgBedDetails bedName,String shareType,String roomName) {
		Email email=new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo=new ArrayList<>();
		sendTo.add(toEmail);
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY - Your happy stay Starts Here! ");
		String body="<h1>Dear "+saveMyBookings.getName()+",</h1>"
				+ "<p>We are delighted to welcome you to ZOY!</p>"
				+ "<p><strong>Your Renting Details:</strong></p>"
				+ "<ul>"
				+ "<li><strong>PG Name:</strong> "+propertyDetail.getPropertyName()+"</li>"
				+ "<li><strong>Room Number:</strong> "+roomName+"</li>"
				+ "<li><strong>Bed Number:</strong> "+bedName.getBedName()+"</li>"
				+ "<li><strong>Share Type: </strong> "+shareType+"</li>"
				+ "<li><strong>Date of Joning: </strong> "+saveMyBookings.getInDate()+"</li>"
				+ "<li><strong>Date of Leaving: </strong> "+saveMyBookings.getOutDate()+"</li>"
				+ "</ul>"
				+ "<p>Manage your bookings, payments and enhance your stay experience with the ZOY mobile application. "
				+ "We are committed to providing you with a seamless and enjoyable experience.</p>"
				+ "<p><strong>Here's what you can look forward to:</strong></p>"
				+ "<ul>"
				+ "<li><strong>Wide Range of Options:</strong> From PGs to co-live, we have everything you need for your perfect stay.</li>"
				+ "<li><strong>Exclusive Deals:</strong> Enjoy special discounts and offers available only to our members.</li>"
				+ "<li><strong>24/7 Customer Support:</strong> Our dedicated support team is here to assist you anytime, anywhere.</li>"
				+ "<li><strong>User-Friendly Interface:</strong> Easily search, book, and manage your reservations with our intuitive platform.</li>"
				+ "</ul>"
				+ "<p><strong>Getting started is easy:</strong></p>"
				+ "<ul>"
				+ "<li><strong>Download the App:</strong> Click below to download and unlock your perfect stay.</li>"
				+ "<li><a href='"+ ownerAppLink +"'>Download ZOY for Android</a></li>"
				+ "<li><strong>Explore:</strong> Browse through our extensive selection of stay options.</li>"
				+ "<li><strong>Book:</strong> Secure your reservations quickly and effortlessly.</li>"
				+ "<li><strong>Manage:</strong> Access, pay dues, and cancel your bookings anytime from your account.</li>"
				+ "</ul>"
				+ "<p>If you have any questions or need assistance, please do not hesitate to contact our customer support team at <a href='mailto:" + zoySupportMail + "'>" + zoySupportMail + "</a></p>"
				+ "<p>Warm regards,<br>ZOY Administrator</p>";
		email.setBody(body);
		email.setContent("text/html");
		//ClassPathResource imageResource = new ClassPathResource("static/images/30.png");
		Path path =Paths.get(zoyTermDoc);
		try {
			byte[] content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(), path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email,multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void sendRentalAgreementToTenant(UserMaster master, String pgName, InputStream is) {
		Email email = new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo = new ArrayList<>();
		sendTo.add(master.getUserEmail());
		email.setTo(sendTo);
		email.setSubject("Web Check-In for " + pgName);
		String body = "<p>Hey " + master.getUserFirstName() +" "+master.getUserLastName() +"</p>"
				+ "<p>Welcome to " + pgName + ".</p>"
				+ "<p>We are delighted to welcome you to "+pgName+". Your check-in has been confirmed, and we are excited to host you for your upcoming stay.</p>"
				+ "<p>Should you have any special requests or require any assistance prior to your arrival, don’t hesitate to reach out to us.</p>"
				+ "<p>Best regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		MultipartFile file = null;
		try {
			file= new CustomMultipartFile(is.readAllBytes(), ZoyConstant.RENTAL_AGGREMENT_PDF_NAME, ZoyConstant.RENTAL_AGGREMENT_PDF_NAME, ContentType.APPLICATION_PDF.getMimeType());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the  email to " + zoyAdminMail + ": " + e.getMessage(), e);
	    }
	}

	public void sendForUserDoUserActiveteDeactivete(AdminUserList user) {
		Email email = new Email();
		List<String> to=new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user.getUserEmail());
		email.setTo(to);
		String message ="";
		if(user.getStatus()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			String deactivationDate = LocalDate.now().format(formatter);

			email.setSubject("Your Account - [" + user.getUserEmail().toUpperCase() + "] Has Been Deactivated");
			message = "<p>Dear " + user.getFirstName() +" "+ user.getLastName() + ",</p>"
					+ "<p>We're writing to inform you that your account with ZOY Admin Platform has been deactivated as of " + deactivationDate + ". "
					+ "This means you'll no longer have access to use ZOY Admin Portal.</p>"
					+ "<p>If you have questions or need further support, don't hesitate to contact our admin support team.</p>"
					+ "<p>We hope to see you again soon!</p>"
					+ "<p>Best regards,<br>ZOY Administrator</p>";
		}else {
			email.setSubject("Welcome Back! Your Account - [" + user.getUserEmail().toUpperCase() + "] is Now Active");

			message = "<p>Dear " + user.getFirstName() +" "+ user.getLastName() + ",</p>"
					+ "<p>We're excited to welcome you back to the ZOY Admin platform! Your account has been successfully reactivated, "
					+ "and you're all set to use the platform as before.</p>"
					+ "<p>If you have any questions or need assistance, contact our admin support team.</p>"
					+ "<p>Best regards,<br>ZOY Administrator</p>";

		}

		email.setBody(message);
		email.setContent("text/html");
		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the  email to " + zoyAdminMail + ": " + e.getMessage(), e);
	    }
	}
	
	public void sendPasswordChangeWarningMails(String[] user) {
		Email email = new Email();
		List<String> to=new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user[0]);
		email.setTo(to);
		String message ="";
		email.setSubject("Reminder: Update Your Password for Enhanced Security");
		message = "<p>Dear " + user[1] + ",</p>"
		        + "<p>We hope this message finds you well.</p>"
		        + "<p>As part of our ongoing commitment to ensuring the security of your account, we remind you to update your password regularly. "
		        + "To maintain optimal account safety as per our security policy, we recommend changing your password every 45 days.</p>"
		        + "<p>It has been " + user[2] + " days since your last password update. Kindly take a moment to update your password by following these simple steps:</p>"
		        + "<ol>"
		        + "<li>Log in to your account at <a href='"+ qaSigninLink +"'>"+ qaSigninLink +"</a></li>"
		        + "<li>Navigate to the  <b>Profile Settings </b> section.</li>"
		        + "<li>Select <b>Change Password </b> and follow the instructions provided.</li>"
		        + "</ol>"
		        + "<p>When setting your new password, please ensure it is unique and meets the following criteria:</p>"
		        + "<ul>"
		        + "<li>A minimum of 8-16 characters.</li>"
		        + "<li>Includes at least one uppercase letter, one lowercase letter, one number, and one special character.</li>"
		        + "</ul>"
		        + "<p>If you need any assistance or experience any issues during the process, our support team is here to help. "
		        + "You can reach us at "+zoySupportMail+" or "+zoySupportPhoneNumber+".</p>"
		        + "<p>Thank you for your attention to this important matter. Taking this small step will significantly enhance the security of your account.</p>"
		        + "<p>Best regards,<br>ZOY Administrator</p>";
			

		email.setBody(message);
		email.setContent("text/html");
		try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Error occurred while sending the Password Change Warning Mails to " + zoyAdminMail + ": " + e.getMessage(), e);
	    }
	}
	
	

	public void sendErrorEmail(String executionId, String errorMessage) {
		try {
			String[] allMails = bulkUploadDetailsRepository.allSuperAdmin();

			Email email = new Email();
			email.setFrom(zoyAdminMail);
			List<String> to = new ArrayList<>();
			Collections.addAll(to, allMails);
			email.setTo(to);

			email.setSubject("Urgent: Document Update Failure – Assistance Required " + executionId);

			// Create HTML body
			String htmlBody = "<p>Dear Team,</p>"
					+ "<p>The system encountered an issue while attempting to update the uploaded data on the Admin Portal.</p>"
					+ "<p>The update process failed, and the system generated the following error the following error log,</p>"
					+ "<p>which is attached below for your reference.</p>"
					+ "<p>This issue is impacting our ability to process documents efficiently. "
					+ "Kindly investigate and provide a resolution at the earliest.</p>"
					+ "<p>Best regards,<br>ZOY Administrator</p>";

			email.setBody(htmlBody);
			email.setContent("text/html");

			StringBuilder attachmentContent = new StringBuilder();
			attachmentContent.append("Error Log Report\n");
			attachmentContent.append("================\n\n");
			attachmentContent.append("Execution ID: ").append(executionId).append("\n");
			attachmentContent.append("Timestamp: ").append(LocalDateTime.now()).append("\n\n");
			attachmentContent.append("Detailed Error Message:\n");
			attachmentContent.append("--------------------\n");
			attachmentContent.append(errorMessage).append("\n");

			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					attachmentContent.toString().getBytes(StandardCharsets.UTF_8));

			byte[] content = pdfGenerateService.imageToBytes(inputStream);
			MultipartFile multipartFile = new CustomMultipartFile(content, "error_log_" + executionId + ".txt",
					"error_log_" + executionId + ".txt", "text/plain");

			emailService.sendEmail(email, multipartFile);

		} catch (Exception e) {
			log.error("Failed to send error email for executionId {}: {}", executionId, e.getMessage(), e);
		}
	}
}
