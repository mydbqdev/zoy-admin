package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "zoy_pg_registered_owner_details", schema = "pgowners")
public class RegisteredPartner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "firstname", nullable = false, length = 50)
	private String firstname;

	@Column(name = "lastname", nullable = false, length = 50)
	private String lastname;

	@Column(name = "email", nullable = false, length = 100)
	private String email;

	@Column(name = "register_id")
	private String registerId;

	@Column(name = "mobile", nullable = false, length = 15)
	private String mobile;

	@Column(name = "address", nullable = false, length = 255)
	private String address;

	@Column(name = "pincode", nullable = false, length = 10)
	private String pincode;

	@Column(name = "property_name", nullable = false, length = 100)
	private String propertyName;

	@Column(name = "ts", nullable = false)
	@CreationTimestamp
	private Timestamp ts;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "state")
    private String state;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "inquired_for")
	private String inquiredFor;
	
	@Column(name = "assign_to_email")
	private String assignedToEmail;
	
	@Column(name = "assign_to_name")
	private String assignedToName;
	
	@Column(name = "description")
	private String description;
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}

	public String getRegisterId() {
		return registerId;
	}

	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInquiredFor() {
		return inquiredFor;
	}

	public void setInquiredFor(String inquiredFor) {
		this.inquiredFor = inquiredFor;
	}

	public String getAssignedToEmail() {
		return assignedToEmail;
	}

	public void setAssignedToEmail(String assignedToEmail) {
		this.assignedToEmail = assignedToEmail;
	}

	public String getAssignedToName() {
		return assignedToName;
	}

	public void setAssignedToName(String assignedToName) {
		this.assignedToName = assignedToName;
	}


}
