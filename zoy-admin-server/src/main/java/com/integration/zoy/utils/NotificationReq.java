package com.integration.zoy.utils;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"title",
"body",
//"topic",
"token"
})
public class NotificationReq {
	@JsonProperty("title")
	private String title;
	@JsonProperty("body")
	private String body;
//	@JsonProperty("topic")
//	private String topic;
	@JsonProperty("token")
	private String token;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
//	public String getTopic() {
//		return topic;
//	}
//	public void setTopic(String topic) {
//		this.topic = topic;
//	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
