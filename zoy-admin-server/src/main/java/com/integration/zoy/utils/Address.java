package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class Address {

   @SerializedName("country")
   String country;

   @SerializedName("careOf")
   String careOf;

   @SerializedName("pin")
   Integer pin;

   @SerializedName("vtc")
   String vtc;

   @SerializedName("street")
   String street;

   @SerializedName("district")
   String district;

   @SerializedName("locality")
   String locality;

   @SerializedName("state")
   String state;

   @SerializedName("landmark")
   String landmark;

   @SerializedName("house")
   String house;

   @SerializedName("subDistrict")
   String subDistrict;

   @SerializedName("postOffice")
   String postOffice;


    public void setCountry(String country) {
        this.country = country;
    }
    public String getCountry() {
        return country;
    }
    
    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }
    public String getCareOf() {
        return careOf;
    }
    
    public void setPin(Integer pin) {
        this.pin = pin;
    }
    public Integer getPin() {
        return pin;
    }
    
    public void setVtc(String vtc) {
        this.vtc = vtc;
    }
    public String getVtc() {
        return vtc;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    public String getStreet() {
        return street;
    }
    
    public void setDistrict(String district) {
        this.district = district;
    }
    public String getDistrict() {
        return district;
    }
    
    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getLocality() {
        return locality;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }
    
    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    public String getLandmark() {
        return landmark;
    }
    
    public void setHouse(String house) {
        this.house = house;
    }
    public String getHouse() {
        return house;
    }
    
    public void setSubDistrict(String subDistrict) {
        this.subDistrict = subDistrict;
    }
    public String getSubDistrict() {
        return subDistrict;
    }
    
    public void setPostOffice(String postOffice) {
        this.postOffice = postOffice;
    }
    public String getPostOffice() {
        return postOffice;
    }
    
}