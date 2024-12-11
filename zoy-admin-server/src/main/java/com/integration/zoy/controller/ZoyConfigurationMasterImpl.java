package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyShareDetails;
import com.integration.zoy.utils.ZoyDataGroupingDto;
import com.integration.zoy.utils.ZoyOtherChargesDto;
import com.integration.zoy.utils.ZoyPgSecurityDepositDetailsDTO;
import com.integration.zoy.utils.ZoyPgSecurityDepositRefundRuleDto;
import com.integration.zoy.utils.ZoyPgTokenDetailsDTO;

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
	@PostMapping(value = "/zoy_admin/config/token_advance",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateToken(@RequestBody ZoyPgTokenDetailsDTO details);

	@Operation(summary = "Admin Configration Security Deposit Limits", description = "Getting Admin Configration Security Deposit Limits", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/security-deposit-limits",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositLimits(@RequestBody ZoyPgSecurityDepositDetailsDTO details);



	@Operation(summary = "Admin Configration Before Check In", description = "Creating/Updating Admin Configration Before Check In", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/before-check-in",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(@RequestBody ZoyBeforeCheckInCancellation details);

	@Operation(summary = "Delete Admin Configuration Before Check In", description = "Deletes Admin Configuration Before Check In by cancellationId", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@DeleteMapping(value = "/zoy_admin/config/delete-early-check-out",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigDeleteBeforeCheckIn(@RequestBody ZoyBeforeCheckInCancellation cancellationID);

	@Operation(summary = "Admin Configration Zoy Share", description = "Creating/Updating Admin Configration Zoy Share", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/zoy-share",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateZoyShare(@RequestBody ZoyShareDetails details);

	@Operation(summary = "Admin Configration Zoy Share", description = "Getting Admin Configration Zoy Share", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/zoy-share",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminConfigGetZoyShare();

	@Operation(summary = "Admin Configration Other Charges", description = "Creating/Updating Admin Configration Other Charges", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/other-charges",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateOtherCharges(@RequestBody ZoyOtherChargesDto details);

	@Operation(summary = "Admin Configration Data Grouping", description = "Creating/Updating Admin Configration Data Grouping", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/data-grouping",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateDataGrouping(@RequestBody ZoyDataGroupingDto details);




	@Operation(summary = "Admin Configration Security Deposit Refund Rules ", description = "Getting Admin Configration Security Deposit Refund Rules", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/security-deposit-refund-rules",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositRefundRules(@RequestBody ZoyPgSecurityDepositRefundRuleDto  ruleDetails);



	@Operation(summary = "Get All Configuration Details ", description = "Getting All  Admin Configration Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/admin-configuration-details",
	produces = { "application/json" })
	ResponseEntity<String> getAllConfigurationDetails();

}
