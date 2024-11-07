package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class SinglePropertyDetails {

   @SerializedName("id")
   String id;

   @SerializedName("basicDetails")
   BasicDetails basicDetails;

   @SerializedName("additionalInformation")
   AdditionalInformation additionalInformation;

   @SerializedName("amenities")
   Ameneties amenities;

   @SerializedName("otherTerms")
   OtherTerms otherTerms;

   @SerializedName("termsConditions")
   TermsConditions termsConditions;

   @SerializedName("propertyInformation")
   PropertyInformation propertyInformation;


    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    
    public void setBasicDetails(BasicDetails basicDetails) {
        this.basicDetails = basicDetails;
    }
    public BasicDetails getBasicDetails() {
        return basicDetails;
    }
    
    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }
    
    public void setAmenities(Ameneties amenities) {
        this.amenities = amenities;
    }
    public Ameneties getAmenities() {
        return amenities;
    }
    
    public void setOtherTerms(OtherTerms otherTerms) {
        this.otherTerms = otherTerms;
    }
    public OtherTerms getOtherTerms() {
        return otherTerms;
    }
    
    public void setTermsConditions(TermsConditions termsConditions) {
        this.termsConditions = termsConditions;
    }
    public TermsConditions getTermsConditions() {
        return termsConditions;
    }
    
    public void setPropertyInformation(PropertyInformation propertyInformation) {
        this.propertyInformation = propertyInformation;
    }
    public PropertyInformation getPropertyInformation() {
        return propertyInformation;
    }
    
}