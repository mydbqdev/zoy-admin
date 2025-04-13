package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "sales_user_login_details", schema = "pgsales")
public class AdminSalesUserLoginDetails {

	@Id
	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "is_lock", nullable = true)
	private Boolean isLock = false;

	@Column(name = "is_active", nullable = true)
	private Boolean isActive = false;

	@Column(name = "last_change_on")
	private Timestamp lastChangeOn;

	@OneToOne
	@JoinColumn(name = "user_email", referencedColumnName = "user_email", insertable = false, updatable = false)
	private UserMaster userMaster;

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Timestamp getLastChangeOn() {
		return lastChangeOn;
	}

	public void setLastChangeOn(Timestamp lastChangeOn) {
		this.lastChangeOn = lastChangeOn;
	}

	public UserMaster getUserMaster() {
		return userMaster;
	}

	public void setUserMaster(UserMaster userMaster) {
		this.userMaster = userMaster;
	}

}
