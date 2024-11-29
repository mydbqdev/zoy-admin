package com.integration.zoy.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.integration.zoy.utils.UploadTenant;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Validated
public interface  ZoyAdminUploadImpl {

	@Operation(summary = "Get Owner & Properties", description = "Getting Owner & Properties", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "zoy_admin/owner_property_details", 
	produces = { "application/json" })
	ResponseEntity<String> ownerPropertyDetails();

	@Operation(summary = "Upload Tenat File", description = "Uploading Tenant File", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "zoy_admin/upload_tenant_file", 
	produces = { "application/json" }, 
	consumes = { "multipart/form-data"}) 
	ResponseEntity<String> uploadTenantFile(@RequestPart(value = "tenant", required = true) String tenant, @RequestPart(value = "file",required = true) MultipartFile file);

	@Operation(summary = "Upload Property File", description = "Uploading Property File", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@PostMapping(value = "zoy_admin/upload_property_file", 
	produces = { "application/json" }, 
	consumes = { "multipart/form-data"}) 
	ResponseEntity<String> uploadPropertyFile(@RequestPart(value = "property", required = true) String tenant, @RequestPart(value = "file",required = true) MultipartFile file);


	@Operation(summary = "Get Bulk Upload Data", description = "Getting Bulk Upload Data", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "zoy_admin/getBulkUpload", 
	produces = { "application/json" }) 
	ResponseEntity<String> getBulkUpload();


	@Operation(summary = "downloadTemplateTenantsGet", description = "downloadTemplateTenants", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_partner/download_template_tenants",
	produces = { "application/json" })
	ResponseEntity<Object> downloadTemplateTenantsGet();


	@Operation(summary = "downloadTemplateGet", description = "downloadTemplateGet", 
			security = {@SecurityRequirement(name = "basicAuth")}, tags = { "Admin Upload" })
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/json")),
			@ApiResponse(responseCode = "400", description = "Bad Request"),
			@ApiResponse(responseCode = "404", description = "Not Found"),
			@ApiResponse(responseCode = "500", description = "Internal Server Error") })
	@GetMapping(value = "/zoy_partner/download_template",
	produces = { "application/json" })
	ResponseEntity<Object> downloadTemplateGet();



}
