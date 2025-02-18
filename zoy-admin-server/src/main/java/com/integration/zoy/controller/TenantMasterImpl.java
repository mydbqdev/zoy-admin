package com.integration.zoy.controller;

import org.simpleframework.xml.core.Validate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.model.UserStatus;
import com.integration.zoy.utils.PaginationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validate
public interface TenantMasterImpl {

	@Operation(summary = "Get Tenant Details", description = "Getting Tenant Management Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Tenant Management" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/manage-tenants",
	produces = { "application/json" })
	ResponseEntity<String> zoyTenantManagement(@RequestBody PaginationRequest paginationRequest);
	
	
	@Operation(summary = "Get Tenant Details", description = "Getting Tenant Management Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Tenant Management" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/manage-tenants-details",
	produces = { "application/json" })
	ResponseEntity<String> zoyTenantManagementDetails(@RequestParam("tenantid") String tenantid);
	
	@Operation(summary = "Update Tenant Status", description = "updating the Tenant Status", security = {
			@SecurityRequirement(name = "basicAuth") }, tags = { "Tenant Management" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/updateTenantStatus", 
	produces = { "application/json" })
	ResponseEntity<String> zoyTenantStatusUpdate(@RequestBody UserStatus userStatus);
	
}
