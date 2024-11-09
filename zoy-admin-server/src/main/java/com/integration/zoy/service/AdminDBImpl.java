package com.integration.zoy.service;

import java.util.List;

import com.integration.zoy.entity.AdminUserLoginDetails;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.AdminUserTemporary;
import com.integration.zoy.entity.AppRole;
import com.integration.zoy.entity.RoleScreen;

public interface AdminDBImpl {

	AdminUserMaster saveAdminUser(AdminUserMaster master);

	AdminUserLoginDetails saveAdminLoginDetails(AdminUserLoginDetails adminUserLoginDetails);

	AdminUserMaster findAdminUserMaster(String email);

	AdminUserMaster updateAdminUser(AdminUserMaster master);

	AppRole saveAppRole(AppRole appRole);

	List<RoleScreen> saveAllRoleScreen(List<RoleScreen> roleScreens);

	AppRole findAppRole(String roleName);

	AppRole updateAppRole(AppRole appRole);

	List<RoleScreen> findRoleScreen(Long id);

	void deleteAllRoleScreen(List<Long> obsoleteScreenIds);

	List<AppRole> findAllAppRole();

	AppRole findAppRoleId(Long id);

	List<AdminUserTemporary> saveAllUserTemporary(List<AdminUserTemporary> adminUserTemporary);

	List<AdminUserMaster> findAllAdminUser();

	List<String[]> findAllAdminUserPrevilages();

	AdminUserLoginDetails findByEmail(String email);

	List<String> findUserRoles(String email);

	List<String[]> findAllAdminUserDetails(String emailId);

}
