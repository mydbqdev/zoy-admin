package com.integration.zoy.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.NotificationRequest;
import com.integration.zoy.utils.Whatsapp;

@Service
public class EmailService {
	private static final Logger log = LoggerFactory.getLogger(EmailService.class);
	private static final Gson gson = new GsonBuilder().create();

	@Autowired
	WebClient webClient;

	@Value("${app.notification.url}")
	private String notificationUrl;

	@Value("${app.notification.username}")
	private String notificationUserName;

	@Value("${app.notification.password}")
	private String notificationPassword;


	public void sendEmail(Email email,MultipartFile attachment) {
		try {
			NotificationRequest notificationRequest = new NotificationRequest();
			notificationRequest.setProcessType("immediate");
			notificationRequest.setAppName("ZOY PG");
			notificationRequest.setMessageType("email");
			notificationRequest.setEmail(email);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("notification", notificationRequest);
			if (attachment != null && !attachment.isEmpty()) {
				body.add("attachments", attachment.getResource());
			}
			byte[] credEncoded = Base64.getEncoder().encode((notificationUserName+":"+notificationPassword).getBytes());
			String authStringEnc = new String(credEncoded);
			ResponseEntity<String> responses = webClient.post()
					.uri(notificationUrl)
					.header("Authorization", "Basic " + authStringEnc) 
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(BodyInserters.fromMultipartData(body))
					.retrieve()
					.toEntity(String.class).block();
			if(responses.getStatusCodeValue()==200) {
				log.info(responses.getBody());
			} else {
				log.error(responses.getBody());
			}
		}
		catch(HttpStatusCodeException ex) {
			log.error(ex.getMessage());
		}

	}
}
