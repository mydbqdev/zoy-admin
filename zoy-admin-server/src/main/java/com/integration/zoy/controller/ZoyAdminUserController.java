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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AdminUserPasswordHistory;
import com.integration.zoy.entity.AdminUserTemporary;
import com.integration.zoy.entity.AdminUserTemporaryPK;
import com.integration.zoy.entity.AppRole;
import com.integration.zoy.entity.RoleScreen;
import com.integration.zoy.model.AdminUserDetails;
import com.integration.zoy.model.AdminUserUpdateDetails;
import com.integration.zoy.model.ForgotPassword;
import com.integration.zoy.model.LoginDetails;
import com.integration.zoy.model.RoleDetails;
import com.integration.zoy.model.Token;
import com.integration.zoy.model.UserRole;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AdminUserPasswordHistoryRepository;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.ZoyAdminService;
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


	@Value("${qa.signin.link}")
	private String qaSigninLink;

	@Override
	public ResponseEntity<String> zoyAdminUserLogin(LoginDetails details) {
		ResponseBody response=new ResponseBody();
		Authentication authentication = null;
		try {
			AdminUserLoginDetails loginDetails=adminDBImpl.findByEmail(details.getEmail());
			if(loginDetails!=null) {
				
				if(!loginDetails.getIsActive()) {
					response.setStatus(HttpStatus.NOT_FOUND.value());
					response.setMessage("Your account has been deactivated. Contact support for assistance. ");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
				}

				String decryptedStoredPassword = passwordDecoder.decryptedText(details.getPassword()); 
				String decryptedLoginPassword = passwordDecoder.decryptedText(loginDetails.getPassword()); 

				boolean isPasswordMatch = decryptedStoredPassword.equals(decryptedLoginPassword);

				if(isPasswordMatch) {
					authentication = authenticationManager
							.authenticate(new UsernamePasswordAuthenticationToken(details.getEmail(), loginDetails.getPassword()));
					response.setStatus(HttpStatus.OK.value());
					response.setEmail(details.getEmail());
					String token = jwtUtil.generateToken(authentication);
					response.setToken(token);
					
					
					// audit here
					auditHistoryUtilities.auditForUserLoginLogout(loginDetails.getUserEmail(), true);
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
				} else {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Incorrect password");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.UNAUTHORIZED);
				}
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User email not found ");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error getting ameneties details API:/zoy_admin/login.zoyAdminUserLogin " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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

			AdminUserLoginDetails adminUserLoginDetails=new AdminUserLoginDetails();
			adminUserLoginDetails.setUserEmail(adminUserDetails.getUserEmail());
			adminUserLoginDetails.setPassword(passwordDecoder.encryptedText(adminUserDetails.getPassword()));
			adminUserLoginDetails.setIsActive(true);
			adminUserLoginDetails.setIsLock(false);
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
			AppRole appRole=adminDBImpl.findAppRole(roleDetails.getRoleName());
			if(appRole!=null) {
				appRole.setRoleDescription(roleDetails.getDesc());
				appRole.setRoleName(roleDetails.getRoleName());
				appRole.setTs(Timestamp.valueOf(LocalDateTime.now()));
				adminDBImpl.updateAppRole(appRole);

				List<RoleScreen> appRoleScreens=adminDBImpl.findRoleScreen(appRole.getId());
				Map<String, RoleScreen> appRoleScreenMap = appRoleScreens.stream()
						.collect(Collectors.toMap(RoleScreen::getScreenName, screen -> screen));

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
			}
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Role Updated Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
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
				adminAppRole.setRoleScreen(roleScreens);
				adminAppRoles.add(adminAppRole);
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
			}
			adminDBImpl.saveAllUserTemporary(adminUserTemporary);
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
	public ResponseEntity<String> zoyAdminUserSendLoginInfo(@RequestParam("userName")String userName) {
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
						+ "<p>Username: "+ login.getUserEmail()+"</p>"
						+ "Password: "+ passwordDecoder.decryptedText(login.getPassword())+"</p>"
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

				if(status.equals("approved")) {
					adminDBImpl.approveUser(userEmail);
					adminDBImpl.insertUserDetails(userEmail);
				}        

				// remove record from temporary table after approve /reject
				adminDBImpl.rejectUser(userEmail);        	
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
				adminDBImpl.deleteRolefromRoleScreen(roleId);   
				adminDBImpl.deleteRolefromApp_role(roleId);      

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

			loginDetails.setPassword(password);
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
		       //zoyEmailService.sendForUserDoUserActiveteDeactivete(details);
				
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


}




