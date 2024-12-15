package com.integration.zoy.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.utils.OtpVerification;
import com.integration.zoy.utils.Whatsapp;

@Service
public class ZoyAdminService {
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminService.class);
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

	public Map<String, String> getForgotPasswordOtp() throws WebServiceException {
		return otpMap;
	}

	public String validateOtp(OtpVerification otpVerify)throws WebServiceException {
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
	public void processTenant(String ownerId, String propertyId, byte[] file,String fileName,String jobExeId) throws WebServiceException {
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
	public void processProperty(String ownerId, String propertyId, byte[] file,String fileName,String jobExecutionId) throws WebServiceException {
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
			log.error("error:::"+e);
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