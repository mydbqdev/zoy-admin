package com.integration.zoy.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.integration.zoy.utils.OtpVerification;

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
}