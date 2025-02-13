package com.integration.zoy.utils;

public class SessionInfo {
	private String token;      
	private long loginTime;
	public SessionInfo(String token, long loginTime) {
		this.token = token;
		this.loginTime = loginTime;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	} 


}
