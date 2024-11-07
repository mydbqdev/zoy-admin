package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class ResponseBody {
	@SerializedName("status")
	private int status;

	@SerializedName("message")
	private String message;

	@SerializedName("error")
	private String error;

	@SerializedName("data")
	private Object data;

	@SerializedName("details")
	private String details;

	@SerializedName("bedId")
	private String bedId;

	@SerializedName("api_key")
	private String apiKey;

	@SerializedName("imageUrl")
	private String imageUrl;

	@SerializedName("result")
	private Boolean result;

	@SerializedName("selected_city")
	private String selectedCity;

	@SerializedName("accountId")
	private String accountId;

	@SerializedName("token")
	private String token;

	@SerializedName("success")
	private Boolean success;

	@SerializedName("ownerId")
	private String ownerId;


	@SerializedName("otp")
	private String otp;
	
	@SerializedName("docId")
	private String docId;
	
	@SerializedName("totalDuesAmount")
	private Long totalDuesAmount;
   
	
	

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Long getTotalDuesAmount() {
		return totalDuesAmount;
	}

	public void setTotalDuesAmount(Long totalDuesAmount) {
		this.totalDuesAmount = totalDuesAmount;
	}

	public String getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(String selectedCity) {
		this.selectedCity = selectedCity;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBedId() {
		return bedId;
	}

	public void setBedId(String bedId) {
		this.bedId = bedId;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Boolean getResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}



}


