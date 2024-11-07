package com.integration.zoy.service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.utils.UserForgotPasswordDto;


@Service
public class ZoyAdminService {
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminService.class);
	private final SimpleDateFormat inputFormatter = new SimpleDateFormat("MMMM d, yyyy, h:mm:ss a Z");;
	private final  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();


	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	RestTemplate httpsRestTemplate;

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	@Autowired
	CommonDBImpl commonDBImpl;

	@Autowired
	WhatsAppService whatsAppService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	NumberToWordsService numberToWordsService;

	@Value("${app.zoy.image}")
	private String zoyImagesPath;


	private final Map<String, String> forgotPasswordOtp = new HashMap<>();


	public void createVerificationToken(UserProfile user, String token) {
		user.setVerifyToken(token);
		commonDBImpl.registerNewUserAccount(user);
	}

	public String validateVerificationToken(String token) {
		UserProfile user = commonDBImpl.findByVerifyToken(token);
		if (user == null) {
			return "invalid";
		}

		user.setEnabled(true);
		commonDBImpl.registerNewUserAccount(user);
//		ZoyPgOwnerDetails zoyPgOwnerDetails=new ZoyPgOwnerDetails();
//		zoyPgOwnerDetails.setPgOwnerEmail(user.getEmailId());
//		zoyPgOwnerDetails.setPgOwnerMobile(user.getMobileNo());
//		zoyPgOwnerDetails.setPgOwnerName(user.getPropertyOwnerName());
//		zoyPgOwnerDetails.setZoyCode(user.getZoyCode());
//		zoyPgOwnerDetails.setPgOwnerEncryptedAadhar(user.getEncryptedAadhar());
//		ownerDBImpl.savePgOwner(zoyPgOwnerDetails);

		zoyEmailService.sendRegistrationMail(user);

		return "valid";
	}


	public Map<String, String> getForgotPasswordOtp() {
		return forgotPasswordOtp;
	}

	public String validateOtpPassword(UserForgotPasswordDto userDto) {
		String otp=forgotPasswordOtp.get(userDto.getToken());
		if(otp!=null) {
			if(!otp.equals(userDto.getOtp())) {
				return "Invalid Otp";
			} 
		} else 
			return "Expired OTP";
		if(!userDto.getNewPassword().equals(userDto.getConfirmPassword())) {
			return "Passwords do not match.";
		}
		UserProfile user=commonDBImpl.findByVerifyToken(userDto.getToken());
		user.setPassword(new BCryptPasswordEncoder().encode(userDto.getNewPassword()));
		commonDBImpl.registerNewUserAccount(user);
		return "success";
	}


}
