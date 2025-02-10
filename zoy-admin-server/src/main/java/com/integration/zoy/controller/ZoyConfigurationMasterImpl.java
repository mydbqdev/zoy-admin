package com.integration.zoy.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.integration.zoy.model.ShortTerm;
import com.integration.zoy.model.ZoyAfterCheckInCancellation;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyCompanyMasterModal;
import com.integration.zoy.model.ZoyCompanyProfileMasterModal;
import com.integration.zoy.model.ZoyPgEarlyCheckOutRule;
import com.integration.zoy.model.ZoySecurityDeadLine;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ZoyCompanyProfileMasterDto;
import com.integration.zoy.utils.ZoyDataGroupingDto;
import com.integration.zoy.utils.ZoyForceCheckOutDto;
import com.integration.zoy.utils.ZoyGstChargesDto;
import com.integration.zoy.utils.ZoyOtherChargesDto;
import com.integration.zoy.utils.ZoyPgSecurityDepositDetailsDTO;
import com.integration.zoy.utils.ZoyPgTokenDetailsDTO;
import com.integration.zoy.utils.ZoyRentingDuration;
import com.integration.zoy.utils.ZoyShortTermDto;

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
	ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(@RequestBody List<ZoyBeforeCheckInCancellation> details);

//	@Operation(summary = "Delete Admin Configuration Before Check In", description = "Deletes Admin Configuration Before Check In by cancellationId", security = {
//			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
//	@ApiResponses(value = { 
//			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
//			@ApiResponse(responseCode = "400", description = "Bad Request"),
//			@ApiResponse(responseCode = "404", description = "Not Found"),
//			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
//	@DeleteMapping(value = "/zoy_admin/config/deleteCancellationRefundRule",
//	produces = { "application/json" },
//	consumes = { "application/json"})
//	ResponseEntity<String> zoyAdminConfigDeleteBeforeCheckIn(@RequestBody ZoyBeforeCheckInCancellation cancellationID);

	@Operation(summary = "Admin Configration Early check out Rules ", description = "Create/Update Admin Configration Early check out Rules", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/early-checkout-rules",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUpadateEarlyCheckOutRules(@RequestBody ZoyPgEarlyCheckOutRule zoyPgEarlyCheckOut);

	@Operation(summary = "Admin Configration After Check In", description = "Create/UpdateAdmin Configration After Check In", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/after-check-in",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUpadateAfterCheckIn(@RequestBody ZoyAfterCheckInCancellation zoyAfterCheckInCancellation);
	
	@Operation(summary = "Admin Configration Security Deposit Deadline", description = "Create/UpdateAdmin Configration Security Deposit Deadline", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/security-deposit-deadline",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUpadateSecurityDepositDeadline(@RequestBody ZoySecurityDeadLine zoySecurityDeadLine);

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
	
	@Operation(summary = "Admin Configration GST Charges", description = "Creating/Updating Admin Configration GST Charges", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/gst-charges",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigCreateUpdateGstCharges(@RequestBody ZoyGstChargesDto details);

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
	
	@Operation(summary = "Get All Triggered Condition ", description = "Getting All Triggered Condition", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/triggered-cond",
	produces = { "application/json" })
	ResponseEntity<String> getAllTriggeredCond();
	
	@Operation(summary = "Get All Triggered On ", description = "Getting All Triggered On", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/triggered-on",
	produces = { "application/json" })
	ResponseEntity<String> getAllTriggeredOn();
	
	@Operation(summary = "Get All Triggered Value ", description = "Getting All Triggered Value", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/triggered-value",
	produces = { "application/json" })
	ResponseEntity<String> getAllTriggeredValue();

	@Operation(summary = "Admin Configration Short Term", description = "Creating/Updating Admin Configration Short Term", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/short-term",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigUpdateShortTerm(@RequestBody ZoyShortTermDto shortTerm);
	
	@Operation(summary = "Admin Configration Force Check Out", description = "Creating/Updating Admin Configration Force Check Out", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/force-checkout",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigUpdateForceCheckOut(@RequestBody ZoyForceCheckOutDto forceCheckOut);

	@Operation(summary = "Create and Update Company Profile master", description = "Creating/Updating Company Profile Master", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/company-profile",
    produces = { "application/json" },
    consumes = { "application/json"})
	ResponseEntity<String> zoyAdminCompanyProfileMaster(@RequestBody ZoyCompanyProfileMasterModal companyProfile);
	
	
	@Operation(summary = "Get all  Company Profile master Details", description = "Get All  Company Profile Master", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/fetch-company-profiles",
    produces = { "application/json" },
    consumes = { "application/json"})
	ResponseEntity<String> zoyAdminCompanyProfiles();
	
	@Operation(summary = "Delete  Company Profile master Details", description = "Delete Company Profile Master", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@DeleteMapping(value = "/zoy_admin/config/remove-company-profiles",
    produces = { "application/json" })
	ResponseEntity<String> zoyAdminRemoveCompanyProfile(@RequestParam("profileId")String profileId);
	
	@Operation(summary = "Create and Update company details", description = "Creating/Updating Company Master", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/company-master-profile",
	produces = { "application/json" },
	consumes = { "multipart/form-data" })
	ResponseEntity<String> zoyAdminCompanyProfileMaster(@RequestPart("companyProfile") String  companyProfile,@RequestPart("companyLogo") MultipartFile companyLogo);
	
	@Operation(summary = "Fetch master  company details", description = "fetch  Company Master", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/config/fetch-master-profile",
	produces = { "application/json" })
	ResponseEntity<String> fetchCompanyProfileMaster();
	
	@Operation(summary = "Admin Configration Short Term Renting Duration", description = "Creating/Updating Admin Configration Short Term Renting Duration", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Configration" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/config/renting-duration",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminConfigShortTermRentingDuration(@RequestBody ZoyRentingDuration rentingDuration);

}
