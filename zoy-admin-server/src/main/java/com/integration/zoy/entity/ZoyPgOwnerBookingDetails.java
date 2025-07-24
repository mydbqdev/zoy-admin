package com.integration.zoy.entity;


import javax.persistence.*;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "zoy_pg_owner_booking_details", schema = "pgowners")
public class ZoyPgOwnerBookingDetails {

    @Id
    //@GeneratedValue(generator = "UUID")
	//@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "booking_id", updatable = false, nullable = false, unique = true, length = 36)
    private String bookingId;

    @Column(name = "property_id")
    private String propertyId;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "out_date")
//    @Temporal(TemporalType.DATE)
    private Timestamp outDate;

    @Column(name = "fixed_rent")
    private BigDecimal fixedRent;

    @Column(name = "selected_bed")
    private String selectedBed;

    @Column(name = "gender")
    private String gender;

    @Column(name = "in_date")
//    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp inDate;

    @Column(name = "is_terms_accepted")
    private Boolean isTermsAccepted;

    @Column(name = "gst")
    private BigDecimal gst;

    @Column(name = "security_deposit")
    private BigDecimal securityDeposit;

    @Column(name = "cal_fixed_rent")
    private BigDecimal calFixedRent;

    @Column(name = "curr_month_start_date")
//    @Temporal(TemporalType.DATE)
    private Timestamp currMonthStartDate;

    @Column(name = "room")
    private String room;

    @Column(name = "curr_month_end_date")
//    @Temporal(TemporalType.DATE)
    private Timestamp currMonthEndDate;

    @Column(name = "no_of_days")
    private String noOfDays;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "rent_cycle_end_date")
//    @Temporal(TemporalType.DATE)
    private Timestamp rentCycleEndDate;

    @Column(name = "due")
    private BigDecimal due;

    @Column(name = "name")
    private String name;

    @Column(name = "lock_in_period")
    private String lockInPeriod;

    @Column(name = "share")
    private String share;

    @Column(name = "send_whatsapp_notifications")
    private Boolean sendWhatsAppNotifications;

    @Column(name = "floor")
    private String floor;

    @Column(name = "booking_mode")
    private String bookingMode;

    @Column(name = "customer_gst_number")
    private String customerGstNumber;
    
    @Column(name = "paid_deposit")
    private BigDecimal paidDeposit;
    
    @Column(name = "deposit_paid")
    private Boolean depositPaid = false;

    @Column(name = "include_current_month")
    private Boolean includeCurrentMonth;
    
    @Column(name = "short_term" ,nullable = false)
    private Boolean shortTerm = false;
    
    @Column(name = "rental_agreement" ,nullable = false)
    private Boolean rentalAgreement = false;
    
    @Column(name = "rent_discount")
    private BigDecimal rentDiscount;
    
    @Column(name = "actual_rent")
    private BigDecimal actualRent;
    
    @Column(name = "actual_in_date")
	private Timestamp actualInDate;
    
	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
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

	public BigDecimal getGst() {
		return gst;
	}

	public void setGst(BigDecimal gst) {
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

	public String getNoOfDays() {
		return noOfDays;
	}

	public void setNoOfDays(String noOfDays) {
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

	public String getShare() {
		return share;
	}

	public void setShare(String share) {
		this.share = share;
	}

	public Boolean getSendWhatsAppNotifications() {
		return sendWhatsAppNotifications;
	}

	public void setSendWhatsAppNotifications(Boolean sendWhatsAppNotifications) {
		this.sendWhatsAppNotifications = sendWhatsAppNotifications;
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

	public BigDecimal getPaidDeposit() {
		return paidDeposit;
	}

	public void setPaidDeposit(BigDecimal paidDeposit) {
		this.paidDeposit = paidDeposit;
	}

	public Boolean getIncludeCurrentMonth() {
		return includeCurrentMonth;
	}

	public void setIncludeCurrentMonth(Boolean includeCurrentMonth) {
		this.includeCurrentMonth = includeCurrentMonth;
	}

	public Boolean getDepositPaid() {
		return depositPaid;
	}

	public void setDepositPaid(Boolean depositPaid) {
		this.depositPaid = depositPaid;
	}
	
	public Boolean getShortTerm() {
		return shortTerm;
	}

	public void setShortTerm(Boolean shortTerm) {
		this.shortTerm = shortTerm;
	}
	
	public Boolean getRentalAgreement() {
		return rentalAgreement;
	}

	public void setRentalAgreement(Boolean rentalAgreement) {
		this.rentalAgreement = rentalAgreement;
	}
	
	public BigDecimal getRentDiscount() {
		return rentDiscount;
	}

	public void setRentDiscount(BigDecimal rentDiscount) {
		this.rentDiscount = rentDiscount;
	}

	public BigDecimal getActualRent() {
		return actualRent;
	}

	public void setActualRent(BigDecimal actualRent) {
		this.actualRent = actualRent;
	}

	public Timestamp getActualInDate() {
		return actualInDate;
	}

	public void setActualInDate(Timestamp actualInDate) {
		this.actualInDate = actualInDate;
	}

	@PrePersist
    private void generateBookingId() {
        if (this.bookingId == null || this.bookingId.isEmpty()) {
            String prefix = "BKG_ZOY";
            String uniquePart = generateUniquePartSafe();
            String formattedDate = String.format("%1$tY%1$tm%1$td", new Timestamp(System.currentTimeMillis())); 
            this.bookingId = prefix + formattedDate + uniquePart.toUpperCase();
        }
    }
	private String generateUniquePartSafe() {
        try {
            String nanoTimestamp = String.valueOf(Instant.now().toEpochMilli()) + System.nanoTime();
            String uuidPart = UUID.randomUUID().toString();
            String rawId = nanoTimestamp + uuidPart;
            return hashWithSHA256Safe(rawId).substring(0, 8);
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    private String hashWithSHA256Safe(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input.substring(0, Math.min(8, input.length()));
        }
    }
}

