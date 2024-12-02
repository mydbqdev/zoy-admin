package com.integration.zoy.service;

import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.utils.NotificationRequest;
import com.integration.zoy.utils.OtpVerification;
import com.integration.zoy.utils.Whatsapp;

import kotlin.Pair;

@Service
public class ZoyAdminService {

	@Autowired
	RestTemplate httpsRestTemplate;

	@Autowired
	CommonDBImpl commonDBImpl;

	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	WhatsAppService whatsAppService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	UploadService uploadService;

	@Autowired
	WebClient webClient;

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job tenantProcessJob;

	@Autowired
	private Job propertyProcessJob;

	@Autowired
	private TenantProcessTasklet tenantProcessTasklet;

	@Autowired
	private PropertyProcessTasklet propertyProcessTasklet;

	@Value("${app.zoy.server.username}")
	String zoyServerUserName;

	@Value("${app.zoy.server.password}")
	String zoyServerPassword;

	@Value("${app.zoy.server.url}")
	String zoyServerUrl;

	private final Map<String, String> otpMap = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public void storeOtp(String userId, String otp) {
		otpMap.put(userId, otp);
		scheduler.schedule(() -> {
			otpMap.remove(userId);
		}, 10, TimeUnit.MINUTES);
	}

	public Map<String, String> getForgotPasswordOtp() {
		return otpMap;
	}

	public String validateOtp(OtpVerification otpVerify) {
		String otp=otpMap.get(otpVerify.getEmail());
		if (otp != null) {
			if (!otp.equals(otpVerify.getOtp())) {
				return "Invalid OTP";
			}
			otpMap.remove(otpVerify.getEmail());  
			return "OTP validated successfully";
		} else {
			return "Expired OTP";
		}
	}

	@Async
	public void processTenant(String ownerId, String propertyId, byte[] file,String fileName,String jobExeId) {
		try {

			tenantProcessTasklet.setParameters(ownerId, propertyId, file);
			JobParameters jobParameters = new JobParametersBuilder()
					.addString("ownerId", ownerId)
					.addString("propertyId", propertyId)
					.addString("fileName", fileName)
					.addString("jobExecutionId", jobExeId) 
					.toJobParameters();
			jobLauncher.run(tenantProcessJob, jobParameters);
		} catch (Exception e) {

		}

	}

	@Async
	public void processProperty(String ownerId, String propertyId, byte[] file,String fileName,String jobExecutionId) {
		try {
			propertyProcessTasklet.setParameters(ownerId, propertyId, file);
			JobParameters jobParameters = new JobParametersBuilder()
					.addString("ownerId", ownerId)
					.addString("propertyId", propertyId)
					.addString("fileName", fileName)
					.addString("jobExecutionId", jobExecutionId) 
					.toJobParameters();
			jobLauncher.run(propertyProcessJob, jobParameters);

		} catch (Exception e) {
			System.out.println("error:::"+e);
		}
	}

	public String validateVerificationToken(String token) {
		UserProfile user = commonDBImpl.findByVerifyToken(token);
		if (user == null) {
			return "invalid";
		}

		if(!user.isEnabled()) {
			user.setEnabled(true);
			commonDBImpl.registerNewUserAccount(user);
			ZoyPgOwnerDetails zoyPgOwnerDetails=new ZoyPgOwnerDetails();
			zoyPgOwnerDetails.setPgOwnerEmail(user.getEmailId());
			zoyPgOwnerDetails.setPgOwnerMobile(user.getMobileNo());
			zoyPgOwnerDetails.setPgOwnerName(user.getPropertyOwnerName());
			zoyPgOwnerDetails.setZoyCode(user.getZoyCode());
			zoyPgOwnerDetails.setPgOwnerEncryptedAadhar(user.getEncryptedAadhar());
			ownerDBImpl.savePgOwner(zoyPgOwnerDetails);

			Whatsapp whatsapp = new Whatsapp();
			whatsapp.tonumber("+91" + user.getMobileNo());
			whatsapp.templateid(ZoyConstant.ZOY_OWNER_REG_WELCOME_MSG);
			Map<Integer, String> params = new HashMap<>();
			params.put(1, user.getPropertyOwnerName());
			whatsapp.setParams(params);
			whatsAppService.sendWhatsappMessage(whatsapp);

			zoyEmailService.sendRegistrationMail(user);

			return "valid";
		} else {
			return "Email already verified";
		}
	}
}