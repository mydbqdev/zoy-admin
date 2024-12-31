package com.integration.zoy.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserDueMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.AuditActivitiesLogDTO;
import com.integration.zoy.model.OwnerPropertyDTO;
import com.integration.zoy.model.UserNameDTO;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.TenantDetails;

public interface UserDBImpl {

	//Notification Mode Master
	NotificationModeMaster saveNotificationMode(NotificationModeMaster notificationModeMaster);
	void deleteNotificationMode(String id);
	List<NotificationModeMaster> findAllNotificationMode();
	NotificationModeMaster findNotificationMode(String id);
	NotificationModeMaster updateNotificationMode(NotificationModeMaster notificationModeMaster);

	//User Ekyc
	UserEkycTypeMaster createEkycType(UserEkycTypeMaster ekycType);
	UserEkycTypeMaster updateEkycType(UserEkycTypeMaster ekycType);
	void deleteEkycType(String id);
	List<UserEkycTypeMaster> findAllEkycTypes();
	UserEkycTypeMaster findEkycTypes(String id);

	
	UserBillingMaster saveUserBillingMaster(UserBillingMaster master);
	List<UserBillingMaster> findAllUserBillingMaster();
	UserBillingMaster findUserBillingMaster(String billingId);
	UserBillingMaster updateUserBillingMaster(UserBillingMaster master);
	void deleteUserBillingMaster(String id);

	//User Due Master
	UserDueMaster saveUserDueMaster(UserDueMaster master);
	List<UserDueMaster> findAllUserDueMaster();
	UserDueMaster findUserDueMaster(String dueTypeId);
	UserDueMaster updateUserDueMaster(UserDueMaster master);
	void deleteUserDueMaster(String id);


	//User Currency Master
	UserCurrencyMaster saveUserCurrency(UserCurrencyMaster currency);
	void deleteUserCurrency(String id);
	UserCurrencyMaster updateUserCurrency(UserCurrencyMaster currency);
	List<UserCurrencyMaster> findAllUserCurrency();
	UserCurrencyMaster findCurrency(String currencyId);
	Page<OwnerPropertyDTO> findAllOwnerWithPropertyCount(PaginationRequest paginationRequest);
	Page<TenantDetails> findAllTenantDetails(PaginationRequest paginationRequest);
	
	//Audit Activities Log
	CommonResponseDTO<AuditActivitiesLogDTO> getAuditActivitiesLogCount(PaginationRequest paginationRequest) throws WebServiceException;
	
	//User Name List
	List<UserNameDTO> getUserNameList() throws WebServiceException;
	
	//User Audit Download
	byte[] generateDynamicReport(PaginationRequest paginationRequest)  throws WebServiceException;


}
