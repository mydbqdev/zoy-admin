package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.integration.zoy.model.ZoyPgVendorModel;
import com.integration.zoy.utils.PaginationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;


@Validated
public interface ZoyVendorManagementImpl {

	
	@Operation(summary = "Vendor users details", description = "getting vendor users details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Vendor" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/getVendorDetails",
	produces = { "application/json" })
	ResponseEntity<String> getVendorDetails(@RequestBody PaginationRequest paginationRequest);

	@Operation(summary = "Vendor approval", description = "approving vendor users details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Vendor" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/approveVendorDetails",
	produces = { "application/json" })
	ResponseEntity<String> approveVendorDetails(@RequestBody ZoyPgVendorModel vendorModel);
	
	@Operation(summary = "Vendor reject", description = "rejecting vendor users details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Vendor" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/rejectingVendorDetails",
	produces = { "application/json" })
	ResponseEntity<String> rejectingVendorDetails(@RequestBody ZoyPgVendorModel vendorModel);
	
	@Operation(summary = "Vendor Status Update", description = "vendor users status update", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Vendor" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/vendorStatusUpdate",
	produces = { "application/json" })
	ResponseEntity<String> vendorStatusUpdate(@RequestBody ZoyPgVendorModel vendorModel);
	
}
