package com.integration.zoy.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
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

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Function;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgFloorNameMaster;
import com.integration.zoy.entity.ZoyPgGenderMaster;
import com.integration.zoy.entity.ZoyPgPropertyRentCycle;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.CSVValidationService;
import com.integration.zoy.service.ExcelValidationService;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.service.ZoyS3Service;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.BulkUploadData;
import com.integration.zoy.utils.ErrorDetail;
import com.integration.zoy.utils.OwnerPropertyDetails;
import com.integration.zoy.utils.PropertyDetails;
import com.integration.zoy.utils.PropertyList;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenantList;
import com.integration.zoy.utils.UploadTenant;


@RestController
@RequestMapping("")
public class ZoyAdminUploadController implements ZoyAdminUploadImpl {

	@Autowired
	ZoyAdminService zoyAdminService;

	@Autowired
	ZoyS3Service zoyS3Service;

	@Value("${app.minio.zoypg.upload.docs.bucket.name}")
	private String zoypgUploadDocsBucketName;
	
	
	@Value("${app.bulk.upload.master.password}")
	private String masterPassword;
	
	
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
	
	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

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

//	@Override
//	public ResponseEntity<String> uploadTenantFile(String data, MultipartFile file) {
//		ResponseBody response=new ResponseBody();
//		try {
//			UploadTenant tenant=gson2.fromJson(data,UploadTenant.class);
//			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
//			String fileName=file.getOriginalFilename();
//			InputStream inputStream = file.getInputStream();
//			byte[] fileBytes = inputStream.readAllBytes();
//			List<ErrorDetail> error = csvValidationService.validateCSV(fileBytes,tenant.getPropertyId());
//			if(error.size()>0) {
//				bulkUploadDetails.setCategory("Tenant");
//				bulkUploadDetails.setOwnerId(tenant.getOwnerId());
//				bulkUploadDetails.setOwnerName(tenant.getOwnerName());
//				bulkUploadDetails.setPropertyId(tenant.getPropertyId());
//				bulkUploadDetails.setPropertyName(tenant.getPropertyName());
//				bulkUploadDetails.setStatus("Failed");
//				bulkUploadDetails.setFileName(file.getOriginalFilename());
//				String uploadedFileName = tenant.getOwnerId() + "/" + tenant.getPropertyId() + "/" + fileName;
//				String fileUrl = zoyS3Service.uploadFile(zoypgUploadDocsBucketName,uploadedFileName,file);
//				bulkUploadDetails.setFilePath(fileUrl);
//				adminDBImpl.saveBulkUpload(bulkUploadDetails);
//				response.setStatus(HttpStatus.CONFLICT.value());
//				response.setData(error);
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
//			} else {
//				String jobExecutionId = UUID.randomUUID().toString();
//				bulkUploadDetails.setJobExeId(jobExecutionId);
//				bulkUploadDetails.setCategory("Tenant");
//				bulkUploadDetails.setOwnerId(tenant.getOwnerId());
//				bulkUploadDetails.setOwnerName(tenant.getOwnerName());
//				bulkUploadDetails.setPropertyId(tenant.getPropertyId());
//				bulkUploadDetails.setPropertyName(tenant.getPropertyName());
//				bulkUploadDetails.setStatus("Processing");
//				bulkUploadDetails.setFileName(file.getOriginalFilename());
//				String uploadedFileName = tenant.getOwnerId() + "/" + tenant.getPropertyId() + "/" + fileName;
//				String fileUrl = zoyS3Service.uploadFile(zoypgUploadDocsBucketName,uploadedFileName,file);
//				bulkUploadDetails.setFilePath(fileUrl);
//				adminDBImpl.saveBulkUpload(bulkUploadDetails);
//				zoyAdminService.processTenant(tenant.getOwnerId(),tenant.getPropertyId(),fileBytes,fileName,jobExecutionId);
//
//				String historyContent=" has uploaded tenent file for "+tenant.getOwnerId()+"/"+tenant.getPropertyId();
//				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_TENANT_FILE_UPLOAD);
//
//				response.setStatus(HttpStatus.OK.value());
//				response.setData("Processed");
//			} 
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
//		} catch (JsonSyntaxException e) {
//			log.error("JSON parsing error API:/zoy_admin/upload_tenant_file.uploadTenantFile ", e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError("Invalid JSON format.");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//
//		}catch (IOException e) {
//			log.error("File processing error API:/zoy_admin/upload_tenant_file.uploadTenantFile " + e.getMessage(), e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError("Error reading the uploaded file.");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//
//		} catch (Exception e) {
//			log.error("Error uploading tenant file API:/zoy_admin/upload_tenant_file.uploadTenantFile " + e.getMessage(), e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError(e.getMessage());
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//
//	}
//
//	@Override
//	public ResponseEntity<String> uploadPropertyFile(String data, MultipartFile file) {
//		ResponseBody response=new ResponseBody();
//		try {
//			UploadTenant property=gson2.fromJson(data,UploadTenant.class);
//			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
//			String fileName=file.getOriginalFilename();
//			InputStream inputStream = file.getInputStream();
//			byte[] fileBytes = inputStream.readAllBytes();
//			List<ErrorDetail> error = excelValidationService.validateExcel(fileBytes);
//			if(error.size()>0) {
//				bulkUploadDetails.setCategory("PG Property");
//				bulkUploadDetails.setOwnerId(property.getOwnerId());
//				bulkUploadDetails.setOwnerName(property.getOwnerName());
//				bulkUploadDetails.setPropertyId(property.getPropertyId());
//				bulkUploadDetails.setPropertyName(property.getPropertyName());
//				bulkUploadDetails.setStatus("Failed");
//				bulkUploadDetails.setFileName(file.getOriginalFilename());
//				String uploadedFileName = property.getOwnerId() + "/" + property.getPropertyId() + "/" + fileName;
//				String fileUrl = zoyS3Service.uploadFile(zoypgUploadDocsBucketName,uploadedFileName,file);
//				bulkUploadDetails.setFilePath(fileUrl);
//				adminDBImpl.saveBulkUpload(bulkUploadDetails);
//				response.setStatus(HttpStatus.CONFLICT.value());
//				response.setData(error);
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
//			} else {
//				String jobExecutionId = UUID.randomUUID().toString();
//				bulkUploadDetails.setJobExeId(jobExecutionId);
//				bulkUploadDetails.setCategory("PG Property");
//				bulkUploadDetails.setOwnerId(property.getOwnerId());
//				bulkUploadDetails.setOwnerName(property.getOwnerName());
//				bulkUploadDetails.setPropertyId(property.getPropertyId());
//				bulkUploadDetails.setPropertyName(property.getPropertyName());
//				bulkUploadDetails.setStatus("Processing");
//				bulkUploadDetails.setFileName(file.getOriginalFilename());
//				String uploadedFileName = property.getOwnerId() + "/" + property.getPropertyId() + "/" + fileName;
//				String fileUrl = zoyS3Service.uploadFile(zoypgUploadDocsBucketName,uploadedFileName,file);
//				bulkUploadDetails.setFilePath(fileUrl);
//				adminDBImpl.saveBulkUpload(bulkUploadDetails);
//				zoyAdminService.processProperty(property.getOwnerId(),property.getPropertyId(),fileBytes,fileName,jobExecutionId);
//				String historyContent=" has uploaded tenent file for "+property.getOwnerId()+"/"+property.getPropertyId();
//				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_PROPERTY_FILE_UPLOAD);
//
//				response.setStatus(HttpStatus.OK.value());
//				response.setData("Sucess");
//
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
//			}
//		} catch (Exception e) {
//			log.error("Error uploading property API:/zoy_admin/upload_property_file.uploadPropertyFile ",e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError(e.getMessage());
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//	}

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
				uploadData.setFilePath(zoyAdminService.generatePreSignedUrl(zoypgUploadDocsBucketName, data.getFilePath()));
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

//	@Override
//	public ResponseEntity<Object> downloadTemplateTenantsGet() {
//		request.getHeader("Accept");
//		ResponseBody response = new ResponseBody();
//		try {
//			ClassPathResource fileResource = new ClassPathResource("templates/tenantUploadTemplate.csv");
//			byte[] fileBytes = Files.readAllBytes(Path.of(fileResource.getURI()));
//			return ResponseEntity.ok()
//					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"tenantUploadTemplate.csv\"")
//					.contentType(MediaType.TEXT_PLAIN).body(fileBytes);
//
//		} catch (Exception e) {
//			log.error("An error occurred while downloading the tenants bulk upload file template API:/zoy_partner/download_template_tenants.downloadTemplateTenantsGet ", e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setMessage(e.getMessage());
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//	}
//
//	@Override
//	public ResponseEntity<Object> downloadTemplateGet() {
//		request.getHeader("Accept");
//		ResponseBody response = new ResponseBody();
//		try {
//			ClassPathResource fileResource = new ClassPathResource("templates/RoomsUploadTemplate.xlsx");
//			if (!fileResource.exists()) {
//				return ResponseEntity.status(HttpStatus.NOT_FOUND)
//						.body("Template file not found.");
//			}
//			byte[] fileBytes = Files.readAllBytes(Path.of(fileResource.getURI()));
//			return ResponseEntity.ok()
//					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"RoomsUploadTemplate.xlsx\"")
//					.contentType(MediaType.APPLICATION_OCTET_STREAM)
//					.body(fileBytes);
//		} catch (Exception e) {
//			log.error("An error occurred while downloading the template file API:/zoy_partner/download_template.downloadTemplateGet ", e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setMessage(e.getMessage());
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//	}

