package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class UserBookingPaymentDTO {
    // User Booking Payment Fields
    private String userId;
    private String userBookingsId;
    private String userPaymentId;
    private String userMoneyDueId;
    private String userPaymentBookingId;
    private Timestamp userPaymentCreatedAt;
    private String userPaymentRazorpayOrderId;
    private String userPaymentRazorpayPaymentId;
    private String userPaymentRazorpaySignature;
    private String userPaymentBankTransactionId;
    private BigDecimal userPaymentResultAmount;
    private BigDecimal userPaymentResultAmountRefunded;
    private String userPaymentResultBank;
    private Boolean userPaymentResultCaptured;
    private String userPaymentResultCardId;
    private String userPaymentResultContact;
    private Timestamp userPaymentResultCreatedAt;
    private String userPaymentResultCurrency;
    private String userPaymentResultDescription;
    private String userPaymentResultEmail;
    private String userPaymentResultEntity;
    private String userPaymentResultErrorCode;
    private String userPaymentResultErrorDescription;
    private String userPaymentResultErrorSource;
    private String userPaymentResultErrorStep;
    private BigDecimal userPaymentResultFee;
    private String userPaymentResultId;
    private Boolean userPaymentResultInternational;
    private String userPaymentResultInvoiceId;
    private String userPaymentResultMethod;
    private String userPaymentResultNotes;
    private String userPaymentResultOrderId;
    private String userPaymentResultRefundStatus;
    private String userPaymentResultStatus;
    private BigDecimal userPaymentResultTax;
    private String userPaymentResultVpa;
    private String userPaymentResultWallet;
    private BigDecimal userPaymentGst;
    private BigDecimal userPaymentPayableAmount;
    private String userPaymentPaymentStatus;
    private Timestamp userPaymentReceiveDate;
    private Timestamp userPaymentTimestamp;
    private String userPaymentZoyPaymentMode;
    private String userPaymentZoyPaymentType;

    // User Booking Fields
    private String userBookingsTenantId;
    private Timestamp userBookingsDate;
    private Boolean userBookingsIsCancelled;
    private String userBookingsPgOwnerId;
    private String userBookingsPropertyId;
    private Timestamp userBookingsWebCheckIn;
    private Timestamp userBookingsWebCheckOut;

    // Property Fields
    private String propertyId;
    private String pgOwnerId;
    private String propertyTypeId;
    private String pgTypeId;
    private String propertyName;
    private String propertyPgEmail;
    private String propertyCity;
    private String propertyState;
    private String propertyContactNumber;
    private BigDecimal propertyLocationLatitude;
    private BigDecimal propertyLocationLongitude;
    private String propertyHouseArea;
    private String propertyManagerName;
    private BigDecimal propertyAgreementCharges;
    private Boolean propertyChargeStatus;
    private String propertyDescription;
    private String propertyImageName;
    private String propertyImageUrl;
    private String propertyOccupancyType;
    private BigDecimal propertyTotalCharges;
    private String propertyOfflineBookingId;
    private Integer propertyGracePeriod;
    private Boolean propertyIsLatePaymentFee;
    private BigDecimal propertyLatePaymentFee;
    private String propertyCinNumber;
    private String propertyGstNumber;
    private String propertyLocality;
    private String propertyPincode;
    private BigDecimal propertyEkycCharges;

    // Booking Details Fields
    private String bookingId;
    private String tenantId;
    private Timestamp outDate;
    private BigDecimal fixedRent;
    private String selectedBed;
    private String gender;
    private Timestamp inDate;
    private Boolean isTermsAccepted;
    private String gst;
    private BigDecimal securityDeposit;
    private BigDecimal calFixedRent;
    private Timestamp currMonthStartDate;
    private String room;
    private Timestamp currMonthEndDate;
    private Integer noOfDays;
    private String phoneNumber;
    private Timestamp rentCycleEndDate;
    private BigDecimal due;
    private String name;
    private String lockInPeriod;
    private Integer share;
    private Boolean sendWhatsappNotifications;
    private String floor;
    private String bookingMode;
    private String customerGstNumber;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserBookingsId() {
		return userBookingsId;
	}
	public void setUserBookingsId(String userBookingsId) {
		this.userBookingsId = userBookingsId;
	}
	public String getUserPaymentId() {
		return userPaymentId;
	}
	public void setUserPaymentId(String userPaymentId) {
		this.userPaymentId = userPaymentId;
	}
	public String getUserMoneyDueId() {
		return userMoneyDueId;
	}
	public void setUserMoneyDueId(String userMoneyDueId) {
		this.userMoneyDueId = userMoneyDueId;
	}
	public String getUserPaymentBookingId() {
		return userPaymentBookingId;
	}
	public void setUserPaymentBookingId(String userPaymentBookingId) {
		this.userPaymentBookingId = userPaymentBookingId;
	}
	public Timestamp getUserPaymentCreatedAt() {
		return userPaymentCreatedAt;
	}
	public void setUserPaymentCreatedAt(Timestamp userPaymentCreatedAt) {
		this.userPaymentCreatedAt = userPaymentCreatedAt;
	}
	public String getUserPaymentRazorpayOrderId() {
		return userPaymentRazorpayOrderId;
	}
	public void setUserPaymentRazorpayOrderId(String userPaymentRazorpayOrderId) {
		this.userPaymentRazorpayOrderId = userPaymentRazorpayOrderId;
	}
	public String getUserPaymentRazorpayPaymentId() {
		return userPaymentRazorpayPaymentId;
	}
	public void setUserPaymentRazorpayPaymentId(String userPaymentRazorpayPaymentId) {
		this.userPaymentRazorpayPaymentId = userPaymentRazorpayPaymentId;
	}
	public String getUserPaymentRazorpaySignature() {
		return userPaymentRazorpaySignature;
	}
	public void setUserPaymentRazorpaySignature(String userPaymentRazorpaySignature) {
		this.userPaymentRazorpaySignature = userPaymentRazorpaySignature;
	}
	public String getUserPaymentBankTransactionId() {
		return userPaymentBankTransactionId;
	}
	public void setUserPaymentBankTransactionId(String userPaymentBankTransactionId) {
		this.userPaymentBankTransactionId = userPaymentBankTransactionId;
	}
	public BigDecimal getUserPaymentResultAmount() {
		return userPaymentResultAmount;
	}
	public void setUserPaymentResultAmount(BigDecimal userPaymentResultAmount) {
		this.userPaymentResultAmount = userPaymentResultAmount;
	}
	public BigDecimal getUserPaymentResultAmountRefunded() {
		return userPaymentResultAmountRefunded;
	}
	public void setUserPaymentResultAmountRefunded(BigDecimal userPaymentResultAmountRefunded) {
		this.userPaymentResultAmountRefunded = userPaymentResultAmountRefunded;
	}
	public String getUserPaymentResultBank() {
		return userPaymentResultBank;
	}
	public void setUserPaymentResultBank(String userPaymentResultBank) {
		this.userPaymentResultBank = userPaymentResultBank;
	}
	public Boolean getUserPaymentResultCaptured() {
		return userPaymentResultCaptured;
	}
	public void setUserPaymentResultCaptured(Boolean userPaymentResultCaptured) {
		this.userPaymentResultCaptured = userPaymentResultCaptured;
	}
	public String getUserPaymentResultCardId() {
		return userPaymentResultCardId;
	}
	public void setUserPaymentResultCardId(String userPaymentResultCardId) {
		this.userPaymentResultCardId = userPaymentResultCardId;
	}
	public String getUserPaymentResultContact() {
		return userPaymentResultContact;
	}
	public void setUserPaymentResultContact(String userPaymentResultContact) {
		this.userPaymentResultContact = userPaymentResultContact;
	}
	public Timestamp getUserPaymentResultCreatedAt() {
		return userPaymentResultCreatedAt;
	}
	public void setUserPaymentResultCreatedAt(Timestamp userPaymentResultCreatedAt) {
		this.userPaymentResultCreatedAt = userPaymentResultCreatedAt;
	}
	public String getUserPaymentResultCurrency() {
		return userPaymentResultCurrency;
	}
	public void setUserPaymentResultCurrency(String userPaymentResultCurrency) {
		this.userPaymentResultCurrency = userPaymentResultCurrency;
	}
	public String getUserPaymentResultDescription() {
		return userPaymentResultDescription;
	}
	public void setUserPaymentResultDescription(String userPaymentResultDescription) {
		this.userPaymentResultDescription = userPaymentResultDescription;
	}
	public String getUserPaymentResultEmail() {
		return userPaymentResultEmail;
	}
	public void setUserPaymentResultEmail(String userPaymentResultEmail) {
		this.userPaymentResultEmail = userPaymentResultEmail;
	}
	public String getUserPaymentResultEntity() {
		return userPaymentResultEntity;
	}
	public void setUserPaymentResultEntity(String userPaymentResultEntity) {
		this.userPaymentResultEntity = userPaymentResultEntity;
	}
	public String getUserPaymentResultErrorCode() {
		return userPaymentResultErrorCode;
	}
	public void setUserPaymentResultErrorCode(String userPaymentResultErrorCode) {
		this.userPaymentResultErrorCode = userPaymentResultErrorCode;
	}
	public String getUserPaymentResultErrorDescription() {
		return userPaymentResultErrorDescription;
	}
	public void setUserPaymentResultErrorDescription(String userPaymentResultErrorDescription) {
		this.userPaymentResultErrorDescription = userPaymentResultErrorDescription;
	}
	public String getUserPaymentResultErrorSource() {
		return userPaymentResultErrorSource;
	}
	public void setUserPaymentResultErrorSource(String userPaymentResultErrorSource) {
		this.userPaymentResultErrorSource = userPaymentResultErrorSource;
	}
	public String getUserPaymentResultErrorStep() {
		return userPaymentResultErrorStep;
	}
	public void setUserPaymentResultErrorStep(String userPaymentResultErrorStep) {
		this.userPaymentResultErrorStep = userPaymentResultErrorStep;
	}
	public BigDecimal getUserPaymentResultFee() {
		return userPaymentResultFee;
	}
	public void setUserPaymentResultFee(BigDecimal userPaymentResultFee) {
		this.userPaymentResultFee = userPaymentResultFee;
	}
	public String getUserPaymentResultId() {
		return userPaymentResultId;
	}
	public void setUserPaymentResultId(String userPaymentResultId) {
		this.userPaymentResultId = userPaymentResultId;
	}
	public Boolean getUserPaymentResultInternational() {
		return userPaymentResultInternational;
	}
	public void setUserPaymentResultInternational(Boolean userPaymentResultInternational) {
		this.userPaymentResultInternational = userPaymentResultInternational;
	}
	public String getUserPaymentResultInvoiceId() {
		return userPaymentResultInvoiceId;
	}
	public void setUserPaymentResultInvoiceId(String userPaymentResultInvoiceId) {
		this.userPaymentResultInvoiceId = userPaymentResultInvoiceId;
	}
	public String getUserPaymentResultMethod() {
		return userPaymentResultMethod;
	}
	public void setUserPaymentResultMethod(String userPaymentResultMethod) {
		this.userPaymentResultMethod = userPaymentResultMethod;
	}
	public String getUserPaymentResultNotes() {
		return userPaymentResultNotes;
	}
	public void setUserPaymentResultNotes(String userPaymentResultNotes) {
		this.userPaymentResultNotes = userPaymentResultNotes;
	}
	public String getUserPaymentResultOrderId() {
		return userPaymentResultOrderId;
	}
	public void setUserPaymentResultOrderId(String userPaymentResultOrderId) {
		this.userPaymentResultOrderId = userPaymentResultOrderId;
	}
	public String getUserPaymentResultRefundStatus() {
		return userPaymentResultRefundStatus;
	}
	public void setUserPaymentResultRefundStatus(String userPaymentResultRefundStatus) {
		this.userPaymentResultRefundStatus = userPaymentResultRefundStatus;
	}
	public String getUserPaymentResultStatus() {
		return userPaymentResultStatus;
	}
	public void setUserPaymentResultStatus(String userPaymentResultStatus) {
		this.userPaymentResultStatus = userPaymentResultStatus;
	}
	public BigDecimal getUserPaymentResultTax() {
		return userPaymentResultTax;
	}
	public void setUserPaymentResultTax(BigDecimal userPaymentResultTax) {
		this.userPaymentResultTax = userPaymentResultTax;
	}
	public String getUserPaymentResultVpa() {
		return userPaymentResultVpa;
	}
	public void setUserPaymentResultVpa(String userPaymentResultVpa) {
		this.userPaymentResultVpa = userPaymentResultVpa;
	}
	public String getUserPaymentResultWallet() {
		return userPaymentResultWallet;
	}
	public void setUserPaymentResultWallet(String userPaymentResultWallet) {
		this.userPaymentResultWallet = userPaymentResultWallet;
	}
	public BigDecimal getUserPaymentGst() {
		return userPaymentGst;
	}
	public void setUserPaymentGst(BigDecimal userPaymentGst) {
		this.userPaymentGst = userPaymentGst;
	}
	public BigDecimal getUserPaymentPayableAmount() {
		return userPaymentPayableAmount;
	}
	public void setUserPaymentPayableAmount(BigDecimal userPaymentPayableAmount) {
		this.userPaymentPayableAmount = userPaymentPayableAmount;
	}
	public String getUserPaymentPaymentStatus() {
		return userPaymentPaymentStatus;
	}
	public void setUserPaymentPaymentStatus(String userPaymentPaymentStatus) {
		this.userPaymentPaymentStatus = userPaymentPaymentStatus;
	}
	public Timestamp getUserPaymentReceiveDate() {
		return userPaymentReceiveDate;
	}
	public void setUserPaymentReceiveDate(Timestamp userPaymentReceiveDate) {
		this.userPaymentReceiveDate = userPaymentReceiveDate;
	}
	public Timestamp getUserPaymentTimestamp() {
		return userPaymentTimestamp;
	}
	public void setUserPaymentTimestamp(Timestamp userPaymentTimestamp) {
		this.userPaymentTimestamp = userPaymentTimestamp;
	}
	public String getUserPaymentZoyPaymentMode() {
		return userPaymentZoyPaymentMode;
	}
	public void setUserPaymentZoyPaymentMode(String userPaymentZoyPaymentMode) {
		this.userPaymentZoyPaymentMode = userPaymentZoyPaymentMode;
	}
	public String getUserPaymentZoyPaymentType() {
		return userPaymentZoyPaymentType;
	}
	public void setUserPaymentZoyPaymentType(String userPaymentZoyPaymentType) {
		this.userPaymentZoyPaymentType = userPaymentZoyPaymentType;
	}
	public String getUserBookingsTenantId() {
		return userBookingsTenantId;
	}
	public void setUserBookingsTenantId(String userBookingsTenantId) {
		this.userBookingsTenantId = userBookingsTenantId;
	}
	public Timestamp getUserBookingsDate() {
		return userBookingsDate;
	}
	public void setUserBookingsDate(Timestamp userBookingsDate) {
		this.userBookingsDate = userBookingsDate;
	}
	public Boolean getUserBookingsIsCancelled() {
		return userBookingsIsCancelled;
	}
	public void setUserBookingsIsCancelled(Boolean userBookingsIsCancelled) {
		this.userBookingsIsCancelled = userBookingsIsCancelled;
	}
	public String getUserBookingsPgOwnerId() {
		return userBookingsPgOwnerId;
	}
	public void setUserBookingsPgOwnerId(String userBookingsPgOwnerId) {
		this.userBookingsPgOwnerId = userBookingsPgOwnerId;
	}
	public String getUserBookingsPropertyId() {
		return userBookingsPropertyId;
	}
	public void setUserBookingsPropertyId(String userBookingsPropertyId) {
		this.userBookingsPropertyId = userBookingsPropertyId;
	}
	public Timestamp getUserBookingsWebCheckIn() {
		return userBookingsWebCheckIn;
	}
	public void setUserBookingsWebCheckIn(Timestamp userBookingsWebCheckIn) {
		this.userBookingsWebCheckIn = userBookingsWebCheckIn;
	}
	public Timestamp getUserBookingsWebCheckOut() {
		return userBookingsWebCheckOut;
	}
	public void setUserBookingsWebCheckOut(Timestamp userBookingsWebCheckOut) {
		this.userBookingsWebCheckOut = userBookingsWebCheckOut;
	}
	public String getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	public String getPgOwnerId() {
		return pgOwnerId;
	}
	public void setPgOwnerId(String pgOwnerId) {
		this.pgOwnerId = pgOwnerId;
	}
	public String getPropertyTypeId() {
		return propertyTypeId;
	}
	public void setPropertyTypeId(String propertyTypeId) {
		this.propertyTypeId = propertyTypeId;
	}
	public String getPgTypeId() {
		return pgTypeId;
	}
	public void setPgTypeId(String pgTypeId) {
		this.pgTypeId = pgTypeId;
	}
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyPgEmail() {
		return propertyPgEmail;
	}
	public void setPropertyPgEmail(String propertyPgEmail) {
		this.propertyPgEmail = propertyPgEmail;
	}
	public String getPropertyCity() {
		return propertyCity;
	}
	public void setPropertyCity(String propertyCity) {
		this.propertyCity = propertyCity;
	}
	public String getPropertyState() {
		return propertyState;
	}
	public void setPropertyState(String propertyState) {
		this.propertyState = propertyState;
	}
	public String getPropertyContactNumber() {
		return propertyContactNumber;
	}
	public void setPropertyContactNumber(String propertyContactNumber) {
		this.propertyContactNumber = propertyContactNumber;
	}
	public BigDecimal getPropertyLocationLatitude() {
		return propertyLocationLatitude;
	}
	public void setPropertyLocationLatitude(BigDecimal propertyLocationLatitude) {
		this.propertyLocationLatitude = propertyLocationLatitude;
	}
	public BigDecimal getPropertyLocationLongitude() {
		return propertyLocationLongitude;
	}
	public void setPropertyLocationLongitude(BigDecimal propertyLocationLongitude) {
		this.propertyLocationLongitude = propertyLocationLongitude;
	}
	public String getPropertyHouseArea() {
		return propertyHouseArea;
	}
	public void setPropertyHouseArea(String propertyHouseArea) {
		this.propertyHouseArea = propertyHouseArea;
	}
	public String getPropertyManagerName() {
		return propertyManagerName;
	}
	public void setPropertyManagerName(String propertyManagerName) {
		this.propertyManagerName = propertyManagerName;
	}
	public BigDecimal getPropertyAgreementCharges() {
		return propertyAgreementCharges;
	}
	public void setPropertyAgreementCharges(BigDecimal propertyAgreementCharges) {
		this.propertyAgreementCharges = propertyAgreementCharges;
	}
	public Boolean getPropertyChargeStatus() {
		return propertyChargeStatus;
	}
	public void setPropertyChargeStatus(Boolean propertyChargeStatus) {
		this.propertyChargeStatus = propertyChargeStatus;
	}
	public String getPropertyDescription() {
		return propertyDescription;
	}
	public void setPropertyDescription(String propertyDescription) {
		this.propertyDescription = propertyDescription;
	}
	public String getPropertyImageName() {
		return propertyImageName;
	}
	public void setPropertyImageName(String propertyImageName) {
		this.propertyImageName = propertyImageName;
	}
	public String getPropertyImageUrl() {
		return propertyImageUrl;
	}
	public void setPropertyImageUrl(String propertyImageUrl) {
		this.propertyImageUrl = propertyImageUrl;
	}
	public String getPropertyOccupancyType() {
		return propertyOccupancyType;
	}
	public void setPropertyOccupancyType(String propertyOccupancyType) {
		this.propertyOccupancyType = propertyOccupancyType;
	}
	public BigDecimal getPropertyTotalCharges() {
		return propertyTotalCharges;
	}
	public void setPropertyTotalCharges(BigDecimal propertyTotalCharges) {
		this.propertyTotalCharges = propertyTotalCharges;
	}
	public String getPropertyOfflineBookingId() {
		return propertyOfflineBookingId;
	}
	public void setPropertyOfflineBookingId(String propertyOfflineBookingId) {
		this.propertyOfflineBookingId = propertyOfflineBookingId;
	}
	public Integer getPropertyGracePeriod() {
		return propertyGracePeriod;
	}
	public void setPropertyGracePeriod(Integer propertyGracePeriod) {
		this.propertyGracePeriod = propertyGracePeriod;
	}
	public Boolean getPropertyIsLatePaymentFee() {
		return propertyIsLatePaymentFee;
	}
	public void setPropertyIsLatePaymentFee(Boolean propertyIsLatePaymentFee) {
		this.propertyIsLatePaymentFee = propertyIsLatePaymentFee;
	}
	public BigDecimal getPropertyLatePaymentFee() {
		return propertyLatePaymentFee;
	}
	public void setPropertyLatePaymentFee(BigDecimal propertyLatePaymentFee) {
		this.propertyLatePaymentFee = propertyLatePaymentFee;
	}
	public String getPropertyCinNumber() {
		return propertyCinNumber;
	}
	public void setPropertyCinNumber(String propertyCinNumber) {
		this.propertyCinNumber = propertyCinNumber;
	}
	public String getPropertyGstNumber() {
		return propertyGstNumber;
	}
	public void setPropertyGstNumber(String propertyGstNumber) {
		this.propertyGstNumber = propertyGstNumber;
	}
	public String getPropertyLocality() {
		return propertyLocality;
	}
	public void setPropertyLocality(String propertyLocality) {
		this.propertyLocality = propertyLocality;
	}
	public String getPropertyPincode() {
		return propertyPincode;
	}
	public void setPropertyPincode(String propertyPincode) {
		this.propertyPincode = propertyPincode;
	}
	public BigDecimal getPropertyEkycCharges() {
		return propertyEkycCharges;
	}
	public void setPropertyEkycCharges(BigDecimal propertyEkycCharges) {
		this.propertyEkycCharges = propertyEkycCharges;
	}
	public String getBookingId() {
		return bookingId;
	}
	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public Timestamp getOutDate() {
		return outDate;
	}
	public void setOutDate(Timestamp outDate) {
		this.outDate = outDate;
	}
	public BigDecimal getFixedRent() {
		return fixedRent;
	}
	public void setFixedRent(BigDecimal fixedRent) {
		this.fixedRent = fixedRent;
	}
	public String getSelectedBed() {
		return selectedBed;
	}
	public void setSelectedBed(String selectedBed) {
		this.selectedBed = selectedBed;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public Timestamp getInDate() {
		return inDate;
	}
	public void setInDate(Timestamp inDate) {
		this.inDate = inDate;
	}
	public Boolean getIsTermsAccepted() {
		return isTermsAccepted;
	}
	public void setIsTermsAccepted(Boolean isTermsAccepted) {
		this.isTermsAccepted = isTermsAccepted;
	}
	public String getGst() {
		return gst;
	}
	public void setGst(String gst) {
		this.gst = gst;
	}
	public BigDecimal getSecurityDeposit() {
		return securityDeposit;
	}
	public void setSecurityDeposit(BigDecimal securityDeposit) {
		this.securityDeposit = securityDeposit;
	}
	public BigDecimal getCalFixedRent() {
		return calFixedRent;
	}
	public void setCalFixedRent(BigDecimal calFixedRent) {
		this.calFixedRent = calFixedRent;
	}
	public Timestamp getCurrMonthStartDate() {
		return currMonthStartDate;
	}
	public void setCurrMonthStartDate(Timestamp currMonthStartDate) {
		this.currMonthStartDate = currMonthStartDate;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public Timestamp getCurrMonthEndDate() {
		return currMonthEndDate;
	}
	public void setCurrMonthEndDate(Timestamp currMonthEndDate) {
		this.currMonthEndDate = currMonthEndDate;
	}
	public Integer getNoOfDays() {
		return noOfDays;
	}
	public void setNoOfDays(Integer noOfDays) {
		this.noOfDays = noOfDays;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public Timestamp getRentCycleEndDate() {
		return rentCycleEndDate;
	}
	public void setRentCycleEndDate(Timestamp rentCycleEndDate) {
		this.rentCycleEndDate = rentCycleEndDate;
	}
	public BigDecimal getDue() {
		return due;
	}
	public void setDue(BigDecimal due) {
		this.due = due;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLockInPeriod() {
		return lockInPeriod;
	}
	public void setLockInPeriod(String lockInPeriod) {
		this.lockInPeriod = lockInPeriod;
	}
	public Integer getShare() {
		return share;
	}
	public void setShare(Integer share) {
		this.share = share;
	}
	public Boolean getSendWhatsappNotifications() {
		return sendWhatsappNotifications;
	}
	public void setSendWhatsappNotifications(Boolean sendWhatsappNotifications) {
		this.sendWhatsappNotifications = sendWhatsappNotifications;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getBookingMode() {
		return bookingMode;
	}
	public void setBookingMode(String bookingMode) {
		this.bookingMode = bookingMode;
	}
	public String getCustomerGstNumber() {
		return customerGstNumber;
	}
	public void setCustomerGstNumber(String customerGstNumber) {
		this.customerGstNumber = customerGstNumber;
	}
	@Override
	public String toString() {
		return "UserBookingPaymentDTO [userId=" + userId + ", userBookingsId=" + userBookingsId + ", userPaymentId="
				+ userPaymentId + ", userMoneyDueId=" + userMoneyDueId + ", userPaymentBookingId="
				+ userPaymentBookingId + ", userPaymentCreatedAt=" + userPaymentCreatedAt
				+ ", userPaymentRazorpayOrderId=" + userPaymentRazorpayOrderId + ", userPaymentRazorpayPaymentId="
				+ userPaymentRazorpayPaymentId + ", userPaymentRazorpaySignature=" + userPaymentRazorpaySignature
				+ ", userPaymentBankTransactionId=" + userPaymentBankTransactionId + ", userPaymentResultAmount="
				+ userPaymentResultAmount + ", userPaymentResultAmountRefunded=" + userPaymentResultAmountRefunded
				+ ", userPaymentResultBank=" + userPaymentResultBank + ", userPaymentResultCaptured="
				+ userPaymentResultCaptured + ", userPaymentResultCardId=" + userPaymentResultCardId
				+ ", userPaymentResultContact=" + userPaymentResultContact + ", userPaymentResultCreatedAt="
				+ userPaymentResultCreatedAt + ", userPaymentResultCurrency=" + userPaymentResultCurrency
				+ ", userPaymentResultDescription=" + userPaymentResultDescription + ", userPaymentResultEmail="
				+ userPaymentResultEmail + ", userPaymentResultEntity=" + userPaymentResultEntity
				+ ", userPaymentResultErrorCode=" + userPaymentResultErrorCode + ", userPaymentResultErrorDescription="
				+ userPaymentResultErrorDescription + ", userPaymentResultErrorSource=" + userPaymentResultErrorSource
				+ ", userPaymentResultErrorStep=" + userPaymentResultErrorStep + ", userPaymentResultFee="
				+ userPaymentResultFee + ", userPaymentResultId=" + userPaymentResultId
				+ ", userPaymentResultInternational=" + userPaymentResultInternational + ", userPaymentResultInvoiceId="
				+ userPaymentResultInvoiceId + ", userPaymentResultMethod=" + userPaymentResultMethod
				+ ", userPaymentResultNotes=" + userPaymentResultNotes + ", userPaymentResultOrderId="
				+ userPaymentResultOrderId + ", userPaymentResultRefundStatus=" + userPaymentResultRefundStatus
				+ ", userPaymentResultStatus=" + userPaymentResultStatus + ", userPaymentResultTax="
				+ userPaymentResultTax + ", userPaymentResultVpa=" + userPaymentResultVpa + ", userPaymentResultWallet="
				+ userPaymentResultWallet + ", userPaymentGst=" + userPaymentGst + ", userPaymentPayableAmount="
				+ userPaymentPayableAmount + ", userPaymentPaymentStatus=" + userPaymentPaymentStatus
				+ ", userPaymentReceiveDate=" + userPaymentReceiveDate + ", userPaymentTimestamp="
				+ userPaymentTimestamp + ", userPaymentZoyPaymentMode=" + userPaymentZoyPaymentMode
				+ ", userPaymentZoyPaymentType=" + userPaymentZoyPaymentType + ", userBookingsTenantId="
				+ userBookingsTenantId + ", userBookingsDate=" + userBookingsDate + ", userBookingsIsCancelled="
				+ userBookingsIsCancelled + ", userBookingsPgOwnerId=" + userBookingsPgOwnerId
				+ ", userBookingsPropertyId=" + userBookingsPropertyId + ", userBookingsWebCheckIn="
				+ userBookingsWebCheckIn + ", userBookingsWebCheckOut=" + userBookingsWebCheckOut + ", propertyId="
				+ propertyId + ", pgOwnerId=" + pgOwnerId + ", propertyTypeId=" + propertyTypeId + ", pgTypeId="
				+ pgTypeId + ", propertyName=" + propertyName + ", propertyPgEmail=" + propertyPgEmail
				+ ", propertyCity=" + propertyCity + ", propertyState=" + propertyState + ", propertyContactNumber="
				+ propertyContactNumber + ", propertyLocationLatitude=" + propertyLocationLatitude
				+ ", propertyLocationLongitude=" + propertyLocationLongitude + ", propertyHouseArea="
				+ propertyHouseArea + ", propertyManagerName=" + propertyManagerName + ", propertyAgreementCharges="
				+ propertyAgreementCharges + ", propertyChargeStatus=" + propertyChargeStatus + ", propertyDescription="
				+ propertyDescription + ", propertyImageName=" + propertyImageName + ", propertyImageUrl="
				+ propertyImageUrl + ", propertyOccupancyType=" + propertyOccupancyType + ", propertyTotalCharges="
				+ propertyTotalCharges + ", propertyOfflineBookingId=" + propertyOfflineBookingId
				+ ", propertyGracePeriod=" + propertyGracePeriod + ", propertyIsLatePaymentFee="
				+ propertyIsLatePaymentFee + ", propertyLatePaymentFee=" + propertyLatePaymentFee
				+ ", propertyCinNumber=" + propertyCinNumber + ", propertyGstNumber=" + propertyGstNumber
				+ ", propertyLocality=" + propertyLocality + ", propertyPincode=" + propertyPincode
				+ ", propertyEkycCharges=" + propertyEkycCharges + ", bookingId=" + bookingId + ", tenantId=" + tenantId
				+ ", outDate=" + outDate + ", fixedRent=" + fixedRent + ", selectedBed=" + selectedBed + ", gender="
				+ gender + ", inDate=" + inDate + ", isTermsAccepted=" + isTermsAccepted + ", gst=" + gst
				+ ", securityDeposit=" + securityDeposit + ", calFixedRent=" + calFixedRent + ", currMonthStartDate="
				+ currMonthStartDate + ", room=" + room + ", currMonthEndDate=" + currMonthEndDate + ", noOfDays="
				+ noOfDays + ", phoneNumber=" + phoneNumber + ", rentCycleEndDate=" + rentCycleEndDate + ", due=" + due
				+ ", name=" + name + ", lockInPeriod=" + lockInPeriod + ", share=" + share
				+ ", sendWhatsappNotifications=" + sendWhatsappNotifications + ", floor=" + floor + ", bookingMode="
				+ bookingMode + ", customerGstNumber=" + customerGstNumber + "]";
	}

	
	
    
}
