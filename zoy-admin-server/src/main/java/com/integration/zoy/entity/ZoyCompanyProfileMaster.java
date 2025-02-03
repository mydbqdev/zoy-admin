package com.integration.zoy.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
@Entity
@Table(name = "zoy_company_profile_master", schema = "pgadmin")
public class ZoyCompanyProfileMaster {
	@Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "company_profile_id",updatable = false, nullable = false, unique = true, length = 36)
	private String companyProfileId;
	
	@Column(name = "type")
    private String type;
	
	@Column(name = "company_name")
    private String companyName;

    @Lob
	@Type(type="org.hibernate.type.BinaryType")
	@Column(name = "logo", columnDefinition = "bytea")
    private byte[] logo;
	
	@Column(name = "address")
    private String address;
	
	@Column(name = "city")
    private String city;
	
	@Column(name = "state")
    private String state;
	
	@Column(name = "gst_number")
    private String gstNumber;
	
	@Column(name = "pan_number")
    private String panNumber;
	
	@Column(name = "email_id")
    private String emailId;
	
	@Column(name = "contact_number")
    private String contactNumber;

	public String getCompanyProfileId() {
		return companyProfileId;
	}

	public void setCompanyProfileId(String companyProfileId) {
		this.companyProfileId = companyProfileId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getPanNumber() {
		return panNumber;
	}

	public void setPanNumber(String panNumber) {
		this.panNumber = panNumber;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	
	
	
}
