package com.integration.zoy.entity;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_role", schema = "pgadmin")
public class AdminUserRole implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private AdminUserRolePK adminUserRolePK;
	
	public AdminUserRolePK getAdminUserRolePK() {
		return adminUserRolePK;
	}
	public void setAdminUserRolePK(AdminUserRolePK adminUserRolePK) {
		this.adminUserRolePK = adminUserRolePK;
	}
	
}
