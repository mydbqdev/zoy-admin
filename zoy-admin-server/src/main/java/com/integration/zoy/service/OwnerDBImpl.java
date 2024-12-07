package com.integration.zoy.service;

import java.util.List;
import java.util.Optional;

import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgDueMaster;
import com.integration.zoy.entity.ZoyPgDueTypeMaster;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.entity.ZoyPgPropertyDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgSecurityDepositRefundRule;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgTermsMaster;
import com.integration.zoy.entity.ZoyPgTimeMaster;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.entity.ZoyShareMaster;

public interface OwnerDBImpl {

	ZoyPgTypeMaster createPgType(ZoyPgTypeMaster pgType);
	ZoyPgTypeMaster getPgTypeById(String id);
	List<ZoyPgTypeMaster> getAllPgTypes();
	ZoyPgTypeMaster updatePgType( ZoyPgTypeMaster pgType);
	void deletePgType(String id);

	//Pg share
	ZoyPgShareMaster createShare(ZoyPgShareMaster share);
	ZoyPgShareMaster updateShare(ZoyPgShareMaster share);
	void deleteShare(String shareId);
	List<ZoyPgShareMaster> getAllShares();
	ZoyPgShareMaster getShareById(String shareId);
	String getShareIdByShareType(String ShareType);

	//Pg Aminieties
	ZoyPgAmenetiesMaster createAmeneties(ZoyPgAmenetiesMaster ameneties);
	List<ZoyPgAmenetiesMaster> createAllAmeneties(List<ZoyPgAmenetiesMaster> ameneties);
	List<ZoyPgAmenetiesMaster> getAllAmeneties();
	ZoyPgAmenetiesMaster updateAmeneties(String id, ZoyPgAmenetiesMaster ameneties);
	ZoyPgAmenetiesMaster findAmeneties(String id);
	void deleteAmeneties(String id);
	List<String>  getIdsOfByAmenitiesList(List<String> amenities);

	ZoyPgRoomTypeMaster createRoomType(ZoyPgRoomTypeMaster roomType);
	void deleteRoomType(String roomTypeId);
	ZoyPgRoomTypeMaster updateRoomType(ZoyPgRoomTypeMaster roomType);
	List<ZoyPgRoomTypeMaster> getAllRoomTypes();
	String getRoomTypeIdByRoomType(String roomTypeName);
	ZoyPgRoomTypeMaster findRoomType(String id);

	ZoyPgDueFactorMaster createDueFactor(ZoyPgDueFactorMaster dueFactor);
	void deleteDueFactor(String factorId);
	ZoyPgDueFactorMaster updateDueFactor(ZoyPgDueFactorMaster dueFactor);
	List<ZoyPgDueFactorMaster> getAllDueFactors();
	String  getFactorIdbyFactorName(String factorName);
	ZoyPgDueFactorMaster findDueFactor(String id);

	//Pg due master
	ZoyPgDueTypeMaster createDueType(ZoyPgDueTypeMaster dueType);
	List<ZoyPgDueTypeMaster> createAllDueType(List<ZoyPgDueTypeMaster> dueType);
	void deleteDueType(String dueId);
	ZoyPgDueTypeMaster updateDueType(ZoyPgDueTypeMaster dueType);
	List<ZoyPgDueTypeMaster> getAllDueTypes();

	//Pg Rent cycle
	ZoyPgRentCycleMaster saveRentCycle(ZoyPgRentCycleMaster rentCycle);
	ZoyPgRentCycleMaster updateRentCycle(ZoyPgRentCycleMaster rentCycle);
	void deleteRentCycle(String cycleId);
	List<ZoyPgRentCycleMaster> getAllRentCycle();
	List<String[]> findRentCycleName(String propertyId);
	ZoyPgRentCycleMaster findRentCycle(String rentCycelId);

	//Pg Time
	ZoyPgTimeMaster saveTime(ZoyPgTimeMaster time);
	void deleteTime(String timeId);
	ZoyPgTimeMaster updateTime(ZoyPgTimeMaster time);
	List<ZoyPgTimeMaster> getAllTime();
	List<String[]> getOwnerPropertyDetails();
	ZoyPgPropertyFloorDetails findFloorDetails(String propertyId, String floorName);
	ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName);
	List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName);
	ZoyPgRentCycleMaster findRentCycleName(String propertyId, String rentCycle);
	ZoyPgOwnerDetails savePgOwner(ZoyPgOwnerDetails zoyPgOwnerDetails);
	List<String> getNameOfByAmenitiesList(List<String> excelAmeneties);
	ZoyPgPropertyDetails getPropertyById(String propertyId);
	ZoyPgOwnerDetails findPgOwnerById(String ownerId);
	ZoyPgOwnerBookingDetails getBookingDetails(String userBookingsId);
	ZoyPgBedDetails getBedsId(String selectedBed);
	List<String> findPropertyAmenetiesName(String propertyId);
	ZoyPgTermsMaster findTermMaster(String propertyId);
	ZoyPgTokenDetails findTokenDetails();
	ZoyPgTokenDetails saveToken(ZoyPgTokenDetails tokenDetails);
	List<ZoyPgTokenDetails> findAllToken();
	List<ZoyPgDueMaster> findAllDueMaster();
	ZoyPgDueMaster saveUserDueMaster(ZoyPgDueMaster userDueMasters);
	ZoyPgDueMaster findUserDueMaster(String id);
	ZoyPgDueMaster updateDueMaster(ZoyPgDueMaster userDueMasters);
	ZoyPgCancellationDetails findBeforeCancellationDetails(String cancellationId);
	ZoyPgCancellationDetails saveBeforeCancellation(ZoyPgCancellationDetails cancelDetails);
	List<ZoyPgCancellationDetails> findAllBeforeCancellation();
	ZoyShareMaster findZoyShareDetails(String zoyShareId);
	ZoyShareMaster saveZoyShare(ZoyShareMaster shareDetails);
	List<ZoyShareMaster> findAllZoyShare();
	ZoyPgOtherCharges findZoyOtherCharges();
	ZoyPgOtherCharges saveOtherCharges(ZoyPgOtherCharges other);
	ZoyDataGrouping findZoyDataGroup();
	ZoyDataGrouping saveDataGroup(ZoyDataGrouping group);
	List<ZoyDataGrouping> findAllDataGroup();
	ZoyPgSecurityDepositDetails findZoySecurityDeposit();
	ZoyPgSecurityDepositDetails saveZoySecurityDepositLimits(ZoyPgSecurityDepositDetails group);
	ZoyPgSecurityDepositRefundRule findSecurityDepositRefundRuleById();
	ZoyPgSecurityDepositRefundRule saveSecurityDepositRefundRule(ZoyPgSecurityDepositRefundRule rule);
}