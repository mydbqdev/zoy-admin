package com.integration.zoy.controller;

import org.simpleframework.xml.core.Validate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.utils.PaginationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validate
public interface SalesMasterImpl {

	@Operation(summary = "Create Admin User", description = "Creating new admin User", security = {
			@SecurityRequirement(name = "basicAuth") }, tags = { "Admin User & Role" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/zoyAdminSalesCreateUser", produces = { "application/json" }, consumes = {
			"application/json" })
	ResponseEntity<String> zoyAdminSalesCreateUser(@RequestBody ZoyPgSalesMasterModel pgSalesMasterModel);
	
	
	@Operation(summary = "sales users details", description = "getting sales users details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/getzoyPgSalesUsersDetails",
	produces = { "application/json" })
	ResponseEntity<String> getzoyPgSalesUsersDetails(@RequestBody PaginationRequest paginationRequest);
	

}
