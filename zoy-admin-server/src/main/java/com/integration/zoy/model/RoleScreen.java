package com.integration.zoy.model;

import com.google.gson.annotations.SerializedName;

   
public class RoleScreen {

   @SerializedName("screen_name")
   String screenName;

   @SerializedName("read_prv")
   boolean readPrv;

   @SerializedName("write_prv")
   boolean writePrv;


    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
    public String getScreenName() {
        return screenName;
    }
    
    public void setReadPrv(boolean readPrv) {
        this.readPrv = readPrv;
    }
    public boolean getReadPrv() {
        return readPrv;
    }
    
    public void setWritePrv(boolean writePrv) {
        this.writePrv = writePrv;
    }
    public boolean getWritePrv() {
        return writePrv;
    }
    
}