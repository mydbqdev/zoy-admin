package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.ZoyVendorMaster;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.VendorResponseDto;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.model.ZoyPgVendorModel;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.VendorDBImpl;
import com.integration.zoy.service.ZoyAdminTicketSmartService;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.UserMaster;

@RestController
@RequestMapping("")
public class ZoyVendorManagmentController implements ZoyVendorManagementImpl {

	@Autowired
	VendorDBImpl vendorDBImpl;

	@Autowired
	ZoyCodeGenerationService passwordGeneration;

	@Autowired
	PasswordDecoder passwordDecoder;

	@Autowired
	private ZoyAdminTicketSmartService zoyAdminTicketSmartService;

	@Autowired
	EmailService emailService;

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;
	
	
	private static final Logger log = LoggerFactory.getLogger(ZoyVendorManagmentController.class);
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
				return new JsonPrimitive(dateFormat.format(src));
			}).registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime());
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			}).setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	private static final Gson gson2 = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	@Override
	public ResponseEntity<String> getVendorDetails(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			Page<VendorResponseDto> vendorDetails = vendorDBImpl.findAllVendorUsers(paginationRequest);
			return new ResponseEntity<>(gson2.toJson(vendorDetails), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching vendor details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch vendor details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Error getting while fetching vendor details API:/zoy_admin/getVendorDetails.getVendorDetails", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> approveVendorDetails(ZoyPgVendorModel vendorModel) {
		ResponseBody response = new ResponseBody();
		try {
			Optional<ZoyVendorMaster> vendorDetails = vendorDBImpl.findVendor(vendorModel.getVendorId());
			if(!vendorDetails.isPresent()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Vendor not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyVendorMaster vendor=vendorDetails.get();
			String autoGeneratedPassword = passwordGeneration.autoGeneratePassword(vendor.getVendorFirstName());
			vendor.setVendorPassword(passwordDecoder.encryptedText(autoGeneratedPassword));
			vendor.setVendorStatus("A");
			vendor.setVendorPasswordChange(true);
			vendor.setVendorDesgination(vendorModel.getUserDesignationName());
			vendor.setVendorGroupName(vendorModel.getUserGroupName());
			vendorDBImpl.saveVendor(vendor);
			sendLogInInfoToUser(vendor, autoGeneratedPassword);

			auditHistoryUtilities.auditForCreateVendorUserDelete(SecurityContextHolder.getContext().getAuthentication().getName(), true, vendor);
			
			ZoyPgSalesMasterModel model = new ZoyPgSalesMasterModel();
			model.setEmailId(vendor.getVendorEmail());
			model.setFirstName(vendor.getVendorFirstName());
			model.setLastName(vendor.getVendorLastName());
			model.setMobileNo(vendor.getVendorMobileNo().toString());
			model.setUserDesignation(vendorModel.getUserDesignation());
			model.setUserGroupId("");
			model.setUserGroupName(vendorModel.getUserGroupName());
			UserMaster userCreated=zoyAdminTicketSmartService.createTicketSmartUser(model);
			log.info(userCreated!=null ? "Ticket smart user created" : "Ticket smart user not created");
			if(userCreated!=null) {
				Boolean userGroup=zoyAdminTicketSmartService.assignTicketToGroup(userCreated,model);
				log.info(userGroup.equals(Boolean.TRUE) ? "User group added in ticket system" : "User group unable to add in ticket system");
			}

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Vendor approved Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching vendor details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch vendor details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Error getting while fetching vendor details API:/zoy_admin/getVendorDetails.getVendorDetails", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}


	}

	private void sendLogInInfoToUser(ZoyVendorMaster vendor,String plainPassword) {
		try {
			Email email = new Email();
			email.setFrom("zoyAdmin@mydbq.com");
			List<String> to = new ArrayList<>();
			to.add(vendor.getVendorEmail());
			email.setTo(to);
			email.setSubject("Zoy Vendor App Signin Information");
			String message = "<html>"
					+ "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
					+ "<p>Hi " + vendor.getVendorFirstName() + " " + vendor.getVendorLastName() + ",</p>"
					+ "<p>Welcome to Zoy Vendor app, We are excited to have you as part of our community!<br>"
					+ "Below are your sign-in credentials for accessing your Vendor App.</p>"
					+ "<p>"
					+ "<strong>Username:</strong> " + vendor.getVendorEmail() + "<br>"
					+ "<strong>Password:</strong> <b>" + plainPassword + "</b><br><br>"
					// + "Zoy Sales App Sign-in Link: <a href='" + qaSigninLink + "'>" + qaSigninLink + "</a>"
					+ "</p>"
					+ "<p class='footer' style='margin-top: 20px;'>"
					+ "Warm regards,<br>"
					+ "<strong>Team ZOY</strong>"
					+ "</p>"
					+ "</body>"
					+ "</html>";
			email.setBody(message);
			email.setContent("text/html");
			emailService.sendEmail(email, null);
			log.info("Signin Details sent successfully to " + vendor.getVendorEmail());

		} catch (Exception e) {
			log.error("Error sending login info to user: " + e.getMessage(), e);
		}
	}

	@Override
	public ResponseEntity<String> rejectingVendorDetails(ZoyPgVendorModel vendorModel) {

		ResponseBody response = new ResponseBody();
		try {
			Optional<ZoyVendorMaster> vendorDetails = vendorDBImpl.findVendor(vendorModel.getVendorId());
			if(!vendorDetails.isPresent()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Vendor not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyVendorMaster vendor=vendorDetails.get();
			vendor.setVendorStatus("R");
			vendor.setVendorRejectedReason(vendorModel.getRejectedReason());
			vendorDBImpl.saveVendor(vendor);
			
			sendVendorRejectionMail(vendor, vendorModel.getRejectedReason());
			
			auditHistoryUtilities.auditForCreateVendorUserDelete(SecurityContextHolder.getContext().getAuthentication().getName(), false, vendor);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Vendor rejected Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching vendor details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch vendor details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Error getting while fetching vendor details API:/zoy_admin/getVendorDetails.getVendorDetails", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}
	
	private void sendVendorRejectionMail(ZoyVendorMaster vendor, String reason) {
	    try {
	        Email email = new Email();
	        email.setFrom("zoyAdmin@mydbq.com");
	        List<String> to = new ArrayList<>();
	        to.add(vendor.getVendorEmail());
	        email.setTo(to);

	        email.setSubject("Zoy Vendor Application Rejection");

	        String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + "<p>Hi " + vendor.getVendorFirstName() + " " + vendor.getVendorLastName() + ",</p>"
	                + "<p>We regret to inform you that your vendor application has been <strong>rejected</strong>.</p>"
	                + "<p><strong>Reason:</strong> " + reason + "</p>"
	                + "<p>We appreciate your interest in Zoy and encourage you to apply again in the future.</p>"
	                + "<p style='margin-top: 20px;'>"
	                + "Warm regards,<br>"
	                + "<strong>Team ZOY</strong>"
	                + "</p>"
	                + "</body>"
	                + "</html>";

	        email.setBody(message);
	        email.setContent("text/html");

	        emailService.sendEmail(email, null);
	        log.info("Rejection email sent successfully to " + vendor.getVendorEmail());

	    } catch (Exception e) {
	        log.error("Error sending rejection email to user: " + e.getMessage(), e);
	    }
	}

	@Override
	public ResponseEntity<String> vendorStatusUpdate(ZoyPgVendorModel vendorModel) {
		ResponseBody response = new ResponseBody();
		try {
			Optional<ZoyVendorMaster> vendorDetails = vendorDBImpl.findVendor(vendorModel.getVendorId());
			if(!vendorDetails.isPresent()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Vendor not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyVendorMaster vendor=vendorDetails.get();
			if(vendor.getVendorStatus().equals(ZoyConstant.ACTIVE)) {
				vendor.setVendorStatus("X");
				vendor.setVendorStatusReason(vendorModel.getReason());
				vendorDBImpl.saveVendor(vendor);
				sendVendorStatusMail(vendor, vendorModel.getReason());
			}
			if(vendor.getVendorStatus().equals(ZoyConstant.INACTIVE)) {
				vendor.setVendorStatus("I");
				vendor.setVendorStatusReason(vendorModel.getReason());
				vendorDBImpl.saveVendor(vendor);
				sendVendorStatusMail(vendor, vendorModel.getReason());
			}
			
			auditHistoryUtilities.auditForCreateVendorUserDelete(SecurityContextHolder.getContext().getAuthentication().getName(), false, vendor);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Vendor status updated to " + vendor.getVendorStatus());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching vendor details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch vendor details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Error getting while fetching vendor details API:/zoy_admin/getVendorDetails.getVendorDetails", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	private void sendVendorStatusMail(ZoyVendorMaster vendor, String reason) {
	    try {
	        Email email = new Email();
	        email.setFrom("zoyAdmin@mydbq.com");
	        email.setTo(Collections.singletonList(vendor.getVendorEmail()));
	        
	        String status = vendor.getVendorStatus();
	        email.setSubject("Zoy Vendor Status Update: " + status);

	        String greeting = "Hi " + vendor.getVendorFirstName() + " " + vendor.getVendorLastName() + ",";
	        String bodyContent;

	        if ("Active".equalsIgnoreCase(status)) {
	            bodyContent = "<p>We are pleased to inform you that your vendor status has been updated to <strong>Active</strong>.</p>"
	                        + "<p>Welcome aboard! You can now start offering your services through the Zoy platform.</p>"
	                        + "<p>We look forward to a successful collaboration with you.</p>";
	        } else if ("Inactive".equalsIgnoreCase(status)) {
	            bodyContent = "<p>We would like to inform you that your vendor status has been updated to <strong>Inactive</strong>.</p>"
	                        + "<p><strong>Reason:</strong> " + reason + "</p>"
	                        + "<p>We value your association with Zoy and encourage you to get in touch if you’d like to reactivate your account. Please contact Zoy admin</p>";
	        } else {
	            bodyContent = "<p>Your vendor status has been updated to <strong>" + status + "</strong>.</p>"
	                        + "<p><strong>Reason:</strong> " + reason + "</p>";
	        }

	        String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + greeting
	                + bodyContent
	                + "<p style='margin-top: 20px;'>Warm regards,<br><strong>Team ZOY</strong></p>"
	                + "</body>"
	                + "</html>";

	        email.setBody(message);
	        email.setContent("text/html");

	        emailService.sendEmail(email, null);
	        log.info("Status email sent successfully to " + vendor.getVendorEmail());

	    } catch (Exception e) {
	        log.error("Error sending status email to vendor: " + e.getMessage(), e);
	    }
	}


}
