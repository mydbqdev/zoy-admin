package com.integration.zoy.utils;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

/**
 * NotificationRequest
 */
@Validated
//@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-06-12T04:02:37.857381730Z[GMT]")


public class NotificationRequest   {
	@JsonProperty("processType")
	private String processType = null;

	@JsonProperty("appName")
	private String appName = null;

	@JsonProperty("unixTimeStamp")
	private Long unixTimeStamp = null;

	@JsonProperty("recurrence")
	private RecurrenceInfo recurrence = null;

	@JsonProperty("messageType")
	private String messageType = null;

	@JsonProperty("email")
	private Email email = null;

	@JsonProperty("sms")
	private Sms sms = null;

	@JsonProperty("whatsapp")
	private Whatsapp whatsapp = null;

	public NotificationRequest processType(String processType) {
		this.processType = processType;
		return this;
	}


	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public NotificationRequest appName(String appName) {
		this.appName = appName;
		return this;
	}


	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public NotificationRequest unixTimeStamp(Long unixTimeStamp) {
		this.unixTimeStamp = unixTimeStamp;
		return this;
	}


	public Long getUnixTimeStamp() {
		return unixTimeStamp;
	}

	public void setUnixTimeStamp(Long unixTimeStamp) {
		this.unixTimeStamp = unixTimeStamp;
	}

	public NotificationRequest recurrence(RecurrenceInfo recurrence) {
		this.recurrence = recurrence;
		return this;
	}


	public RecurrenceInfo getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(RecurrenceInfo recurrence) {
		this.recurrence = recurrence;
	}

	public NotificationRequest messageType(String messageType) {
		this.messageType = messageType;
		return this;
	}


	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public NotificationRequest email(Email email) {
		this.email = email;
		return this;
	}

	public Email getEmail() {
		return email;
	}

	public void setEmail(Email email) {
		this.email = email;
	}

	public NotificationRequest sms(Sms sms) {
		this.sms = sms;
		return this;
	}

	public Sms getSms() {
		return sms;
	}

	public void setSms(Sms sms) {
		this.sms = sms;
	}

	public NotificationRequest whatsapp(Whatsapp whatsapp) {
		this.whatsapp = whatsapp;
		return this;
	}

	public Whatsapp getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(Whatsapp whatsapp) {
		this.whatsapp = whatsapp;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		NotificationRequest notificationRequest = (NotificationRequest) o;
		return Objects.equals(this.processType, notificationRequest.processType) &&
				Objects.equals(this.appName, notificationRequest.appName) &&
				Objects.equals(this.unixTimeStamp, notificationRequest.unixTimeStamp) &&
				Objects.equals(this.recurrence, notificationRequest.recurrence) &&
				Objects.equals(this.messageType, notificationRequest.messageType) &&
				Objects.equals(this.email, notificationRequest.email) &&
				Objects.equals(this.sms, notificationRequest.sms) &&
				Objects.equals(this.whatsapp, notificationRequest.whatsapp);
	}

	@Override
	public int hashCode() {
		return Objects.hash(processType, appName, unixTimeStamp, recurrence, messageType, email, sms, whatsapp);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class NotificationRequest {\n");

		sb.append("    processType: ").append(toIndentedString(processType)).append("\n");
		sb.append("    appName: ").append(toIndentedString(appName)).append("\n");
		sb.append("    unixTimeStamp: ").append(toIndentedString(unixTimeStamp)).append("\n");
		sb.append("    recurrence: ").append(toIndentedString(recurrence)).append("\n");
		sb.append("    messageType: ").append(toIndentedString(messageType)).append("\n");
		sb.append("    email: ").append(toIndentedString(email)).append("\n");
		sb.append("    sms: ").append(toIndentedString(sms)).append("\n");
		sb.append("    whatsapp: ").append(toIndentedString(whatsapp)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
