package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class CustomerBookingDetails {

   @SerializedName("bookingDetails")
   BookingDetails bookingDetails;

   @SerializedName("basicDetails")
   BasicDetails basicDetails;

   @SerializedName("termsConditions")
   TermsConditions termsConditions;

   @SerializedName("bookingInfo")
   BookingInfo bookingInfo;
   
   @SerializedName("profileDetails")
   ProfileImage profileImage;
   
   @SerializedName("currentPg")
   ZoyPgRequestDetails zoyPgRequestDetails;
   
   @SerializedName("registrationDetails")
   ZoyCustomerDetails zoyCustomerDetails;
   
   
    public void setBookingDetails(BookingDetails bookingDetails) {
        this.bookingDetails = bookingDetails;
    }
    public BookingDetails getBookingDetails() {
        return bookingDetails;
    }
    
    public void setBasicDetails(BasicDetails basicDetails) {
        this.basicDetails = basicDetails;
    }
    public BasicDetails getBasicDetails() {
        return basicDetails;
    }
    
    public void setTermsConditions(TermsConditions termsConditions) {
        this.termsConditions = termsConditions;
    }
    public TermsConditions getTermsConditions() {
        return termsConditions;
    }
    
    public void setBookingInfo(BookingInfo bookingInfo) {
        this.bookingInfo = bookingInfo;
    }
    public BookingInfo getBookingInfo() {
        return bookingInfo;
    }
	public ProfileImage getProfileImage() {
		return profileImage;
	}
	public void setProfileImage(ProfileImage profileImage) {
		this.profileImage = profileImage;
	}
	public ZoyPgRequestDetails getZoyPgRequestDetails() {
		return zoyPgRequestDetails;
	}
	public void setZoyPgRequestDetails(ZoyPgRequestDetails zoyPgRequestDetails) {
		this.zoyPgRequestDetails = zoyPgRequestDetails;
	}
	public ZoyCustomerDetails getZoyCustomerDetails() {
		return zoyCustomerDetails;
	}
	public void setZoyCustomerDetails(ZoyCustomerDetails zoyCustomerDetails) {
		this.zoyCustomerDetails = zoyCustomerDetails;
	}
	
	
}