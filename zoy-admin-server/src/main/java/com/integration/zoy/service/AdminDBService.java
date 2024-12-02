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
import com.integration.zoy.repository.AdminUserLoginDetailsRepository;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.AdminUserTemporaryRepository;
import com.integration.zoy.repository.AppRoleRepository;
import com.integration.zoy.repository.BulkUploadDetailsRepository;
import com.integration.zoy.repository.RoleScreenRepository;
import com.integration.zoy.repository.UserMasterRepository;

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
	


	@Override
	public AdminUserMaster saveAdminUser(AdminUserMaster master) {
		return userMasterRepository.save(master);
	}
	
	

	@Override
	public AdminUserLoginDetails saveAdminLoginDetails(AdminUserLoginDetails adminUserLoginDetails) {
		return userLoginDetailsRepository.save(adminUserLoginDetails);
	}

	@Override
	public AdminUserMaster findAdminUserMaster(String email) {
		return userMasterRepository.findById(email).orElse(null);
	}

	@Override
	@Transactional
	public AdminUserMaster updateAdminUser(AdminUserMaster updatedUser) {
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
	public AppRole saveAppRole(AppRole appRole) {
		return roleRepository.save(appRole);
	}

	@Override
	public List<RoleScreen> saveAllRoleScreen(List<RoleScreen> roleScreens) {
		return roleScreenRepository.saveAll(roleScreens);
	}

	@Override
	public AppRole findAppRole(String roleName) {
		return roleRepository.findAppRole(roleName);
	}

	@Override
	public AppRole updateAppRole(AppRole appRole) {
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
	public List<RoleScreen> findRoleScreen(Long id) {
		return roleScreenRepository.findRoleScreen(id);
	}

	@Override
	public void deleteAllRoleScreen(List<Long> obsoleteScreenIds) {
		roleScreenRepository.deleteAllById(obsoleteScreenIds);
	}

	@Override
	public List<AppRole> findAllAppRole() {
		return roleRepository.findAllRolesOrderedByCreateAtDesc();
	}

	@Override
	public AppRole findAppRoleId(Long id) {
		return roleRepository.findById(id).orElse(null);
	}

	@Override
	public List<AdminUserTemporary> saveAllUserTemporary(List<AdminUserTemporary> adminUserTemporary) {
		return userTemporaryRepository.saveAll(adminUserTemporary);
	}

	@Override
	public List<AdminUserMaster> findAllAdminUser() {
		return userMasterRepository.findAllAdminUsersOrderedByCreationDateDesc();
	}

	@Override
	public  List<Object[]> findAllAdminUserPrevilages() {
		return userMasterRepository.findAllAdminUserPrivileges();
	}

	@Override
	public AdminUserLoginDetails findByEmail(String email) {
		return userLoginDetailsRepository.findById(email).orElse(null);
	}

	@Override
	public List<String> findUserRoles(String email) {
		String result= roleScreenRepository.findUserRole(email);
		if(result!=null)
			return Arrays.asList(result.split(","));
		else 
			return new ArrayList<>();
	}

	@Override
	public List<String[]> findAllAdminUserDetails(String emailId) {
		return userMasterRepository.findAllAdminUserDetails(emailId);
	}

	@Override
	public void insertUserDetails(String user_email) {
		userMasterRepository.insertUserDetails(user_email);

	}

	@Override
	public void approveUser(String user_email) {
		userMasterRepository.approveUser(user_email);

	}

	@Override
	public void rejectUser(String user_email) {
		userMasterRepository.rejectUser( user_email);

	}

	@Override
	public  List<Integer> findRoleIfAssigned(int  role_id) {
		return roleScreenRepository.findAssignedRole(role_id);
	}

	@Override
	public void deleteRolefromApp_role(int  role_id) {
		roleScreenRepository.deleteRolefromApp_role( role_id);	
	}

	@Override
	public void deleteRolefromRoleScreen(int role_id) {
		roleScreenRepository.deleteRolefromRoleScreen( role_id);	
	}



	@Override
	public List<Object[]> findAllAdminUserPrivileges1() {
		return userMasterRepository.findAllAdminUserPrivileges1();
	}

	@Override
	public List<AdminUserMaster> userdata(String[] userMails) {
		return	userMasterRepository.userdata(userMails);

	}

	@Override
	public AdminUserLoginDetails findAdminRegisterEmail(String Usermail) {
		return userLoginDetailsRepository.findRegisterEmail(Usermail).orElse(null);
	}

	@Override
	public BulkUploadDetails saveBulkUpload(BulkUploadDetails bulkUploadDetails) {
		return bulkUploadDetailsRepository.save(bulkUploadDetails);
	}

	@Override
	public List<BulkUploadDetails> findAllBulkUpload() {
		return bulkUploadDetailsRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
	}



	@Override
	public boolean existsByUserEmail(String userEmail) {
		return userMasterRepository.existsByUserEmail(userEmail);
	}



	@Override
	public List<Object[]> findSuperAdminCardsDetails() {
		return userMasterRepository.getUsersWithNonNullPinAndActiveOwnersPropertiesCount();
	}
	
	@Override
	public void doUserActiveteDeactivete(String user_email,boolean status) {
		userLoginDetailsRepository.doUserActiveteDeactiveteInUserLoginDetails(user_email ,status);
		userLoginDetailsRepository.doUserActiveteDeactiveteInuserMaster(user_email ,status);

	}
}
