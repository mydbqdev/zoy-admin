package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
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
import com.integration.zoy.service.UserDBImpl;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenantDetails;
@RestController
@RequestMapping("")
public class TenantMasterController implements TenantMasterImpl{

	private static final Logger log = LoggerFactory.getLogger(ZoyAdminMasterController.class);
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
	UserDBImpl userDBImpl;
	
	@Override
	public ResponseEntity<String> zoyTenantManagement(PaginationRequest paginationRequest) {
	    ResponseBody response = new ResponseBody();
		try {
			Page<TenantDetails> ownerPropertyList = userDBImpl.findAllTenantDetails( paginationRequest);
	            return new ResponseEntity<>(gson2.toJson(ownerPropertyList), HttpStatus.OK);
 		}catch (DataAccessException dae) {
	        log.error("Database error occurred while fetching Tenant details: " + dae.getMessage(), dae);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError("Database error: Unable to fetch Tenant details");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        
	    }catch (Exception e) {
	        log.error("Unexpected error occurredAPI:/zoy_admin/manage-tenants", e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }

	}

}
