package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyBeforeCheckInCancellationModel;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.BulkUploadDetailsRepository;
import com.integration.zoy.utils.AdminUserList;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.ZoyShortTermDetails;
import com.integration.zoy.utils.ZoyShortTermDto;

@Service
public class ZoyEmailService {

	@Autowired
	EmailService emailService;

	@Autowired
	PdfGenerateService pdfGenerateService;

	@Autowired
	AdminUserMasterRepository adminUserMasterRepo;

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

	public void sendForgotEmail(AdminUserLoginDetails user, String otp) throws WebServiceException {
		Email email = new Email();
		List<String> to = new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user.getUserEmail());
		email.setTo(to);
		email.setSubject("Change new password");
		String message = "<p>Hi,</p>"
				+ "<p>We received a request to reset your password for your Zoy Admin Portal account. "
				+ "Please use the following One-Time Password (OTP) to reset your password:</p>" + "<p><strong>OTP: "
				+ otp + "</strong></p>"
				+ "<p>This OTP is valid for the next 10 minutes. If you did not request this password reset, please ignore this email.</p>"
				+ "<p>Zoy Admin Portal Sign-in Link: <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a></p>"
				+ "<p class=\"footer\">Warm regards,<br>Team ZOY</p>";
		email.setBody(message);
		email.setContent("text/html");
		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the registration email to " + zoyAdminMail + ": " + e.getMessage(),
					e);
		}
	}

	public void sendZoyCode(String owneremail, String firstName, String lastName, String zoyCode, String token) {
		String verifyLink = emailVerificationUrl + token;
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
				+ "<h4><strong>Steps to Register:</strong></h4>" + "<ul>" + "<li>Download ZOY Owner App from <a href='"
				+ ownerAppLink + "'>" + ownerAppLink + "</a>.</li>"
				+ "<li>Open the app and select &quot;Register&quot;.</li>"
				+ "<li>Enter your invitation code provided above to fetch your details.</li>"
				+ "<li>Enter your desired Password</li>"
				+ "<li>Start exploring amazing functions tailored just for you!</li>" + "</ul>"
				+ "<p>This verification ensures you a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at <a href='mailto:"
				+ zoySupportMail + "'>" + zoySupportMail + "</a>.</p>"
				+ "<p>Welcome aboard, and we can't wait to make your experience amazing!</p>" + "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");
		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the registration email to " + owneremail + ": " + e.getMessage(),
					e);
		}
	}

	public void resendPgOwnerDetails(String owneremail, String firstName, String lastName, String zoyCode,
			String token) {
		String verifyLink = emailVerificationUrl + token;
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
				+ "<h4><strong>Steps to Register:</strong></h4>" + "<ul>" + "<li>Download ZOY Owner App from <a href='"
				+ ownerAppLink + "'>" + ownerAppLink + "</a>.</li>"
				+ "<li>Open the app and select &quot;Register&quot;.</li>"
				+ "<li>Enter your invitation code provided above to fetch your details.</li>"
				+ "<li>Enter your desired Password</li>"
				+ "<li>Start exploring amazing functions tailored just for you!</li>" + "</ul>"
				+ "<p>This verification ensures you a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at <a href='mailto:"
				+ zoySupportMail + "'>" + zoySupportMail + "</a>.</p>"
				+ "<p>Welcome aboard, and we can't wait to make your experience amazing!</p>" + "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the registration email to " + owneremail + ": " + e.getMessage(),
					e);
		}
	}

	public void sendRegistrationMail(UserProfile user) {
		Email email = new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo = new ArrayList<>();
		sendTo.add(user.getEmailId());
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY – Let's Boost Your Bookings!");
		String body = "<p>Dear " + user.getPropertyOwnerName() + ",</p>"
				+ "<p>We are thrilled to welcome you to ZOY, your new partner in maximizing your property’s online bookings and enhancing your guests’ experiences. We are excited to have you on board and are here to help you every step of the way.</p>"
				+ "<p>As a valued member of our platform, you now have access to a range of features designed to simplify your booking process and increase your visibility to potential guests.</p>"
				+ "<p>To help you get started, here are a few steps you can take right now:</p>" + "<ol>"
				+ "<li><strong>Log in to Your Account:</strong> Access your dashboard through ZOY owner application using your credentials.</li>"
				+ "<li><strong>Complete Your Profile:</strong> Ensure your hotel information is up-to-date and add any attractive photos or descriptions to catch the eye of potential guests.</li>"
				+ "<li><strong>Set Your Rates and Availability:</strong> Adjust your rates and availability to reflect your current offerings.</li>"
				+ "</ol>"
				+ "<p>We believe in building strong partnerships and are committed to your success. Our goal is to make your experience as seamless and rewarding as possible. Should you need any assistance, please do not hesitate to reach out to our support team at [support email/phone number].</p>"
				+ "<p>Once again, welcome to ZOY. We look forward to a fruitful partnership!</p>"
				+ "<p>Best regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		// ClassPathResource imageResource = new
		// ClassPathResource("static/images/30.png");
		Path path = Paths.get(zoyBanner30Path);
		byte[] content = null;
		try {
			content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(),
					path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendUserWelcomeMail(RegisterUser registerUser) {
		Email email = new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo = new ArrayList<>();
		sendTo.add(registerUser.getEmail());
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY - Your happy stay Starts Here!");
		String body = "<h1>Dear " + registerUser.getFirstName() + ",</h1>"
				+ "<p>We are delighted to welcome you to ZOY!</p>"
				+ "<p>Thank you for choosing us to manage your bookings and enhance your stay experience...</p>"
				+ "<p>Regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		// ClassPathResource imageResource = new
		// ClassPathResource("static/images/30.png");
		Path path = Paths.get(zoyBanner30Path);
		try {
			byte[] content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(),
					path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBookingEmail(String toEmail, ZoyPgOwnerBookingDetails saveMyBookings,
			ZoyPgPropertyDetails propertyDetail, ZoyPgOwnerDetails zoyPgOwnerDetails, ZoyPgBedDetails bedName,
			String shareType, String roomName) {
		Email email = new Email();
		email.setFrom("zoyPg@mydbq.com");
		List<String> sendTo = new ArrayList<>();
		sendTo.add(toEmail);
		email.setTo(sendTo);
		email.setSubject("Welcome to ZOY - Your happy stay Starts Here! ");
		String body = "<h1>Dear " + saveMyBookings.getName() + ",</h1>"
				+ "<p>We are delighted to welcome you to ZOY!</p>" + "<p><strong>Your Renting Details:</strong></p>"
				+ "<ul>" + "<li><strong>PG Name:</strong> " + propertyDetail.getPropertyName() + "</li>"
				+ "<li><strong>Room Number:</strong> " + roomName + "</li>" + "<li><strong>Bed Number:</strong> "
				+ bedName.getBedName() + "</li>" + "<li><strong>Share Type: </strong> " + shareType + "</li>"
				+ "<li><strong>Date of Joning: </strong> " + saveMyBookings.getInDate() + "</li>"
				+ "<li><strong>Date of Leaving: </strong> " + saveMyBookings.getOutDate() + "</li>" + "</ul>"
				+ "<p>Manage your bookings, payments and enhance your stay experience with the ZOY mobile application. "
				+ "We are committed to providing you with a seamless and enjoyable experience.</p>"
				+ "<p><strong>Here's what you can look forward to:</strong></p>" + "<ul>"
				+ "<li><strong>Wide Range of Options:</strong> From PGs to co-live, we have everything you need for your perfect stay.</li>"
				+ "<li><strong>Exclusive Deals:</strong> Enjoy special discounts and offers available only to our members.</li>"
				+ "<li><strong>24/7 Customer Support:</strong> Our dedicated support team is here to assist you anytime, anywhere.</li>"
				+ "<li><strong>User-Friendly Interface:</strong> Easily search, book, and manage your reservations with our intuitive platform.</li>"
				+ "</ul>" + "<p><strong>Getting started is easy:</strong></p>" + "<ul>"
				+ "<li><strong>Download the App:</strong> Click below to download and unlock your perfect stay.</li>"
				+ "<li><a href='" + ownerAppLink + "'>Download ZOY for Android</a></li>"
				+ "<li><strong>Explore:</strong> Browse through our extensive selection of stay options.</li>"
				+ "<li><strong>Book:</strong> Secure your reservations quickly and effortlessly.</li>"
				+ "<li><strong>Manage:</strong> Access, pay dues, and cancel your bookings anytime from your account.</li>"
				+ "</ul>"
				+ "<p>If you have any questions or need assistance, please do not hesitate to contact our customer support team at <a href='mailto:"
				+ zoySupportMail + "'>" + zoySupportMail + "</a></p>" + "<p>Warm regards,<br>ZOY Administrator</p>";
		email.setBody(body);
		email.setContent("text/html");
		// ClassPathResource imageResource = new
		// ClassPathResource("static/images/30.png");
		Path path = Paths.get(zoyTermDoc);
		try {
			byte[] content = Files.readAllBytes(path);
			MultipartFile multipartFile = new CustomMultipartFile(content, path.getFileName().toString(),
					path.getFileName().toString(), Files.probeContentType(path.toFile().toPath()));
			emailService.sendEmail(email, multipartFile);
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
		String body = "<p>Hey " + master.getUserFirstName() + " " + master.getUserLastName() + "</p>" + "<p>Welcome to "
				+ pgName + ".</p>" + "<p>We are delighted to welcome you to " + pgName
				+ ". Your check-in has been confirmed, and we are excited to host you for your upcoming stay.</p>"
				+ "<p>Should you have any special requests or require any assistance prior to your arrival, don’t hesitate to reach out to us.</p>"
				+ "<p>Best regards,<br>Team ZOY</p>";
		email.setBody(body);
		email.setContent("text/html");
		MultipartFile file = null;
		try {
			file = new CustomMultipartFile(is.readAllBytes(), ZoyConstant.RENTAL_AGGREMENT_PDF_NAME,
					ZoyConstant.RENTAL_AGGREMENT_PDF_NAME, ContentType.APPLICATION_PDF.getMimeType());
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
		List<String> to = new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user.getUserEmail());
		email.setTo(to);
		String message = "";
		if (user.getStatus()) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			String deactivationDate = LocalDate.now().format(formatter);

			email.setSubject("Your Account - [" + user.getUserEmail().toUpperCase() + "] Has Been Deactivated");
			message = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>"
					+ "<p>We're writing to inform you that your account with ZOY Admin Platform has been deactivated as of "
					+ deactivationDate + ". " + "This means you'll no longer have access to use ZOY Admin Portal.</p>"
					+ "<p>If you have questions or need further support, don't hesitate to contact our admin support team.</p>"
					+ "<p>We hope to see you again soon!</p>" + "<p>Best regards,<br>ZOY Administrator</p>";
		} else {
			email.setSubject("Welcome Back! Your Account - [" + user.getUserEmail().toUpperCase() + "] is Now Active");

			message = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>"
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
		List<String> to = new ArrayList<>();
		email.setFrom(zoyAdminMail);
		to.add(user[0]);
		email.setTo(to);
		String message = "";
		email.setSubject("Reminder: Update Your Password for Enhanced Security");
		message = "<p>Dear " + user[1] + ",</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>As part of our ongoing commitment to ensuring the security of your account, we remind you to update your password regularly. "
				+ "To maintain optimal account safety as per our security policy, we recommend changing your password every 45 days.</p>"
				+ "<p>It has been " + user[2]
				+ " days since your last password update. Kindly take a moment to update your password by following these simple steps:</p>"
				+ "<ol>" + "<li>Log in to your account at <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a></li>"
				+ "<li>Navigate to the  <b>Profile Settings </b> section.</li>"
				+ "<li>Select <b>Change Password </b> and follow the instructions provided.</li>" + "</ol>"
				+ "<p>When setting your new password, please ensure it is unique and meets the following criteria:</p>"
				+ "<ul>" + "<li>A minimum of 8-16 characters.</li>"
				+ "<li>Includes at least one uppercase letter, one lowercase letter, one number, and one special character.</li>"
				+ "</ul>"
				+ "<p>If you need any assistance or experience any issues during the process, our support team is here to help. "
				+ "You can reach us at " + zoySupportMail + " or " + zoySupportPhoneNumber + ".</p>"
				+ "<p>Thank you for your attention to this important matter. Taking this small step will significantly enhance the security of your account.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");
		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the Password Change Warning Mails to " + zoyAdminMail + ": "
					+ e.getMessage(), e);
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

	public void sendApprovalNotificationEmail(String approverName, String ruleTitle) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.masterConfigurationAccess();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for sending approval notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);

		List<String> to = new ArrayList<>();
		Collections.addAll(to, allMails);
		email.setTo(to);
		email.setSubject("Approval Request Approved by " + approverName);

		String message = "<p>Dear Team,</p>" + "<p>I hope this message finds you well.</p>"
				+ "<p>We are pleased to inform you that the recent approval request titled " + "<strong>\"" + ruleTitle
				+ "\"</strong> has been reviewed and approved by <strong>" + approverName + "</strong>.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the approval notification email to recipients " + to + ": "
					+ e.getMessage(), e);
		}
	}

	public void sendApprovalRequestRaisedEmail(String requestorName, String ruleName, String dateRaised,
			String effectiveDateMin) throws WebServiceException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(effectiveDateMin, formatter);
		LocalDate dateMinus15 = date.minusDays(15);
		String result = dateMinus15.format(formatter);

		String[] allMails = adminUserMasterRepo.masterConfigurationAccess();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for sending approval notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);

		List<String> to = new ArrayList<>();
		Collections.addAll(to, allMails);
		email.setTo(to);
		email.setSubject("Approval Request Raised – Configuration Master Update");

		String message = "<p>Dear Team,</p>"
				+ "<p>This is to inform you that an approval request has been raised for a proposed change in the Configuration Master. "
				+ "Please review the request at your earliest convenience and take the necessary action to approve on or before <strong>"
				+ result + "</strong>.</p>" + "<p><strong>Summary of Request:</strong></p>" + "<ul>"
				+ "<li>Change Type: " + ruleName + "</li>" + "<li>Requested By: " + requestorName + "</li>"
				+ "<li>Date Raised: " + dateRaised + "</li>" + "</ul>"
				+ "<p>You can view the full details and approve the request through the ZOY admin portal:--> Settings --> Master Configuration </p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the approval request email to  recipients " + to + ": "
					+ e.getMessage(), e);
		}
	}

	public void sendApprovalRejectionEmail(String ruleTitle, String approverName, String effectiveDateMin,
			String rejectionReason) throws WebServiceException {

		String[] allMails = adminUserMasterRepo.masterConfigurationAccess();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for sending approval notification.");
			return;
		}
		Email email = new Email();
		email.setFrom(zoyAdminMail);

		List<String> to = new ArrayList<>();
		Collections.addAll(to, allMails);
		email.setTo(to);
		email.setSubject("Approval Request Rejected by " + approverName);

		String message = "<p>Dear team,</p>" + "<p>I hope this message finds you well.</p>"
				+ "<p>This is to inform you that your recent approval request titled <strong>\"" + ruleTitle
				+ "\"</strong>, " + "With an effective date <strong>" + effectiveDateMin
				+ "</strong>, has been reviewed and <strong>rejected</strong> by " + "<strong>" + approverName
				+ "</strong>.</p>" + "<p><strong>Reason:</strong><br>" + rejectionReason + "</p>"
				+ "<p>You can revise and resubmit the request again for the approval.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the rejection email to  recipients " + to + ": " + e.getMessage(),
					e);
		}
	}

	public void sendAutoRejectionEmail(String ruleTitle, String effectiveDateMin) throws WebServiceException {

		String[] allMails = adminUserMasterRepo.masterConfigurationAccess();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for sending auto-rejection notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);

		List<String> to = new ArrayList<>();
		Collections.addAll(to, allMails);
		email.setTo(to);
		email.setSubject("Approval Request Automatically Rejected by System");

		String message = "<p>Dear Team,</p>" + "<p>I hope this message finds you well.</p>"
				+ "<p>We would like to inform you that your recent approval request titled <strong>\"" + ruleTitle
				+ "\"</strong>, effective from <strong>" + effectiveDateMin + "</strong>, has been "
				+ "<strong>automatically rejected by the system</strong>.</p>"
				+ "<p><strong>Reason:</strong> The approval time window has expired.</p>"
				+ "<p>You may revise and resubmit the request for approval at your convenience.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the automatic rejection email to recipients " + to + ": "
					+ e.getMessage(), e);
		}
	}

