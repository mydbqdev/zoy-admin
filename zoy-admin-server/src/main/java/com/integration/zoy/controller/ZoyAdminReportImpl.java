package com.integration.zoy.controller;

import java.sql.Timestamp;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.utils.UserPaymentFilterRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface ZoyAdminReportImpl {

	@Operation(summary = "Get User Pyament Details", description = "Getting  User payment Transfer Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/payment_transfer_details",
	produces = { "application/json" })
	ResponseEntity<String> getUserPaymentsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get User gst report by date range", description = "Getting  User gst report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_gst_report_details",
	produces = { "application/json" })
	ResponseEntity<String> getUserGstReportByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	
	
	@Operation(summary = "Get Consolidated Finance Report by date range", description = "Getting  consolidateD  Finance Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/consolidated_finance_report_details",
	produces = { "application/json" })
	ResponseEntity<String> getConsolidatedFinanceByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Tenant Dues Report by date range", description = "Getting Tenant Dues Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/tenant-dues-report_details",
	produces = { "application/json" })
	ResponseEntity<String> getTenantDuesByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);

	@Operation(summary = "Get vendor payment Report by date range", description = "Getting vendor payment Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/vendor-payment-report_details",
	produces = { "application/json" })
	ResponseEntity<String> getVendorPaymentDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get vendor payment DuesReport by date range", description = "Getting vendor payment Dues Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/vendor-payment-dues-report",
	produces = { "application/json" })
	ResponseEntity<String> getVendorPaymentDuesByDateRange(@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate") Timestamp toDate);
	
	@Operation(summary = "Get vendor payments GST Report by date range", description = "Getting vendor payments GST  Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/vendor-payment-gst-report",
	produces = { "application/json" })
	ResponseEntity<String> getVendorPaymentGstReportByDateRange(@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate") Timestamp toDate);
	@Operation(summary = "download User Pyament Details", description = "download  User payment Transfer Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	
	
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/download_report",
	produces = { "application/json" })
	ResponseEntity<byte[]> downloadDynamicReportByDateRange(UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "get All City Details", description = "get all city Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Master" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK" , content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/city_list",
	produces = { "application/json" })
	ResponseEntity<String[]> zoyCityList();
	
	@Operation(summary = "Get User refund Details", description = "Getting  User refund Transfer Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_refund_details",
	produces = { "application/json" })
	ResponseEntity<String> getUserRefundDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Users reviews and ratings Details", description = "Getting  User reviews and ratings Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/user_reviews_ratings_details",
	produces = { "application/json" })
	ResponseEntity<String> getUserReviewsAndRatingsDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Upcoming Tenants Details", description = "Getting the details of upcoming tenants", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/upcoming_tenant_details",
	produces = { "application/json" })
	ResponseEntity<String> getUpcomingTenantsReportDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Active Tenants Details", description = "Getting the details of Active tenants", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/active_tenant_details",
	produces = { "application/json" })
	ResponseEntity<String> getActiveTenantsReportDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);	
	
	@Operation(summary = "Get Inactive Tenants Details", description = "Getting the details of Inactive tenants", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/inactive_tenant_details",
	produces = { "application/json" })
	ResponseEntity<String> getInActiveTenantsReportDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Suspended Tenants Details", description = "Getting the details of Suspended tenants", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/suspended_tenant_details",
	produces = { "application/json" })
	ResponseEntity<String> getSuspendedTenantsReportDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get In-Active Property Details", description = "Getting the details of In-Active Property Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/inactive_property_details",
	produces = { "application/json" })
	ResponseEntity<String> getInActivePropertyDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Suspended Property Details", description = "Getting the details of Suspended Property Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/suspended_property_details",
	produces = { "application/json" })
	ResponseEntity<String> getSuspendedPropertyDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);

	
	@Operation(summary = "Get Register Tenants Details", description = "Getting the details of Register Tenants Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/register_tenants_details",
	produces = { "application/json" })
	ResponseEntity<String> getRegisterTenantsDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Failure Transaction Details", description = "Getting the details of Failure Transaction", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/failure_transactions_details",
	produces = { "application/json" })
	ResponseEntity<String> getFailureTransactionDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);

	@Operation(summary = "Get Potential Property Details", description = "Getting the details of Potential Properties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/potential_property_details",
	produces = { "application/json" })
	ResponseEntity<String> getPotentialPropertyDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);

	@Operation(summary = "Get up coming Potential Property Details", description = "Getting the details of up coming Potential Properties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/up_coming_potential_property_details",
	produces = { "application/json" })
	ResponseEntity<String> getUpComingPotentialPropertyDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get up Non Potential Property Details", description = "Getting the details of Non Potential Properties", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/non_potential_property_details",
	produces = { "application/json" })
	ResponseEntity<String> getNonPotentialPropertyDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);
	
	@Operation(summary = "Get Zoy Share Details", description = "Getting the details of Zoy Share", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "/zoy_admin/zoyShareReportDetails",
	produces = { "application/json" })
	ResponseEntity<String> getZoyShareDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest);

	
	
}
