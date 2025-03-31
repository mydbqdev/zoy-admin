package com.integration.zoy.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PgOwnerMasterModel {
	

	    @JsonProperty("firstName")
	    private String firstName;

	    @JsonProperty("lastName")
	    private String lastName;

	    @JsonProperty("emailId")
	    private String emailId;

	    @JsonProperty("mobileNo")
	    private String mobileNo;
	    
	    @JsonProperty("zoyShare")
	    private BigDecimal zoyShare;

	    @JsonProperty("registerId")
	    private String registerId;
	   
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

		public BigDecimal getZoyShare() {
			return zoyShare;
		}

		public void setZoyShare(BigDecimal zoyShare) {
			this.zoyShare = zoyShare;
		}

		public String getRegisterId() {
			return registerId;
		}

		public void setRegisterId(String registerId) {
			this.registerId = registerId;
		}


}
