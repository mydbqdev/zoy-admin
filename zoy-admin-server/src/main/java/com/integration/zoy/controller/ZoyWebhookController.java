package com.integration.zoy.controller;

import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.zoy.service.NotificationsAndAlertsService;
import com.integration.zoy.utils.ReviewRatingDto;
import com.integration.zoy.utils.WebhookPayload;

@Controller
public class ZoyWebhookController implements ZoyWebhookImpl{

	@Value("${app.zoy.webhook.secret}")
	private String zoyServerSecret;

	@Autowired
	NotificationsAndAlertsService notificationsAndAlertsService;

	private static final Logger log = LoggerFactory.getLogger(ZoyWebhookController.class);
	private final ObjectMapper objectMapper;
	public ZoyWebhookController(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	@Override
	public ResponseEntity<String> zoyWebhook(HttpServletRequest request, String ticketSignature,String eventId) {
		try (InputStream inputStream = request.getInputStream()) {
			if(zoyServerSecret.equals(ticketSignature)) {
				switch (eventId) {
				case "review.status":
					WebhookPayload<ReviewRatingDto> status = objectMapper.readValue(inputStream,new TypeReference<WebhookPayload<ReviewRatingDto>>() {});
					log.info("Received webhook event: {}", status.getEvent());
					if(status.getData()!=null) {
						ReviewRatingDto dto=status.getData();
						notificationsAndAlertsService.reviewRatingAlert(dto);
					}
					break;
				default:
					log.info("Unhandled webhook event: {}", eventId);
					return ResponseEntity.ok("Unhandled event");
				}

				return ResponseEntity.ok("Webhook processed successfully");
			} else {
				return ResponseEntity.badRequest().body("Invalid Signature");
			}

		} catch (Exception ex) {
			log.error("Error processing webhook: ", ex);
			return ResponseEntity.internalServerError().body("Failed to process webhook");
		}
	}

}
