package com.integration.zoy.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JsonParserUtil {
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> T fromJson(String json, TypeReference<T> typeReference) throws Exception {
		return objectMapper.readValue(json, typeReference);
	}
}