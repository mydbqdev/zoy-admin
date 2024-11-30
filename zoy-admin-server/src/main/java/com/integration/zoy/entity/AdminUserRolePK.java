package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
public class AdminUserRolePK  implements Serializable {
	private static final long serialVersionUID = 1L;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "role_id")
    private Long roleId;

    @Override
	public int hashCode() {
		return Objects.hash(userEmail, roleId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AdminUserRolePK other = (AdminUserRolePK) obj;
		return Objects.equals(userEmail, other.userEmail) && Objects.equals(roleId, other.roleId);
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public Long getRoleId() {
		return roleId;
	}
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	   
}
