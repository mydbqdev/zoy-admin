package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	public ResponseEntity<String> getUserPaymentsByDateRange(LocalDateTime fromDate,
			LocalDateTime toDate) {
		ResponseBody response=new ResponseBody();
		try {

			Timestamp fromTimestamp = Timestamp.valueOf(fromDate);
			Timestamp toTimestamp = Timestamp.valueOf(toDate);
			List<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(fromTimestamp,toTimestamp);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting paymentDetails : " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getUserGstReportByDateRange(LocalDateTime fromDate,
			LocalDateTime toDate) {
		ResponseBody response=new ResponseBody();
		try {

			Timestamp fromTimestamp = Timestamp.valueOf(fromDate);
			Timestamp toTimestamp = Timestamp.valueOf(toDate);
			List<UserPaymentDTO> paymentDetails =  adminReportImpl.getUserPaymentDetails(fromTimestamp,toTimestamp);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting paymentDetails: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getConsolidatedFinanceByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
		ResponseBody response=new ResponseBody();
		try {

			Timestamp fromTimestamp = Timestamp.valueOf(fromDate);
			Timestamp toTimestamp = Timestamp.valueOf(toDate);
			List<ConsilidatedFinanceDetails> paymentDetails =  adminReportImpl.getConsolidatedFinanceDetails(fromTimestamp,toTimestamp);
			return new ResponseEntity<>(gson.toJson(paymentDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ConsilidatedFinanceDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<String> getTenantDuesByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
		ResponseBody response=new ResponseBody();
		try {

			Timestamp fromTimestamp = Timestamp.valueOf(fromDate);
			Timestamp toTimestamp = Timestamp.valueOf(toDate);
			List<TenentDues> tenentDuesDetails =  adminReportImpl.getTenentDuesDetails(fromTimestamp,toTimestamp);
			return new ResponseEntity<>(gson.toJson(tenentDuesDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> getVendorPaymentDetailsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
		ResponseBody response=new ResponseBody();
		try {

			Timestamp fromTimestamp = Timestamp.valueOf(fromDate);
			Timestamp toTimestamp = Timestamp.valueOf(toDate);
			List<VendorPayments> vendorPaymentsDetails =  adminReportImpl.getVendorPaymentDetails(fromTimestamp,toTimestamp);
			return new ResponseEntity<>(gson.toJson(vendorPaymentsDetails), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting tenentDuesDetails details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}


