package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.service.AdminReportImpl;
import com.integration.zoy.utils.ConsilidatedFinanceDetails;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenentDues;
import com.integration.zoy.utils.UserPaymentDTO;
import com.integration.zoy.utils.VendorPayments;
import com.integration.zoy.utils.VendorPaymentsDues;
import com.integration.zoy.utils.VendorPaymentsGst;

@RestController
@RequestMapping("")
public class ZoyAdminReportController implements ZoyAdminReportImpl{
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminMasterController.class);
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
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	@Autowired
	AdminReportImpl adminReportImpl;


	@Override
	public ResponseEntity<String> getUserPaymentsByDateRange(Timestamp fromDate,
			Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {
			List<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting paymentDetails : " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getUserGstReportByDateRange(Timestamp fromDate,
			Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {
			List<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting paymentDetails: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getConsolidatedFinanceByDateRange(Timestamp fromDate, Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {
			List<ConsilidatedFinanceDetails> paymentDetails =  adminReportImpl.getConsolidatedFinanceDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ConsilidatedFinanceDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<String> getTenantDuesByDateRange(Timestamp fromDate, Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {
			List<TenentDues> tenentDuesDetails =  adminReportImpl.getTenentDuesDetails(fromDate,fromDate);
			return new ResponseEntity<>(gson.toJson(tenentDuesDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentDetailsByDateRange(Timestamp fromDate, Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {

			List<VendorPayments> vendorPaymentsDetails =  adminReportImpl.getVendorPaymentDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentDuesByDateRange(Timestamp fromDate, Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {

			List<VendorPaymentsDues> vendorPaymentsDuesDetails =  adminReportImpl.getVendorPaymentDuesDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsDuesDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentGstReportByDateRange(Timestamp fromDate, Timestamp toDate) {
		ResponseBody response=new ResponseBody();
		try {

			List<VendorPaymentsGst> vendorPaymentsGstDetails =  adminReportImpl.getVendorPaymentGstDetails(fromDate,toDate);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsGstDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<byte[]> downloadUserPaymentsByDateRange(Timestamp fromDate, Timestamp toDate) {	
		byte[] pdfData = adminReportImpl.downloadUserPaymentDetails(fromDate,toDate);

		if (pdfData.length == 0) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Vendor_Dues_Report.pdf")
				.contentType(MediaType.APPLICATION_PDF)
				.body(pdfData);

	}
}
