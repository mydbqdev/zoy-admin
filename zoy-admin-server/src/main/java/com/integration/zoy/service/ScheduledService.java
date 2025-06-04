package com.integration.zoy.service;



import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.repository.UserHelpRequestRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;

@Service
public class ScheduledService {
	private static final Logger log=LoggerFactory.getLogger(ScheduledService.class);
	
	@Autowired
	private PasswordChangeWarningMailSchedulerService passwordChangeWarningMailScheduler; 
	
	@Value("${spring.jackson.time-zone}")
	private String timeZon;
	
	@Autowired
    private AutoCancellationOfMasterConfiguration autoCancellationOfMasterConfi;
	
	@Autowired
	private ZoyEmailService zoyEmailService;
	
	@Autowired
    private UserHelpRequestRepository userHelpRequestRepo;
    
    
    @Autowired
    private ZoyPgOwnerDetailsRepository zoyPgOwnerDetailsRepo ;
	
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
	
    @Transactional
	@Scheduled(cron = "${auto.cancellation.token.advance.cron}", zone = "${spring.jackson.time-zone}")
	public void autoCancellationForMasterConfiguration() {
	    try {
	        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        format.setTimeZone(TimeZone.getTimeZone(timeZon));
	        Date date = new Date();
	        String dateTime = format.format(date);
	        autoCancellationOfMasterConfi.autoCancellationOfMaterConfiguration();
	        log.info("Auto cancellation for master configuraton executed at: " + dateTime);
	    } catch (Exception e) {
	        log.error("Error in autoCanceForTokenAdvance(): ", e);
	    }
	}

//    @Scheduled(cron = "${rule.intimation.effective.date.cron}", zone = "${spring.jackson.time-zone}")
//    @Transactional
    public void sendRuleEffectiveDateNotifications() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format.setTimeZone(TimeZone.getTimeZone(timeZon)); // Ensure this is configured
            String dateTime = format.format(new Date());

            autoCancellationOfMasterConfi.sendForceCheckoutRuleEffective();
            autoCancellationOfMasterConfi.sendNoRentalAgreementRuleEffective();
            autoCancellationOfMasterConfi.sendTokenAdvanceRuleEffective();
            autoCancellationOfMasterConfi.sendSecurityDepositRuleEffective();
            autoCancellationOfMasterConfi.sendOtherChargersRuleEffective();
            autoCancellationOfMasterConfi.sendAfterCheckInDateRuleEffective();
            autoCancellationOfMasterConfi.sendEarlyCheckOutRuleEffective();
            autoCancellationOfMasterConfi.sendSecurityDepositDeadlineRuleEffective();
            autoCancellationOfMasterConfi.sendShortTermRuleEffective();
            
            log.info("Auto cancellation for master configuration executed at: " + dateTime);
        } catch (Exception e) {
            log.error("Error in autoCanceForTokenAdvance(): ", e);
        }
    }


    @Transactional
    @Scheduled(cron = "${support.ticket.status.change.cron}", zone = "${spring.jackson.time-zone}")
    public void closeResolvedUserHelpRequests() {
        try {
            ZoneId zoneId = ZoneId.of(timeZon);
            LocalDateTime now = LocalDateTime.now(zoneId);
            String timestampString = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            zoyPgOwnerDetailsRepo.updateResolvedRequestsToClosedAfter48Hours(timestampString);
            userHelpRequestRepo.updateResolvedRequestsToClosedAfter48Hours( timestampString);  	

            log.info("Closed resolved user help requests older than 48 hours.");
        } catch (Exception e) {
            log.error("Error closing resolved user help requests: ", e);
        }
    }
	
}