package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.service.AdminReportImpl;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.UserPaymentFilterRequest;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

@RestController
@RequestMapping("")
public class ZoyAdminReportController implements ZoyAdminReportImpl{
	private static final Logger log=LoggerFactory.getLogger(PgOwnerMasterController.class);
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
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	@Autowired
	AdminReportImpl adminReportImpl;


	@Override
	public ResponseEntity<String> getUserPaymentsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		ResponseBody response=new ResponseBody();
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			CommonResponseDTO<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(filterRequest,filterData,applyPagination);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getUserPaymentsByDateRange API:/zoy_admin/payment_transfer_details.getUserPaymentsByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getUserGstReportByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		ResponseBody response=new ResponseBody();
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			CommonResponseDTO<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(filterRequest,filterData,applyPagination);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getUserGstReportByDateRange API:/zoy_admin/user_gst_report_details.getUserGstReportByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getConsolidatedFinanceByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		ResponseBody response=new ResponseBody();
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			CommonResponseDTO<ConsilidatedFinanceDetails> paymentDetails =  adminReportImpl.getConsolidatedFinanceDetails(filterRequest,filterData,applyPagination);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getConsolidatedFinanceByDateRange details API:/zoy_admin/consolidated_finance_report_details.getConsolidatedFinanceByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> getTenantDuesByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		ResponseBody response=new ResponseBody();
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			CommonResponseDTO<TenentDues> tenentDuesDetails =  adminReportImpl.getTenentDuesDetails(filterRequest,filterData,applyPagination);
			return new ResponseEntity<>(gson.toJson(tenentDuesDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getTenantDuesByDateRange details API:/zoy_admin/tenant-dues-report_details.getTenantDuesByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentDetailsByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		ResponseBody response=new ResponseBody();
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			CommonResponseDTO<VendorPayments> vendorPaymentsDetails =  adminReportImpl.getVendorPaymentDetails(filterRequest,filterData,applyPagination);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getTenantDuesByDateRange details API:/zoy_admin/vendor-payment-report_details.getVendorPaymentDetailsByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentDuesByDateRange(String fromDate, String toDate) {
		ResponseBody response=new ResponseBody();
		try {
			CommonResponseDTO<VendorPaymentsDues> vendorPaymentsDuesDetails =  adminReportImpl.getVendorPaymentDuesDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsDuesDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getVendorPaymentDuesByDateRange details API:/zoy_admin/vendor-payment-dues-report.getVendorPaymentDuesByDateRange ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentGstReportByDateRange(String fromDate, String toDate) {
		ResponseBody response=new ResponseBody();
		try {

			CommonResponseDTO<VendorPaymentsGst> vendorPaymentsGstDetails =  adminReportImpl.getVendorPaymentGstDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsGstDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getVendorPaymentGstReportByDateRange details API:/zoy_admin/vendor-payment-gst-report.getVendorPaymentGstReportByDateRange",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<byte[]> downloadDynamicReportByDateRange(@RequestBody UserPaymentFilterRequest filterRequest) {
		byte[] fileData=null;
		String fileName ="";
		MediaType contentType = null;
		boolean applyPagination = false;
		try {
			FilterData filterData=gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			fileData = adminReportImpl.generateDynamicReport(filterRequest,filterData,applyPagination);

			if (fileData.length == 0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  
			}

			String fileExtension;

			switch (filterRequest.getDownloadType().toLowerCase()) {
			case "excel":
				contentType = MediaType.APPLICATION_OCTET_STREAM; 
				fileExtension = ".xlsx";
				break;
			case "csv":
				contentType = MediaType.TEXT_PLAIN; 
				fileExtension = ".csv";
				break;
			case "pdf":
			default:
				contentType = MediaType.APPLICATION_PDF;
				fileExtension = ".pdf";
				break;
			}

			fileName = filterRequest.getReportType() + fileExtension;
		}catch(Exception ex) {
			log.error("Error getting download DynamicReport ByDateRange API:/zoy_admin/download_report.downloadDynamicReportByDateRange ",ex);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage().getBytes());
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.contentType(contentType)
				.body(fileData);
	}

	@Override
	public ResponseEntity<String[]> zoyCityList() {
		try {
			String[] cities = adminReportImpl.getDistinctCities();
			if (cities == null || cities.length == 0) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(cities, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting zoyCityList API:/zoy_admin/city_list.zoyCityList ",e);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}


}
