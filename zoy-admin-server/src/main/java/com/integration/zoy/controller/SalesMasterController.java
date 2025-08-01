package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.entity.AdminUserPasswordHistory;
import com.integration.zoy.entity.ZoyPgSalesMaster;
import com.integration.zoy.entity.ZoyPgSalesUserLoginDetails;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.repository.AdminUserPasswordHistoryRepository;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.JsonParserUtil;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.SalesDBImpl;
import com.integration.zoy.service.TicketSmartService;
import com.integration.zoy.service.ZoyAdminTicketSmartService;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.UserGroupResponse;
import com.integration.zoy.utils.UserGroupResponseDto;
import com.integration.zoy.utils.UserMaster;

@RestController
@RequestMapping("")
public class SalesMasterController implements SalesMasterImpl {

	private static final Logger log = LoggerFactory.getLogger(ZoyAdminMasterController.class);
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

	@Autowired
	SalesDBImpl salesDBImpl;

	@Autowired
	ZoyCodeGenerationService passwordGeneration;

	@Autowired
	PasswordDecoder passwordDecoder;

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

	@Autowired
	AdminUserPasswordHistoryRepository adminUserPasswordHistoryRepo;

	@Autowired
	EmailService emailService;

	@Value("${spring.jackson.time-zone}")
	private String currentTimeZone;

	@Autowired
	JsonParserUtil jsonParserUtil;

	@Autowired
	private ZoyAdminTicketSmartService zoyAdminTicketSmartService;


