package com.integration.zoy.service;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
	private static final Logger log=LoggerFactory.getLogger(ScheduledService.class);
	
	@Autowired
	private PasswordChangeWarningMailSchedulerService passwordChangeWarningMailScheduler; 
	
	@Value("${spring.jackson.time-zone}")
	private String timeZon;
	
	
	/**
	 * passwordChangeWarMails execute based on schedule date & time. it will send password change warning email every 39-45 days
	 */
	@Scheduled(cron="${password.change.war.mails}", zone="${spring.jackson.time-zone}")
    public void passwordChangeWarMails() {
		try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 format.setTimeZone(TimeZone.getTimeZone(timeZon));
				 Date date = new Date();
				 SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 Date dateTime= dateParser.parse(format.format(date));
				passwordChangeWarningMailScheduler.passwordChangeWarner();
				log.info("Password change remainder email date and time "+dateTime);
		} catch (Exception e) {
			log.error("Error in passwordChangeWarningMailScheduler(): "+ e);
		}
    }
	

	
}