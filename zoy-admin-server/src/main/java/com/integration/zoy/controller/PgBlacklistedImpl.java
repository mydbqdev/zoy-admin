package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.model.BlacklistDeleteRequest;
import com.integration.zoy.model.PgBlacklistedModel;
import com.integration.zoy.model.PgOwnerFilter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface  PgBlacklistedImpl {
	
	@Operation(summary = "save blacklisted detalis", description = "to save the details of the blacklisted", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Pg Owner" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "409", description = "Already exist as zoy code against email id"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/save_blacklisted",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> saveBlacklistedDetails(@RequestBody PgBlacklistedModel model);
	
	
	@Operation(summary = "save blacklisted detalis", description = "to save the details of the blacklisted", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Pg Owner" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "409", description = "Already exist as zoy code against email id"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@DeleteMapping(value = "/zoy_admin/delete_blacklisted",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> deleteBlacklistedDetails(@RequestBody BlacklistDeleteRequest model);
	
	@Operation(summary = "Get blacklisted details", description = "Get paginated list of blacklisted data with optional search by email/mobile",
	           security = { @SecurityRequirement(name = "basicAuth") }, tags={ "Pg Owner" })
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
	        @ApiResponse(responseCode = "400", description = "Bad Request"),
	        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/blacklisted", produces = { "application/json" })
	ResponseEntity<String> getBlacklisted(@RequestBody PgOwnerFilter pgOwnerFilter);
	
	@Operation(summary = "Get blacklisted details", description = "Get paginated list of blacklisted data with optional search by email/mobile",
	           security = { @SecurityRequirement(name = "basicAuth") }, tags={ "Pg Owner" })
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
	        @ApiResponse(responseCode = "400", description = "Bad Request"),
	        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/getBlacklisted", produces = { "application/json" })
	ResponseEntity<String> getAllBlacklisted();
	

}
