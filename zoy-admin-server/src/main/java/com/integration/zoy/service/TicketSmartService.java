package com.integration.zoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@Service
public class TicketSmartService {

	@Value("${app.ticket.smart.url}")
	private String ticketSmartUrl;

	@Value("${app.ticket.smart.api.key}")
	private String ticketSmartApiKey;

	@Value("${app.ticket.smart.api.secret}")
	private String ticketSmartApiSecret;

	@Autowired
	RestTemplate restTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String getTicketSmartAPI(String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-KEY", ticketSmartApiKey);
		headers.set("X-API-SECRET", ticketSmartApiSecret);
		HttpEntity<String> entity = new HttpEntity<>("",headers);
		try {
			ResponseEntity<String> response = restTemplate.exchange(ticketSmartUrl+"/"+url,HttpMethod.GET,entity,String.class);
			System.out.println(response.getBody());
			if(response.getStatusCodeValue() == 200) {
				return response.getBody();
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("Error getting ticket smart get api response for "+ url +" "+ e);
			return null;
		}
	}

	public String postTicketSmartAPI(String url,Object pojo) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-KEY", ticketSmartApiKey);
		headers.set("X-API-SECRET", ticketSmartApiSecret);
		try {
			String json = objectMapper.writeValueAsString(pojo);
			System.out.println(json);
			HttpEntity<String> entity = new HttpEntity<>(json,headers);
			ResponseEntity<String> response = restTemplate.exchange(ticketSmartUrl+"/"+url,HttpMethod.POST,entity,String.class);
			System.out.println("Response:");
			System.out.println(response.getBody());
			if(response.getStatusCodeValue() == 200) {
				return response.getBody();
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("Error getting ticket smart get api response for "+ url +" "+ e);
			return null;
		}
	}


}
