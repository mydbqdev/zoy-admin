package com.integration.zoy.controller;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterables;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.config.JwtUtil;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AdminUserPasswordHistory;
import com.integration.zoy.entity.AdminUserTemporary;
import com.integration.zoy.entity.AdminUserTemporaryPK;
import com.integration.zoy.entity.AdminUsersLock;
import com.integration.zoy.entity.AppRole;
import com.integration.zoy.entity.RoleScreen;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.AdminUserDetails;
import com.integration.zoy.model.AdminUserUpdateDetails;
import com.integration.zoy.model.ForgotPassword;
import com.integration.zoy.model.LoginDetails;
import com.integration.zoy.model.RoleDetails;
import com.integration.zoy.model.Token;
import com.integration.zoy.model.UserRole;
import com.integration.zoy.repository.AdminUserLockRepository;
import com.integration.zoy.repository.AdminUserLoginDetailsRepository;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AdminUserPasswordHistoryRepository;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.service.ZoyEmailService;
import com.integration.zoy.utils.AdminAppRole;
import com.integration.zoy.utils.AdminUserDetailPrevilage;
import com.integration.zoy.utils.AdminUserList;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.ChangePassWord;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.OtpVerification;
import com.integration.zoy.utils.ResetPassWord;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.RoleModel;

@RestController
@RequestMapping("")
public class ZoyAdminUserController implements ZoyAdminUserImpl {


	private static final Logger log = LoggerFactory.getLogger(ZoyAdminUserController.class);
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
	private final Random random = new Random();
	@Autowired
	AdminDBImpl adminDBImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	EmailService emailService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	PasswordDecoder passwordDecoder;

	@Autowired
	AdminUserMasterRepository adminUserMasterRepository;

	@Autowired
	ZoyAdminService zoyAdminService;

	@Autowired
	AdminUserPasswordHistoryRepository passwordHistoryRepository;
	
	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;
	
	@Autowired
	ZoyCodeGenerationService passwordGeneration;
	
	@Autowired
	private AdminUserLockRepository userRepository;

	@Autowired
	private AdminUserLoginDetailsRepository adminUserLoginDetailsRepository;

	@Value("${qa.signin.link}")
	private String qaSigninLink;
	
	@Value("${spring.jackson.time-zone}")
	private String currentTimeZone;
	

