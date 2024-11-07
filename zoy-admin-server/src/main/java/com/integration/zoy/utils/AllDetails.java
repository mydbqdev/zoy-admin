package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;


public class AllDetails {

   @SerializedName("personalDetails")
   PersonalDetails personalDetails;

   @SerializedName("familyDetails")
   FamilyDetails familyDetails;

   @SerializedName("localDetails")
   LocalDetails localDetails;

   @SerializedName("rentingDetails")
   RentingDetails rentingDetails;


    public void setPersonalDetails(PersonalDetails personalDetails) {
        this.personalDetails = personalDetails;
    }
    public PersonalDetails getPersonalDetails() {
        return personalDetails;
    }
    
    public void setFamilyDetails(FamilyDetails familyDetails) {
        this.familyDetails = familyDetails;
    }
    public FamilyDetails getFamilyDetails() {
        return familyDetails;
    }
    
    public void setLocalDetails(LocalDetails localDetails) {
        this.localDetails = localDetails;
    }
    public LocalDetails getLocalDetails() {
        return localDetails;
    }
    
    public void setRentingDetails(RentingDetails rentingDetails) {
        this.rentingDetails = rentingDetails;
    }
    public RentingDetails getRentingDetails() {
        return rentingDetails;
    }
    
}