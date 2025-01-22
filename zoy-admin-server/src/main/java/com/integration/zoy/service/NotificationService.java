package com.integration.zoy.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.NotificationsAndAlerts;
import com.integration.zoy.repository.NotificationsAndAlertsRepository;

@Service
public class NotificationService {

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

    public void notifyUserAccountunlockRequest(String[] userIds, String approvalDetails) throws Exception {
        String notificationMessage = "Unlock required for user: " + approvalDetails;
        String screenName = "LOCKED_USER";
        String category = "user emergency message";
        String infoType = "alert";
        notifyUsers(userIds, notificationMessage, screenName, category, infoType);
    }
}
