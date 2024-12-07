package com.integration.zoy.exception;

public class WebServiceException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public WebServiceException(String message){
		super(message);
	}
	public WebServiceException(Exception ex,String message){
		super(message);
	}	
}
