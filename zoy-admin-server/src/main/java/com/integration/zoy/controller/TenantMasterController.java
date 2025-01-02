package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.ActiveBookings;
import com.integration.zoy.model.BasicPropertyInformation;
import com.integration.zoy.model.Bed;
import com.integration.zoy.model.CancellationDetails;
import com.integration.zoy.model.FloorInformation;
import com.integration.zoy.model.PgOwnerAdditionalInfo;
import com.integration.zoy.model.PgOwnerBusinessInfo;
import com.integration.zoy.model.PgOwnerProfile;
import com.integration.zoy.model.PgOwnerPropertyInformation;
import com.integration.zoy.model.PgOwnerbasicInformation;
import com.integration.zoy.model.PgOwnerdetailPortfolio;
import com.integration.zoy.model.Room;
import com.integration.zoy.model.TenantDetailPortfolio;
import com.integration.zoy.model.TenantProfile;
import com.integration.zoy.model.UpcomingBookingDetails;
import com.integration.zoy.repository.UserMasterRepository;
import com.integration.zoy.service.UserDBImpl;
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
	        List<String[]> tenantDetails = userRepo.fetchTenantDetails(tenantId);
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
	        profile.setUserProfile(details[18] != null ? details[18] : "");

	        // Fetch active bookings details
	        ActiveBookings activeBookings = new ActiveBookings();
			activeBookings.setPgName(details[6] != null ? details[6] : ""); 
			activeBookings.setMonthlyRent(details[19] != null ? new BigDecimal(details[19]) : BigDecimal.ZERO);
			activeBookings.setSecurityDeposit(details[20] != null ? new BigDecimal(details[20]) : BigDecimal.ZERO);
			activeBookings.setCheckInDate(details[21] != null ? Timestamp.valueOf(details[21]) : null);
			activeBookings.setCheckOutDate(details[22] != null ? Timestamp.valueOf(details[22]) : null);
			activeBookings.setRoomBedName((details[23] != null ? details[23] : "") + "/" + (details[24] != null ? details[24] : "")); 
			activeBookings.setRentCycle(details[25] != null ? details[25] : "");
			activeBookings.setNoticePeriod(details[26] != null ? details[26] : "");
			activeBookings.setTotalDueAmount(details[27] != null ? new BigDecimal(details[27]) : BigDecimal.ZERO);

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

}
