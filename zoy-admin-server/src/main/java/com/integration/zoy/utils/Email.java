package com.integration.zoy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Email
 */
@Validated
//@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-06-12T04:02:37.857381730Z[GMT]")


public class Email   {
	@JsonProperty("from")
	private String from = null;

	@JsonProperty("to")
	private List<String> to = null;

	@JsonProperty("subject")
	private String subject = null;

	@JsonProperty("body")
	private String body = null;

	@JsonProperty("content")
	private String content = null;

	public Email from(String from) {
		this.from = from;
		return this;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Email to(List<String> to) {
		this.to = to;
		return this;
	}

	public Email addToItem(String toItem) {
		if (this.to == null) {
			this.to = new ArrayList<String>();
		}
		this.to.add(toItem);
		return this;
	}


	public List<String> getTo() {
		return to;
	}

	public void setTo(List<String> to) {
		this.to = to;
	}

	public Email subject(String subject) {
		this.subject = subject;
		return this;
	}


	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Email body(String body) {
		this.body = body;
		return this;
	}


	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Email content(String content) {
		this.content = content;
		return this;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Email email = (Email) o;
		return Objects.equals(this.from, email.from) &&
				Objects.equals(this.to, email.to) &&
				Objects.equals(this.subject, email.subject) &&
				Objects.equals(this.body, email.body) &&
				Objects.equals(this.content, email.content);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to, subject, body, content);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Email {\n");

		sb.append("    from: ").append(toIndentedString(from)).append("\n");
		sb.append("    to: ").append(toIndentedString(to)).append("\n");
		sb.append("    subject: ").append(toIndentedString(subject)).append("\n");
		sb.append("    body: ").append(toIndentedString(body)).append("\n");
		sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
