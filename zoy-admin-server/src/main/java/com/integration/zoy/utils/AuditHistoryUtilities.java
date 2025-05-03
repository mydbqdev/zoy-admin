package com.integration.zoy.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AuditHistory;
import com.integration.zoy.entity.LeadHistory;
import com.integration.zoy.entity.UserHelpRequestHistory;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AuditHistoryRepository;
import com.integration.zoy.repository.LeadHistoryRepository;
import com.integration.zoy.repository.UserHelpRequestHistoryRepository;
@Service
public class AuditHistoryUtilities {
	private static final Logger log = LoggerFactory.getLogger(AuditHistoryUtilities.class);
	@Autowired
	private AuditHistoryRepository auditHistoryRepository;
	@Autowired
	AdminUserMasterRepository userMasterRepository;
	@Autowired
	LeadHistoryRepository leadHistoryRepository;
	
	@Autowired
	UserHelpRequestHistoryRepository userHelpRequestHistoryRepository;
	
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
	
	public void auditForUserLoginLogout(String email,boolean islogin) throws WebServiceException {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
			 Date date = new Date();
			String currentDate=dateFormat.format(date);
			
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(islogin? ZoyConstant.ZOY_ADMIN_LOGIN : ZoyConstant.ZOY_ADMIN_LOGOUT);
			String textAudit="";
			if(islogin) {
				textAudit=userName+" has logged in the system at "+currentDate;
			}else {
				textAudit=userName+" has logged out the system at "+currentDate;
			}
			auditHistory.setOperation(islogin? ZoyConstant.ZOY_ADMIN_LOGIN : ZoyConstant.ZOY_ADMIN_LOGOUT);
			auditHistory.setHistoryData(textAudit);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForUserLoginLogout"+email+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	public void auditForCreateUserDelete(String email,boolean isCreate,AdminUserMaster object) throws WebServiceException {
		try {
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			String histotyData=null;
			if(isCreate) {
				histotyData=userName+" has created the user for, User Name="+object.getFirstName()+" "+object.getLastName()+", Designation="+object.getDesignation()+", Contact Number="+object.getContactNumber()+", Email="+object.getUserEmail();
			}else {
				String status=object.getStatus() ? "Active":"Inactive";
				histotyData=userName+" has deleted the user for, User Name="+object.getFirstName()+" "+object.getLastName()+", Designation="+object.getDesignation()+", Contact Number="+object.getContactNumber()+", Email="+object.getUserEmail()+", status="+status;	
			}
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(isCreate? ZoyConstant.ZOY_ADMIN_USER_CREATE : ZoyConstant.ZOY_ADMIN_USER_DELETE);
			
			auditHistory.setHistoryData(histotyData);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForCreateUserDelete"+email+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	public void auditForUpdateUser(String email,AdminUserMaster object,AdminUserMaster dbObject)throws WebServiceException {
		try {
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			StringBuffer histotyData=new StringBuffer(userName+" has updated the user for,");
			if(!dbObject.getFirstName().equals(object.getFirstName()) || ! String.valueOf(dbObject.getLastName()).equals(String.valueOf(object.getLastName()))) {
				if(!(",".equals(histotyData.substring(histotyData.length()-1)))) {
					histotyData.append(",");
				}
				histotyData.append(" User Name from "+dbObject.getFirstName()+" "+dbObject.getLastName()+" to "+object.getFirstName()+" "+object.getLastName());
			}
			
			if(!dbObject.getDesignation().equals(object.getDesignation())) {
				if(!(",".equals(histotyData.substring(histotyData.length()-1)))) {
					histotyData.append(",");
				}
				histotyData.append(" Designation from "+dbObject.getDesignation()+" to "+object.getDesignation());
			}
			
			if(!String.valueOf(dbObject.getContactNumber()).equals(String.valueOf(object.getContactNumber()))) {
				if(!(",".equals(histotyData.substring(histotyData.length()-1)))) {
					histotyData.append(",");
				}
				histotyData.append(" Contact Number from "+dbObject.getContactNumber()+" to "+object.getContactNumber());
			}
			

			if(!dbObject.getStatus()==object.getStatus()) {
				if(!(",".equals(histotyData.substring(histotyData.length()-1)))) {
					histotyData.append(",");
				}
				String oldStatus=dbObject.getStatus() ? "Active":"Inactive";
				String status=object.getStatus() ? "Active":"Inactive";
				histotyData.append(" Status from "+oldStatus +" to "+status);
			}
			
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(ZoyConstant.ZOY_ADMIN_USER_UPDATE);
			
			auditHistory.setHistoryData(histotyData.toString());
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForCreateUserDelete"+email+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	public void auditForRoleCreate(String email,boolean isCreate,String history) {
		try {
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			history=history.replace("true", "Yes");
			history=history.replace("false", "No");
			history=history.replace("role_name", "Role Name");
			history=history.replace("desc", "Description");
			history=history.replace("role_screen", "Roles");
			history=history.replace("screen_name", "Screen Name");
			history=history.replace("read_prv", "Read");
			history=history.replace("write_prv", "Write");
			history=history.replace("\"", "");
			
			String histotyData=null;
			if(isCreate) {
				histotyData=userName+" has created the role for, "+history;
			}else {
				histotyData=userName+" has deleted the role for, "+history;	
			}
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(isCreate? ZoyConstant.ZOY_ADMIN_ROLE_CREATE: ZoyConstant.ZOY_ADMIN_ROLE_DELETE);
			
			auditHistory.setHistoryData(histotyData);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForRoleCreate"+email+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	

	public void auditForRoleUpdate(String roleName,StringBuffer history, List<com.integration.zoy.model.RoleScreen> appRoleScreensDB,
			List<com.integration.zoy.model.RoleScreen> appRoleScreensNewUpdate,List<com.integration.zoy.model.RoleScreen> appRoleScreenDeleted) {
		final String email=SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			final List<com.integration.zoy.model.RoleScreen> listOneListDB = appRoleScreensDB.stream().filter(two -> appRoleScreensNewUpdate.stream()
		              .anyMatch(one -> one.getScreenName().equals(two.getScreenName()))) 
		              .collect(Collectors.toList());
			if(appRoleScreenDeleted !=null && !appRoleScreenDeleted.isEmpty()){
				listOneListDB.addAll(appRoleScreenDeleted);
			}
			String appRoleScreens=(listOneListDB!=null && !listOneListDB.isEmpty()) ? gson.toJson(listOneListDB).toString() : "-" ;
			String appRoleScreensnew=(appRoleScreensNewUpdate!=null && !appRoleScreensNewUpdate.isEmpty()) ? gson.toJson(appRoleScreensNewUpdate).toString():"-";
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			appRoleScreens=appRoleScreens.replace("true", "Yes");
			appRoleScreens=appRoleScreens.replace("false", "No");
			appRoleScreens=appRoleScreens.replace("screen_name", "Screen Name");
			appRoleScreens=appRoleScreens.replace("read_prv", "Read");
			appRoleScreens=appRoleScreens.replace("write_prv", "Write");
			appRoleScreens=appRoleScreens.replace("\"", "");
			
			appRoleScreensnew=appRoleScreensnew.replace("true", "Yes");
			appRoleScreensnew=appRoleScreensnew.replace("false", "No");
			appRoleScreensnew=appRoleScreensnew.replace("screen_name", "Screen Name");
			appRoleScreensnew=appRoleScreensnew.replace("read_prv", "Read");
			appRoleScreensnew=appRoleScreensnew.replace("write_prv", "Write");
			appRoleScreensnew=appRoleScreensnew.replace("\"", "");
			
			StringBuffer histotyData=new StringBuffer(userName+" has updated the role ("+roleName+") for, ");
			if(history!=null && !(String.valueOf(history).isEmpty())) {
				histotyData.append(history);
				histotyData.append(", ");
			}
			
			histotyData.append("Roles from ");
			histotyData.append(appRoleScreens);
			histotyData.append(" to ");
			histotyData.append(appRoleScreensnew);
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(ZoyConstant.ZOY_ADMIN_ROLE_UPDATE);
			
			auditHistory.setHistoryData(histotyData.toString());
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForRoleUpdate"+email+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}

	public void auditForRoleAssign(String loginEmail, String history, String userEmail) {
		try {
			String userName="";
			String userNameFor="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(loginEmail);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			
			Optional<AdminUserMaster> user2=userMasterRepository.findById(userEmail);
			if(user2.isPresent()) {
				userNameFor=user2.get().getFirstName()+" "+user2.get().getLastName();
			}
			history=history.replace("\"", "");
			String histotyData=	userName+" has assigned the role , "+history+" for "+userNameFor;

			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(loginEmail);
			auditHistory.setOperation(ZoyConstant.ZOY_ADMIN_USER_AUTHORZITION_ASSIGN);
			
			auditHistory.setHistoryData(histotyData);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForRoleAssign"+loginEmail+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	public void auditForCommon(String loginEmail, String history,String operation) {
		try {
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(loginEmail);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			String histotyData=	userName+" "+history;

			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(loginEmail);
			auditHistory.setOperation(operation);
			
			auditHistory.setHistoryData(histotyData);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForCommon"+loginEmail+":",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	public void leadHistory(String history,String supportEmail,String inquryNumber,String status) {
		try {
			LeadHistory auditHistory=new LeadHistory();
			auditHistory.setUserEmail(supportEmail);
			auditHistory.setHistoryData(history);
			auditHistory.setInquiryNumber(inquryNumber);
			auditHistory.setStatus(status);
			leadHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for Lead History",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
	
	
	public void userHelpRequestHistory(String history,String status,String inquryNumber) {
		try {
			UserHelpRequestHistory auditHistory=new UserHelpRequestHistory();
			auditHistory.setRequestStatus(status);
			auditHistory.setDescription(history);
			auditHistory.setUserHelpRequestHistoryId(inquryNumber);
			userHelpRequestHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for User Help Request History",e);
			new ZoyAdminApplicationException(e, "");
		}
	}
}
