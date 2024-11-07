/**
 * 
 */
package com.integration.zoy.utils;

/**
 * @author PraveenRamesh
 *
 */
import com.google.gson.annotations.SerializedName;


public class PaymentDetails {

   @SerializedName("payment_type")
   String paymentType;

   @SerializedName("remarks")
   String remarks;

   @SerializedName("paid_amount")
   double paidAmount;

   @SerializedName("dues_amount")
   double duesAmount;

   @SerializedName("net_balance")
   double netBalance;
   

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
    public String getPaymentType() {
        return paymentType;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
    public String getRemarks() {
        return remarks;
    }
    
    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }
    public double getPaidAmount() {
        return paidAmount;
    }
    
    public void setDuesAmount(double duesAmount) {
        this.duesAmount = duesAmount;
    }
    public double getDuesAmount() {
        return duesAmount;
    }
    
    public void setNetBalance(double netBalance) {
        this.netBalance = netBalance;
    }
    public double getNetBalance() {
        return netBalance;
    }
    
}