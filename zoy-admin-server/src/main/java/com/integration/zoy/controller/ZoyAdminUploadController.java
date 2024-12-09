package com.integration.zoy.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.CSVValidationService;
import com.integration.zoy.service.ExcelValidationService;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.utils.BulkUploadData;
import com.integration.zoy.utils.ErrorDetail;
import com.integration.zoy.utils.OwnerPropertyDetails;
import com.integration.zoy.utils.PropertyDetails;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.UploadTenant;


@RestController
@RequestMapping("")
public class ZoyAdminUploadController implements ZoyAdminUploadImpl {

	@Autowired
	ZoyAdminService zoyAdminService;

	private final HttpServletRequest request;

	public ZoyAdminUploadController(HttpServletRequest request) {
		this.request = request;
	}


	private static final Logger log = LoggerFactory.getLogger(ZoyAdminUploadController.class);
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception ex) {
					throw new JsonParseException("Failed to parse Timestamp", ex);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();
	
	private static final Gson gson2 = new GsonBuilder().create();


	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	AdminDBImpl adminDBImpl;

	@Autowired
	CSVValidationService csvValidationService;

	@Autowired
	ExcelValidationService excelValidationService;

	@Override
	public ResponseEntity<String> ownerPropertyDetails() {
		ResponseBody response=new ResponseBody();
		try {
			List<String[]> ownerPropertyDetails= ownerDBImpl.getOwnerPropertyDetails();
			List<OwnerPropertyDetails> details = new ArrayList<>();
			if(ownerPropertyDetails.size() > 0 ) {
				for(String[] data:ownerPropertyDetails) {
					OwnerPropertyDetails detail=new OwnerPropertyDetails();
					String[] ownerDetail=data[0].split(",");
					detail.setOwnerId(ownerDetail[0]);
					detail.setOwnerName(ownerDetail[1].trim()+"-"+ownerDetail[2]);
					detail.setPropertyDetails(data[1] != null ? Arrays.stream(data[1].split(","))
							.map(property -> property.split("\\|")).filter(parts -> parts.length == 2)   
							.map(parts -> new PropertyDetails(parts[0], parts[1].trim()))
							.collect(Collectors.toList()) : new ArrayList<>());
					details.add(detail);
				}
			}
			return new ResponseEntity<>(gson.toJson(details), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting owner property details API:/zoy_admin/owner_property_details.ownerPropertyDetails",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> uploadTenantFile(String data, MultipartFile file) {
		ResponseBody response=new ResponseBody();
		try {
			UploadTenant tenant=gson2.fromJson(data,UploadTenant.class);
			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
			String fileName=file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();
			byte[] fileBytes = inputStream.readAllBytes();
			List<ErrorDetail> error = csvValidationService.validateCSV(fileBytes,tenant.getPropertyId());
			if(error.size()>0) {
				bulkUploadDetails.setCategory("Tenent");
				bulkUploadDetails.setOwnerId(tenant.getOwnerId());
				bulkUploadDetails.setOwnerName(tenant.getOwnerName());
				bulkUploadDetails.setPropertyId(tenant.getPropertyId());
				bulkUploadDetails.setPropertyName(tenant.getPropertyName());
				bulkUploadDetails.setStatus("Failed");
				bulkUploadDetails.setFileName(file.getOriginalFilename());
				adminDBImpl.saveBulkUpload(bulkUploadDetails);
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setData(error);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			} else {
				String jobExecutionId = UUID.randomUUID().toString();
				bulkUploadDetails.setJobExeId(jobExecutionId);
				bulkUploadDetails.setCategory("Tenent");
				bulkUploadDetails.setOwnerId(tenant.getOwnerId());
				bulkUploadDetails.setOwnerName(tenant.getOwnerName());
				bulkUploadDetails.setPropertyId(tenant.getPropertyId());
				bulkUploadDetails.setPropertyName(tenant.getPropertyName());
				bulkUploadDetails.setStatus("Processing");
				bulkUploadDetails.setFileName(file.getOriginalFilename());
				adminDBImpl.saveBulkUpload(bulkUploadDetails);
				zoyAdminService.processTenant(tenant.getOwnerId(),tenant.getPropertyId(),fileBytes,fileName,jobExecutionId);
				response.setStatus(HttpStatus.OK.value());
				response.setData("Processed");
			} 
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (JsonSyntaxException e) {
			log.error("JSON parsing error API:/zoy_admin/upload_tenant_file.uploadTenantFile ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Invalid JSON format.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (IOException e) {
			log.error("File processing error API:/zoy_admin/upload_tenant_file.uploadTenantFile " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Error reading the uploaded file.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		} catch (Exception e) {
			log.error("Error uploading tenant file API:/zoy_admin/upload_tenant_file.uploadTenantFile " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> uploadPropertyFile(String data, MultipartFile file) {
		ResponseBody response=new ResponseBody();
		try {
			UploadTenant property=gson2.fromJson(data,UploadTenant.class);
			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
			String fileName=file.getOriginalFilename();
			InputStream inputStream = file.getInputStream();
			byte[] fileBytes = inputStream.readAllBytes();
			List<ErrorDetail> error = excelValidationService.validateExcel(fileBytes);
			if(error.size()>0) {
				bulkUploadDetails.setCategory("PG Property");
				bulkUploadDetails.setOwnerId(property.getOwnerId());
				bulkUploadDetails.setOwnerName(property.getOwnerName());
				bulkUploadDetails.setPropertyId(property.getPropertyId());
				bulkUploadDetails.setPropertyName(property.getPropertyName());
				bulkUploadDetails.setStatus("Failed");
				bulkUploadDetails.setFileName(file.getOriginalFilename());
				adminDBImpl.saveBulkUpload(bulkUploadDetails);
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setData(error);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			} else {
				String jobExecutionId = UUID.randomUUID().toString();
				bulkUploadDetails.setJobExeId(jobExecutionId);
				bulkUploadDetails.setCategory("PG Property");
				bulkUploadDetails.setOwnerId(property.getOwnerId());
				bulkUploadDetails.setOwnerName(property.getOwnerName());
				bulkUploadDetails.setPropertyId(property.getPropertyId());
				bulkUploadDetails.setPropertyName(property.getPropertyName());
				bulkUploadDetails.setStatus("Processing");
				bulkUploadDetails.setFileName(file.getOriginalFilename());
				adminDBImpl.saveBulkUpload(bulkUploadDetails);
				zoyAdminService.processProperty(property.getOwnerId(),property.getPropertyId(),fileBytes,fileName,jobExecutionId);
				response.setStatus(HttpStatus.OK.value());
				response.setData("Sucess");

				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property API:/zoy_admin/upload_property_file.uploadPropertyFile ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getBulkUpload() {
		ResponseBody response=new ResponseBody();
		try {
			List<BulkUploadDetails> bulkUploadDetails=adminDBImpl.findAllBulkUpload();
			List<BulkUploadData> bulkUploadDatas=new ArrayList<>();
			for(BulkUploadDetails data: bulkUploadDetails) {
				BulkUploadData uploadData=new BulkUploadData();
				uploadData.setCategory(data.getCategory());
				uploadData.setFileName(data.getFileName());
				uploadData.setId(data.getId());
				uploadData.setOwnerId(data.getOwnerId());
				uploadData.setOwnerName(data.getOwnerName());
				uploadData.setPropertyId(data.getPropertyId());
				uploadData.setPropertyName(data.getPropertyName());
				uploadData.setStatus(data.getStatus());
				uploadData.setCreateAt(data.getCreatedAt());
				bulkUploadDatas.add(uploadData);
			}
			return new ResponseEntity<>(gson.toJson(bulkUploadDatas), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error uploading property details API:/zoy_admin/getBulkUpload.getBulkUpload ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<Object> downloadTemplateTenantsGet() {
		request.getHeader("Accept");
		ResponseBody response = new ResponseBody();
		try {
			ClassPathResource fileResource = new ClassPathResource("templates/tenantUploadTemplate.csv");
			byte[] fileBytes = Files.readAllBytes(Path.of(fileResource.getURI()));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tenantUploadTemplate.csv\"")
					.contentType(MediaType.TEXT_PLAIN).body(fileBytes);

		} catch (Exception e) {
			log.error("An error occurred while downloading the tenants bulk upload file template API:/zoy_partner/download_template_tenants.downloadTemplateTenantsGet ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<Object> downloadTemplateGet() {
		request.getHeader("Accept");
		ResponseBody response = new ResponseBody();
		try {
			ClassPathResource fileResource = new ClassPathResource("templates/RoomsUploadTemplate.xlsx");
			if (!fileResource.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Template file not found.");
			}
			byte[] fileBytes = Files.readAllBytes(Path.of(fileResource.getURI()));
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"RoomsUploadTemplate.xlsx\"")
					.contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(fileBytes);
		} catch (Exception e) {
			log.error("An error occurred while downloading the template file API:/zoy_partner/download_template.downloadTemplateGet ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
}


