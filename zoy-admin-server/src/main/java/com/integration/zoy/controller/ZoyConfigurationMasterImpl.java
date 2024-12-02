package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.integration.zoy.model.LoginDetails;
import com.integration.zoy.model.ZoyToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface ZoyConfigurationMasterImpl {

	@Operation(summary = "Admin Configration Token", description = "Creating/Updating Admin Configration Token", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/token",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateToken(@RequestBody ZoyToken details);
	
	@Operation(summary = "Admin Configration Token", description = "Getting Admin Configration Token", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/getToken",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminConfigGetToken();
	
	
}
