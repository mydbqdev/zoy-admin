package com.integration.zoy.utils;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class Data1 {

   @SerializedName("isEkycVerified")
   boolean isEkycVerified;

   @SerializedName("data")
   Data data;

   @SerializedName("ekycType")
   Integer ekycType;

   @SerializedName("timestamp")
   Date timestamp;


    public void setIsEkycVerified(boolean isEkycVerified) {
        this.isEkycVerified = isEkycVerified;
    }
    public boolean getIsEkycVerified() {
        return isEkycVerified;
    }
    
    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }
    
    public void setEkycType(Integer ekycType) {
        this.ekycType = ekycType;
    }
    public Integer getEkycType() {
        return ekycType;
    }
    
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    
}