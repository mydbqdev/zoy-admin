package com.integration.zoy.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AuditHistory;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AuditHistoryRepository;
@Service
public class AuditHistoryUtilities {
	private static final Logger log = LoggerFactory.getLogger(AuditHistoryUtilities.class);
	@Autowired
	private AuditHistoryRepository auditHistoryRepository;
	@Autowired
	AdminUserMasterRepository userMasterRepository;
	
	public void auditForUserLoginLogout(String email,boolean islogin) {
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
		}
	}
	
	public void auditForCreateUserDelete(String email,boolean isCreate,AdminUserMaster object) {
		try {
			String userName="";
			Optional<AdminUserMaster> user=userMasterRepository.findById(email);
			if(user.isPresent()) {
				userName=user.get().getFirstName()+" "+user.get().getLastName();
			}
			String histotyData=null;
			if(isCreate) {
				histotyData=userName+" has created the user for User Name="+object.getFirstName()+" "+object.getLastName()+", Designation="+object.getDesignation()+", Contact Number="+object.getContactNumber()+", Email="+object.getUserEmail();
			}else {
				String status=object.getStatus() ? "Active":"Inactive";
				histotyData=userName+" has deleted the user for User Name="+object.getFirstName()+" "+object.getLastName()+", Designation="+object.getDesignation()+", Contact Number="+object.getContactNumber()+", Email="+object.getUserEmail()+", status="+status;	
			}
			AuditHistory auditHistory=new AuditHistory();
			auditHistory.setUserEmail(email);
			auditHistory.setOperation(isCreate? ZoyConstant.ZOY_ADMIN_USER_CREATE : ZoyConstant.ZOY_ADMIN_USER_DELETE);
			
			auditHistory.setHistoryData(histotyData);
			auditHistoryRepository.save(auditHistory);
		}catch(Exception e) {
			log.error("Error in audit entry for auditForCreateUserDelete"+email+":",e);
		}
	}
	
	public void auditForUpdateUser(String email,AdminUserMaster object,AdminUserMaster dbObject) {
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
		}
	}
}