	@Override
	public ResponseEntity<String> zoyAdminSalesCreateUser(ZoyPgSalesMasterModel pgSalesMasterModel) {
		ResponseBody response = new ResponseBody();
		try {
			if (salesDBImpl.existsByUserEmail(pgSalesMasterModel.getEmailId())) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("User already exists with this email");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			if (salesDBImpl.existsByUserMobileNo(pgSalesMasterModel.getMobileNo())) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("User already exists with this mobile no");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyPgSalesMaster master = new ZoyPgSalesMaster();
			master.setFirstName(pgSalesMasterModel.getFirstName());
			master.setLastName(pgSalesMasterModel.getLastName());
			master.setMiddleName(pgSalesMasterModel.getMiddleName());
			master.setEmployeeId(pgSalesMasterModel.getEmployeeId());
			master.setMobileNo(pgSalesMasterModel.getMobileNo());
			master.setEmailId(pgSalesMasterModel.getEmailId());
			master.setUserId(UUID.randomUUID().toString());
			master.setStatus(true);
			salesDBImpl.saveAdminSalesUser(master);

			String autoGeneratedPassword = passwordGeneration.autoGeneratePassword(pgSalesMasterModel.getFirstName());

			ZoyPgSalesUserLoginDetails adminSalesUserLoginDetails = new ZoyPgSalesUserLoginDetails();
			adminSalesUserLoginDetails.setUserEmail(pgSalesMasterModel.getEmailId());
			adminSalesUserLoginDetails.setPassword(passwordDecoder.encryptedText(autoGeneratedPassword));
			adminSalesUserLoginDetails.setIsActive(true);
			adminSalesUserLoginDetails.setIsLock(false);
			adminSalesUserLoginDetails.setIsPasswordChange(false);

			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);
			long currentTimeMillis = calendar.getTimeInMillis();
			Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
			adminSalesUserLoginDetails.setLastChangeOn(currentTimestamp);

			salesDBImpl.saveAdminLoginDetails(adminSalesUserLoginDetails);
			sendLogInInfoToUser(adminSalesUserLoginDetails,autoGeneratedPassword);
			AdminUserPasswordHistory newPasswordHistory = new AdminUserPasswordHistory();
			newPasswordHistory.setUserEmail(pgSalesMasterModel.getEmailId());
			newPasswordHistory.setPassword(adminSalesUserLoginDetails.getPassword());
			adminUserPasswordHistoryRepo.save(newPasswordHistory);
			// audit here
			auditHistoryUtilities.auditForCreateSalesUserDelete(SecurityContextHolder.getContext().getAuthentication().getName(), true, master);
			//Ticket user creation and group added
			UserMaster userCreated=zoyAdminTicketSmartService.createTicketSmartUser(pgSalesMasterModel);
			log.info(userCreated!=null ? "Ticket smart user created" : "Ticket smart user not created");
			if(userCreated!=null) {
				Boolean userGroup=zoyAdminTicketSmartService.assignTicketToGroup(userCreated,pgSalesMasterModel);
				log.info(userGroup.equals(Boolean.TRUE) ? "User group added in ticket system" : "User group unable to add in ticket system");
			}
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("sales User created Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting sales users details  API:/zoy_admin/zoyAdminSalesCreateUser", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getzoyPgSalesUsersDetails(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			Page<ZoyPgSalesMasterModel> ownerPropertyList = salesDBImpl.findAllSalesUsers( paginationRequest);
			return new ResponseEntity<>(gson2.toJson(ownerPropertyList), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching owner details: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch owner details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Error getting while fetching  sales user register details details API:/zoy_admin/getzoyPgSalesUsersDetails.getzoyPgSalesUsersDetails", e);
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

	private void sendLogInInfoToUser(ZoyPgSalesUserLoginDetails adminSalesUserLoginDetails,String plainPassword) {
		try {
			Optional<ZoyPgSalesMaster> masterdetails = salesDBImpl.findByEmail(adminSalesUserLoginDetails.getUserEmail());
			ZoyPgSalesMaster master =masterdetails.get();
			Email email = new Email();
			email.setFrom("zoyAdmin@mydbq.com");
			List<String> to = new ArrayList<>();
			to.add(adminSalesUserLoginDetails.getUserEmail());
			email.setTo(to);
			email.setSubject("Zoy Sales App Signin Information");
			String message = "<html>"
					+ "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
					+ "<p>Hi " + master.getFirstName() + " " + master.getLastName() + ",</p>"
					+ "<p>Welcome to Zoy Sales Portal, We are excited to have you as part of our community!<br>"
					+ "Below are your sign-in credentials for accessing your account.</p>"
					+ "<p>"
					+ "<strong>Username:</strong> " + adminSalesUserLoginDetails.getUserEmail() + "<br>"
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
			log.info("Signin Details sent successfully to " + adminSalesUserLoginDetails.getUserEmail());

		} catch (Exception e) {
			log.error("Error sending login info to user: " + e.getMessage(), e);
		}
	}

	@Override
	public ResponseEntity<String> resendUserDetails(String email) {
		ResponseBody response = new ResponseBody();
		try {
			if (email == null || email.trim().isEmpty()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Email cannot be empty");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			Optional<ZoyPgSalesMaster> userOptional = salesDBImpl.findByEmail(email);
			if (userOptional.isEmpty()) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("User not found with email: " + email);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}

			Optional<ZoyPgSalesUserLoginDetails> loginDetailsOptional = salesDBImpl.findLoginDetailsByEmail(email);
			if (loginDetailsOptional.isEmpty()) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("No login credentials found for user: " + email);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}

			ZoyPgSalesUserLoginDetails loginDetails = loginDetailsOptional.get();
			String plainPassword =loginDetails.getPassword();
			sendLogInInfoToUser(loginDetails,  passwordDecoder.decryptedText(plainPassword)); // Modify email content as needed


			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Login details resent successfully to " + email);
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Failed to resend user details for email: " + email, e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Failed to resend credentials: " + e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> userDesignation() {
		ResponseBody response = new ResponseBody();
		try {
			Object userDesignation=zoyAdminTicketSmartService.getTicketSmartUserDesignation();
			if(userDesignation==null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("Unable to get the data");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.OK.value());
			response.setData(userDesignation);
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error while getting reponse for user desgination " +  e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Failed to get user designation: " + e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<String> salesGroup() {
		ResponseBody response = new ResponseBody();
		try {
			List<UserGroupResponseDto> userGroup=zoyAdminTicketSmartService.getTicketSmartSalesUserGroup();
			if(userGroup==null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("Unable to get the data");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			List<UserGroupResponse> userGroupResponseList = userGroup.stream()
				    .map(dto -> new UserGroupResponse(dto.getId(), dto.getName(), dto.getDescription()))
				    .collect(Collectors.toList());
			response.setStatus(HttpStatus.OK.value());
			response.setData(userGroupResponseList);
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error while getting reponse for user Group " +  e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Failed to get user Group: " + e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}

}
