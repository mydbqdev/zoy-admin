package com.integration.zoy.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.data.web.JsonPath;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.model.AdminUserDetails;
import com.integration.zoy.model.AdminUserUpdateDetails;
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
import com.integration.zoy.model.LoginDetails;
import com.integration.zoy.model.NotificationMode;
import com.integration.zoy.model.NotificationModeId;
import com.integration.zoy.model.PgType;
import com.integration.zoy.model.PgTypeId;
import com.integration.zoy.model.RentCycle;
import com.integration.zoy.model.RentCycleId;
import com.integration.zoy.model.RoleDetails;
import com.integration.zoy.model.RoomType;
import com.integration.zoy.model.RoomTypeId;
import com.integration.zoy.model.ShareType;
import com.integration.zoy.model.ShareTypeId;
import com.integration.zoy.model.Token;
import com.integration.zoy.model.UserRole;



@Validated
public interface ZoyAdminUserImpl {

	
	@Operation(summary = "Admin User Login", description = "Admin User Login", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/login",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminUserLogin(@RequestBody LoginDetails details);
	
	@Operation(summary = "Admin User details", description = "Getting Admin User details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_details",
	produces = { "application/json" },
	consumes = { "application/json"})
	ResponseEntity<String> zoyAdminUserDetails(@RequestBody Token token);
	
	
	@Operation(summary = "Create Admin User", description = "Creating new admin User", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_create",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminCreateUser(@RequestBody AdminUserDetails adminUserDetails);
	
	@Operation(summary = "Get Admin User Availability", description = "Getting Admin User Availablity", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/user_availability/{email}",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminUserAvailability(@PathVariable String email);

	@Operation(summary = "Update Admin User", description = "Updating Admin User", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/user_update/{email}",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminUserUpdate(@PathVariable String email,@RequestBody AdminUserUpdateDetails adminUserDetails);
	
	@Operation(summary = "Create Admin User Role", description = "Creating Admin User Role", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/role_create",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminUserRoleCreate(@RequestBody RoleDetails roleDetails);
	
	@Operation(summary = "Update Admin User Role", description = "Updating Admin User Role", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PutMapping(value = "/zoy_admin/role_update",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminUserRoleUpdate(@RequestBody RoleDetails roleDetails);
	
	@Operation(summary = "Get Admin User Role", description = "Getting Admin User Role", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/role_list",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminUserRoleList();
	
	@Operation(summary = "Post Admin User Assign", description = "Posting Admin User Assign", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_assign",
	produces = { "application/json" },
	consumes = { "application/json" })
	ResponseEntity<String> zoyAdminUserAssign(@RequestBody UserRole userRole);
	
	@Operation(summary = "Get Admin User List", description = "Getting Admin User List", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/user_list",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminUserList();
	
	@Operation(summary = "Send Admin User Sigin info", description = "Sending Admin User Sigin info", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/send_login_info",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminUserSendLoginInfo(@RequestParam("userName")String userName);
	
	@Operation(summary = "Approve or Reject Role", description = "Approve or Reject a user role request based on email and status", 
		    security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin User & Role" })
		@ApiResponses(value = {
		        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
		        @ApiResponse(responseCode = "400", description = "Bad Request"),
		        @ApiResponse(responseCode = "404", description = "Not Found"),
		        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
		@PostMapping(value = "/zoy_admin/approve_or_reject_role", 
		    produces = { "application/json" })
		ResponseEntity<String> approveOrRejectRole(  @RequestParam("userEmail") String userEmail,@RequestParam("status") String status );
	
	@Operation(summary = "delete Role", description = "delete a role that is not assigned to any user", 
		    security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin User & Role" })
	@ApiResponses(value = {
	        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
	        @ApiResponse(responseCode = "400", description = "Bad Request"),
	        @ApiResponse(responseCode = "404", description = "Not Found"),
	        @ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "zoy_admin/role_delete", 
    produces = { "application/json" })
	ResponseEntity<String> deleteRole(  @RequestParam("roleId") int roleId ,@RequestParam("roleName") String roleName);
	
	
	@Operation(summary = "Get Admin User's not approved roles", description = "Getting Admin User roles List", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin User & Role" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/userListNotApprove",
	produces = { "application/json" })
	ResponseEntity<String> zoyAdminNotApprovedRoles();
	
}
