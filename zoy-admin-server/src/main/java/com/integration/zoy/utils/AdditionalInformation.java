package com.integration.zoy.utils;
import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class AdditionalInformation {

   @SerializedName("chargesStatus")
   boolean chargesStatus;

   @SerializedName("ekycCharges")
   BigDecimal ekycCharges;

   @SerializedName("agreementCharges")
   BigDecimal agreementCharges;

   @SerializedName("occupancyType")
   String occupancyType;

   @SerializedName("images")
   List<Images> images;

   @SerializedName("totalCharges")
   BigDecimal totalCharges;

   @SerializedName("description")
   String description;


    public void setChargesStatus(boolean chargesStatus) {
        this.chargesStatus = chargesStatus;
    }
    public boolean getChargesStatus() {
        return chargesStatus;
    }
    
    public void setEkycCharges(BigDecimal ekycCharges) {
        this.ekycCharges = ekycCharges;
    }
    public BigDecimal getEkycCharges() {
        return ekycCharges;
    }
    
    public void setAgreementCharges(BigDecimal agreementCharges) {
        this.agreementCharges = agreementCharges;
    }
    public BigDecimal getAgreementCharges() {
        return agreementCharges;
    }
    
    public void setOccupancyType(String occupancyType) {
        this.occupancyType = occupancyType;
    }
    public String getOccupancyType() {
        return occupancyType;
    }
    
    public void setImages(List<Images> images) {
        this.images = images;
    }
    public List<Images> getImages() {
        return images;
    }
    
    public void setTotalCharges(BigDecimal totalCharges) {
        this.totalCharges = totalCharges;
    }
    public BigDecimal getTotalCharges() {
        return totalCharges;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    
}