	@Override
	public ResponseEntity<Object> downloadBulUploadTemplateGet() {
		ResponseBody response = new ResponseBody();
		try {
			ClassPathResource fileResource = new ClassPathResource("templates/BulkUpload.xlsm");
			if (!fileResource.exists()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Template file not found.");
			}

			try (InputStream fis = fileResource.getInputStream(); 
					XSSFWorkbook workbook = new XSSFWorkbook(fis);
					ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {

				Sheet masterSheet = getOrResetSheet(workbook, "Master");
				populateColumn(masterSheet, 0, ownerDBImpl.getAllAmeneties(), ZoyPgAmenetiesMaster::getAmenetiesName);
				populateColumn(masterSheet, 1, ownerDBImpl.getAllFloorNames(), ZoyPgFloorNameMaster::getFloorName);

				List<ZoyPgShareMaster> filteredShares = ownerDBImpl.getAllShares().stream()
						.filter(share -> share.getShareOccupancyCount() > 0)
						.collect(Collectors.toList());
				populateColumn(masterSheet, 2, filteredShares, ZoyPgShareMaster::getShareType);
				populateColumn(masterSheet, 3, filteredShares, ZoyPgShareMaster::getShareOccupancyCount);
				populateColumn(masterSheet, 4, ownerDBImpl.getAllRoomTypes(), ZoyPgRoomTypeMaster::getRoomTypeName);
				populateColumn(masterSheet, 5, ownerDBImpl.getAllGenderTypes() , ZoyPgGenderMaster::getGenderName);
				setStaticValues(masterSheet, 6, List.of("Yes", "No","Partial"));
				populateColumn(masterSheet, 7, ownerDBImpl.getAllRentCycle(), ZoyPgRentCycleMaster::getCycleName);
				
				masterSheet.protectSheet(masterPassword);
				workbook.write(outStream);

				byte[] fileBytes = outStream.toByteArray();

				return ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"BulkUpload.xlsm\"")
						//.contentType(MediaType.APPLICATION_OCTET_STREAM)
						.contentType(MediaType.parseMediaType("application/vnd.ms-excel.sheet.macroEnabled.12"))
						.body(fileBytes);
			}

		} catch (Exception e) {
			log.error("Error during template download:", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Download failed: " + e.getMessage());
			return new ResponseEntity<>(new Gson().toJson(response), HttpStatus.BAD_REQUEST);
		}
}
	private Sheet getOrResetSheet(XSSFWorkbook workbook, String sheetName) {
		Sheet sheet = workbook.getSheet(sheetName);
		if (sheet == null) return workbook.createSheet(sheetName);

		for (int i = sheet.getLastRowNum(); i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null) sheet.removeRow(row);
		}
		return sheet;
	}

