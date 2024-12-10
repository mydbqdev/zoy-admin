package com.integration.zoy.utils;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

   
public class Data {

   @SerializedName("maskedNumber")
   String maskedNumber;

   @SerializedName("address")
   Address address;

   @SerializedName("gender")
   String gender;

   @SerializedName("phone")
   String phone;

   @SerializedName("generatedAt")
   Timestamp generatedAt;

   @SerializedName("name")
   String name;

   @SerializedName("photo")
   String photo;

   @SerializedName("dateOfBirth")
   Timestamp dateOfBirth;

   @SerializedName("email")
   String email;


    public void setMaskedNumber(String maskedNumber) {
        this.maskedNumber = maskedNumber;
    }
    public String getMaskedNumber() {
        return maskedNumber;
    }
    
    public void setAddress(Address address) {
        this.address = address;
    }
    public Address getAddress() {
        return address;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getGender() {
        return gender;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getPhone() {
        return phone;
    }
    
    public void setGeneratedAt(Timestamp generatedAt) {
        this.generatedAt = generatedAt;
    }
    public Timestamp getGeneratedAt() {
        return generatedAt;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setPhoto(String photo) {
        this.photo = photo;
    }
    public String getPhoto() {
        return photo;
    }
    
    public void setDateOfBirth(Timestamp dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    public Timestamp getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    
}