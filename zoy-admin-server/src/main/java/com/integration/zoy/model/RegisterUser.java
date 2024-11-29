package com.integration.zoy.model;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
public class RegisterUser {
	@JsonProperty("firstName")
	private String firstName = null;

	@JsonProperty("lastName")
	private String lastName = null;

	@JsonProperty("mobile")
	private String mobile = null;

	@JsonProperty("notificationMode")
	private List<String> notificationMode = null;

	@JsonProperty("email")
	private String email = null;

	@JsonProperty("gender")
	private String gender=null;

	public RegisterUser firstName(String firstName) {
		this.firstName = firstName;
		return this;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public RegisterUser lastName(String lastName) {
		this.lastName = lastName;
		return this;
	}

	


	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public RegisterUser mobile(String mobile) {
		this.mobile = mobile;
		return this;
	}

	

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public RegisterUser notificationMode(List<String> notificationMode) {
		this.notificationMode = notificationMode;
		return this;
	}

	

	public List<String> getNotificationMode() {
		return notificationMode;
	}

	public void setNotificationMode(List<String> notificationMode) {
		this.notificationMode = notificationMode;
	}

	public RegisterUser email(String email) {
		this.email = email;
		return this;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	@Override
	public String toString() {
		return "RegisterUser [firstName=" + firstName + ", lastName=" + lastName + ", mobile=" + mobile
				+ ", notificationMode=" + notificationMode + ", email=" + email + ", gender=" + gender + "]";
	}

}
