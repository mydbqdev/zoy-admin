package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.model.TicketAssign;
import com.integration.zoy.utils.SupportUsres;
import com.integration.zoy.utils.UserPaymentFilterRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface ZoyAdminSupportImpl {
	@Operation(summary = "Get Registered Lead Details", description = "Getting the details Leads", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/registered_lead_details",
	produces = { "application/json" })
	ResponseEntity<String> getRegisteredLeadDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get support user Details", description = "Getting support user details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/support_user_details",
	produces = { "application/json" })
	ResponseEntity<String> getSupportUserDetails(@RequestParam("inquiryNumber")String inquiryNumber);
	
	@Operation(summary = "Set Support User To the Lead", description = "assign ticket to the support team", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/assign_ticket",
	produces = { "application/json" })
	ResponseEntity<String> assignTicketsToSupportTeam(@RequestBody TicketAssign ticket);
	
	@Operation(summary = "Get Lead Follow up History", description = "Getting Lead Follow up History details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/lead_followup_history",
	produces = { "application/json" })
	ResponseEntity<String> getLeadFollowUpHostory(@RequestParam("inquiryNumber")String inquiryNumber);
}

