/**
 * 
 */
package com.integration.zoy.utils;

/**
 * @author PraveenRamesh
 *
 */
import java.util.List;

import com.google.gson.annotations.SerializedName;


public class InvoiceData {

	@SerializedName("manager_name")
	String managerName;

	@SerializedName("manager_contact_number")
	String managerContactNumber;

	@SerializedName("property_name")
	String propertyName;

	@SerializedName("address")
	String address;

	@SerializedName("gst_number")
	String gstNumber;

	@SerializedName("payee_name")
	String payeeName;

	@SerializedName("contact_number")
	String contactNumber;

	@SerializedName("room_bed")
	String roomBed;

	@SerializedName("dueType")
	String dueType;

	@SerializedName("occupancy")
	String occupancy;

	@SerializedName("payment_details")
	List<PaymentDetails> paymentDetails;

	@SerializedName("invoice_time_stamp")
	String invoiceTimeStamp;

	@SerializedName("mode_of_payment")
	String modeOfPayment;

	@SerializedName("amount_paid_by_tenant")
	double amountPaidByTenant;

	@SerializedName("invoice_number")
	String invoiceNumber;

	@SerializedName("image")
	String base64Image;
	
	String numberInWords;



	public String getNumberInWords() {
		return numberInWords;
	}
	public void setNumberInWords(String numberInWords) {
		this.numberInWords = numberInWords;
	}
	public String getBase64Image() {
		return base64Image;
	}
	public void setBase64Image(String base64Image) {
		this.base64Image = base64Image;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getManagerName() {
		return managerName;
	}

	public void setManagerContactNumber(String managerContactNumber) {
		this.managerContactNumber = managerContactNumber;
	}
	public String getManagerContactNumber() {
		return managerContactNumber;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public String getPropertyName() {
		return propertyName;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddress() {
		return address;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}
	public String getGstNumber() {
		return gstNumber;
	}

	public void setPayeeName(String payeeName) {
		this.payeeName = payeeName;
	}
	public String getPayeeName() {
		return payeeName;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getContactNumber() {
		return contactNumber;
	}

	public void setRoomBed(String roomBed) {
		this.roomBed = roomBed;
	}
	public String getRoomBed() {
		return roomBed;
	}

	public void setDueType(String dueType) {
		this.dueType = dueType;
	}
	public String getDueType() {
		return dueType;
	}

	public void setOccupancy(String occupancy) {
		this.occupancy = occupancy;
	}
	public String getOccupancy() {
		return occupancy;
	}

	public void setPaymentDetails(List<PaymentDetails> paymentDetails) {
		this.paymentDetails = paymentDetails;
	}
	public List<PaymentDetails> getPaymentDetails() {
		return paymentDetails;
	}

	public void setInvoiceTimeStamp(String invoiceTimeStamp) {
		this.invoiceTimeStamp = invoiceTimeStamp;
	}
	public String getInvoiceTimeStamp() {
		return invoiceTimeStamp;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setAmountPaidByTenant(double amountPaidByTenant) {
		this.amountPaidByTenant = amountPaidByTenant;
	}
	public double getAmountPaidByTenant() {
		return amountPaidByTenant;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

}
