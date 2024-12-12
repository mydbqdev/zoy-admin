package com.integration.zoy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.integration.zoy.model.Amenetie;
import com.integration.zoy.model.AmenetiesId;
import com.integration.zoy.model.BillingType;
import com.integration.zoy.model.BillingTypeId;
import com.integration.zoy.model.CurrencyType;
import com.integration.zoy.model.CurrencyTypeId;
import com.integration.zoy.model.DueFactor;
import com.integration.zoy.model.DueFactorId;
import com.integration.zoy.model.DueType;
import com.integration.zoy.model.DueTypeId;
import com.integration.zoy.model.EkycType;
import com.integration.zoy.model.EkycTypeId;
import com.integration.zoy.model.NotificationMode;
import com.integration.zoy.model.NotificationModeId;
import com.integration.zoy.model.PgType;
import com.integration.zoy.model.PgTypeId;
import com.integration.zoy.model.RentCycle;
import com.integration.zoy.model.RentCycleId;
import com.integration.zoy.model.RoomType;
import com.integration.zoy.model.RoomTypeId;
import com.integration.zoy.model.ShareType;
import com.integration.zoy.model.ShareTypeId;
import com.integration.zoy.utils.OwnerLeadPaginationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;



@Validated
public interface ZoyAdminMasterImpl {

	//Ameneties
	@Operation(summary = "Get Ameneties", description = "Getting Ameneties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/ameneties",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminAmenities();

	@Operation(summary = "Post Ameneties", description = "Posting Ameneties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/ameneties",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminAmenitiesPost(@RequestBody Amenetie amenetie);

	@Operation(summary = "Put Ameneties", description = "Updating Ameneties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/ameneties",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminAmenitiesPut(@RequestBody AmenetiesId amenetie);

	//Due Factor
	@Operation(summary = "Get Due Factor", description = "Getting Due Factor", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/factor",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminFactor();

	@Operation(summary = "Post Due Factor", description = "Posting Due Factor", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/factor",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminFactorPost(@RequestBody DueFactor dueFactor);

	@Operation(summary = "Put Due Factor", description = "Updating Due Factor", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/factor",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminFactorPut(@RequestBody DueFactorId dueFactorId);

	//Rent Cycle
	@Operation(summary = "Get Rent Cycle", description = "Getting Rent Cycle", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/rentCycle",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminRentCycle();

	@Operation(summary = "Post Rent Cycle", description = "Posting Rent Cycle", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/rentCycle",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminRentCyclePost(@RequestBody RentCycle rentCycle);

	@Operation(summary = "Put Rent Cycle", description = "Updating Rent Cycle", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/rentCycle",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminRentCyclePut(@RequestBody RentCycleId rentCycleId);

	//Rome Type
	@Operation(summary = "Get Room Type", description = "Getting Room Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/roomType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminRoomType();

	@Operation(summary = "Post Room Type", description = "Posting Room Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/roomType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminRoomTypePost(@RequestBody RoomType roomType);

	@Operation(summary = "Put Room Type", description = "Updating Room Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/roomType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminRoomTypePut(@RequestBody RoomTypeId roomTypeId);

	//Share Type
	@Operation(summary = "Get Share Type", description = "Getting Share Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/shareType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminShareType();

	@Operation(summary = "Post Share Type", description = "Posting Share Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/shareType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminShareTypePost(@RequestBody ShareType shareType);

	@Operation(summary = "Put Share Type", description = "Updating Share Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/shareType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminShareTypePut(@RequestBody ShareTypeId shareTypeId);

	//Pg Type
	@Operation(summary = "Get Pg Type", description = "Getting Pg Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/pgType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminPgType();

	@Operation(summary = "Post Pg Type", description = "Posting Pg Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/pgType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminPgTypePost(@RequestBody PgType pgType);

	@Operation(summary = "Put Pg Type", description = "Updating Pg Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/pgType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminPgTypePut(@RequestBody PgTypeId pgTypeId);

	//Notification
	@Operation(summary = "Get Notification Mode", description = "Getting Notification Mode", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/notification_mode",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminNotificationModeName();

	@Operation(summary = "Post Notification Mode", description = "Posting Notification Mode", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/notification_mode",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminNotificationModeNamePost(@RequestBody NotificationMode notificationMode);
	
	@Operation(summary = "Put Notification Mode", description = "Updating Notification Mode", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/notification_mode",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminNotificationModeNamePut(@RequestBody NotificationModeId notificationModeId);
	
	//Billing Type
	@Operation(summary = "Get Billing Type", description = "Getting Billing Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/billingType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminBillingType();
	
	@Operation(summary = "Post Billing Type", description = "Posting Billing Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/billingType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminBillingTypePost(@RequestBody BillingType billingType);
	
	@Operation(summary = "Put Billing Type", description = "Updating Billing Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "Bad Request" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/billingType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminBillingTypePut(@RequestBody BillingTypeId billingType);

	//Currency Type
	@Operation(summary = "Get Currency Type", description = "Getting Currency Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/currencyType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminCurrencyType();
	
	@Operation(summary = "Post Currency Type", description = "Posting Currency Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/currencyType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminCurrencyTypePost(@RequestBody CurrencyType currencyType);
	
	@Operation(summary = "Put Currency Type", description = "Updating Currency Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/currencyType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminCurrencyTypePut(@RequestBody CurrencyTypeId currencyTypeId);

	//Due Type
	@Operation(summary = "Get Due Type", description = "Getting Due Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/dueType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminDueType();
	
	@Operation(summary = "Post Due Type", description = "Posting Due Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/dueType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminDueTypePost(@RequestBody DueType dueType);
	
	@Operation(summary = "Put Due Type", description = "Updating Due Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/dueType",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminDueTypePut(@RequestBody DueTypeId dueTypeId);

	//Ekyc Type
	@Operation(summary = "Get Ekyc Type", description = "Getting Ekyc Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/ekycType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminEkycType();
	
	@Operation(summary = "Post Ekyc Type", description = "Posting Ekyc Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/ekycType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminEkycTypePost(@RequestBody EkycType ekycType);
	
	@Operation(summary = "Put Ekyc Type", description = "Updating Ekyc Type", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/ekycType",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminEkycTypePut(@RequestBody EkycTypeId ekycTypeId);
	
	
	@Operation(summary = "PG onwer details", description = "getting pg onwer details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/manage-owners",
	produces = { "application/json" })
	ResponseEntity<String> zoyPgOwnerDetails(@RequestBody OwnerLeadPaginationRequest paginationRequest);
	
	
	@Operation(summary = "audit activities log details", description = "getting audit activities log details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/audit-activitieslog",
	produces = { "application/json" })
	ResponseEntity<String> zoyAuditActivitiesLogDetails(@RequestBody OwnerLeadPaginationRequest paginationRequest);
	
	@Operation(summary = "user name list details", description = "getting user name list details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/userName-List",
	produces = { "application/json" })
	ResponseEntity<String> zoyUserNameList();
	
	
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/download_user_audit_report",
	produces = { "application/json" })
	ResponseEntity<byte[]> downloadUserAuditReport(@RequestBody OwnerLeadPaginationRequest paginationRequest);

}




