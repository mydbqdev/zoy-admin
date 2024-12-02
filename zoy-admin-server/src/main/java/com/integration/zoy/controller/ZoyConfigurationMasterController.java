package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.model.ZoyToken;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.utils.BulkUploadData;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class ZoyConfigurationMasterController implements ZoyConfigurationMasterImpl{

	private static final Logger log = LoggerFactory.getLogger(ZoyConfigurationMasterController.class);
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	@Autowired
	OwnerDBImpl ownerDBImpl;


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateToken(ZoyToken details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if(details.getTokenId()!=null && !details.getTokenId().isEmpty()) {
				ZoyPgTokenDetails tokenDetails=ownerDBImpl.findTokenDetails(details.getTokenId());
				if(tokenDetails==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get token details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				tokenDetails.setFixedToken(details.getFixedToken()!=0?new BigDecimal(details.getFixedToken()):BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken()!=0?new BigDecimal(details.getVariableToken()):BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Updated Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgTokenDetails tokenDetails=new ZoyPgTokenDetails();
				tokenDetails.setFixedToken(details.getFixedToken()!=0?new BigDecimal(details.getFixedToken()):BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken()!=0?new BigDecimal(details.getVariableToken()):BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetToken() {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgTokenDetails tokenDetails=ownerDBImpl.findAllToken().get(0);
			if(tokenDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyToken token=new ZoyToken();
			token.setTokenId(tokenDetails.getTokenId());
			token.setVariableToken(tokenDetails.getVariableToken()!=null?tokenDetails.getVariableToken().doubleValue():0.0);
			token.setFixedToken(tokenDetails.getFixedToken()!=null?tokenDetails.getFixedToken().doubleValue():0.0);
			return new ResponseEntity<>(gson.toJson(token), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
