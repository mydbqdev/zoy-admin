package com.integration.zoy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.NotificationsAndAlerts;
import com.integration.zoy.repository.NotificationsAndAlertsRepository;
import com.integration.zoy.utils.ReviewRatingDto;

@Service
public class InappNotificationService {

    @Autowired
    private NotificationWebSocketHandler notificationWebSocketHandler;

    @Autowired
    private NotificationsAndAlertsRepository notificationsAndAlertsRepository;

    private void notifyUsers(String[] userIds, String notificationMessage, String screenName, 
                             String category, String infoType) throws Exception {
        
        List<NotificationsAndAlerts> notifications = new ArrayList<>();
        for (String userId : userIds) {
            NotificationsAndAlerts notification = new NotificationsAndAlerts();
            notification.setUserId(userId);
            notification.setCategory(category);
            notification.setInfoType(infoType);
            notification.setMessage(notificationMessage);
            notification.setScreenName(screenName);
            notification.setIsSeen(false);
            notifications.add(notification);
        }

        List<NotificationsAndAlerts> savedNotifications = notificationsAndAlertsRepository.saveAll(notifications);

        for (NotificationsAndAlerts savedNotification : savedNotifications) {
            notificationWebSocketHandler.sendNotification(savedNotification.getUserId(), savedNotification);
        }
    }

    public void notifyUserApprovalRequest(String[] userIds, String approvalDetails) throws Exception {
        String notificationMessage = "Approval required for user: " + approvalDetails;
        String screenName = "ROLE_AND_PERMISSION";
        String category = "regular message";
        String infoType = "notification";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyUserRoleIsApproved(String userId,String approvalDetails) throws Exception {
        String notificationMessage = "New role : "+approvalDetails+"+ assigned to you";
        String screenName = "ROLE_AND_PERMISSION";
        String category = "System Alert";
        String infoType = "alert";
        String[] emailArray = userId.split(",");
        notifyUsers(emailArray, notificationMessage, screenName, category, infoType);
    }

    public void notifyUserAccountunlockRequest(String[] userIds, String approvalDetails) throws Exception {
        String notificationMessage = "Unlock required for user: " + approvalDetails;
        String screenName = "LOCKED_USER";
        String category = "user emergency message";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyTicketAssignedToSupportUser(String[] userIds,String notificationMessage) throws Exception {
        String screenName = "TICKETS";
        String category = "Ticket Assignment";
        String infoType = "notification";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyForRuleChange(String[] userIds, String ruleCreatedBy,String ruleName) throws Exception {
    	String notificationMessage = "Approval Request Raised for rule change in Configuration Master by " 
                + ruleCreatedBy + " for " + ruleName;      
                String screenName = "CONFIGURATION_MASTER,CONFIGURATION_MASTER_APPROVAL";
        String category = "Rule Approval Request";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyForRuleApprove(String[] userIds, String approvalDoneBy,String ruleName) throws Exception {
        String notificationMessage = "Approval Request for "+ruleName+" has been Approved by "+approvalDoneBy;
        String screenName = "CONFIGURATION_MASTER,CONFIGURATION_MASTER_APPROVAL";
        String category = "Rule Approved";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyForRuleReject(String[] userIds, String rejectionDoneBy,String ruleName) throws Exception {
        String notificationMessage = "Approval Request for "+ruleName+"has been Rejected by "+rejectionDoneBy;
        String screenName = "CONFIGURATION_MASTER,CONFIGURATION_MASTER_APPROVAL";
        String category = "Rule Approval Rejected";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
    public void notifyForRuleAutoReject(String[] userIds,String ruleName) throws Exception {
        String notificationMessage = "Approval Request for "+ruleName+"has been Auto Rejected";
        String screenName = "CONFIGURATION_MASTER,CONFIGURATION_MASTER_APPROVAL";
        String category = "Rule Approval Auto Rejected";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
    
//    @Scheduled(cron = "0 0 8 * * ?") //we need to chnage this after jira update 
//    public void sendRemainderNotificationForLeadsFollowUp() {
//    	
//    }
    
    public void notifyForReviewRating(String[] userIds, ReviewRatingDto dto) throws Exception {
    	String notificationMessage = "A review given by "+dto.getTenantName()+ " rated for " + dto.getPgName() + " with " + dto.getStar()+"stars";      
                String screenName = "CONFIGURATION_MASTER,CONFIGURATION_MASTER_APPROVAL";
        String category = "Low Review and Rating";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
}
