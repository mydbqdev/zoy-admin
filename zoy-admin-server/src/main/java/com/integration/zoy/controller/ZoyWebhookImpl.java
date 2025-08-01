package com.integration.zoy.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface ZoyWebhookImpl {

	
	@Operation(summary = "Vendor users details", description = "getting vendor users details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Vendor" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
    @PostMapping(value = "/zoy_admin/zoyWebhook",
        produces = { "application/json" }, 
        consumes = { "application/json" })
	ResponseEntity<String> zoyWebhook(HttpServletRequest request,@RequestHeader("X-Ticket-Signature") String ticketSignature,
			@RequestHeader("X-Ticket-Event-Id") String eventId);
	
	
	
}