	private <T> void populateColumn(Sheet sheet, int columnIndex, List<T> data, Function<T, Object> valueExtractor) {
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) row = sheet.createRow(i);
			Object value = valueExtractor.apply(data.get(i));
			if (value != null) {
				row.createCell(columnIndex).setCellValue(value.toString());
			}
		}
	}

	private void setStaticValues(Sheet sheet, int columnIndex, List<String> values) {
		for (int i = 0; i < values.size(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) row = sheet.createRow(i);
			row.createCell(columnIndex).setCellValue(values.get(i));
		}
	}

	@Override
	public ResponseEntity<String> uploadBulkPropertyTenantFile(String data, MultipartFile file) {
		ResponseBody response=new ResponseBody();
		MultipartFile actualFile= file;
		String fileName=file.getOriginalFilename();
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(actualFile.getInputStream().readAllBytes());
				Workbook workbook = new XSSFWorkbook(inputStream)){

			String validationDone = zoyAdminService.parseMasterSheet(workbook.getSheet("Tenant"));
			
			if(validationDone==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Macros Validation is not done, Please upload validated files only");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			List<PropertyList> propertyList = zoyAdminService.parsePropertySheet(workbook.getSheet("Property"));
			List<TenantList> tenantList = zoyAdminService.parseTenantSheet(workbook.getSheet("Tenant"));

			UploadTenant property=gson2.fromJson(data,UploadTenant.class);
			BulkUploadDetails bulkUploadDetails=new BulkUploadDetails();
			//InputStream inputStream = file.getInputStream();
			//byte[] fileBytes = inputStream.readAllBytes();
			String jobExecutionId = UUID.randomUUID().toString();
			bulkUploadDetails.setJobExeId(jobExecutionId);
			bulkUploadDetails.setCategory("Property & Tenant");
			bulkUploadDetails.setOwnerId(property.getOwnerId());
			bulkUploadDetails.setOwnerName(property.getOwnerName());
			bulkUploadDetails.setPropertyId(property.getPropertyId());
			bulkUploadDetails.setPropertyName(property.getPropertyName());
			bulkUploadDetails.setStatus(ZoyConstant.UPLOAD_PROCESSING);
			bulkUploadDetails.setFileName(fileName);
			
//			String uploadedFileName = property.getOwnerId() + "/" + property.getPropertyId() + "/" + fileName;
//			String fileUrl = zoyS3Service.uploadFile(zoypgUploadDocsBucketName,uploadedFileName,file);
//			
//			bulkUploadDetails.setFilePath(fileUrl);
			BulkUploadDetails saved=adminDBImpl.saveBulkUpload(bulkUploadDetails);

			zoyAdminService.processBulkUpload(property.getOwnerId(),property.getPropertyId(),tenantList,propertyList,fileName,jobExecutionId,saved);

			String historyContent=" has uploaded tenent file for "+property.getOwnerId()+"/"+property.getPropertyId();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_PROPERTY_FILE_UPLOAD);

			response.setStatus(HttpStatus.OK.value());
			response.setData("Sucess");

			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error uploading property API:/zoy_admin/upload_property_file.uploadPropertyFile ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}
}


