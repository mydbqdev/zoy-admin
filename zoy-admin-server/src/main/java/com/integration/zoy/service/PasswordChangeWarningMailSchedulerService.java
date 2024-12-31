package com.integration.zoy.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.AdminUserLoginDetailsRepository;



@Service
public class PasswordChangeWarningMailSchedulerService {
	
	@Autowired
	private AdminUserLoginDetailsRepository adminUserLoginDetailsRepository;


	@Autowired
	ZoyEmailService zoyEmailService;
	
	
	@Value("${spring.jackson.time-zone}")
	private String currentTimeZone;
	

	public void passwordChangeWarner() throws ParseException {
		try {
		
			 TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
		     Calendar calendar = Calendar.getInstance(timeZone);
		     long currentTimeMillis = calendar.getTimeInMillis();
		     Timestamp currentTimestamp = new Timestamp(currentTimeMillis);
		    
		     adminUserLoginDetailsRepository.deleteDataInAdminUsersLockDetails(currentTimestamp);
			 adminUserLoginDetailsRepository.doLockAfterPWExpired(currentTimestamp);
			 List<String[]> list = adminUserLoginDetailsRepository.getPasswordWarnerMailsData(currentTimestamp);
				for(String[] str : list) {
					zoyEmailService.sendPasswordChangeWarningMails(str);
			    }		
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
}


