package com.integration.zoy.service;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.integration.zoy.utils.NotificationRequest;
import com.integration.zoy.utils.Whatsapp;

@Service
public class WhatsAppService {

	private static final Logger log = LoggerFactory.getLogger(WhatsAppService.class);
	private static final Gson gson = new GsonBuilder().create();

	@Autowired
	WebClient webClient;

	@Value("${app.notification.url}")
	private String notificationUrl;

	@Value("${app.notification.username}")
	private String notificationUserName;

	@Value("${app.notification.password}")
	private String notificationPassword;


	public void sendWhatsappMessage(Whatsapp whatsapp) {
		try {
			NotificationRequest notificationRequest = new NotificationRequest();
			notificationRequest.setProcessType("immediate");
			notificationRequest.setAppName("ZOY PG");
			notificationRequest.setMessageType("whatsapp");
			notificationRequest.setWhatsapp(whatsapp);

			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("notification", notificationRequest);

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
