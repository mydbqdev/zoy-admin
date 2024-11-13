package com.integration.zoy.controller;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
import com.integration.zoy.entity.AdminUserTemporary;
import com.integration.zoy.entity.AppRole;
import com.integration.zoy.entity.RoleScreen;
import com.integration.zoy.model.AdminUserDetails;
import com.integration.zoy.model.AdminUserUpdateDetails;
import com.integration.zoy.model.LoginDetails;
import com.integration.zoy.model.RoleDetails;
import com.integration.zoy.model.Token;
import com.integration.zoy.model.UserRole;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.utils.AdminAppRole;
import com.integration.zoy.utils.AdminUserDetailPrevilage;
import com.integration.zoy.utils.AdminUserList;
import com.integration.zoy.utils.Email;
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
	AdminDBImpl adminDBImpl;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	EmailService emailService;

	@Autowired
	PasswordDecoder passwordDecoder;

	@Override
	public ResponseEntity<String> zoyAdminUserLogin(LoginDetails details) {
		ResponseBody response=new ResponseBody();
		Authentication authentication = null;
		try {
			AdminUserLoginDetails loginDetails=adminDBImpl.findByEmail(details.getEmail());
			if(loginDetails!=null) {
			
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
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
				} else {
					response.setStatus(HttpStatus.UNAUTHORIZED.value());
					response.setMessage("Incorrect password");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.UNAUTHORIZED);
				}
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User email not found " + details.getEmail());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error getting user login details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@Override
	public ResponseEntity<String> zoyAdminUserDetails(Token token) {
		ResponseBody response=new ResponseBody();
		try {
			String emailId=jwtUtil.getUserName(token.getToken());
			if(emailId!=null) {
				List<String[]> user=adminDBImpl.findAllAdminUserDetails(emailId);
				AdminUserDetailPrevilage adminUserList=new AdminUserDetailPrevilage();
				if(user.size()>0) {
					adminUserList.setToken(token.getToken());
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
			log.error("Error getting User details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@Override
	public ResponseEntity<String> zoyAdminCreateUser(AdminUserDetails adminUserDetails) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=new AdminUserMaster();
			master.setFirstName(adminUserDetails.getFirstName());
			master.setLastName(adminUserDetails.getLastName());
			master.setDesignation(adminUserDetails.getDesignation());
			master.setContactNumber(adminUserDetails.getMobileNumber());
			master.setUserEmail(adminUserDetails.getUserEmail());
			master.setStatus(true);
			adminDBImpl.saveAdminUser(master);

			AdminUserLoginDetails adminUserLoginDetails=new AdminUserLoginDetails();
			adminUserLoginDetails.setUserEmail(adminUserDetails.getUserEmail());
			adminUserLoginDetails.setPassword(passwordDecoder.encryptedText(adminUserDetails.getPassword()));
			adminUserLoginDetails.setIsActive(true);
			adminUserLoginDetails.setIsLock(false);
			adminDBImpl.saveAdminLoginDetails(adminUserLoginDetails);

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("User created Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error creating users: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
			log.error("Error getting user availabilty details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserUpdate(String email,AdminUserUpdateDetails adminUserDetails) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(email);
			if(master!=null) {
				master.setFirstName(adminUserDetails.getFirstName());
				master.setLastName(adminUserDetails.getLastName());
				master.setDesignation(adminUserDetails.getDesignation());
				master.setContactNumber(adminUserDetails.getMobileNumber());
				adminDBImpl.updateAdminUser(master);

				response.setStatus(HttpStatus.OK.value());
				response.setMessage("User updated Successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("User email not found" + email);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating user details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
			response.setMessage("App role created Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error creating role: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
			response.setMessage("App role Updated Successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error updating role: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserRoleList() {
		ResponseBody response=new ResponseBody();
		List<AdminAppRole> adminAppRoles=new ArrayList<>();
		try {
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
			log.error("Error getting role list: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminUserAssign(UserRole userRole) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(userRole.getUserEmail());
			List<AdminUserTemporary> adminUserTemporary=new ArrayList<>();
			for(Long id:userRole.getRoleId()) {
				AppRole appRole=adminDBImpl.findAppRoleId(id);
				if(appRole!=null) {
					AdminUserTemporary userTemporary=new AdminUserTemporary();
					userTemporary.setUserMaster(master);
					userTemporary.setAppRole(appRole);
					userTemporary.setIsApprove(false);
					userTemporary.setCreatedOn(new Timestamp(System.currentTimeMillis()));
					adminUserTemporary.add(userTemporary);
				}
			}
			adminDBImpl.saveAllUserTemporary(adminUserTemporary);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("User assign saved for approval successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error adding user assign: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@Override
//	public ResponseEntity<String> zoyAdminUserList() {
//		ResponseBody response=new ResponseBody();
//		try {
//			List<String[]> master=adminDBImpl.findAllAdminUserPrevilages();
//			List<AdminUserList> adminUserTemporary=new ArrayList<>();
//			if(master.size()>0) {
//				for(String[] user:master) {
//					AdminUserList adminUserList=new AdminUserList();
//					adminUserList.setFirstName(user[0]);
//					adminUserList.setLastName(user[1]!=null?user[1]:"");
//					adminUserList.setUserEmail(user[2]);
//					adminUserList.setContactNumber(user[3]);
//					adminUserList.setStatus(user[5]);
//					adminUserList.setApprovedPrivilege(user[6]!=null?Arrays.asList(user[6].split(",")):new ArrayList<>());
//					adminUserList.setUnapprovedPrivilege(user[7]!=null?Arrays.asList(user[7].split(",")):new ArrayList<>());
//					adminUserTemporary.add(adminUserList);
//				}
//			}
//			return new ResponseEntity<>(gson.toJson(adminUserTemporary), HttpStatus.OK);
//		} catch (Exception e) {
//			log.error("Error getting ameneties details: " + e.getMessage(),e);
//			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			response.setError("Internal server error");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//
//	}

	@Override
	public ResponseEntity<String> zoyAdminUserSendLoginInfo(LoginDetails details) {
		ResponseBody response=new ResponseBody();
		try {
			AdminUserMaster master=adminDBImpl.findAdminUserMaster(details.getEmail());
			if(master!=null) {
				Email email=new Email();
				email.setFrom("zoyAdmin@mydbq.com");
				List<String> to=new ArrayList<>();
				to.add(details.getEmail());
				email.setTo(to);
				email.setSubject("Zoy Admin Portal Signin Information");
				String message = "<p>Hi "+master.getFirstName()+" "+master.getLastName()+",</p>"
						+ "<p>Welcome to Zoy Admin Portal, We are excited to have you as part of our community! "
						+ "Below are your sign-in credentials for accessing your account.</p>"
						+ "<p>Username: "+ details.getEmail()+"</p>"
						+ "Password: "+ passwordDecoder.decryptedText(details.getPassword())+"</p>"
						+ "<p class=\"footer\">Warm regards,<br>Team ZOY</p>";
				email.setBody(message);
				email.setContent("text/html");
				emailService.sendEmail(email,null);
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Signin info sent successfully");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("User not found");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error sending signin info: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
	        log.error("Error getting user list details: " + e.getMessage(), e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError("Internal server error");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
   	        log.error("Error getting amenities details: " + e.getMessage(), e);
   	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
   	        response.setError("Internal server error");
   	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
   	    }
   	}



}
