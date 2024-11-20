package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.integration.zoy.model.PgOwnerFilter;
import com.integration.zoy.model.PgOwnerMasterModel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@Validated
public interface PgOwnerMasterImpl {
	
	@Operation(summary = "save owner detalis", description = "to save the details of the owner", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Pg Owner" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/savePgOwnerData",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> pgOwnerDetalaisSave(@RequestBody PgOwnerMasterModel model);
	

	@Operation(summary = "resend owner detalis", description = "to resend the details of the owner", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Pg Owner" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/resendPgOwnerData",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> pgOwnerDetalaisresend(@RequestBody PgOwnerMasterModel model);
		
	
	@Operation(summary = "Get all owner details", description = "To retrieve the details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Pg Owner" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/getAllPgOwnerData",
	produces = { "application/json" })
	ResponseEntity<String> pgOwnerDetailsGet();
	
	@Operation(summary = "Search for posts", 
            description = "Retrieves posts based on the provided filter criteria.", 
            tags = { "Post Controller" })
 @ApiResponses(value = {
     @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
     @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = "application/json")),
     @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json")) })
 @RequestMapping(value = "/zoy_admin/PgOwnersDataBySearch", 
                 produces = { "application/json" }, 
                 method = {RequestMethod.GET,RequestMethod.POST})
 ResponseEntity<Object> searchPgOwnerDetails(
     @Parameter(description = "Filter criteria for retrieving Pg owner details", required = true, schema = @Schema(implementation = PgOwnerFilter.class)) 
     @RequestBody PgOwnerFilter pgOwnerFilter);
}
