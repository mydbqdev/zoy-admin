package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.SuperAdminCardsDetails;

@RestController
@RequestMapping("")
public class ZoySuperAdminController implements ZoySuperAdminImpl {
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminUserController.class);
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	@Autowired
	AdminDBImpl adminDBImpl;

	@Override
	public ResponseEntity<String> zoySuperAdminCardsDetails() {
		ResponseBody response = new ResponseBody();
		try {
			List<Object[]> result=adminDBImpl.findSuperAdminCardsDetails();
			if (result == null || result.isEmpty()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("No data found for super admin card details.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			Object[] row = result.get(0);
			Long usersWithNonNullPin = (row[0] != null && row[0] instanceof Number) ? ((Number) row[0]).longValue() : 0L;
			Long activeOwnersCount = (row[1] != null && row[1] instanceof Number) ? ((Number) row[1]).longValue() : 0L;
			Long activePropertiesCount = (row[2] != null && row[2] instanceof Number) ? ((Number) row[2]).longValue() : 0L;
			SuperAdminCardsDetails superAdminCardsDetails = new SuperAdminCardsDetails(
					usersWithNonNullPin, activeOwnersCount, activePropertiesCount,0L);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Successfully fetched super admin card details.");
			return new ResponseEntity<>(gson.toJson(superAdminCardsDetails), HttpStatus.OK);
		} catch (DataAccessException e) {
			log.error("Database error API:/zoy_admin/admin_cards_details.zoySuperAdminCardsDetails ", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Database error occurred while fetching super admin card details.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			log.error("Unexpected error API:/zoy_admin/admin_cards_details.zoySuperAdminCardsDetails", e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("An unexpected error occurred.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}



}
