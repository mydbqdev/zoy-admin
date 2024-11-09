package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class AdminAppRole {
	@SerializedName("id")
	Long id;

	@SerializedName("roleName")
	String roleName;

	@SerializedName("desc")
	String desc;

	@SerializedName("roleScreen")
	List<RoleScreen> roleScreen;


	public void setId(Long id) {
		this.id = id;
	}
	public Long getId() {
		return id;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getRoleName() {
		return roleName;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDesc() {
		return desc;
	}

	public void setRoleScreen(List<RoleScreen> roleScreen) {
		this.roleScreen = roleScreen;
	}
	public List<RoleScreen> getRoleScreen() {
		return roleScreen;
	}
}
