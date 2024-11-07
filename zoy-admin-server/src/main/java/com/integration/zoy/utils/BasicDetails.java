package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class BasicDetails {

   @SerializedName("pincode")
   String pincode;

   @SerializedName("emailAddress")
   String emailAddress;

   @SerializedName("propertyname")
   String propertyname;

   @SerializedName("city")
   String city;

   @SerializedName("contactNumber")
   String contactNumber;

   @SerializedName("locality")
   String locality;

   @SerializedName("houseArea")
   String houseArea;

   @SerializedName("managername")
   String managername;

   @SerializedName("location")
   Location location;

   @SerializedName("state")
   String state;


    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    public String getPincode() {
        return pincode;
    }
    
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
    public String getEmailAddress() {
        return emailAddress;
    }
    
    public void setPropertyname(String propertyname) {
        this.propertyname = propertyname;
    }
    public String getPropertyname() {
        return propertyname;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    public String getCity() {
        return city;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getLocality() {
        return locality;
    }
    
    public void setHouseArea(String houseArea) {
        this.houseArea = houseArea;
    }
    public String getHouseArea() {
        return houseArea;
    }
    
    public void setManagername(String managername) {
        this.managername = managername;
    }
    public String getManagername() {
        return managername;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    public String getState() {
        return state;
    }
    
}