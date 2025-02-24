package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface ZoySuperAdminImpl {

	@Operation(summary = "Get HomePage Card Details", description = "Getting HomePage Card Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Super Admin Dashboard" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/admin_cards_details",
	produces = { "application/json" })
	ResponseEntity<String> zoySuperAdminCardsDetails();
	
	@Operation(summary = "Get Tenant Card Details", description = "Getting Tenant Card Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Super Admin Dashboard" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/tenant_card_details",
	produces = { "application/json" })
	ResponseEntity<String> zoyTenantCardsDetails();
}
