package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;


public class SearchData {

   @SerializedName("address")
   String address;

   @SerializedName("latitude")
   BigDecimal latitude;

   @SerializedName("longitude")
   BigDecimal longitude;

   @SerializedName("name")
   String name;

   @SerializedName("searchText")
   String searchText;

   @SerializedName("timestamp")
   Timestamp timestamp;


    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress() {
        return address;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }
    public String getSearchText() {
        return searchText;
    }
    
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    
}
