package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class EkycDetails {

   @SerializedName("status")
   String status;

   @SerializedName("data")
   Data1 data;


    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    
    public void setData(Data1 data) {
        this.data = data;
    }
    public Data1 getData() {
        return data;
    }
    
}