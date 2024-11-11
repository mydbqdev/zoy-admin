	package com.integration.zoy.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserDueMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.repository.NotificationModeMasterRepository;
import com.integration.zoy.repository.UserBillingMasterRepository;
import com.integration.zoy.repository.UserCurrencyMasterRepository;
import com.integration.zoy.repository.UserDueMasterRepository;
import com.integration.zoy.repository.UserEkycTypeMasterRepository;

@Service
public class UserDBService implements UserDBImpl{

	@Autowired
	private NotificationModeMasterRepository notificationModeMasterRepository;

	@Autowired
	private UserEkycTypeMasterRepository userEkycTypeMasterRepository;

	@Autowired
	private UserBillingMasterRepository userBillingMasterRepository;

	@Autowired
	private UserDueMasterRepository userDueMasterRepository;

	@Autowired
	private UserCurrencyMasterRepository userCurrencyMasterRepository;


	@PersistenceContext
	private EntityManager entityManager;


	//Notification Mode	
	@Override
	public NotificationModeMaster saveNotificationMode(NotificationModeMaster notificationModeMaster) {
		return notificationModeMasterRepository.save(notificationModeMaster);
	}

	@Override
	public void deleteNotificationMode(String id) {
		notificationModeMasterRepository.deleteById(id);
	}

	@Override
	public List<NotificationModeMaster> findAllNotificationMode() {
		return notificationModeMasterRepository.findAll();
	}

	@Override
	public NotificationModeMaster findNotificationMode(String id) {
		return notificationModeMasterRepository.findById(id).orElse(null);
	}

	@Override
	public NotificationModeMaster updateNotificationMode(NotificationModeMaster notificationModeMaster) {
		Optional<NotificationModeMaster> existingMode = notificationModeMasterRepository.findById(notificationModeMaster.getNotificationModeId());
		if (existingMode.isPresent()) {
			NotificationModeMaster updateMode = existingMode.get();
			updateMode.setNotificationModName(notificationModeMaster.getNotificationModName());
			return notificationModeMasterRepository.save(updateMode);
		}
		return null;
	}


	//User Ekyc
	@Override
	public UserEkycTypeMaster createEkycType(UserEkycTypeMaster ekycType) {
		return userEkycTypeMasterRepository.save(ekycType);
	}

	@Override
	public UserEkycTypeMaster updateEkycType(UserEkycTypeMaster ekycType) {
		Optional<UserEkycTypeMaster> existingType = userEkycTypeMasterRepository.findById(ekycType.getUserEkycTypeId());
		if (existingType.isPresent()) {
			UserEkycTypeMaster updatedType = existingType.get();
			updatedType.setUserEkycTypeName(ekycType.getUserEkycTypeName() != null && !ekycType.getUserEkycTypeName().trim().isEmpty()
					? ekycType.getUserEkycTypeName() : updatedType.getUserEkycTypeName());
			return userEkycTypeMasterRepository.save(updatedType);
		} else {
			throw new RuntimeException("Ekyc type not found with id: " + ekycType.getUserEkycTypeId());
		}
	}

	@Override
	public void deleteEkycType(String id) {
		userEkycTypeMasterRepository.deleteById(id);
	}

	@Override
	public List<UserEkycTypeMaster> findAllEkycTypes() {
		return userEkycTypeMasterRepository.findAll();
	}
	@Override
	public UserEkycTypeMaster findEkycTypes(String id) {
		return userEkycTypeMasterRepository.findById(id).orElse(null);
	}

	//User bIlling master
	@Override
	public UserBillingMaster saveUserBillingMaster(UserBillingMaster master) {
		return userBillingMasterRepository.save(master);
	}

	@Override
	public List<UserBillingMaster> findAllUserBillingMaster() {
		return userBillingMasterRepository.findAll();
	}

	@Override
	public UserBillingMaster findUserBillingMaster(String billingId) {
		return userBillingMasterRepository.findById(billingId).orElse(null);
	}

	@Override
	public UserBillingMaster updateUserBillingMaster(UserBillingMaster master) {
		Optional<UserBillingMaster> existingMaster = userBillingMasterRepository.findById(master.getBillingTypeId());
		if (existingMaster.isPresent()) {
			UserBillingMaster updatedMaster = existingMaster.get();
			updatedMaster.setBillingTypeName(master.getBillingTypeName() != null && !master.getBillingTypeName().trim().isEmpty() ? master.getBillingTypeName() : updatedMaster.getBillingTypeName());
			return userBillingMasterRepository.save(updatedMaster);
		} else {
			throw new RuntimeException("Billing type not found with id: " + master.getBillingTypeId());
		}
	}

	@Override
	public void deleteUserBillingMaster(String id) {
		userBillingMasterRepository.deleteById(id);
	}

	//User Due Master
	@Override
	public UserDueMaster saveUserDueMaster(UserDueMaster master) {
		return userDueMasterRepository.save(master);
	}

	@Override
	public List<UserDueMaster> findAllUserDueMaster() {
		return userDueMasterRepository.findAll();
	}

	@Override
	public UserDueMaster findUserDueMaster(String dueTypeId) {
		return userDueMasterRepository.findById(dueTypeId).orElse(null);
	}

	@Override
	public UserDueMaster updateUserDueMaster(UserDueMaster master) {
		Optional<UserDueMaster> existingMaster = userDueMasterRepository.findById(master.getDueTypeId());
		if (existingMaster.isPresent()) {
			UserDueMaster updatedMaster = existingMaster.get();
			updatedMaster.setDueTypeName(master.getDueTypeName() != null && !master.getDueTypeName().trim().isEmpty() ? master.getDueTypeName() : updatedMaster.getDueTypeName());
			return userDueMasterRepository.save(updatedMaster);
		} else {
			throw new RuntimeException("Due type not found with id: " + master.getDueTypeId());
		}
	}

	@Override
	public void deleteUserDueMaster(String id) {
		userDueMasterRepository.deleteById(id);
	}

	//User Currency 
	@Override
	public UserCurrencyMaster saveUserCurrency(UserCurrencyMaster currency) {
		return userCurrencyMasterRepository.save(currency);
	}

	@Override
	public void deleteUserCurrency(String id) {
		userCurrencyMasterRepository.deleteById(id);
	}

	@Override
	public UserCurrencyMaster updateUserCurrency(UserCurrencyMaster currency) {
		return userCurrencyMasterRepository.findById(currency.getCurrencyId())
				.map(existingCurrency -> {
					existingCurrency.setCurrencyName(currency.getCurrencyName() != null && !currency.getCurrencyName().trim().isEmpty() ? currency.getCurrencyName() : existingCurrency.getCurrencyName());
					return userCurrencyMasterRepository.save(existingCurrency);
				}).orElseThrow(() -> new RuntimeException("UserCurrencyMaster not found with id: " + currency.getCurrencyId()));
	}

	@Override
	public List<UserCurrencyMaster> findAllUserCurrency() {
		return userCurrencyMasterRepository.findAll();
	}

	@Override
	public UserCurrencyMaster findCurrency(String currencyId) {
		return userCurrencyMasterRepository.findById(currencyId).orElse(null);
	}

}
