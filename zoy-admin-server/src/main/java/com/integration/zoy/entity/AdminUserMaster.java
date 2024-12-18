package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "user_master", schema = "pgadmin")
public class AdminUserMaster {

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Id
	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "designation", nullable = false)
	private String designation;

	@Column(name = "contact_number")
	private String contactNumber;

	@Column(name = "status", nullable = false)
	private Boolean status;
	
	@Column(name = "create_at", nullable = false)
	@CreationTimestamp
	private Timestamp ts;
	
	@Lob
	@Type(type="org.hibernate.type.BinaryType")
    @Column(name = "user_profile_picture", columnDefinition = "bytea")
    private byte[] userProfilePicture;
    
	public byte[] getUserProfilePicture() {
		return userProfilePicture;
	}

	public void setUserProfilePicture(byte[] userProfilePicture) {
		this.userProfilePicture = userProfilePicture;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

	
	public AdminUserMaster() {
		super();
	}

	public AdminUserMaster(AdminUserMaster object) {
		super();
		this.firstName = object.firstName;
		this.lastName = object.lastName;
		this.userEmail = object.userEmail;
		this.designation = object.designation;
		this.contactNumber = object.contactNumber;
		this.status = object.status;
		this.ts = object.ts;
	}
	
	
}