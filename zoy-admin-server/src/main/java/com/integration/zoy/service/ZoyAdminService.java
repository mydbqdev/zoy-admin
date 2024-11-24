package com.integration.zoy.service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.integration.zoy.utils.NotificationRequest;
import com.integration.zoy.utils.OtpVerification;

import kotlin.Pair;

@Service
public class ZoyAdminService {

	@Autowired
	RestTemplate httpsRestTemplate;

	@Autowired
	CommonDBImpl commonDBImpl;

	@Autowired
	WhatsAppService whatsAppService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	WebClient webClient;

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

	public Pair<Boolean,String> processTenant(String ownerId, String propertyId, MultipartFile file) {

		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			if (file != null && !file.isEmpty()) {
				body.add("file", file.getResource());
			}
			byte[] credEncoded = Base64.getEncoder().encode((zoyServerUserName+":"+zoyServerPassword).getBytes());
			String authStringEnc = new String(credEncoded);
			ResponseEntity<String> responses = webClient.post()
					.uri(zoyServerUrl+ownerId+"/"+propertyId+"/writeData")
					.header("Authorization", "Basic " + authStringEnc) 
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData(body))
					.retrieve()
					.toEntity(String.class).block();
			if(responses.getStatusCodeValue()==200) {
				return new Pair<Boolean,String>(true, responses.getBody());
			} else {
				return new Pair<Boolean,String>(false, responses.getBody());
			}
		} catch (Exception e) {
			return new Pair<Boolean,String>(false, e.getMessage());
		}
	}

	public Pair<Boolean, String> processProperty(String ownerId, String propertyId, MultipartFile file) {
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			if (file != null && !file.isEmpty()) {
				body.add("file", file.getResource());
			}
			byte[] credEncoded = Base64.getEncoder().encode((zoyServerUserName+":"+zoyServerPassword).getBytes());
			String authStringEnc = new String(credEncoded);
			ResponseEntity<String> responses = webClient.post()
					.uri(zoyServerUrl+ownerId+"/"+propertyId+"/upload_xlsx")
					.header("Authorization", "Basic " + authStringEnc) 
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData(body))
					.retrieve()
					.toEntity(String.class).block();
			if(responses.getStatusCodeValue()==200) {
				return new Pair<Boolean,String>(true, responses.getBody());
			} else {
				return new Pair<Boolean,String>(false, responses.getBody());
			}
		} catch (Exception e) {
			return new Pair<Boolean,String>(false, e.getMessage());
		}
	}
}