package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

   
public class TermsConditions {

   @SerializedName("noticePeriod")
   String noticePeriod;

   @SerializedName("securityDeposit")
   String securityDeposit;

   @SerializedName("agreementDuration")
   String agreementDuration;


    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }
    public String getNoticePeriod() {
        return noticePeriod;
    }
    
    public void setSecurityDeposit(String securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    public String getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setAgreementDuration(String agreementDuration) {
        this.agreementDuration = agreementDuration;
    }
    public String getAgreementDuration() {
        return agreementDuration;
    }
    
}