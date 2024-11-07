package com.integration.zoy.utils;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.annotation.Validated;

/**
 * Sms
 */
@Validated
//@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-06-12T04:02:37.857381730Z[GMT]")


public class Sms   {
	@JsonProperty("to")
	private String to = null;

	@JsonProperty("from")
	private String from = null;

	@JsonProperty("message")
	private String message = null;

	@JsonProperty("templateid")
	private String templateid = null;

	@JsonProperty("params")
	private Map<String, String> params = null;

	public Sms to(String to) {
		this.to = to;
		return this;
	}


	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Sms from(String from) {
		this.from = from;
		return this;
	}


	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Sms message(String message) {
		this.message = message;
		return this;
	}


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Sms templateid(String templateid) {
		this.templateid = templateid;
		return this;
	}


	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public Sms params(Map<String, String> params) {
		this.params = params;
		return this;
	}

	public Sms putParamsItem(String key, String paramsItem) {
		if (this.params == null) {
			this.params = new HashMap<String, String>();
		}
		this.params.put(key, paramsItem);
		return this;
	}


	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Sms sms = (Sms) o;
		return Objects.equals(this.to, sms.to) &&
				Objects.equals(this.from, sms.from) &&
				Objects.equals(this.message, sms.message) &&
				Objects.equals(this.templateid, sms.templateid) &&
				Objects.equals(this.params, sms.params);
	}

	@Override
	public int hashCode() {
		return Objects.hash(to, from, message, templateid, params);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Sms {\n");

		sb.append("    to: ").append(toIndentedString(to)).append("\n");
		sb.append("    from: ").append(toIndentedString(from)).append("\n");
		sb.append("    message: ").append(toIndentedString(message)).append("\n");
		sb.append("    templateid: ").append(toIndentedString(templateid)).append("\n");
		sb.append("    params: ").append(toIndentedString(params)).append("\n");
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
