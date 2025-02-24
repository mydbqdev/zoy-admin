package com.integration.zoy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
import com.integration.zoy.repository.AdminUserLoginDetailsRepository;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AdminUserTemporaryRepository;
import com.integration.zoy.repository.AppRoleRepository;
import com.integration.zoy.repository.BulkUploadDetailsRepository;
import com.integration.zoy.repository.RoleScreenRepository;
import com.integration.zoy.repository.TriggeredCondRepository;
import com.integration.zoy.repository.TriggeredOnRepository;
import com.integration.zoy.repository.TriggeredValueRepository;

@Service
public  class AdminDBService implements AdminDBImpl {

	@Autowired
	AdminUserMasterRepository userMasterRepository;

	@Autowired
	AdminUserLoginDetailsRepository userLoginDetailsRepository;

	@Autowired
	AppRoleRepository roleRepository;

	@Autowired
	RoleScreenRepository roleScreenRepository;

	@Autowired
	AdminUserTemporaryRepository userTemporaryRepository;

	@Autowired
	BulkUploadDetailsRepository bulkUploadDetailsRepository;

	@Autowired
	TriggeredCondRepository triggeredCondRepository;
	
	@Autowired
	TriggeredOnRepository triggeredOnRepository;
	
	@Autowired
	TriggeredValueRepository triggeredValueRepository;

	@Override
	public AdminUserMaster saveAdminUser(AdminUserMaster master) throws WebServiceException {
		return userMasterRepository.save(master);
	}



	@Override
	public AdminUserLoginDetails saveAdminLoginDetails(AdminUserLoginDetails adminUserLoginDetails) throws WebServiceException{
		return userLoginDetailsRepository.save(adminUserLoginDetails);
	}

	@Override
	public AdminUserMaster findAdminUserMaster(String email) throws WebServiceException{
		return userMasterRepository.findById(email).orElse(null);
	}

	@Override
	@Transactional
	public AdminUserMaster updateAdminUser(AdminUserMaster updatedUser) throws WebServiceException {
		Optional<AdminUserMaster> user = userMasterRepository.findById(updatedUser.getUserEmail());
		if(user.isPresent()) {
			AdminUserMaster existingUser=user.get();
			if (updatedUser.getFirstName() != null) 
				existingUser.setFirstName(updatedUser.getFirstName());
			if (updatedUser.getLastName() != null) 
				existingUser.setLastName(updatedUser.getLastName());
			if (updatedUser.getDesignation() != null) 
				existingUser.setDesignation(updatedUser.getDesignation());
			if (updatedUser.getContactNumber() != null) 
				existingUser.setContactNumber(updatedUser.getContactNumber());
			if (updatedUser.getStatus() != null) 
				existingUser.setStatus(updatedUser.getStatus());
			return userMasterRepository.save(existingUser);
		}  
		return null;
	}

	@Override
	public AppRole saveAppRole(AppRole appRole) throws WebServiceException {
		return roleRepository.save(appRole);
	}

	@Override
	public List<RoleScreen> saveAllRoleScreen(List<RoleScreen> roleScreens) throws WebServiceException {
		return roleScreenRepository.saveAll(roleScreens);
	}

	@Override
	public AppRole findAppRole(String roleName) throws WebServiceException{
		return roleRepository.findAppRole(roleName);
	}

	@Override
	public AppRole updateAppRole(AppRole appRole) throws WebServiceException{
		Optional<AppRole> existingRole = roleRepository.findById(appRole.getId());
		if(!existingRole.isPresent()) {
			AppRole role= existingRole.get(); 
			if (appRole.getRoleName() !=null) 
				role.setRoleName(appRole.getRoleName());
			if (appRole.getRoleDescription() != null) 
				role.setRoleDescription(appRole.getRoleDescription());
			return roleRepository.save(appRole);
		}  
		return null;
	}

	@Override
	public List<RoleScreen> findRoleScreen(Long id) throws WebServiceException{
		return roleScreenRepository.findRoleScreen(id);
	}

	@Override
	public void deleteAllRoleScreen(List<Long> obsoleteScreenIds) throws WebServiceException {
		roleScreenRepository.deleteAllById(obsoleteScreenIds);
	}

	@Override
	public List<AppRole> findAllAppRole() throws WebServiceException{
		return roleRepository.findAllRolesOrderedByCreateAtDesc();
	}

