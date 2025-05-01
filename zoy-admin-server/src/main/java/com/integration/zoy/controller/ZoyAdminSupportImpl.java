package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.model.FollowUp;
import com.integration.zoy.model.TicketAssign;
import com.integration.zoy.model.UpdateStatus;
import com.integration.zoy.utils.PaginationRequest;
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
	ResponseEntity<String> getSupportUserDetails();
	
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
	
	@Operation(summary = "Get Assigned Lead Tickets", description = "Getting Assigned Lead Tickets", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/assigned_tickets",
	produces = { "application/json" })
	ResponseEntity<String> getAssignedLeadTickets(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Update Assigned Ticket Status", description = "Update Assigned Ticket Status", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/update_inquiry_status",
	produces = { "application/json" })
	ResponseEntity<String> updateInquiryStatus(@RequestBody UpdateStatus updateStatus);
	
	@Operation(summary = "Save follow up details ", description = "Saving remarks & follow up details ", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/follow_up_details",
	produces = { "application/json" })
	ResponseEntity<String> createFollowUp(@RequestBody FollowUp followUp);
	
	@Operation(summary = "get all follow up details ", description = "fetching all follow up details ", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/follow_up_details",
	produces = { "application/json" })
	ResponseEntity<String> FetchAllFollowUpDetails();
	
	
	/**
	 *  if isUserActivity is false then admin all ticket list
	 *  if isUserActivity is true then support user all ticket list
	 * @param paginationRequest
	 * @return
	 */
	@Operation(summary = "My /All Open Support Ticket list", description = "getting support ticket details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Support Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/open-support-ticket",
	produces = { "application/json" })
	ResponseEntity<String> zoyOpenSupportTicketList(@RequestBody PaginationRequest paginationRequest);
	
	/**
	 *  if isUserActivity is false then admin all ticket list
	 *  if isUserActivity is true then support user all ticket list
	 * @param paginationRequest
	 * @return
	 */
	@Operation(summary = "My /All Closed Support Ticket list", description = "getting support ticket details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Support Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/close-support-ticket",
	produces = { "application/json" })
	ResponseEntity<String> zoyCloseSupportTicketList(@RequestBody PaginationRequest paginationRequest);
}

