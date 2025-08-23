package com.integration.zoy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.model.NotificationManagerRequest;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.NotificationReq;

@Service
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
	private Gson gson = (new GsonBuilder()).create();
	@Autowired
	RestTemplate httpsRestTemplate;

//	@Autowired
//	FcmService fcmService;


	@Value("${app.notification.v2.url}")
	private String notificationV2Url;

	@Value("${app.notification.v2.apikey}")
	private String notificationV2ApiKey;

	public String sendNotification(NotificationManagerRequest req) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("x-api-key", notificationV2ApiKey);

			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
			if (notEmpty(req.getEmail())) 
				formData.add("email", req.getEmail());
			if (notEmpty(req.getMergeInfo())) 
				formData.add("mergeInfo", req.getMergeInfo());
			if (notEmpty(req.getTemplateId())) 
				formData.add("templateId", req.getTemplateId());
			if (notEmpty(req.getMobile())) 
				formData.add("mobile", req.getMobile());
			if (req.getFile() != null) {
				formData.add("files", new ByteArrayResource(req.getFile().getBytes()) {
					@Override
					public String getFilename() {
						return req.getFile().getOriginalFilename();
					}
				});
			}
			//formData.add("files", req.getFile());
			if (notEmpty(req.getToken())) {
	            for (String token : req.getToken()) {
	                formData.add("token", token);
	            }
	        }

	        log.info("Form Data: " + gson.toJson(formData));
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
			ResponseEntity<String> response = httpsRestTemplate.postForEntity(notificationV2Url+"unifiedmsg/send", requestEntity, String.class);
			log.info("Notification Sent: " + response.getBody());
			return response.getBody();
		} catch (Exception e) {
			log.error("Error sending notification: " + e);
			return null;
		}
	}

	
	public String sendEmail(Email email,MultipartFile file) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("x-api-key", notificationV2ApiKey);

			MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
	        for (String to : email.getTo()) {
	            formData.add("to", to);
	        }
	        if (email.getCc() != null) {
	            for (String cc : email.getCc()) {
	                formData.add("cc", cc);
	            }
	        }
	        formData.add("subject", email.getSubject());
	        formData.add("htmlbody", email.getBody());
	        if (file != null) {
	        	formData.add("files", new ByteArrayResource(file.getBytes()) {
	        		@Override
	        		public String getFilename() {
	        			return file.getOriginalFilename();
	        		}
	        	});
	        }
	        log.info("Form Data: " + gson.toJson(formData));
			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
			ResponseEntity<String> response = httpsRestTemplate.postForEntity(notificationV2Url+"email/send", requestEntity, String.class);
			log.info("Notification Sent: " + response.getBody());
			return response.getBody();
		} catch (Exception e) {
			log.error("Error sending email notification: " + e);
			return null;
		}
	}
	
	
	public String sendInappNotification(NotificationReq req) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.set("x-api-key", notificationV2ApiKey);
			HttpEntity<NotificationReq> requestEntity = new HttpEntity<>(req, headers);
			ResponseEntity<String> response = httpsRestTemplate.postForEntity(notificationV2Url+"inapp/send", requestEntity, String.class);
			log.info("Notification Sent: " + response.getBody());
			return response.getBody();
		} catch (Exception e) {
			log.error("Error sending inapp notification: " + e);
			return null;
		}
	}
	
	private boolean notEmpty(String val) {
		return val != null && !val.trim().isEmpty();
	}

	private boolean notEmpty(List<String> val) {
		return val != null && !val.isEmpty();
	}

	public NotificationManagerRequest buildUserRequest(UserMaster master, String mergeVars,
			String templateId,MultipartFile file) {
		List<String> token = new ArrayList<>();
//		if (master.getUserId() != null) {
//			Set<String> tokenSet = fcmService.getUsersMap().get(master.getUserId());
//			if(tokenSet !=null && !tokenSet.isEmpty())
//				token = tokenSet.stream().collect(Collectors.toList());
//		}
//		log.info("User id " + master.getUserId() +" : token : "+ this.gson.toJson(token));
		NotificationManagerRequest request = new NotificationManagerRequest();
		if (isNotEmpty(master.getUserEmail())) 
			request.setEmail(master.getUserEmail());
		if (isNotEmpty(master.getUserMobile())) 
			request.setMobile(master.getUserMobile());
		if (mergeVars != null && !mergeVars.isEmpty()) 
			request.setMergeInfo(mergeVars);
		if (isNotEmpty(templateId)) 
			request.setTemplateId(templateId);
		if (isNotEmpty(token)) 
			request.setToken(token);
		if (file != null) 
			request.setFile(file);
		return request;
	}

	public NotificationManagerRequest buildOwnerRequest(ZoyPgOwnerDetails master, String mergeVars,
			String templateId,MultipartFile file) {
		List<String> token = new ArrayList<>();
//		if (master.getPgOwnerId() != null) {
//			Set<String> tokenSet = fcmService.getUsersMap().get(master.getPgOwnerId());
//			if(tokenSet !=null && !tokenSet.isEmpty())
//				token = tokenSet.stream().collect(Collectors.toList());
//		}
//		log.info("User id " + master.getPgOwnerId() +" : token : "+ this.gson.toJson(token));
		NotificationManagerRequest request = new NotificationManagerRequest();
		if (isNotEmpty(master.getPgOwnerEmail())) 
			request.setEmail(master.getPgOwnerEmail());
		if (isNotEmpty(master.getPgOwnerMobile())) 
			request.setMobile(master.getPgOwnerMobile());
		if (mergeVars != null && !mergeVars.isEmpty()) 
			request.setMergeInfo(mergeVars);
		if (isNotEmpty(templateId)) 
			request.setTemplateId(templateId);
		if (isNotEmpty(token)) 
			request.setToken(token);
		if (file != null) 
			request.setFile(file);
		return request;
	}

	private boolean isNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

	private boolean isNotEmpty(List<String> str) {
		return str != null && !str.isEmpty();
	}


	public NotificationManagerRequest buildSMSRequest(String phoneNumber, String mergeVars, String templateId) {
		NotificationManagerRequest request = new NotificationManagerRequest();
		if (isNotEmpty(phoneNumber)) 
			request.setMobile(phoneNumber);
		if (mergeVars != null && !mergeVars.isEmpty()) 
			request.setMergeInfo(mergeVars);
		if (isNotEmpty(templateId)) 
			request.setTemplateId(templateId);
		return request;
	}

