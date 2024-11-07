package com.integration.zoy.utils;
import java.util.List;
import java.math.BigDecimal;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

   
public class OtherTerms {

   @SerializedName("gracePeriod")
   String gracePeriod;

   @SerializedName("latePaymentFee")
   BigDecimal latePaymentFee;

   @SerializedName("rentCycle")
   List<RentCycle> rentCycle;

   @SerializedName("isLatePaymentFee")
   boolean isLatePaymentFee;


    public void setGracePeriod(String gracePeriod) {
        this.gracePeriod = gracePeriod;
    }
    public String getGracePeriod() {
        return gracePeriod;
    }
    
    public void setLatePaymentFee(BigDecimal latePaymentFee) {
        this.latePaymentFee = latePaymentFee;
    }
    public BigDecimal getLatePaymentFee() {
        return latePaymentFee;
    }
    
    public void setRentCycle(List<RentCycle> rentCycle) {
        this.rentCycle = rentCycle;
    }
    public List<RentCycle> getRentCycle() {
        return rentCycle;
    }
    
    public void setIsLatePaymentFee(boolean isLatePaymentFee) {
        this.isLatePaymentFee = isLatePaymentFee;
    }
    public boolean getIsLatePaymentFee() {
        return isLatePaymentFee;
    }
    
}