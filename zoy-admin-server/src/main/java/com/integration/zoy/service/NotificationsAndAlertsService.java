package com.integration.zoy.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.NotificationsAndAlertsRepository;
import com.integration.zoy.utils.ReviewRatingDto;

@Service
public class NotificationsAndAlertsService {

	@Autowired
	private NotificationsAndAlertsRepository notificationsAndAlertsRepository;
	
    @Autowired
    private InappNotificationService notificationService;

    public void approveUser( String userId,  String approvalDetails) throws Exception {
    	String screenname="ROLE_AND_PERMISSION";
    	String[] emails =notificationsAndAlertsRepository.findScreenAccess(screenname);
        notificationService.notifyUserApprovalRequest(emails, approvalDetails);
    }

    public void roleAssigned( String userId,String notificationDetails) throws Exception {
        notificationService.notifyUserRoleIsApproved(userId,notificationDetails);
    }
    
    public void accountlock( String userId,  String approvalDetails) throws Exception {
    	String screenname="LOCKED_USER";
    	String[] emails =notificationsAndAlertsRepository.findScreenAccess(screenname);
        notificationService.notifyUserAccountunlockRequest(emails, approvalDetails);
    }
    
    
    public void ticketAssign( String[] userId, String notificationMessage) throws Exception {
        notificationService.notifyTicketAssignedToSupportUser(userId,notificationMessage);
    }
    
    public void masterConfigurationRulechange(String ruleCreatedBy, String ruleName) throws Exception {
        List<String> screens = Arrays.asList("CONFIGURATION_MASTER", "CONFIGURATION_MASTER_APPROVAL");
        String[] emails = notificationsAndAlertsRepository.findUsersWithScreenAccess(screens);
        notificationService.notifyForRuleChange(emails, ruleCreatedBy,ruleName);
    }

    public void masterConfigurationRuleApproval( String approvalDoneBy,String ruleName) throws Exception {
        List<String> screens = Arrays.asList("CONFIGURATION_MASTER", "CONFIGURATION_MASTER_APPROVAL");
        String[] emails = notificationsAndAlertsRepository.findUsersWithScreenAccess(screens);
        notificationService.notifyForRuleApprove(emails, approvalDoneBy,ruleName);
    } 
    
    public void masterConfigurationRuleRejection(String rejectionDoneBy,String ruleName) throws Exception {
        List<String> screens = Arrays.asList("CONFIGURATION_MASTER", "CONFIGURATION_MASTER_APPROVAL");
        String[] emails = notificationsAndAlertsRepository.findUsersWithScreenAccess(screens);
        notificationService.notifyForRuleReject(emails, rejectionDoneBy,ruleName);
    }
    
    public void masterConfigurationRuleRejection(String ruleName) throws Exception {
        List<String> screens = Arrays.asList("CONFIGURATION_MASTER", "CONFIGURATION_MASTER_APPROVAL");
        String[] emails = notificationsAndAlertsRepository.findUsersWithScreenAccess(screens);
        notificationService.notifyForRuleAutoReject(emails,ruleName);
    }
    
    public void reviewRatingAlert(ReviewRatingDto dto) throws Exception {
        String screenname="LOW_RATINGS";
    	String[] emails =notificationsAndAlertsRepository.findScreenAccess(screenname);
        notificationService.notifyForReviewRating(emails, dto);
    }
}
