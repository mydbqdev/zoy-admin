package com.integration.zoy.controller;

import java.sql.Timestamp;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	@GetMapping(value = "/zoy_admin/payment_transfer_details",
	produces = { "application/json" })
	
	ResponseEntity<String> getUserPaymentsByDateRange(@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate")Timestamp toDate);
	
	@Operation(summary = "Get User gst report by date range", description = "Getting  User gst report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/user_gst_report_details",
	produces = { "application/json" })
	ResponseEntity<String> getUserGstReportByDateRange(@RequestParam("fromDate") Timestamp  fromDate,
            @RequestParam("toDate") Timestamp toDate);
	
	
	
	@Operation(summary = "Get Consolidated Finance Report by date range", description = "Getting  consolidateD  Finance Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/consolidated_finance_report_details",
	produces = { "application/json" })
	ResponseEntity<String> getConsolidatedFinanceByDateRange(@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate") Timestamp toDate);
	
	@Operation(summary = "Get Tenant Dues Report by date range", description = "Getting Tenant Dues Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/tenant-dues-report_details",
	produces = { "application/json" })
	ResponseEntity<String> getTenantDuesByDateRange(Timestamp fromDate,
            @RequestParam("toDate") Timestamp toDate);

	@Operation(summary = "Get vendor payment Report by date range", description = "Getting vendor payment Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/vendor-payment-report_details",
	produces = { "application/json" })
	ResponseEntity<String> getVendorPaymentDetailsByDateRange(@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate") Timestamp toDate);
	
	@Operation(summary = "Get vendor payment DuesReport by date range", description = "Getting vendor payment Dues Report Details", security = {
			@SecurityRequirement(name = "basicAuth")}, tags={ "Admin Report" })
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_admin/vendor-payment-dues-report",
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
	@GetMapping(value = "/zoy_admin/vendor-payment-gst-report",
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
	@GetMapping(value = "/zoy_admin/download_report",
	produces = { "application/json" })
	ResponseEntity<byte[]> downloadDynamicReportByDateRange(@RequestParam("templateName") String templateName,@RequestParam("fileType") String fileType,@RequestParam("fromDate") Timestamp fromDate,
            @RequestParam("toDate")Timestamp toDate);
}
