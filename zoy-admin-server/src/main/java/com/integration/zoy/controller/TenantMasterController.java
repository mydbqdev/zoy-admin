package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.ActiveBookings;
import com.integration.zoy.model.CancellationDetails;
import com.integration.zoy.model.TenantDetailPortfolio;
import com.integration.zoy.model.TenantProfile;
import com.integration.zoy.model.UpcomingBookingDetails;
import com.integration.zoy.model.UserStatus;
import com.integration.zoy.repository.UserMasterRepository;
import com.integration.zoy.service.UserDBImpl;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenantDetails;
@RestController
@RequestMapping("")
public class TenantMasterController implements TenantMasterImpl{

	private static final Logger log = LoggerFactory.getLogger(ZoyAdminMasterController.class);
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
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();



	@Autowired
	UserDBImpl userDBImpl;

	@Autowired
	private UserMasterRepository userRepo;
	
	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;
	
	@Value("${app.minio.user.photos.bucket.name}")
	private String userPhotoBucketName;
	
	@Value("${app.minio.aadhaar.photos.bucket.name}")
	String aadhaarPhotoBucket;
	
	@Autowired
	ZoyAdminService zoyAdminService;

	@Override
	public ResponseEntity<String> zoyTenantManagement(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			Page<TenantDetails> ownerPropertyList = userDBImpl.findAllTenantDetails( paginationRequest);
			return new ResponseEntity<>(gson2.toJson(ownerPropertyList), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching Tenant details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch Tenant details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Unexpected error occurredAPI:/zoy_admin/manage-tenants", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> zoyTenantManagementDetails(String tenantId) {
	    ResponseBody response = new ResponseBody();

	    try {
	        // Fetch tenant profile details
	        List<String[]> tenantDetails = userRepo.fetchTenantProfileDetails(tenantId);
	        if (tenantDetails == null || tenantDetails.isEmpty()) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Tenant details not found.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        String[] details = tenantDetails.get(0);

	        // Mapping tenant profile details
	        TenantProfile profile = new TenantProfile();
	        profile.setTenantName(details[0] != null ? details[0] : "");
	        profile.setContactNumber(details[1] != null ? details[1] : "");
	        profile.setUserEmail(details[2] != null ? details[2] : "");
	        profile.setStatus(details[3] != null ? details[3] : "");
	        profile.setEkycStatus(details[4] != null ? details[4] : "");
	        profile.setRegisteredDate(details[5] != null ? Timestamp.valueOf(details[5]) : null);
	        profile.setCurrentPropertyName(details[6] != null ? details[6] : "");
	        profile.setEmergencyContactNumber(details[7] != null ? details[7] : "");
	        profile.setAlternatePhone(details[8] != null ? details[8] : "");
	        profile.setTenantType(details[9] != null ? details[9] : "");
	        profile.setGender(details[10] != null ? details[10] : "");
	        profile.setDateOfBirth(details[11] != null ? Timestamp.valueOf(details[11]) : null);
	        profile.setBloodGroup(details[12] != null ? details[12] : "");
	        profile.setFatherName(details[13] != null ? details[13] : "");
	        profile.setCurrentAddress(details[14] != null ? details[14] : "");
	        profile.setPermanentAddress(details[15] != null ? details[15] : "");
	        profile.setNationality(details[16] != null ? details[16] : "");
	        profile.setMotherTongue(details[17] != null ? details[17] : "");
	        String customerImagePath = details[18];
			String customerImageUrl="";
			if (customerImagePath != null && !customerImagePath.isEmpty()) {
				String folderName = customerImagePath.split("/")[0];
				if(folderName.equals(tenantId))
					customerImageUrl= zoyAdminService.generatePreSignedUrl(userPhotoBucketName, customerImagePath);
				else 
					customerImageUrl= zoyAdminService.generatePreSignedUrl(aadhaarPhotoBucket, customerImagePath);
			}
	        profile.setUserProfile(customerImageUrl);
	        
	        List<String[]> activeBookingDetails = userRepo.fetchActiveBookingDetails(tenantId);
	        ActiveBookings activeBookings = new ActiveBookings();
	        if (activeBookingDetails != null && !activeBookingDetails.isEmpty()) {
	            String[] bookingDetails = activeBookingDetails.get(0);
	            activeBookings.setPgName(bookingDetails[0] != null ? bookingDetails[0] : ""); 
	            activeBookings.setMonthlyRent(bookingDetails[1] != null ? new BigDecimal(bookingDetails[1]) : BigDecimal.ZERO);
	            activeBookings.setSecurityDeposit(bookingDetails[2] != null ? new BigDecimal(bookingDetails[2]) : BigDecimal.ZERO);
	            activeBookings.setCheckInDate(bookingDetails[3] != null ? Timestamp.valueOf(bookingDetails[3]) : null);
	            activeBookings.setCheckOutDate(bookingDetails[4] != null ? Timestamp.valueOf(bookingDetails[4]) : null);
	            activeBookings.setRoomBedName((bookingDetails[5] != null ? bookingDetails[5] : "") + "/" + (bookingDetails[6] != null ? bookingDetails[6] : "")); 
	            activeBookings.setRentCycle(bookingDetails[7] != null ? bookingDetails[7] : "");
	            activeBookings.setNoticePeriod(bookingDetails[8] != null ? bookingDetails[8] : "");
	            activeBookings.setTotalDueAmount(bookingDetails[9] != null ? new BigDecimal(bookingDetails[9]) : BigDecimal.ZERO);
	        }

	        // Fetch closed bookings details
	        List<String[]> tenantCancelBookingDetails = userRepo.fetchCancelBookingDetails(tenantId);
	        List<CancellationDetails> closedBookingsList = new ArrayList<>();
	        for (String[] cancelDetails : tenantCancelBookingDetails) {
	            CancellationDetails closedBooking = new CancellationDetails();
	            closedBooking.setBookingId(cancelDetails[0] != null ? cancelDetails[0] : "");
	            closedBooking.setBookingDate(cancelDetails[1] != null ? Timestamp.valueOf(cancelDetails[1]) : null);
	            closedBooking.setPropertyName(cancelDetails[2] != null ? cancelDetails[2] : "");
	            closedBooking.setPropertyAddress((cancelDetails[3] != null ? cancelDetails[3] : "") + "-" + (cancelDetails[4] != null ? cancelDetails[4] : ""));
	            closedBooking.setPropertyContactNumber(cancelDetails[5] != null ? cancelDetails[5] : "");
	            closedBooking.setBedNumber(cancelDetails[6] != null ? cancelDetails[6] : "");
	            closedBooking.setMonthlyRent(cancelDetails[7] != null ? cancelDetails[7] : "");
	            closedBooking.setSecurityDeposit(cancelDetails[8] != null ? cancelDetails[8] : "");
	            closedBooking.setBookingStatus(cancelDetails[9] != null ? cancelDetails[9] : "");
	            closedBooking.setCancellationDate(cancelDetails[10] != null ? Timestamp.valueOf(cancelDetails[10]) : null);
	            closedBookingsList.add(closedBooking);
	        }

	        // Fetch upcoming bookings details
	        List<String[]> tenantUpcomingBookingDetails = userRepo.fetchUpcomingBookingDetails(tenantId);
	        List<UpcomingBookingDetails> upcomingBookingsList = new ArrayList<>();
	        for (String[] upcomingDetails : tenantUpcomingBookingDetails) {
	            UpcomingBookingDetails upcomingBooking = new UpcomingBookingDetails();
	            upcomingBooking.setBookingId(upcomingDetails[0] != null ? upcomingDetails[0] : "");
	            upcomingBooking.setBookingDate(upcomingDetails[1] != null ? Timestamp.valueOf(upcomingDetails[1]) : null);
	            upcomingBooking.setPropertyName(upcomingDetails[2] != null ? upcomingDetails[2] : "");
	            upcomingBooking.setPropertyAddress((upcomingDetails[3] != null ? upcomingDetails[3] : "") + "-" + (upcomingDetails[4] != null ? upcomingDetails[4] : ""));
	            upcomingBooking.setPropertyContactNumber(upcomingDetails[5] != null ? upcomingDetails[5] : "");
	            upcomingBooking.setBedNumber(upcomingDetails[6] != null ? upcomingDetails[6] : "");
	            upcomingBooking.setMonthlyRent(upcomingDetails[7] != null ? upcomingDetails[7] : "");
	            upcomingBooking.setSecurityDeposit(upcomingDetails[8] != null ? upcomingDetails[8] : "");
	            upcomingBooking.setSecurityDepositStatus(upcomingDetails[9] != null ? upcomingDetails[9] : "");
	            upcomingBookingsList.add(upcomingBooking);
	        }

	        TenantDetailPortfolio root = new TenantDetailPortfolio();
	        root.setProfile(profile);
	        root.setActiveBookings(activeBookings);
	        root.setClosedBookings(closedBookingsList);
	        root.setUpcomingBookings(upcomingBookingsList);

	        response.setStatus(HttpStatus.OK.value());
	        response.setData(root);
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error("Error fetching Tenant Management Details API:/zoy_admin/tenant-management-details", e);
	        try {
	            new ZoyAdminApplicationException(e, "");
	        } catch (Exception ex) {
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
	public ResponseEntity<String> zoyTenantStatusUpdate(UserStatus userStatus) {
	    ResponseBody response = new ResponseBody();
	    
	    try {
	        Optional<UserMaster> userOpt = userRepo.findById(userStatus.getUserId());

	        if (userOpt.isPresent()) {
	            UserMaster user = userOpt.get();
	            user.setUserStatus(userStatus.getStatus());
	            user.setReasonMessage(userStatus.getReasonMessage());
	            userRepo.save(user);

	            response.setStatus(HttpStatus.OK.value());
	            response.setMessage("User status and reason updated successfully.");
	            
	            String historyContent = " has Updated tenent Status for " + userStatus.getUserId();
	            auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_TENANT_USER_STATUS_UPDATE);
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	        } else {
	            response.setStatus(HttpStatus.NOT_FOUND.value());
	            response.setError("User not found.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	        }
	    } catch (Exception e) {
	        log.error("Error occurred while updating user status: API:/zoy_admin/updateUserStatus.zoyTenantStatusUpdate", e);
	        try {
	            new ZoyAdminApplicationException(e, "");
	        } catch (Exception ex) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError(ex.getMessage());
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}



}