//	public NotificationManagerRequest buildInAppRequest(String userId,String mobile,String email, String mergeVars, String templateId,MultipartFile file) {
//		List<String> token = new ArrayList<>();
//		if (userId != null) {
//			Set<String> tokenSet = fcmService.getUsersMap().get(userId);
//			if(tokenSet !=null && !tokenSet.isEmpty())
//				token = tokenSet.stream().collect(Collectors.toList());
//		}
//		log.info("User id " + userId +" : token : "+ this.gson.toJson(token));
//		NotificationManagerRequest request = new NotificationManagerRequest();
//		if (isNotEmpty(email)) 
//			request.setEmail(email);
//		if (isNotEmpty(mobile)) 
//			request.setMobile(mobile);
//		if (mergeVars != null && !mergeVars.isEmpty()) 
//			request.setMergeInfo(mergeVars);
//		if (isNotEmpty(templateId)) 
//			request.setTemplateId(templateId);
//		if (isNotEmpty(token)) 
//			request.setToken(token);
//		if (file != null) 
//			request.setFile(file);
//		return request;
//	}
	
	
//	public void sendInapp(String id, String body) {
//		List<String> token = new ArrayList<>();
//		if (id != null) {
//			Set<String> tokenSet = fcmService.getUsersMap().get(id);
//			if(tokenSet !=null && !tokenSet.isEmpty())
//				token = tokenSet.stream().collect(Collectors.toList());
//		}
//		log.info("User id " + id +" : token : "+ this.gson.toJson(token));
//		if(!token.isEmpty()) {
//			for(String inappToken : token) {
//				NotificationReq inapp = new NotificationReq();
//				inapp.setBody(body);
//				inapp.setTitle(ZoyConstant.ZOY);
//				inapp.setToken(inappToken);
//				sendInappNotification(inapp);
//			}
//		}
//	}
}


