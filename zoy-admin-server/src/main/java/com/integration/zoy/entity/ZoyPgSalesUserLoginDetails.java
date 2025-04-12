package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user_login_details", schema = "pgsales")
public class ZoyPgSalesUserLoginDetails {

	@Id
	@Column(name = "user_email", nullable = false, length = 255)
	private String userEmail;

	@Column(name = "password", nullable = false, columnDefinition = "text")
	private String password;

	@Column(name = "is_lock", nullable = true)
	private Boolean isLock = false;

	@Column(name = "is_active", nullable = true)
	private Boolean isActive = false;

	@Column(name = "last_change_on")
	private Timestamp lastChangeOn;
	
	@Column(name = "is_password_change")
	private Boolean isPasswordChange;

	@OneToOne
	@JoinColumn(name = "user_email", referencedColumnName = "email_id", insertable = false, updatable = false, foreignKey = @ForeignKey(name = "user_login_details_fk"))
	private ZoyPgSalesMaster zoyPgSalesMaster;

	// Getters and Setters

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

	public ZoyPgSalesMaster getZoyPgSalesMaster() {
		return zoyPgSalesMaster;
	}

	public void setZoyPgSalesMaster(ZoyPgSalesMaster zoyPgSalesMaster) {
		this.zoyPgSalesMaster = zoyPgSalesMaster;
	}

	public Boolean getIsPasswordChange() {
		return isPasswordChange;
	}

	public void setIsPasswordChange(Boolean isPasswordChange) {
		this.isPasswordChange = isPasswordChange;
	}

}
