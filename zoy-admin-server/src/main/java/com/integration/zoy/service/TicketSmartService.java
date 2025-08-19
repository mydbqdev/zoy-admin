package com.integration.zoy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

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
	RestTemplate httpsRestTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	public String getTicketSmartAPI(String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-API-KEY", ticketSmartApiKey);
		headers.set("X-API-SECRET", ticketSmartApiSecret);
		HttpEntity<String> entity = new HttpEntity<>("",headers);
		try {
			ResponseEntity<String> response = httpsRestTemplate.exchange(ticketSmartUrl+"/"+url,HttpMethod.GET,entity,String.class);
			//System.out.println(response.getBody());
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
			//System.out.println(json);
			HttpEntity<String> entity = new HttpEntity<>(json,headers);
			ResponseEntity<String> response = httpsRestTemplate.exchange(ticketSmartUrl+"/"+url,HttpMethod.POST,entity,String.class);
			//System.out.println("Response:");
			//System.out.println(response.getBody());
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

	
	public String postTicketSmartAPI(String url, String ticketJson, List<MultipartFile> mediaFiles) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		//headers.setAccept(Collections.singletonList(MediaType.ALL));
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		headers.set("X-API-KEY", ticketSmartApiKey);
		headers.set("X-API-SECRET", ticketSmartApiSecret);
		try {
			MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
			body.add("ticket", ticketJson);
			if (mediaFiles != null && !mediaFiles.isEmpty()) {
				for (MultipartFile file : mediaFiles) {
					ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
						@Override
						public String getFilename() {
							return file.getOriginalFilename();
						}
					};
					HttpHeaders filePartHeader = new HttpHeaders();
					filePartHeader.setContentDispositionFormData("files", file.getOriginalFilename());
					filePartHeader.setContentType(MediaType.parseMediaType(file.getContentType()));
					HttpEntity<ByteArrayResource> fileEntity = new HttpEntity<>(resource, filePartHeader);
					body.add("files", fileEntity);
				}
			} else {
				body.add("files", null);
			}

			HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
			ResponseEntity<String> response = httpsRestTemplate.exchange(ticketSmartUrl+"/"+url,HttpMethod.POST,entity,String.class);
			//System.out.println("Response:");
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
