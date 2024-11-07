package com.integration.zoy.utils;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

   
public class LocationResponse {

   @SerializedName("latitude")
   BigDecimal latitude;

   @SerializedName("longitude")
   BigDecimal longitude;


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
	@Override
	public String toString() {
		return "Location [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
    
    
    
}