//
//	public void sendPolicyChangeNotificationEmail(String ruleName, String effectiveDate,String existingRule, String upcomingRule) throws WebServiceException {
//
//	    String[] allMails = adminUserMasterRepo.masterConfigurationAccess();
//
//	    if (allMails == null || allMails.length == 0) {
//	        log.warn("No recipient email addresses found for sending policy update notification.");
//	        return;
//	    }
//
//	    Email email = new Email();
//	    email.setFrom(zoyAdminMail);
//
//	    List<String> to = new ArrayList<>();
//	    Collections.addAll(to, allMails);
//	    email.setTo(to);
//
//	    email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from " + effectiveDate);
//
//	    String message = "<p>Dear users </p>"
//	            + "<p>We hope this message finds you well.</p>"
//	            + "<p>We are writing to inform you about an important update regarding our <strong>Cancellation & Payment rules</strong> and <strong>Policy Terms</strong>, which will come into effect from <strong>" + effectiveDate + "</strong>.</p>"
//	            + "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
//
//	            + "<p><strong>What’s Changing:</strong></p>"
//	            + "<p><strong>" + ruleName + "</strong></p>"
//
//	            + "<table style='border-collapse: collapse; width: 100%; font-family: Arial, sans-serif;'>"
//	            + "<tr style='background-color: #f2f2f2;'>"
//	            + "<th style='border: 1px solid #999; padding: 10px; text-align: left;'>Existing Rule</th>"
//	            + "<th style='border: 1px solid #999; padding: 10px; text-align: left;'>Upcoming Rule</th>"
//	            + "</tr>"
//	            + "<tr>"
//	            + "<td style='border: 1px solid #999; padding: 10px;'>" + existingRule + "</td>"
//	            + "<td style='border: 1px solid #999; padding: 10px;'>" + upcomingRule + "</td>"
//	            + "</tr>"
//	            + "</table>"
//
//	            + "<p>We kindly request you to review the updated rules and ensure that you understand the changes, as they will be applicable to all bookings made on or after the effective date mentioned above.</p>"
//	            + "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <strong>support@zoy.com</strong>.</p>"
//	            + "<p>Thank you for your continued support and cooperation.</p>"
//	            + "<p>Best regards,<br>ZOY Administrator</p>";
//
//	    email.setBody(message);
//	    email.setContent("text/html");
//
//	    try {
//	        emailService.sendEmail(email, null);
//	    } catch (Exception e) {
//	        log.error("Error occurred while sending the policy update email to recipients " + to + ": " + e.getMessage(), e);
//	    }
//	}
//	
//	
	public void sendTokenAdvanceRuleChangeEmail(String effectiveDate, BigDecimal oldFixed, BigDecimal oldVariable,
			BigDecimal newFixed, BigDecimal newVariable) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Token Advance rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail); // Make sure zoyAdminMail is properly initialized with the sender's email
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in Token Advance Policy, Effective from " + effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>This is to inform you of an important change in our Token Advance policy, effective from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency, we have revised certain clauses in our Token Advance policy. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>A fixed amount of " + oldFixed
				+ " rupees or " + oldVariable + "% of the monthly rent, whichever is higher.</td>"
				+ "<td>A fixed amount of " + newFixed + " rupees or " + newVariable
				+ "% of the monthly rent, whichever is higher.</td></tr>" + "</table>"
				+ "<p>We kindly request that you review the updated rules and ensure that you understand the changes, as they will be applicable to all bookings made on or after the effective date mentioned above.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a> or call us at [Support Phone Number].</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Token Advance update email: " + e.getMessage(), e);
		}
	}

	public void sendSecurityDepositLimitsChangeEmail(String effectiveDate, BigDecimal oldFixed, BigDecimal oldVariable,
			BigDecimal newFixed, BigDecimal newVariable) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Security Deposit rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + " <strong>Security Deposit Limits:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>Between Rs. " + oldFixed
				+ " – Rs. " + oldVariable + "</td><td>Between Rs. " + newFixed + " – Rs. " + newVariable + "</td></tr>"
				+ "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes, as they will be applicable to all bookings made on or after the effective date mentioned above.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";
		
		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Security Deposit Limits update email: " + e.getMessage(), e);
		}
	}

	public void sendGstChargesChangeEmail(String effectiveDate, BigDecimal oldFixed, BigDecimal oldVariable,
			BigDecimal newFixed, BigDecimal newVariable) throws WebServiceException {
		String[] allMails =adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for GST Charges rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + " <strong>GST Charges:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>GST @" + oldFixed
				+ "% for a daily rent greater than or equal to Rs. " + oldVariable + "</td><td>GST @" + newFixed
				+ "% for a daily rent greater than or equal to Rs. " + newVariable + "</td></tr>" + "</table>"
				+ "<p>We kindly request that you review the updated rules and ensure that you understand the changes, as they will be applicable to all bookings made on or after the effective date mentioned above.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a> or call us at [Support Phone Number].</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";
		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send GST Charges update email: " + e.getMessage(), e);
		}
	}

	public void sendOtherChargesChangeEmailForOwners(String effectiveDate, BigDecimal oldFixed, BigDecimal oldVariable,
			BigDecimal newFixed, BigDecimal newVariable) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Other Charges rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>"
				+ " <strong>Other Charges (eKYC and Documentation):</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>Rs. " + oldFixed
				+ " for eKYC<br>Rs. " + oldVariable + " for Documentation</td><td>Rs. " + newFixed + " for eKYC<br>Rs. "
				+ newVariable + " for Documentation</td></tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Other Charges update email: " + e.getMessage(), e);
		}
	}

	public void sendOtherChargesChangeEmailForUsers(String effectiveDate, BigDecimal oldFixed, BigDecimal oldVariable,
			BigDecimal newFixed, BigDecimal newVariable) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Other Charges rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>"
				+ " <strong>Other Charges (eKYC and Documentation):</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>Rs. " + oldFixed
				+ " for eKYC<br>Rs. " + oldVariable + " for Documentation</td><td>Rs. " + newFixed + " for eKYC<br>Rs. "
				+ newVariable + " for Documentation</td></tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Other Charges update email: " + e.getMessage(), e);
		}
	}

	public void sendForceCheckoutChangeEmail(String effectiveDate, int oldFixed, int newFixed)
			throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Force Checkout rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + " <strong>Force Checkout:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>" + oldFixed + " Days</td><td>"
				+ newFixed + " Days</td></tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Force Checkout update email: " + e.getMessage(), e);
		}
	}

	public void sendNoRentalAgreementChangeEmail(String effectiveDate, int oldFixed, int newFixed)
			throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Rental Agreement rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>"
				+ " <strong>No Rental Agreement (Trigger Days):</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>" + oldFixed + " Days</td><td>"
				+ newFixed + " Days</td></tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Rental Agreement rule update email: " + e.getMessage(), e);
		}
	}

	public void sendEarlyCheckoutChangeEmail(String effectiveDate, Long oldFixed, Long newFixed, String oldCondition,
			String newCondition) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Early Check-out rule change notification.");
			return;
		}

		// Mapping comparison symbols to their corresponding descriptions
		Map<String, String> comparisonMap = new HashMap<>();
		comparisonMap.put(">=", "on or after");
		comparisonMap.put(">", "after");
		comparisonMap.put("<=", "on or before");
		comparisonMap.put("<", "before");
		comparisonMap.put("==", "equal to");

		// Replace old and new conditions with their descriptions
		String oldConditionDescription = comparisonMap.getOrDefault(oldCondition, oldCondition);
		String newConditionDescription = comparisonMap.getOrDefault(newCondition, newCondition);

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + " <strong>Early Check-out Rules:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>Check-out "
				+ oldConditionDescription + " " + oldFixed
				+ " days from check-in = rent deducted prorate via short-term slabs</td>" + "<td>Check-out "
				+ newConditionDescription + " " + newFixed
				+ " days from check-in = rent deducted prorate via short-term slabs</td></tr>" + "</table>"
				+ "<p>We kindly request that you review the updated rules and ensure that you understand the changes, as they will be applicable to all bookings made on or after the effective date mentioned above.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a> or call us at [Support Phone Number].</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";
		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Early Check-out rule update email: " + e.getMessage(), e);
		}
	}

	public void sendSecurityDepositDeadlineChangeEmail(String effectiveDate, Long oldFixed, BigDecimal oldVariable,
			Long newFixed, BigDecimal newVariable, String oldCondition, String newCondition)
			throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Security Deposit Deadline change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		Map<String, String> comparisonMap = new HashMap<>();
		comparisonMap.put(">=", "on or after");
		comparisonMap.put(">", "after");
		comparisonMap.put("<=", "on or before");
		comparisonMap.put("<", "before");
		comparisonMap.put("==", "equal to");

		// Replace old and new conditions with their descriptions
		String oldConditionDescription = comparisonMap.getOrDefault(oldCondition, oldCondition);
		String newConditionDescription = comparisonMap.getOrDefault(newCondition, newCondition);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + " <strong>Security Deposit Deadline:</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>Total deposit to be paid "
				+ oldConditionDescription + " " + oldFixed + " days of check-in. " + oldVariable
				+ "% penalty on failure and booking will be cancelled.</td>" + "<td>Total deposit to be paid "
				+ newConditionDescription + " " + newFixed + " days of check-in. " + newVariable
				+ "% penalty on failure and booking will be cancelled.</td></tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Security Deposit Deadline update email: " + e.getMessage(), e);
		}
	}

	public void sendAutoCancellationAfterCheckinChangeEmail(String effectiveDate, Long oldFixed, Long newFixed,
			String oldCondition, String newCondition) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Auto Cancellation rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ effectiveDate);

		Map<String, String> comparisonMap = new HashMap<>();
		comparisonMap.put(">=", "on or after");
		comparisonMap.put(">", "after");
		comparisonMap.put("<=", "on or before");
		comparisonMap.put("<", "before");
		comparisonMap.put("==", "equal to");

		// Replace old and new conditions with their descriptions
		String oldConditionDescription = comparisonMap.getOrDefault(oldCondition, oldCondition);
		String newConditionDescription = comparisonMap.getOrDefault(newCondition, newCondition);

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ effectiveDate + "</strong>.</p>"
				+ "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>" + "<strong>Auto Cancellation After Check-in:</strong></p>"
				+ "<p>The number of days after the check-in date to automatically cancel the bookings if the tenant fails to check-in.</p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr><td>If tenant fails to check-in "
				+ oldConditionDescription + " " + oldFixed
				+ " days of check-in date, rent will be deducted based on the short term renting slabs on prorate basis and the booking will be cancelled automatically</td>"
				+ "<td>If tenant fails to check-in " + newConditionDescription + " " + newFixed
				+ " days of check-in date, rent will be deducted based on the short term renting slabs on prorate basis and the booking will be cancelled automatically</td></tr>"
				+ "</table>" 
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Auto Cancellation After Check-in rule update email: " + e.getMessage(), e);
		}
	}


	public void sendCancellationRefundPolicyChangeEmail(ZoyBeforeCheckInCancellationModel zoyPreviousData,
			ZoyBeforeCheckInCancellationModel zoyUpdatedData) throws WebServiceException {
		String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

		if (allMails == null || allMails.length == 0) {
			log.warn("No recipient email addresses found for Cancellation & Refund Policy rule change notification.");
			return;
		}

		Email email = new Email();
		email.setFrom(zoyAdminMail);
		email.setTo(Arrays.asList(allMails));
		email.setSubject("Important Update: Changes in cancellation and payment rules, policy terms effective from "
				+ zoyUpdatedData.getEffectiveDate());

		Map<String, String> comparisonMap = new HashMap<>();
		comparisonMap.put(">=", "on or after");
		comparisonMap.put(">", "after");
		comparisonMap.put("<=", "on or before");
		comparisonMap.put("<", "before");
		comparisonMap.put("==", "equal to");

		StringBuilder oldRulesTable = new StringBuilder();
		StringBuilder newRulesTable = new StringBuilder();

		// Build old rules
		if (zoyPreviousData != null ) {
			for (ZoyBeforeCheckInCancellation prev : zoyPreviousData.getZoyBeforeCheckInCancellationInfo()) {
				oldRulesTable.append("<tr><td>").append(getConditionDescriptionforcancel(prev, comparisonMap))
						.append("</td><td>").append(prev.getDeductionPercentage()).append("% of total paid")
						.append("</td></tr>");
			}
		}

		// Build new rules
		if (zoyUpdatedData != null && zoyUpdatedData.getZoyBeforeCheckInCancellationInfo() != null) {
			for (ZoyBeforeCheckInCancellation updated : zoyUpdatedData.getZoyBeforeCheckInCancellationInfo()) {
				newRulesTable.append("<tr><td>").append(getConditionDescriptionforcancel(updated, comparisonMap))
						.append("</td><td>").append(updated.getDeductionPercentage()).append("% of total paid")
						.append("</td></tr>");
			}
		}

		String message = "<p>Dear Owner/Tenant,</p>" + "<p>We hope this message finds you well.</p>"
				+ "<p>We are writing to inform you about an important update regarding our Cancellation & Payment rules and Policy Terms, which will come into effect from <strong>"
				+ zoyUpdatedData.getEffectiveDate() + "</strong>.</p>" 
			    + "<p>As part of our ongoing efforts to enhance user experience and ensure transparency for both property owners and tenants, we have revised certain clauses and rules in our cancellation, payment, refund policies and terms of stay rules. These changes aim to create a fair and balanced approach that protects the interests of all parties involved.</p>"
				+ "<p><strong>What's Changing:</strong><br/>"
				+ " <strong>Cancellation and Refund Policy (Before Check-in):</strong></p>"
				+ "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
				+ "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>" + "<tr>" + "<td><table border='0'>"
				+ oldRulesTable.toString() + "</table></td>" + "<td><table border='0'>" + newRulesTable.toString()
				+ "</table></td>" + "</tr>" + "</table>"
				+ "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
				+ "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
				+ "<p>Thank you for your continued support and cooperation.</p>"
				+ "<p>Best regards,<br>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");

		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Failed to send Cancellation & Refund Policy update email: " + e.getMessage(), e);
		}
	}

	private String getConditionDescriptionforcancel(ZoyBeforeCheckInCancellation item, Map<String, String> comparisonMap) {
		String triggerCondition = comparisonMap.getOrDefault(item.getTriggerCondition(), item.getTriggerCondition());
		return triggerCondition + " " + item.getBeforeCheckinDays() + " days from check-in date";
	}

	
	public void sendEmailNotificationsForShortTerm(ZoyShortTermDetails previousShortTerm, ZoyShortTermDetails newShortTerm) {
	    String[] allMails = adminUserMasterRepo.allTenantAndOwnerEmails();

	    if (allMails == null || allMails.length == 0) {
	        log.warn("No recipient email addresses found for Short Term Rule change notification.");
	        return;
	    }

	    Email email = new Email();
	    email.setFrom(zoyAdminMail);
	    email.setTo(Arrays.asList(allMails));
	    email.setSubject("Important Update: Changes in short-term stay rent slabs effective from "
	            + newShortTerm.getEffectiveDate());

	    StringBuilder oldRulesTable = new StringBuilder();
	    StringBuilder newRulesTable = new StringBuilder();

	    if (previousShortTerm != null && previousShortTerm.getZoyShortTermDtoInfo() != null) {
	        for (ZoyShortTermDto oldDto : previousShortTerm.getZoyShortTermDtoInfo()) {
	            if (Boolean.TRUE.equals(oldDto.getDelete())) continue;
	            oldRulesTable.append("<tr><td>")
	                    .append(oldDto.getStartDay()).append("-").append(oldDto.getEndDay()).append(" days")
	                    .append("</td><td>")
	                    .append("Daily rent as ").append(oldDto.getPercentage()).append("% of monthly rent")
	                    .append("</td></tr>");
	        }
	    }

	    if (newShortTerm != null && newShortTerm.getZoyShortTermDtoInfo() != null) {
	        for (ZoyShortTermDto newDto : newShortTerm.getZoyShortTermDtoInfo()) {
	            if (Boolean.TRUE.equals(newDto.getDelete())) continue;
	            newRulesTable.append("<tr><td>")
	                    .append(newDto.getStartDay()).append("-").append(newDto.getEndDay()).append(" days")
	                    .append("</td><td>")
	                    .append("Daily rent as ").append(newDto.getPercentage()).append("% of monthly rent")
	                    .append("</td></tr>");
	        }
	    }

	    String message = "<p>Dear Owner/Tenant,</p>"
	            + "<p>We hope this message finds you well.</p>"
	            + "<p>We are writing to inform you about an important update regarding our Short-Term Stay Rent Calculation Policy, which will come into effect from <strong>"
	            + newShortTerm.getEffectiveDate() + "</strong>.</p>"
	            + "<p>This update refines the daily rent calculation slabs for stays shorter than 30 days, ensuring transparency and consistency for both property owners and tenants.</p>"
				+ "<p><strong>What's Changing:</strong><br/>"
	            + "<p><strong>Short-term Duration Slabs:</strong></p>"
	            + "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse: collapse;'>"
	            + "<tr><th>Existing Rule</th><th>Upcoming Rule</th></tr>"
	            + "<tr><td><table border='0'>" + oldRulesTable.toString() + "</table></td>"
	            + "<td><table border='0'>" + newRulesTable.toString() + "</table></td></tr>"
	            + "</table>"
	            + "<p>We kindly request you to review the updated rules and ensure that you understand the changes.</p>"
	            + "<p>If you have any questions or require further clarification, feel free to reach out to our support team at <a href='mailto:support@zoy.com'>support@zoy.com</a>.</p>"
	            + "<p>Thank you for your continued support and cooperation.</p>"
	            + "<p>Best regards,<br>ZOY Administrator</p>";

	    email.setBody(message);
	    email.setContent("text/html");

	    try {
	        emailService.sendEmail(email, null);
	    } catch (Exception e) {
	        log.error("Failed to send Short-Term Stay Policy update email: " + e.getMessage(), e);
	    }
	}

	public void sendExistingOwnerZoyCode(String owneremail, String firstName, String lastName, String zoyCode) {
		Email email = new Email();
		email.setFrom(zoyAdminMail);
		List<String> to = new ArrayList<>();
		to.add(owneremail);
		email.setTo(to);
		email.setSubject("Welcome to ZOY! Unlock Your Journey With Zoy Today!");
		String message = "<p>Dear " + firstName + " " + lastName + ",</p>"
				+ "<p>We are excited to welcome you to ZOY, your trusted companion for hassle-free PG Management. To get started, we've made it quick and simple for you!</p>"
				+ "<p><strong>Your Invitation Code: </strong>" + zoyCode + "</p>"
				+ "<p>Please use this code to complete your new property registration in the app.</p>"
				+ "<h4><strong>Steps to Add Property:</strong></h4>" + "<ul>" 
				+ "<li>Open the app with existing credentials.</li>"
				+ "<li>Go to profile and click on add property.</li>"
				+ "<li>Enter your invitation code provided above.</li>"
				+ "<li>Start filling the new property details.</li>"
				+ "<li>Start exploring amazing functions tailored just for you!</li>" + "</ul>"
				+ "<p>This verification ensures you a secure experience.</p>"
				+ "<p>If you have any questions or need assistance, feel free to reach out to our support team at <a href='mailto:"
				+ zoySupportMail + "'>" + zoySupportMail + "</a>.</p>"
				+ "<p>Welcome aboard, and we can't wait to make your experience amazing!</p>" + "<p>Best regards,</p>"
				+ "<p>ZOY Administrator</p>";

		email.setBody(message);
		email.setContent("text/html");
		try {
			emailService.sendEmail(email, null);
		} catch (Exception e) {
			log.error("Error occurred while sending the registration email to " + owneremail + ": " + e.getMessage(),
					e);
		}
	
		
	}

	
	
}