	@Override
	public AppRole findAppRoleId(Long id) throws WebServiceException {
		return roleRepository.findById(id).orElse(null);
	}

	@Override
	public List<AdminUserTemporary> saveAllUserTemporary(List<AdminUserTemporary> adminUserTemporary) throws WebServiceException{
		return userTemporaryRepository.saveAll(adminUserTemporary);
	}

	@Override
	public List<AdminUserMaster> findAllAdminUser() throws WebServiceException {
		return userMasterRepository.findAllAdminUsersOrderedByCreationDateDesc();
	}

	@Override
	public  List<Object[]> findAllAdminUserPrevilages() throws WebServiceException {
		return userMasterRepository.findAllAdminUserPrivileges();
	}

	@Override
	public AdminUserLoginDetails findByEmail(String email) throws WebServiceException {
		return userLoginDetailsRepository.findById(email).orElse(null);
	}

	@Override
	public List<String> findUserRoles(String email) throws WebServiceException {
		String result= roleScreenRepository.findUserRole(email);
		if(result!=null)
			return Arrays.asList(result.split(","));
		else 
			return new ArrayList<>();
	}

	@Override
	public List<String[]> findAllAdminUserDetails(String emailId) throws WebServiceException {
		return userMasterRepository.findAllAdminUserDetails(emailId);
	}

	@Override
	public void insertUserDetails(String user_email) throws WebServiceException {
		userMasterRepository.insertUserDetails(user_email);

	}

	@Override
	public void approveUser(String user_email) throws WebServiceException{
		userMasterRepository.approveUser(user_email);

	}

	@Override
	public void rejectUser(String user_email) throws WebServiceException{
		userMasterRepository.rejectUser( user_email);

	}

	@Override
	public  List<Integer> findRoleIfAssigned(int  role_id) throws WebServiceException{
		return roleScreenRepository.findAssignedRole(role_id);
	}

	@Override
	public void deleteRolefromApp_role(int  role_id) throws WebServiceException {
		roleScreenRepository.deleteRolefromApp_role( role_id);	
	}

	@Override
	public void deleteRolefromRoleScreen(int role_id) throws WebServiceException {
		roleScreenRepository.deleteRolefromRoleScreen( role_id);	
	}



	@Override
	public List<Object[]> findAllAdminUserPrivileges1() throws WebServiceException {
		return userMasterRepository.findAllAdminUserPrivileges1();
	}

	@Override
	public List<AdminUserMaster> userdata(String[] userMails) throws WebServiceException{
		return	userMasterRepository.userdata(userMails);

	}

	@Override
	public AdminUserLoginDetails findAdminRegisterEmail(String Usermail) throws WebServiceException{
		return userLoginDetailsRepository.findRegisterEmail(Usermail).orElse(null);
	}

	@Override
	public BulkUploadDetails saveBulkUpload(BulkUploadDetails bulkUploadDetails) throws WebServiceException{
		return bulkUploadDetailsRepository.save(bulkUploadDetails);
	}

	@Override
	public List<BulkUploadDetails> findAllBulkUpload()throws WebServiceException {
		return bulkUploadDetailsRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
	}



	@Override
	public boolean existsByUserEmail(String userEmail) throws WebServiceException{
		return userMasterRepository.existsByUserEmail(userEmail);
	}



	@Override
	public List<Object[]> findSuperAdminCardsDetails() throws WebServiceException {
		return userMasterRepository.getUsersWithNonNullPinAndActiveOwnersPropertiesCount();
	}

	@Override
	public void doUserActiveteDeactivete(String user_email,boolean status)throws WebServiceException {
		userLoginDetailsRepository.doUserActiveteDeactiveteInUserLoginDetails(user_email ,status);
		userLoginDetailsRepository.doUserActiveteDeactiveteInuserMaster(user_email ,status);

	}



	@Override
	public List<TriggeredCond> findTriggeredCond() {
		return triggeredCondRepository.findAll();
	}



	@Override
	public List<TriggeredOn> findTriggeredOn() {
		return triggeredOnRepository.findAll();
	}



	@Override
	public List<TriggeredValue> findTriggeredValue() {
		return triggeredValueRepository.findAll();
	}



	@Override
	public List<Object[]> findTenantsCardsDetails() throws WebServiceException {
		return userMasterRepository.getTenantCardsDetails();
	}
	
}
