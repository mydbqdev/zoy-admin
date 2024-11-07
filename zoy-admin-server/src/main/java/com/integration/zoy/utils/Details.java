package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;
   
public class Details {

   @SerializedName("result")
   Result result;

   @SerializedName("razorpay_signature")
   String razorpaySignature;

   @SerializedName("razorpay_order_id")
   String razorpayOrderId;

   @SerializedName("razorpay_payment_id")
   String razorpayPaymentId;


    public void setResult(Result result) {
        this.result = result;
    }
    public Result getResult() {
        return result;
    }
    
    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }
    public String getRazorpaySignature() {
        return razorpaySignature;
    }
    
    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }
    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }
    
    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }
    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }
    
}