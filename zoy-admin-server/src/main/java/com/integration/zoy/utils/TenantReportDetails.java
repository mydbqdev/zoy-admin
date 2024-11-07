package com.integration.zoy.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.google.gson.annotations.SerializedName;

   
public class TenantReportDetails {

   @SerializedName("srNo")
   int srNo;

   @SerializedName("tenantName")
   String tenantName;

   @SerializedName("tenantPhone")
   String tenantPhone;

   @SerializedName("room")
   String room;

   @SerializedName("rent")
   BigDecimal rent;

   @SerializedName("dues")
   BigDecimal dues;

   @SerializedName("securityDeposit")
   BigDecimal securityDeposit;

   @SerializedName("dateOfJoining")
   Timestamp dateOfJoining;


    public void setSrNo(int srNo) {
        this.srNo = srNo;
    }
    public int getSrNo() {
        return srNo;
    }
    
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
    public String getTenantName() {
        return tenantName;
    }
    
    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }
    public String getTenantPhone() {
        return tenantPhone;
    }
    
    public void setRoom(String room) {
        this.room = room;
    }
    public String getRoom() {
        return room;
    }
    
    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }
    public BigDecimal getRent() {
        return rent;
    }
    
    public void setDues(BigDecimal dues) {
        this.dues = dues;
    }
    public BigDecimal getDues() {
        return dues;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setDateOfJoining(Timestamp dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }
    public Timestamp getDateOfJoining() {
        return dateOfJoining;
    }
    
}