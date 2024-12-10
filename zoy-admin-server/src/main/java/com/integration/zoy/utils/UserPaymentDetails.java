package com.integration.zoy.utils;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class UserPaymentDetails {

   @SerializedName("id")
   String id;

   @SerializedName("createdAt")
   Timestamp createdAt;

   @SerializedName("zoy_payment_type")
   String zoyPaymentType;

   @SerializedName("payment_status")
   String paymentStatus;

   @SerializedName("gst")
   BigDecimal gst;

   @SerializedName("receiveDate")
   Timestamp receiveDate;

   @SerializedName("details")
   Details details;

   @SerializedName("payableAmount")
   BigDecimal payableAmount;

   @SerializedName("bookingId")
   String bookingId;

   @SerializedName("timestamp")
   Timestamp timestamp;

   @SerializedName("zoy_payment_mode")
   String zoyPaymentMode;

   @SerializedName("notificationMode")
   List<String> notificationMode;

   @SerializedName("mobile")
   String mobile;

   @SerializedName("email")
   String email;

   @SerializedName("registerDate")
   Timestamp registerDate;

   @SerializedName("firstName")
   String firstName;

   @SerializedName("lastName")
   String lastName;

   @SerializedName("gender")
   String gender;

   @SerializedName("pg_owner_id")
   String pgOwnerId;

   @SerializedName("property_id")
   String propertyId;

   @SerializedName("booking_date")
   Timestamp bookingDate;

   @SerializedName("tenant_id")
   String tenantId;

   @SerializedName("fixedRent")
   String fixedRent;

   @SerializedName("outDate")
   Timestamp outDate;

   @SerializedName("selectedBed")
   String selectedBed;

   @SerializedName("inDate")
   Timestamp inDate;

   @SerializedName("is_terms_accepted")
   boolean isTermsAccepted;

   @SerializedName("calFixedRent")
   BigDecimal calFixedRent;

   @SerializedName("securityDeposit")
   String securityDeposit;

   @SerializedName("currMonthStartDate")
   Timestamp currMonthStartDate;

   @SerializedName("room")
   String room;

   @SerializedName("currMonthEndDate")
   Timestamp currMonthEndDate;

   @SerializedName("noOfDays")
   String noOfDays;

   @SerializedName("includeCurrentMonth")
   boolean includeCurrentMonth;

   @SerializedName("phoneNumber")
   String phoneNumber;

   @SerializedName("rentCycleEndDate")
   Timestamp rentCycleEndDate;

   @SerializedName("due")
   BigDecimal due;

   @SerializedName("name")
   String name;

   @SerializedName("property")
   String property;

   @SerializedName("lockInPeriod")
   String lockInPeriod;

   @SerializedName("share")
   String share;

   @SerializedName("floor")
   String floor;

   @SerializedName("booking_mode")
   String bookingMode;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setZoyPaymentType(String zoyPaymentType) {
        this.zoyPaymentType = zoyPaymentType;
    }
    public String getZoyPaymentType() {
        return zoyPaymentType;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setGst(BigDecimal gst) {
        this.gst = gst;
    }
    public BigDecimal getGst() {
        return gst;
    }
    
    public void setReceiveDate(Timestamp receiveDate) {
        this.receiveDate = receiveDate;
    }
    public Timestamp getReceiveDate() {
        return receiveDate;
    }
    
    public void setDetails(Details details) {
        this.details = details;
    }
    public Details getDetails() {
        return details;
    }
    
    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }
    public BigDecimal getPayableAmount() {
        return payableAmount;
    }
    
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
    public String getBookingId() {
        return bookingId;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
    public void setZoyPaymentMode(String zoyPaymentMode) {
        this.zoyPaymentMode = zoyPaymentMode;
    }
    public String getZoyPaymentMode() {
        return zoyPaymentMode;
    }
    
    public void setNotificationMode(List<String> notificationMode) {
        this.notificationMode = notificationMode;
    }
    public List<String> getNotificationMode() {
        return notificationMode;
    }
    
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getMobile() {
        return mobile;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    
    public void setRegisterDate(Timestamp registerDate) {
        this.registerDate = registerDate;
    }
    public Timestamp getRegisterDate() {
        return registerDate;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getFirstName() {
        return firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getLastName() {
        return lastName;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }
    
    public void setPgOwnerId(String pgOwnerId) {
        this.pgOwnerId = pgOwnerId;
    }
    public String getPgOwnerId() {
        return pgOwnerId;
    }
    
    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }
    public String getPropertyId() {
        return propertyId;
    }
    
    
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    public String getTenantId() {
        return tenantId;
    }
    
    public void setFixedRent(String fixedRent) {
        this.fixedRent = fixedRent;
    }
    public String getFixedRent() {
        return fixedRent;
    }
    
    
    public void setSelectedBed(String selectedBed) {
        this.selectedBed = selectedBed;
    }
    public String getSelectedBed() {
        return selectedBed;
    }
    
    
    public void setIsTermsAccepted(boolean isTermsAccepted) {
        this.isTermsAccepted = isTermsAccepted;
    }
    public boolean getIsTermsAccepted() {
        return isTermsAccepted;
    }
    
    public void setCalFixedRent(BigDecimal calFixedRent) {
        this.calFixedRent = calFixedRent;
    }
    public BigDecimal getCalFixedRent() {
        return calFixedRent;
    }
    
    public void setSecurityDeposit(String securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    public String getSecurityDeposit() {
        return securityDeposit;
    }
    
    
    public void setRoom(String room) {
        this.room = room;
    }
    public String getRoom() {
        return room;
    }
 
    public void setNoOfDays(String string) {
        this.noOfDays = string;
    }
    public String getNoOfDays() {
        return noOfDays;
    }
    
    public void setIncludeCurrentMonth(boolean includeCurrentMonth) {
        this.includeCurrentMonth = includeCurrentMonth;
    }
    public boolean getIncludeCurrentMonth() {
        return includeCurrentMonth;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    
    public void setDue(BigDecimal due) {
        this.due = due;
    }
    public BigDecimal getDue() {
        return due;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setProperty(String property) {
        this.property = property;
    }
    public String getProperty() {
        return property;
    }
    
    public void setLockInPeriod(String lockInPeriod) {
        this.lockInPeriod = lockInPeriod;
    }
    public String getLockInPeriod() {
        return lockInPeriod;
    }
    
    public void setShare(String share) {
        this.share = share;
    }
    public String getShare() {
        return share;
    }
    
    public void setFloor(String floor) {
        this.floor = floor;
    }
    public String getFloor() {
        return floor;
    }
    
    public void setBookingMode(String bookingMode) {
        this.bookingMode = bookingMode;
    }
    public String getBookingMode() {
        return bookingMode;
    }
	public Timestamp getBookingDate() {
		return bookingDate;
	}
	public void setBookingDate(Timestamp bookingDate) {
		this.bookingDate = bookingDate;
	}
	public Timestamp getOutDate() {
		return outDate;
	}
	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}
	public Timestamp getInDate() {
		return inDate;
	}
	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}
	public Timestamp getCurrMonthStartDate() {
		return currMonthStartDate;
	}
	public void setCurrMonthStartDate(Timestamp currMonthStartDate) {
		this.currMonthStartDate = currMonthStartDate;
	}
	public Timestamp getCurrMonthEndDate() {
		return currMonthEndDate;
	}
	public void setCurrMonthEndDate(Timestamp currMonthEndDate) {
		this.currMonthEndDate = currMonthEndDate;
	}
	public Timestamp getRentCycleEndDate() {
		return rentCycleEndDate;
	}
	public void setRentCycleEndDate(Timestamp rentCycleEndDate) {
		this.rentCycleEndDate = rentCycleEndDate;
	}
	public void setTermsAccepted(boolean isTermsAccepted) {
		this.isTermsAccepted = isTermsAccepted;
	}
    
}