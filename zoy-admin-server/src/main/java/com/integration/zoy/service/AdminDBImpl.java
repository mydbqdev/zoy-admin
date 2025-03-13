package com.integration.zoy.service;

import java.util.List;

import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AdminUserTemporary;
import com.integration.zoy.entity.AppRole;
import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.entity.RoleScreen;
import com.integration.zoy.entity.TriggeredCond;
import com.integration.zoy.entity.TriggeredOn;
import com.integration.zoy.entity.TriggeredValue;
import com.integration.zoy.exception.WebServiceException;

public interface AdminDBImpl {

	AdminUserMaster saveAdminUser(AdminUserMaster master) throws WebServiceException;

	AdminUserLoginDetails saveAdminLoginDetails(AdminUserLoginDetails adminUserLoginDetails) throws WebServiceException;

	AdminUserMaster findAdminUserMaster(String email) throws WebServiceException;

	AdminUserMaster updateAdminUser(AdminUserMaster master)throws WebServiceException;

	AppRole saveAppRole(AppRole appRole) throws WebServiceException;

	List<RoleScreen> saveAllRoleScreen(List<RoleScreen> roleScreens) throws WebServiceException;

	AppRole findAppRole(String roleName) throws WebServiceException;

	AppRole updateAppRole(AppRole appRole) throws WebServiceException;

	List<RoleScreen> findRoleScreen(Long id) throws WebServiceException;

	void deleteAllRoleScreen(List<Long> obsoleteScreenIds) throws WebServiceException;

	List<AppRole> findAllAppRole() throws WebServiceException;

	AppRole findAppRoleId(Long id)throws WebServiceException;

	List<AdminUserTemporary> saveAllUserTemporary(List<AdminUserTemporary> adminUserTemporary)throws WebServiceException;

	List<AdminUserMaster> findAllAdminUser() throws WebServiceException;
	
	boolean existsByUserEmail(String userEmail) throws WebServiceException;

	List<Object[]> findAllAdminUserPrevilages() throws WebServiceException;

	AdminUserLoginDetails findByEmail(String email) throws WebServiceException ;

	List<String> findUserRoles(String email)throws WebServiceException;

	List<String[]> findAllAdminUserDetails(String emailId)throws WebServiceException;
	
    void insertUserDetails(String user_email)throws WebServiceException;

	void approveUser(String user_email);
	
	void rejectUser(String user_email) throws WebServiceException;
	
    List<Integer>  findRoleIfAssigned(int role_id)throws WebServiceException;
	
	void deleteRolefromApp_role(int role_id) throws WebServiceException;
	
	void deleteRolefromRoleScreen(int role_id) throws WebServiceException;
	
	List<AdminUserMaster> userdata(String[] userMails) throws WebServiceException;

	List<Object[]> findAllAdminUserPrivileges1() throws WebServiceException;
	
	AdminUserLoginDetails findAdminRegisterEmail(String Usermail) throws WebServiceException;

	BulkUploadDetails saveBulkUpload(BulkUploadDetails bulkUploadDetails)throws WebServiceException;

	List<BulkUploadDetails> findAllBulkUpload() throws WebServiceException;

	List<Object[]> findSuperAdminCardsDetails() throws WebServiceException;
	
	List<Object[]> findTenantsCardsDetails() throws WebServiceException;
	
	List<Object[]> findOwnerCardsDetails() throws WebServiceException;
	
	void doUserActiveteDeactivete(String user_email ,boolean status)throws WebServiceException;

	List<TriggeredCond> findTriggeredCond();

	List<TriggeredOn> findTriggeredOn();

	List<TriggeredValue> findTriggeredValue();

}