	@Override
	public ResponseEntity<String> zoyAdminUserLogin(LoginDetails details) {
		ResponseBody response = new ResponseBody();
		try {
			AdminUserLoginDetails loginDetails = adminDBImpl.findByEmail(details.getEmail());

			if (loginDetails == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User email not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
			if (!loginDetails.getIsActive()) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("Your account has been deactivated. Contact support for assistance.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
						
			AdminUsersLock userLock = userRepository.findByUsername(details.getEmail());
			if (userLock != null) {
				if (userLock.getLockCount() == 1 && userLock.getAttemptSequence() == 0) {
					if (userLock.getLockTime() != null
							&& userLock.getLockTime().toLocalDateTime().isAfter(LocalDateTime.now().minusMinutes(15))) {
						response.setStatus(HttpStatus.BAD_REQUEST.value());
						response.setMessage(
								"Your account is locked. It will be unlocked automatically after 15 minutes.");
						return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
					} else {
						userLock.setAttemptSequence(0);
						adminUserLoginDetailsRepository.unLockUserByEmail(details.getEmail());
						userRepository.save(userLock);
					}
				}
			}else if(loginDetails.getIsLock()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("Your password has been expired. Please create a new one or use the 'Forgot Password' link to reset it.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				
			}

			String decryptedStoredPassword = passwordDecoder.decryptedText(loginDetails.getPassword());
			String decryptedLoginPassword = passwordDecoder.decryptedText(details.getPassword());

			boolean isPasswordMatch = decryptedStoredPassword.equals(decryptedLoginPassword);

			if (isPasswordMatch) {
				Authentication authentication = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(details.getEmail(), loginDetails.getPassword()));
				String token = jwtUtil.generateToken(authentication);
				userRepository.deleteByUsername(details.getEmail());
				response.setStatus(HttpStatus.OK.value());
				response.setEmail(details.getEmail());
				response.setToken(token);

				auditHistoryUtilities.auditForUserLoginLogout(loginDetails.getUserEmail(), true);

				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {

				if (userLock == null) {
					userLock = new AdminUsersLock();
					userLock.setUsername(details.getEmail());
					userLock.setAttemptSequence(0);
					userLock.setLockCount(0);
					userLock.setLockTime(null);
				}

				if (userLock.getLockCount() == 2) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Your account has been permanently locked. Please contact Admin Support.");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				}

				userLock.setAttemptSequence(userLock.getAttemptSequence() + 1);

				if (userLock.getAttemptSequence() == 1 && userLock.getLockCount() == 0) {
					userLock.setAttemptSequence(1);
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Invalid credentials.");
				} else if (userLock.getAttemptSequence() == 2 && userLock.getLockCount() == 0) {
					userLock.setAttemptSequence(2);
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Invalid credentials. One more failed attempt will lock your account.");
				} else if (userLock.getAttemptSequence() == 3 && userLock.getLockCount() == 0) {
					userLock.setLockCount(1);
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					userLock.setAttemptSequence(0);
					adminUserLoginDetailsRepository.lockUserByEmail(details.getEmail());
					response.setStatus(HttpStatus.LOCKED.value());
					response.setMessage(
							"Your account is locked due to multiple invalid login attempts. Please try again after 15 minutes.");
				} else if (userLock.getAttemptSequence() == 1 && userLock.getLockCount() == 1) {
					userLock.setAttemptSequence(1);
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Invalid credentials.");
				} else if (userLock.getAttemptSequence() == 2 && userLock.getLockCount() == 1) {
					userLock.setAttemptSequence(2);
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage(
							"Invalid credentials. Provide the correct password or your account will be locked again.");
				} else if (userLock.getAttemptSequence() == 3 && userLock.getLockCount() == 1) {
					userLock.setAttemptSequence(3);
					userLock.setLockCount(2); // Permanently lock account
					userLock.setLockTime(Timestamp.valueOf(LocalDateTime.now()));
					adminUserLoginDetailsRepository.lockUserByEmail(details.getEmail());
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Your account has been permanently locked. Please contact Admin Support.");
				}

				userRepository.save(userLock);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.UNAUTHORIZED);
			}
		} catch (Exception e) {
			log.error("Error in zoyAdminUserLogin: " + e.getMessage(), e);
			try {
				new ZoyAdminApplicationException(e, "");
			} catch (Exception ex) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public ResponseEntity<String> zoyAdminUserDetails(Token token) {
		ResponseBody response=new ResponseBody();
		try {
			String emailId=jwtUtil.getUserName(token.getToken());
			if(emailId!=null) {
				String generateToken = jwtUtil.doGenerateRefreshToken(emailId);
				AdminUserDetailPrevilage adminUserList=new AdminUserDetailPrevilage();
				List<String[]> user=adminDBImpl.findAllAdminUserDetails(emailId);
				if(user.size()>0) {
					adminUserList.setToken(generateToken);
					adminUserList.setFirstName(user.get(0)[0]);
					adminUserList.setLastName(user.get(0)[1]!=null?user.get(0)[1]:"");
					adminUserList.setUserEmail(user.get(0)[2]);
					adminUserList.setContactNumber(user.get(0)[3]);
					adminUserList.setDesignation(user.get(0)[4]);
					adminUserList.setPrivilege(user.get(0)[6]!=null?Arrays.asList(user.get(0)[6].split(",")):new ArrayList<>());
				}	
				return new ResponseEntity<>(gson.toJson(adminUserList), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.BAD_GATEWAY.value());
				response.setMessage("Token is invalid");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_GATEWAY);
			}
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/user_details.zoyAdminUserDetails " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	@Override
	public ResponseEntity<String> doUserlogout() {
		ResponseBody response=new ResponseBody();
		try {
			String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			SecurityContextHolder.getContext().setAuthentication(null);
			
			auditHistoryUtilities.auditForUserLoginLogout(userEmail, false);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("You have logged out successfully.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}  catch (DisabledException e) {
			response.setStatus(HttpStatus.BAD_GATEWAY.value());
			response.setMessage("User Inactive");
			log.error("Exception occured while dbq/userlogout: User Inactive" +e);
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_GATEWAY);
		} catch (BadCredentialsException e) {
			//throw new InvalidUserCredentialsException("Invalid Credentials");
			log.error("Exception occured while dbq/userlogout: Invalid Credentials" +e);
			new ZoyAdminApplicationException(e, "");
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage("Invalid Credentials");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
		
	}

	
	@Override
	public ResponseEntity<String> zoyAdminCreateUser(AdminUserDetails adminUserDetails) {
		ResponseBody response=new ResponseBody();
		try {
			if (adminDBImpl.existsByUserEmail(adminUserDetails.getUserEmail())) {
	            response.setStatus(HttpStatus.CONFLICT.value());
	            response.setError("User already exists with this email");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
	        }
			AdminUserMaster master=new AdminUserMaster();
			master.setFirstName(adminUserDetails.getFirstName());
			master.setLastName(adminUserDetails.getLastName());
			master.setDesignation(adminUserDetails.getDesignation());
			master.setContactNumber(adminUserDetails.getContactNumberr());
			master.setUserEmail(adminUserDetails.getUserEmail());
			master.setStatus(true);
			adminDBImpl.saveAdminUser(master);
			
			String autoGeneratedPassword =passwordGeneration.autoGeneratePassword(adminUserDetails.getFirstName());
			
			AdminUserLoginDetails adminUserLoginDetails=new AdminUserLoginDetails();
			adminUserLoginDetails.setUserEmail(adminUserDetails.getUserEmail());
			adminUserLoginDetails.setPassword(passwordDecoder.encryptedText(autoGeneratedPassword));
			adminUserLoginDetails.setIsActive(true);
			adminUserLoginDetails.setIsLock(false);
			
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
	        Calendar calendar = Calendar.getInstance(timeZone);
	        long currentTimeMillis = calendar.getTimeInMillis();
	        Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
	        adminUserLoginDetails.setLastChangeOn(currentTimestamp);
	        
			adminDBImpl.saveAdminLoginDetails(adminUserLoginDetails);

			AdminUserPasswordHistory newPasswordHistory = new AdminUserPasswordHistory();
			newPasswordHistory.setUserEmail(adminUserDetails.getUserEmail());
			newPasswordHistory.setPassword(adminUserLoginDetails.getPassword());
			passwordHistoryRepository.save(newPasswordHistory);
			//audit here
			auditHistoryUtilities.auditForCreateUserDelete(SecurityContextHolder.getContext().getAuthentication().getName(),true,master);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("User created Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ameneties details  API:/zoy_admin/user_create.zoyAdminCreateUser",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserAvailability(String email) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(email);
			if(master!=null) 
				response.setData("1");
			else 
				response.setData("0");
			response.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/user_availability/{email}.zoyAdminUserAvailability ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserUpdate(String email,AdminUserUpdateDetails adminUserDetails) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(email);
			//AdminUserMaster masterOld=SerializationUtils.clone((AdminUserMaster) master);
			AdminUserMaster masterOld=new AdminUserMaster(master);
			if(master!=null) {
				master.setFirstName(adminUserDetails.getFirstName());
				master.setLastName(adminUserDetails.getLastName());
				master.setDesignation(adminUserDetails.getDesignation());
				master.setContactNumber(adminUserDetails.getContactNumber());
				adminDBImpl.updateAdminUser(master);

				//audit here
				auditHistoryUtilities.auditForUpdateUser(SecurityContextHolder.getContext().getAuthentication().getName(),master,masterOld);
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("User updated Successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("User email not found" + email);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/user_update/{email}.zoyAdminUserUpdate " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> zoyAdminUserRoleCreate(RoleDetails roleDetails) {
		ResponseBody response=new ResponseBody();
		try {
			AppRole appRole=new AppRole();
			appRole.setRoleDescription(roleDetails.getDesc());
			appRole.setRoleName(roleDetails.getRoleName());
			AppRole save=adminDBImpl.saveAppRole(appRole);
			List<RoleScreen> roleScreens=new ArrayList<>();
			for(com.integration.zoy.model.RoleScreen screen:roleDetails.getRoleScreen()) {
				RoleScreen roleScreen = new RoleScreen();
				roleScreen.setScreenName(screen.getScreenName());
				roleScreen.setReadPrv(screen.getReadPrv());
				roleScreen.setWritePrv(screen.getWritePrv());
				roleScreen.setAppRole(save);
				roleScreens.add(roleScreen);
			}
			adminDBImpl.saveAllRoleScreen(roleScreens);
			String history=gson.toJson(roleDetails).toString();
			
			//audit here
			auditHistoryUtilities.auditForRoleCreate(SecurityContextHolder.getContext().getAuthentication().getName(),true,history);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Role created Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/role_create.zoyAdminUserRoleCreate",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserRoleUpdate(RoleDetails roleDetails) {
		ResponseBody response=new ResponseBody();
		try {
			StringBuffer history=new StringBuffer();
			AppRole appRole=adminDBImpl.findAppRole(roleDetails.getRoleName());
			if(appRole!=null) {
				if(!(String.valueOf(appRole.getRoleDescription()).equals(roleDetails.getDesc()))) {
					history.append("Description from "+appRole.getRoleDescription()+" to "+roleDetails.getDesc());
				}
				appRole.setRoleDescription(roleDetails.getDesc());
				appRole.setRoleName(roleDetails.getRoleName());
				appRole.setTs(Timestamp.valueOf(LocalDateTime.now()));
				adminDBImpl.updateAppRole(appRole);

				List<RoleScreen> appRoleScreens=adminDBImpl.findRoleScreen(appRole.getId());
				Map<String, RoleScreen> appRoleScreenMap = appRoleScreens.stream()
						.collect(Collectors.toMap(RoleScreen::getScreenName, screen -> screen));

				List<com.integration.zoy.model.RoleScreen> appRoleScreensDb=new ArrayList<>();
				appRoleScreens.forEach(role->{
					com.integration.zoy.model.RoleScreen sc=new com.integration.zoy.model.RoleScreen();
					sc.setScreenName(role.getScreenName());
					sc.setReadPrv(role.getReadPrv());
					sc.setWritePrv(role.getWritePrv());
					appRoleScreensDb.add(sc);
				});
				List<RoleScreen> newScreens = new ArrayList<>();
				List<RoleScreen> updatedScreens = new ArrayList<>();
				List<Long> obsoleteScreenIds = new ArrayList<>();

				roleDetails.getRoleScreen().forEach(screen -> {
					RoleScreen roleScreen = new RoleScreen();
					roleScreen.setScreenName(screen.getScreenName());
					roleScreen.setReadPrv(screen.getReadPrv());
					roleScreen.setWritePrv(screen.getWritePrv());
					roleScreen.setAppRole(appRole);
					RoleScreen existingScreen = appRoleScreenMap.remove(screen.getScreenName());
					if (existingScreen == null) {
						newScreens.add(roleScreen);
					} else if (!existingScreen.getReadPrv().equals(screen.getReadPrv()) ||
							!existingScreen.getWritePrv().equals(screen.getWritePrv())) {
						existingScreen.setReadPrv(screen.getReadPrv());
						existingScreen.setWritePrv(screen.getWritePrv());
						updatedScreens.add(existingScreen);
					}
				});

				obsoleteScreenIds.addAll(appRoleScreenMap.values().stream().map(RoleScreen::getId).collect(Collectors.toList()));

				if(newScreens.size()>0)
					adminDBImpl.saveAllRoleScreen(newScreens);
				if(updatedScreens.size()>0)
					adminDBImpl.saveAllRoleScreen(updatedScreens);
				if(obsoleteScreenIds.size()>0)
					adminDBImpl.deleteAllRoleScreen(obsoleteScreenIds);
				//audit here
				List<RoleScreen> appRoleScreensnew=new ArrayList<>(newScreens);
				appRoleScreensnew.addAll(updatedScreens);
				List<com.integration.zoy.model.RoleScreen> appRoleScreensUpdate=new ArrayList<>();
				appRoleScreensnew.forEach(role->{
					com.integration.zoy.model.RoleScreen sc=new com.integration.zoy.model.RoleScreen();
					sc.setScreenName(role.getScreenName());
					sc.setReadPrv(role.getReadPrv());
					sc.setWritePrv(role.getWritePrv());
					appRoleScreensUpdate.add(sc);
				});
				List<com.integration.zoy.model.RoleScreen> appRoleScreenDeleted=new ArrayList<>();
				appRoleScreenMap.values().stream().forEach(deleted ->{
					com.integration.zoy.model.RoleScreen sc=new com.integration.zoy.model.RoleScreen();
					sc.setScreenName(deleted.getScreenName());
					sc.setReadPrv(deleted.getReadPrv());
					sc.setWritePrv(deleted.getWritePrv());
					appRoleScreenDeleted.add(sc);
				});
				
				auditHistoryUtilities.auditForRoleUpdate(appRole.getRoleName(),history,appRoleScreensDb,appRoleScreensUpdate,appRoleScreenDeleted);
				
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Role Updated Successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("Not found the role "+roleDetails.getRoleName());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/role_update.zoyAdminUserRoleUpdate " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserRoleList() {
		ResponseBody response=new ResponseBody();
		List<AdminAppRole> adminAppRoles=new ArrayList<>();
		try { 
			//System.out.println("SecurityContextHolder.getContext().getAuthentication().getDetails().toString():"+SecurityContextHolder.getContext().getAuthentication().getName()); 
			List<AppRole> appRole=adminDBImpl.findAllAppRole();
			for(AppRole role:appRole) {
				AdminAppRole adminAppRole=new AdminAppRole();
				List<RoleScreen> roleScreen=adminDBImpl.findRoleScreen(role.getId());
				adminAppRole.setId(role.getId());
				adminAppRole.setRoleName(role.getRoleName());
				adminAppRole.setDesc(role.getRoleDescription());
				List<com.integration.zoy.utils.RoleScreen> roleScreens=new ArrayList<>();
				for(RoleScreen screen : roleScreen) {
					com.integration.zoy.utils.RoleScreen data=new com.integration.zoy.utils.RoleScreen();
					data.setScreenName(screen.getScreenName());
					data.setReadPrv(screen.getReadPrv());
					data.setWritePrv(screen.getWritePrv());
					roleScreens.add(data);
				}
				//System.out.println("roleScreens::"+roleScreens);
				adminAppRole.setRoleScreen(roleScreens);
				adminAppRoles.add(adminAppRole);
				//System.out.println("roleScreens::"+gson.toJson(adminAppRoles));
			}
			return new ResponseEntity<>(gson.toJson(adminAppRoles), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting zoy Admin User Role List details API:/zoy_admin/role_list.zoyAdminUserRoleList " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserAssign(UserRole userRole) {
		ResponseBody response=new ResponseBody();
		List<String> history=new ArrayList<>();
		try {
			List<AdminUserTemporary> adminUserTemporary=new ArrayList<>();
			for(Long id:userRole.getRoleId()) {
					AdminUserTemporary userTemporary=new AdminUserTemporary();
					AdminUserTemporaryPK userTemporaryPk=new AdminUserTemporaryPK();
					
					userTemporaryPk.setUserEmail(userRole.getUserEmail());
					userTemporaryPk.setRoleId(id);
					userTemporary.setAdminUserTemporaryPK(userTemporaryPk);
					userTemporary.setIsApprove(false);
					userTemporary.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					adminUserTemporary.add(userTemporary);
					AppRole approle=adminDBImpl.findAppRoleId(id);
					history.add(approle.getRoleName());
			}
			adminDBImpl.saveAllUserTemporary(adminUserTemporary);
			
			//audit here
			auditHistoryUtilities.auditForRoleAssign(SecurityContextHolder.getContext().getAuthentication().getName(),gson.toJson(history).toString(),userRole.getUserEmail());
			
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Role Assigned & sent for approval successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/user_assign.zoyAdminUserAssign",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminUserSendLoginInfo(String userName) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(userName);
			if(master!=null) {
				AdminUserLoginDetails login=adminDBImpl.findByEmail(master.getUserEmail());
				Email email=new Email();
				email.setFrom("zoyAdmin@mydbq.com");
				List<String> to=new ArrayList<>();
				to.add(login.getUserEmail());
				email.setTo(to);
				email.setSubject("Zoy Admin Portal Signin Information");
				String message = "<p>Hi "+master.getFirstName()+" "+master.getLastName()+",</p>"
						+ "<p>Welcome to Zoy Admin Portal, We are excited to have you as part of our community! "
						+ "Below are your sign-in credentials for accessing your account.</p>"
						+ "<p>Username: "+ login.getUserEmail()
						+ "<br>Password: <b>"+ passwordDecoder.decryptedText(login.getPassword())+"</b><br>"
						+ "Zoy Admin Portal Sign-in Link : <a href='"+ qaSigninLink +"'>"+ qaSigninLink +"</a></p>"
						+ "<p class=\"footer\">Warm regards,<br>Team ZOY</p>";
				email.setBody(message);
				email.setContent("text/html");
				emailService.sendEmail(email,null); 
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Signin Details sent successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/send_login_info.zoyAdminUserSendLoginInfo " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<String> zoyAdminUserListOld() {
		ResponseBody response = new ResponseBody();
		try {
			List<Object[]> master = adminDBImpl.findAllAdminUserPrevilages();
			List<AdminUserList> adminUserTemporary = new ArrayList<>();

			for (Object[] result : master) {
				AdminUserList user = new AdminUserList();
				user.setFirstName((String) result[0]);
				user.setLastName((String) result[1]);
				user.setUserEmail((String) result[2]);
				user.setContactNumber((String) result[3]);
				user.setDesignation((String) result[4]);
				user.setStatus((Boolean) result[5]);
				List<RoleModel> roles = new ArrayList<>();
				if (result[7] != null && !"null".equals(result[7])) {
					int roleId = (Integer) result[6];
					String roleName = (String) result[7];
					String approveStatus=(String) result[8];
					RoleModel role = new RoleModel(roleId, roleName,approveStatus); 

					String approvedPrivileges = (String) result[9];  
					if (approvedPrivileges != null && !approvedPrivileges.isEmpty()) {
						String[] approvedPrivilegesArray = approvedPrivileges.split(",");
						for (String privilege : approvedPrivilegesArray) {
							role.addApprovedPrivilege(privilege); 
						}
					}

					String unapprovedPrivileges = (String) result[10];  
					if (unapprovedPrivileges != null && !unapprovedPrivileges.isEmpty()) {
						String[] unapprovedPrivilegesArray = unapprovedPrivileges.split(",");
						for (String privilege : unapprovedPrivilegesArray) {
							role.addUnapprovedPrivilege(privilege);  
						}
					}

					roles.add(role);
				}

				user.setRoleModel(roles);
				adminUserTemporary.add(user);
			}

			return new ResponseEntity<>(gson.toJson(adminUserTemporary), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error getting amenities details " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminUserList() {
		ResponseBody response = new ResponseBody();
		try {
			List<AdminUserMaster> master =adminDBImpl.findAllAdminUser();
			List<Object[]> userPrevilages = adminDBImpl.findAllAdminUserPrevilages();
			List<AdminUserList> adminUserTemporary = new ArrayList<>();

			for (AdminUserMaster result : master) {
				AdminUserList user = new AdminUserList();
				user.setFirstName(result.getFirstName());
				user.setLastName(result.getLastName());
				user.setUserEmail(result.getUserEmail());
				user.setContactNumber(result.getContactNumber());
				user.setDesignation(result.getDesignation());
				user.setStatus(result.getStatus());
				List<RoleModel> roles = new ArrayList<>();

				List<Object[]>previlages= userPrevilages.stream()
						.filter(p ->p[0].equals(result.getUserEmail()) && null != p[1] && !"null".equals(p[1])  )
						.collect(Collectors.toList());

				if (!previlages.isEmpty()) {
					for(Object[] pre:previlages) {
						int roleId = pre[1] instanceof BigInteger ? ((BigInteger) pre[1]).intValue() : (Integer) pre[1];

						//	int roleId = (Integer) pre[1];
						String roleName = (String) pre[3];
						String approveStatus=(String) pre[2];

						RoleModel role = new RoleModel(roleId, roleName,approveStatus); 

						String screens = (String) pre[4];  
						if (screens != null && !screens.isEmpty()) {
							String[] screensSet = screens.split(",");
							for (String screensNames : screensSet) {
								role.addScreens(screensNames); 
							}
						}
						roles.add(role);

					}

				}

				user.setRoleModel(roles);
				adminUserTemporary.add(user);
			}

			return new ResponseEntity<>(gson.toJson(adminUserTemporary), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error getting user list details API:/zoy_admin/user_list.zoyAdminUserList " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	public ResponseEntity<String> approveOrRejectRole( String userEmail, String status) {

		ResponseBody response = new ResponseBody();     
		try {

			if (status == null || (!status.equals("approved") && !status.equals("rejected"))) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Invalid status value. Status must be either 'approved' or 'rejected'.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}else {
				final List<String> existingRole=adminUserMasterRepository.getRoleAssigned(userEmail);
				final List<String> newTempRole=adminUserMasterRepository.getRoleTempBeforeApproved(userEmail);
				
				if(status.equals("approved")) {
					adminDBImpl.approveUser(userEmail);
					adminDBImpl.insertUserDetails(userEmail);
				}        

				// remove record from temporary table after approve /reject
				adminDBImpl.rejectUser(userEmail);
				
				//audit here
				Optional<AdminUserMaster> user2=adminUserMasterRepository.findById(userEmail);
				String userNameFor="";
				if(user2.isPresent()) {
					userNameFor=user2.get().getFirstName()+" "+user2.get().getLastName();
				}
				StringBuffer history=new StringBuffer(" has "+status+" the role assign to "+userNameFor+" for, Roles from ");
				history.append(existingRole!=null && !existingRole.isEmpty() ? gson.toJson(existingRole).toString(): "-");
				history.append(" to ");
				history.append(newTempRole!=null && !newTempRole.isEmpty() ? gson.toJson(newTempRole).toString(): "-");				
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),history.toString(),status.equals("approved") ? ZoyConstant.ZOY_ADMIN_USER_AUTHORZITION_APPROVE:ZoyConstant.ZOY_ADMIN_USER_AUTHORZITION_REJECTED);
				
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("The Assigned Role has been " + status + " successfully.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error in approveOrRejectRole API:/zoy_admin/approve_or_reject_role.approveOrRejectRole " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	@Override
	public ResponseEntity<String> deleteRole(int roleId ,String roleName) {
		ResponseBody response = new ResponseBody();

		try {
			List<Integer> roleIdPresent = adminDBImpl.findRoleIfAssigned(roleId);

			if (roleIdPresent == null || roleIdPresent.isEmpty()|| roleIdPresent.size()==0) {
				AppRole approle=adminDBImpl.findAppRoleId(Long.valueOf(roleId));
				RoleDetails roleDetails=new RoleDetails();
				List<RoleScreen> roleScreen= adminDBImpl.findRoleScreen(Long.valueOf(roleId));
				List<com.integration.zoy.model.RoleScreen> roleScreens=new ArrayList<>();
				for(RoleScreen screen : roleScreen) {
					com.integration.zoy.model.RoleScreen data=new com.integration.zoy.model.RoleScreen();
					data.setScreenName(screen.getScreenName());
					data.setReadPrv(screen.getReadPrv());
					data.setWritePrv(screen.getWritePrv());
					roleScreens.add(data);
				}
				roleDetails.setRoleScreen(roleScreens);
				roleDetails.setRoleName(approle.getRoleName());
				roleDetails.setDesc(approle.getRoleDescription());
				String history=gson.toJson(roleDetails).toString();
				
				adminDBImpl.deleteRolefromRoleScreen(roleId);   
				adminDBImpl.deleteRolefromApp_role(roleId); 
				
				//audit here
				auditHistoryUtilities.auditForRoleCreate(SecurityContextHolder.getContext().getAuthentication().getName(),false,history);
				response.setStatus(HttpStatus.OK.value());
				response.setMessage( roleName + " role has been deleted successfully.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("Deletion is not possible as the role " + roleName + " is currently assigned.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error("Error in deleteRole API:zoy_admin/role_delete.deleteRole " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminNotApprovedRoles() {
		ResponseBody response = new ResponseBody();
		try {

			List<Object[]> userPrevilages = adminDBImpl.findAllAdminUserPrivileges1();


			Iterable<String> empMails = userPrevilages.stream().map(e -> String.valueOf(e[0])).collect(Collectors.toSet());

			List<AdminUserMaster> master = adminDBImpl.userdata(Iterables.toArray(empMails,String.class));

			List<AdminUserList> adminUserTemporary = new ArrayList<>();

			for (AdminUserMaster result : master) {
				AdminUserList user = new AdminUserList();
				user.setFirstName(result.getFirstName());
				user.setLastName(result.getLastName());
				user.setUserEmail(result.getUserEmail());
				user.setContactNumber(result.getContactNumber());
				user.setDesignation(result.getDesignation());
				user.setStatus(result.getStatus());
				List<RoleModel> roles = new ArrayList<>();

				List<Object[]>previlages= userPrevilages.stream()
						.filter(p ->p[0].equals(result.getUserEmail()) && null != p[1] && !"null".equals(p[1])  )
						.collect(Collectors.toList());

				if (!previlages.isEmpty()) {
					for(Object[] pre:previlages) {
						int roleId = pre[1] instanceof BigInteger ? ((BigInteger) pre[1]).intValue() : (Integer) pre[1];

						//	int roleId = (Integer) pre[1];
						String roleName = (String) pre[3];
						String approveStatus=(String) pre[2];

						RoleModel role = new RoleModel(roleId, roleName,approveStatus); 

						String screens = (String) pre[4];  
						if (screens != null && !screens.isEmpty()) {
							String[] screensSet = screens.split(",");
							for (String screensNames : screensSet) {
								role.addScreens(screensNames); 
							}
						}
						roles.add(role);

					}

				}

				user.setRoleModel(roles);
				adminUserTemporary.add(user);
			}

			return new ResponseEntity<>(gson.toJson(adminUserTemporary), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error getting user list details /zoy_admin/userListNotApprove.zoyAdminNotApprovedRoles" + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}



	@Override
	public ResponseEntity<String> zoyAdminUserForgotpasswordPost(ForgotPassword forgotPassword) {
		ResponseBody response = new ResponseBody();
		try {
			if(forgotPassword!=null && forgotPassword.getEmail()!=null) {
				AdminUserLoginDetails user = adminDBImpl.findByEmail(forgotPassword.getEmail().toLowerCase());
				if(user==null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("User does not exists");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				}
				String otp = String.valueOf(100000 + random.nextInt(900000));
				zoyAdminService.getForgotPasswordOtp().put(user.getUserEmail(), otp);
				zoyEmailService.sendForgotEmail(user,otp);

				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Password Reset OTP has been sent successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

			} else {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("All fields are required");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			log.error("Error occurred during registration API:/zoy_admin/admin_forgot_password.zoyAdminUserForgotpasswordPost", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	
	public ResponseEntity<String> zoyAdminUserOtpValidation(OtpVerification verifiOtp) {	    
	    ResponseBody response = new ResponseBody();
	    
	    try {
	        
	        String otpResponse = zoyAdminService.validateOtp(verifiOtp);
	        if (otpResponse.equals("OTP validated successfully")) {
	            response.setStatus(HttpStatus.OK.value()); 
	            response.setMessage(otpResponse);      
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	        } else if (otpResponse.equals("Invalid OTP")) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setMessage(otpResponse);  
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        } else if (otpResponse.equals("Expired OTP")) {
	            response.setStatus(HttpStatus.GONE.value());
	            response.setMessage(otpResponse);       
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.GONE);
	        } else {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setMessage("Something went wrong");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }
	        
	    } catch (Exception e) {
	        log.error("Error occurred during OTP validation for user API:/zoy_admin/admin_otp_verify.zoyAdminUserOtpValidation", verifiOtp.getEmail(), e.getMessage(), e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setMessage(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}

	@Override
	public ResponseEntity<String> zoyAdminUserPasswordSave(ChangePassWord verifiOtp) {
		ResponseBody response = new ResponseBody();
		try {
			AdminUserLoginDetails loginDetails = adminDBImpl.findByEmail(verifiOtp.getEmail());
			if (loginDetails == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User not found.");
			
				return  new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
				
			}
			List<AdminUserPasswordHistory> passwordHistoryList = passwordHistoryRepository.findTop3ByUserEmailOrderByTsDesc(verifiOtp.getEmail());

			String password = verifiOtp.getPassword();

			

			boolean passwordExistsInHistory = passwordHistoryList.stream()
			        .anyMatch(history -> isPasswordInHistory(history.getPassword(), password));


			if (passwordExistsInHistory) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("Please use a different password. This password has already been used.");
				return  new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			AdminUserPasswordHistory newPasswordHistory = new AdminUserPasswordHistory();
			newPasswordHistory.setUserEmail(verifiOtp.getEmail());
			newPasswordHistory.setPassword(password);
			passwordHistoryRepository.save(newPasswordHistory);

			if (passwordHistoryList.size() > 2) {
				AdminUserPasswordHistory oldestPasswordHistory = passwordHistoryList.get(passwordHistoryList.size() - 1);
				passwordHistoryRepository.delete(oldestPasswordHistory);
			}

			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
	        Calendar calendar = Calendar.getInstance(timeZone);
	        long currentTimeMillis = calendar.getTimeInMillis();
	        Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
	        
	        loginDetails.setIsLock(false);
			loginDetails.setPassword(password);
			loginDetails.setLastChangeOn(currentTimestamp);
			adminDBImpl.saveAdminLoginDetails(loginDetails);
			
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Password updated successfully.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
	        log.error("Error occurred while updating password for user API:/zoy_admin/admin_reset_password.zoyAdminUserPasswordSave", e);
			e.printStackTrace();
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage(e.getMessage());
			return  new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}
	}
	
	private boolean isPasswordInHistory(String historyPassword, String password) {
	    try {
	        return passwordDecoder.decryptedText(historyPassword)
	                .equals(passwordDecoder.decryptedText(password));
	    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
	            | InvalidAlgorithmParameterException | IllegalBlockSizeException
	            | BadPaddingException e) {
	        e.printStackTrace();
	        log.error("Error occurred while decrypting password during history check ", e.getMessage(), e);
	        return false; 
	    }
	}



	@Override
	public ResponseEntity<String> zoyAdminResetPasswordSave(ResetPassWord resetPassword) {
		AdminUserLoginDetails loginDetails = adminDBImpl.findByEmail(resetPassword.getEmail());
		ResponseBody response = new ResponseBody();

		if (loginDetails != null) {
			try {
				String decryptedOldPassword = passwordDecoder.decryptedText(resetPassword.getOldPassWord()); 
				String decryptedStoredPassword = passwordDecoder.decryptedText(loginDetails.getPassword()); 

				if (!decryptedOldPassword.equals(decryptedStoredPassword)) {

					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setMessage("Old password is incorrect.");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				}

				ChangePassWord changePassWord = new ChangePassWord();
				changePassWord.setEmail(resetPassword.getEmail());
				changePassWord.setPassword(resetPassword.getNewPassword());

				return zoyAdminUserPasswordSave(changePassWord);
			} catch (Exception e) {
	            log.error("Error occurred while resetting password for user API:/zoy_admin/admin_reset_password.zoyAdminResetPasswordSave ", e);
				e.printStackTrace();
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage(e.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
		}
		
		response.setStatus(HttpStatus.NOT_FOUND.value());
		response.setMessage("User not found.");
		return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);

	}



	@Override
	public ResponseEntity<String> doUserActiveteDeactivete(AdminUserList details) {
		ResponseBody response = new ResponseBody();     
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(details.getUserEmail());
			AdminUserMaster masterOld=new AdminUserMaster(master);
				adminDBImpl.doUserActiveteDeactivete(details.getUserEmail(),!details.getStatus()); 
		        zoyEmailService.sendForUserDoUserActiveteDeactivete(details);
				
				//audit here
				master.setStatus(!details.getStatus());
				auditHistoryUtilities.auditForUpdateUser(SecurityContextHolder.getContext().getAuthentication().getName(),master,masterOld);
				String status = details.getStatus()? "deactivated" :"activated";
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("The user has been " + status + " successfully.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error in doUserActiveteDeactivete API:/zoy_admin/doUserActiveteDeactivete.doUserActiveteDeactivete " + e.getMessage(), e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminUserUnlock(AdminUserList request) {
		ResponseBody response = new ResponseBody();

		try {
			AdminUsersLock user = userRepository.findByUsername(request.getUserEmail());
			if (user == null || user.getUsername() == null) {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User email not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
			userRepository.unlockUser(request.getUserEmail());
			adminUserLoginDetailsRepository.unLockUserByEmail(request.getUserEmail());

			userRepository.save(user);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("User has been unlocked successfully.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception ex) {
			log.error("Error in zoyAdminUserUnlock API:/zoy_admin/zoyAdminUserUnlock.zoyAdminUserUnlock", ex);
			 try {
		            new ZoyAdminApplicationException(ex, "");
		        } catch (Exception e) {
		            response.setStatus(HttpStatus.BAD_REQUEST.value());
		            response.setError(e.getMessage());
		            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		        }
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(ex.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminlockedUsers() {
		ResponseBody response = new ResponseBody();
		try {

			List<Object[]> lockedUsersList = userRepository.findLockedUsers();

			List<AdminUserList> adminuserslockedList = new ArrayList<>();

			for (Object[] details : lockedUsersList) {
				AdminUserList lockedUsers = new AdminUserList();

				lockedUsers.setUserEmail(details[0] != null ? (String) details[0] : null);
				lockedUsers.setFirstName(details[1] != null ? (String) details[1] : null);
				lockedUsers.setLastName(details[2] != null ? (String) details[2] : null);
				lockedUsers.setDesignation(details[3] != null ? (String) details[3] : null);
			//	lockedUsers.setStatus(details[3] != null ? (String) details[3] : null);

				adminuserslockedList.add(lockedUsers);
			}

			return new ResponseEntity<>(gson.toJson(adminuserslockedList), HttpStatus.OK);

		} catch (Exception ex) {
			log.error("Error getting user list details /zoy_admin/userListNotApprove.zoyAdminNotApprovedRoles", ex);
			try {
	            new ZoyAdminApplicationException(ex, "");
	        } catch (Exception e) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError(e.getMessage());
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(ex.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

}




