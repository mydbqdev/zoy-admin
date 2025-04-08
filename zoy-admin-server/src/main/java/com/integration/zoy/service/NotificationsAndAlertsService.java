package com.integration.zoy.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.NotificationsAndAlertsRepository;

@Service
public class NotificationsAndAlertsService {

	@Autowired
	private NotificationsAndAlertsRepository notificationsAndAlertsRepository;
	
    @Autowired
    private NotificationService notificationService;

    public void approveUser( String userId,  String approvalDetails) throws Exception {
    	String screenname="ROLE_AND_PERMISSION";
    	String[] emails =notificationsAndAlertsRepository.findScreenAccess(screenname);
        notificationService.notifyUserApprovalRequest(emails, approvalDetails);
    }

    public void accountlock( String userId,  String approvalDetails) throws Exception {
    	String screenname="LOCKED_USER";
    	String[] emails =notificationsAndAlertsRepository.findScreenAccess(screenname);
        notificationService.notifyUserAccountunlockRequest(emails, approvalDetails);
    }
    
    
    public void ticketAssign( String[] userId,  String ticketId) throws Exception {
        notificationService.notifyTicketAssignedToSupportUser(userId, ticketId);
    }
    

}
