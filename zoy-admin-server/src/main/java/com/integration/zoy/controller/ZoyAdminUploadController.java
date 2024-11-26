package com.integration.zoy.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import kotlin.Pair;


@RestController
@RequestMapping("")
public class ZoyAdminUploadController implements ZoyAdminUploadImpl {
	
	@Autowired
	ZoyAdminService zoyAdminService;
	
	
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminUploadController.class);
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
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
			log.error("Error getting owner property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> uploadTenantFile(String data, MultipartFile file) {
		ResponseBody response=new ResponseBody();
		try {
			UploadTenant tenant=gson2.fromJson(data,UploadTenant.class);
			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
			List<ErrorDetail> error = csvValidationService.validateCSV(file.getInputStream(),tenant.getPropertyId());
			if(error.size()>0) {
				bulkUploadDetails.setCategory(tenant.getCategory());
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
				Pair<Boolean, String> status=zoyAdminService.processTenant(tenant.getOwnerId(),tenant.getPropertyId(),file);
				if(status.getFirst()) {
					bulkUploadDetails.setCategory(tenant.getCategory());
					bulkUploadDetails.setOwnerId(tenant.getOwnerId());
					bulkUploadDetails.setOwnerName(tenant.getOwnerName());
					bulkUploadDetails.setPropertyId(tenant.getPropertyId());
					bulkUploadDetails.setPropertyName(tenant.getPropertyName());
					bulkUploadDetails.setStatus("Sucess");
					bulkUploadDetails.setFileName(file.getOriginalFilename());
					adminDBImpl.saveBulkUpload(bulkUploadDetails);
					response.setStatus(HttpStatus.OK.value());
					response.setData("Sucess");
				} else {
					bulkUploadDetails.setCategory(tenant.getCategory());
					bulkUploadDetails.setOwnerId(tenant.getOwnerId());
					bulkUploadDetails.setOwnerName(tenant.getOwnerName());
					bulkUploadDetails.setPropertyId(tenant.getPropertyId());
					bulkUploadDetails.setPropertyName(tenant.getPropertyName());
					bulkUploadDetails.setStatus("Failed");
					bulkUploadDetails.setFileName(file.getOriginalFilename());
					adminDBImpl.saveBulkUpload(bulkUploadDetails);
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setData(status.getSecond());
				}
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (IOException e) {
			log.error("Error uploading tenant details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> uploadPropertyFile(String data, MultipartFile file) {
		ResponseBody response=new ResponseBody();
		try {
			UploadTenant property=gson2.fromJson(data,UploadTenant.class);
			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
			List<ErrorDetail> error = excelValidationService.validateExcel(file);
			if(error.size()>0) {
				bulkUploadDetails.setCategory(property.getCategory());
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
				Pair<Boolean, String> status=zoyAdminService.processProperty(property.getOwnerId(),property.getPropertyId(),file);
				if(status.getFirst()) {
					bulkUploadDetails.setCategory(property.getCategory());
					bulkUploadDetails.setOwnerId(property.getOwnerId());
					bulkUploadDetails.setOwnerName(property.getOwnerName());
					bulkUploadDetails.setPropertyId(property.getPropertyId());
					bulkUploadDetails.setPropertyName(property.getPropertyName());
					bulkUploadDetails.setStatus("Sucess");
					bulkUploadDetails.setFileName(file.getOriginalFilename());
					adminDBImpl.saveBulkUpload(bulkUploadDetails);
					response.setStatus(HttpStatus.OK.value());
					response.setData("Sucess");
				} else {
					bulkUploadDetails.setCategory(property.getCategory());
					bulkUploadDetails.setOwnerId(property.getOwnerId());
					bulkUploadDetails.setOwnerName(property.getOwnerName());
					bulkUploadDetails.setPropertyId(property.getPropertyId());
					bulkUploadDetails.setPropertyName(property.getPropertyName());
					bulkUploadDetails.setStatus("Failed");
					bulkUploadDetails.setFileName(file.getOriginalFilename());
					adminDBImpl.saveBulkUpload(bulkUploadDetails);
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
					response.setData(status.getSecond());
				}
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
				bulkUploadDatas.add(uploadData);
			}
			return new ResponseEntity<>(gson.toJson(bulkUploadDatas), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}