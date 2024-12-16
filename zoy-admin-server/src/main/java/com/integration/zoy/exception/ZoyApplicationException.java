package com.integration.zoy.exception;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.id.IdentifierGenerationException;



public class ZoyApplicationException {
	private String msg;
	
	public ZoyApplicationException(Exception ex ,String type) throws WebServiceException{
		String decodedPath = System.getProperty("user.dir") + "/";
		Properties props = new Properties();
		
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(Paths.get(decodedPath + "input/app.properties").toFile()), "UTF-8");
			props.load(in);
		} catch (IOException e) {
			throw new WebServiceException("Something went wrong, Please try after sometimes");
		}
		
		if(ex.getCause() != null && ex.getCause() instanceof ConstraintViolationException) {
			ConstraintViolationException se = (ConstraintViolationException)ex.getCause();
			//System.out.println("msg:se.getErrorCode() :"+se.getErrorCode() );
			//System.out.println("msg:se.getLocalizedMessage()() :"+se.getCause().getLocalizedMessage());
			//System.out.println("msg:se.getConstraintName()() :"+se.getConstraintName());
			if(se.getErrorCode() == 1062) {
	        	msg=String.valueOf(se.getCause().getLocalizedMessage()).replaceAll(se.getConstraintName(),props.getProperty(se.getConstraintName()));
	        	throw new WebServiceException(msg);
	        }else if(se.getErrorCode() == 1452) {
	        	msg="Invalid or missing the reference column data with parent record ";
	        	throw new WebServiceException(msg);
	        }else if(se.getErrorCode() == 1048) {

	        	msg="Data can not be blank or null,Please verify it.";
	        	throw new WebServiceException(msg);
	        }else{
	            
	        	msg="Something went wrong, Please try again";
	        	throw new WebServiceException(msg);
	        }
	    }else if(ex.getCause() != null && ex.getCause() instanceof IdentifierGenerationException) {	       
            
        	msg="Identifier or primary column data is missing, Please verify it.";
        	throw new WebServiceException(msg);
       }else if(ex.getCause() != null && ex.getCause() instanceof SQLException) {
	            //SQLException se = (SQLException)ex.getCause();	       
	            
	        	msg="Some problem with database, Please try again";
	        	throw new WebServiceException(msg);
	    }else if(ex.getCause() != null && ex.getCause() instanceof NullPointerException) {
	        	throw new WebServiceException("Some content returns null or blank value, Please verify it.");
	    }else if(type!=null && !"".equalsIgnoreCase(type)){
	    	throw new WebServiceException(type);
	    }else{
	         // handled exceptions
	    	msg="Something went wrong, Please try again";
	    	throw new WebServiceException(msg);
	    }
		
		
	}
	
}
