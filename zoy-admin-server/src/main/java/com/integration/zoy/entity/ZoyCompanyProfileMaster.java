package com.integration.zoy.entity;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
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
	
	@Column(name = "pin_code")
	private String pinCode;
	
	@Column(name = "state")
    private String state;
	
	@Column(name = "city")
    private String city;
	
	@Column(name = "address_line_one")
	private String addressLineOne;

	@Column(name = "address_line_two")
	private String addressLineTwo;
	
	@Column(name = "address_line_three")
	private String addressLineThree;
	
	@Column(name = "contact_number_one")
    private String contactNumberOne;
   
	@Column(name = "contact_number_two")
    private String contactNumbertwo;
   
	@Column(name = "email_id_one")
    private String emailIdOne;
   
	@Column(name = "email_id_two")
    private String emailIdTwo;
	
	@Column(name = "status")
	private String status;

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

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddressLineOne() {
		return addressLineOne;
	}

	public void setAddressLineOne(String addressLineOne) {
		this.addressLineOne = addressLineOne;
	}

	public String getAddressLineTwo() {
		return addressLineTwo;
	}

	public void setAddressLineTwo(String addressLineTwo) {
		this.addressLineTwo = addressLineTwo;
	}

	public String getAddressLineThree() {
		return addressLineThree;
	}

	public void setAddressLineThree(String addressLineThree) {
		this.addressLineThree = addressLineThree;
	}

	public String getContactNumberOne() {
		return contactNumberOne;
	}

	public void setContactNumberOne(String contactNumberOne) {
		this.contactNumberOne = contactNumberOne;
	}

	public String getContactNumbertwo() {
		return contactNumbertwo;
	}

	public void setContactNumbertwo(String contactNumbertwo) {
		this.contactNumbertwo = contactNumbertwo;
	}

	public String getEmailIdOne() {
		return emailIdOne;
	}

	public void setEmailIdOne(String emailIdOne) {
		this.emailIdOne = emailIdOne;
	}

	public String getEmailIdTwo() {
		return emailIdTwo;
	}

	public void setEmailIdTwo(String emailIdTwo) {
		this.emailIdTwo = emailIdTwo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
}
