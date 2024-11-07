package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ZoyPgRequestDetails {
	@SerializedName("tenant_id")
	String tenantId;

	@SerializedName("booking_id")
	String bookingId;

	@SerializedName("rentalNeedDetails")
	RentalNeedDetails rentalNeedDetails;

	@SerializedName("pg_owner_id")
	String pgOwnerId;

	@SerializedName("property_id")
	String propertyId;

	@SerializedName("check_out_request")
	CheckOutRequest checkOutRequest;
	
	@SerializedName("move_out_request")
	MoveOutRequest moveOutRequest;


	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public String getTenantId() {
		return tenantId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	public String getBookingId() {
		return bookingId;
	}

	public void setRentalNeedDetails(RentalNeedDetails rentalNeedDetails) {
		this.rentalNeedDetails = rentalNeedDetails;
	}
	public RentalNeedDetails getRentalNeedDetails() {
		return rentalNeedDetails;
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

	public void setCheckOutRequest(CheckOutRequest checkOutRequest) {
		this.checkOutRequest = checkOutRequest;
	}
	public CheckOutRequest getCheckOutRequest() {
		return checkOutRequest;
	}
	public MoveOutRequest getMoveOutRequest() {
		return moveOutRequest;
	}
	public void setMoveOutRequest(MoveOutRequest moveOutRequest) {
		this.moveOutRequest = moveOutRequest;
	}
	
	
	
}
