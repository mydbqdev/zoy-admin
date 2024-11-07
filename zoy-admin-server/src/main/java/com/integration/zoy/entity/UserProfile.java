package com.integration.zoy.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "user_profile", schema = "pgcommon")
public class UserProfile implements Serializable {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "email_id", nullable = false, length = 50)
	    private String emailId;

	    @Column(name = "mobile_no", nullable = false, length = 50)
	    private String mobileNo;

	    @Column(name = "pwd", nullable = false, length = 100)
	    private String password;

	    @Column(name = "enabled", nullable = false)
	    private boolean enabled = false;

	    @Column(name = "verify_token", length = 100)
	    private String verifyToken;

	    @CreationTimestamp
	    @Column(name = "created_at", nullable = false)
	    private Timestamp createdAt;

	    @Column(name = "zoy_code", length = 50)
	    private String zoyCode;

	    @Column(name = "property_owner_name", length = 100)
	    private String propertyOwnerName;

	    @Column(name = "user_application_name", nullable = false, length = 50)
	    private String userApplicationName;
	    
	    @Column(name = "encrypted_aadhar")
	    private String encryptedAadhar;

	    // Getters and Setters

	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getEmailId() {
	        return emailId;
	    }

	    public void setEmailId(String emailId) {
	        this.emailId = emailId;
	    }

	    public String getMobileNo() {
	        return mobileNo;
	    }

	    public void setMobileNo(String mobileNo) {
	        this.mobileNo = mobileNo;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public boolean isEnabled() {
	        return enabled;
	    }

	    public void setEnabled(boolean enabled) {
	        this.enabled = enabled;
	    }

	    public String getVerifyToken() {
	        return verifyToken;
	    }

	    public void setVerifyToken(String verifyToken) {
	        this.verifyToken = verifyToken;
	    }

	    public Timestamp getCreatedAt() {
	        return createdAt;
	    }

	    public void setCreatedAt(Timestamp createdAt) {
	        this.createdAt = createdAt;
	    }

	    public String getZoyCode() {
	        return zoyCode;
	    }

	    public void setZoyCode(String zoyCode) {
	        this.zoyCode = zoyCode;
	    }

	    public String getPropertyOwnerName() {
	        return propertyOwnerName;
	    }

	    public void setPropertyOwnerName(String propertyOwnerName) {
	        this.propertyOwnerName = propertyOwnerName;
	    }

	    public String getUserApplicationName() {
	        return userApplicationName;
	    }

	    public void setUserApplicationName(String userApplicationName) {
	        this.userApplicationName = userApplicationName;
	    }

		public String getEncryptedAadhar() {
			return encryptedAadhar;
		}

		public void setEncryptedAadhar(String encryptedAadhar) {
			this.encryptedAadhar = encryptedAadhar;
		}
	    
	    
	    
	}