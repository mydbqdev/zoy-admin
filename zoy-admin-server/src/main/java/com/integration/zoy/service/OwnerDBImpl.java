package com.integration.zoy.service;

import java.util.List;

import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgAutoCancellationPeriod;
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
import com.integration.zoy.exception.WebServiceException;

public interface OwnerDBImpl {

	ZoyPgTypeMaster createPgType(ZoyPgTypeMaster pgType)throws WebServiceException;
	ZoyPgTypeMaster getPgTypeById(String id)throws WebServiceException;
	List<ZoyPgTypeMaster> getAllPgTypes()throws WebServiceException;
	ZoyPgTypeMaster updatePgType( ZoyPgTypeMaster pgType)throws WebServiceException;
	void deletePgType(String id)throws WebServiceException;

	//Pg share
	ZoyPgShareMaster createShare(ZoyPgShareMaster share) throws WebServiceException;
	ZoyPgShareMaster updateShare(ZoyPgShareMaster share)throws WebServiceException;
	void deleteShare(String shareId)throws WebServiceException;
	List<ZoyPgShareMaster> getAllShares()throws WebServiceException;
	ZoyPgShareMaster getShareById(String shareId)throws WebServiceException;
	String getShareIdByShareType(String ShareType)throws WebServiceException;

	//Pg Aminieties
	ZoyPgAmenetiesMaster createAmeneties(ZoyPgAmenetiesMaster ameneties) throws WebServiceException;
	List<ZoyPgAmenetiesMaster> createAllAmeneties(List<ZoyPgAmenetiesMaster> ameneties)throws WebServiceException;
	List<ZoyPgAmenetiesMaster> getAllAmeneties()throws WebServiceException;
	ZoyPgAmenetiesMaster updateAmeneties(String id, ZoyPgAmenetiesMaster ameneties)throws WebServiceException;
	ZoyPgAmenetiesMaster findAmeneties(String id) throws WebServiceException;
	void deleteAmeneties(String id)throws WebServiceException;
	List<String>  getIdsOfByAmenitiesList(List<String> amenities)throws WebServiceException;

	ZoyPgRoomTypeMaster createRoomType(ZoyPgRoomTypeMaster roomType)throws WebServiceException;
	void deleteRoomType(String roomTypeId)throws WebServiceException;
	ZoyPgRoomTypeMaster updateRoomType(ZoyPgRoomTypeMaster roomType)throws WebServiceException;
	List<ZoyPgRoomTypeMaster> getAllRoomTypes()throws WebServiceException;
	String getRoomTypeIdByRoomType(String roomTypeName)throws WebServiceException;
	ZoyPgRoomTypeMaster findRoomType(String id);

	ZoyPgDueFactorMaster createDueFactor(ZoyPgDueFactorMaster dueFactor)throws WebServiceException;
	void deleteDueFactor(String factorId)throws WebServiceException;
	ZoyPgDueFactorMaster updateDueFactor(ZoyPgDueFactorMaster dueFactor)throws WebServiceException;
	List<ZoyPgDueFactorMaster> getAllDueFactors()throws WebServiceException;
	String  getFactorIdbyFactorName(String factorName)throws WebServiceException;
	ZoyPgDueFactorMaster findDueFactor(String id)throws WebServiceException;

	//Pg due master
	ZoyPgDueTypeMaster createDueType(ZoyPgDueTypeMaster dueType)throws WebServiceException;
	List<ZoyPgDueTypeMaster> createAllDueType(List<ZoyPgDueTypeMaster> dueType)throws WebServiceException;
	void deleteDueType(String dueId)throws WebServiceException;
	ZoyPgDueTypeMaster updateDueType(ZoyPgDueTypeMaster dueType)throws WebServiceException;
	List<ZoyPgDueTypeMaster> getAllDueTypes()throws WebServiceException;

	//Pg Rent cycle
	ZoyPgRentCycleMaster saveRentCycle(ZoyPgRentCycleMaster rentCycle)throws WebServiceException;
	ZoyPgRentCycleMaster updateRentCycle(ZoyPgRentCycleMaster rentCycle)throws WebServiceException;
	void deleteRentCycle(String cycleId)throws WebServiceException;
	List<ZoyPgRentCycleMaster> getAllRentCycle()throws WebServiceException;
	List<String[]> findRentCycleName(String propertyId)throws WebServiceException;
	ZoyPgRentCycleMaster findRentCycle(String rentCycelId)throws WebServiceException;
	String findRentCycleByName(String rentCycelName)throws WebServiceException;
	//Pg Time
	ZoyPgTimeMaster saveTime(ZoyPgTimeMaster time)throws WebServiceException;
	void deleteTime(String timeId)throws WebServiceException;
	ZoyPgTimeMaster updateTime(ZoyPgTimeMaster time)throws WebServiceException;
	List<ZoyPgTimeMaster> getAllTime()throws WebServiceException;
	List<String[]> getOwnerPropertyDetails() throws WebServiceException;
	ZoyPgPropertyFloorDetails findFloorDetails(String propertyId, String floorName)throws WebServiceException;
	ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName)throws WebServiceException;
	List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName)throws WebServiceException;
	ZoyPgRentCycleMaster findRentCycleName(String propertyId, String rentCycle)throws WebServiceException;
	ZoyPgOwnerDetails savePgOwner(ZoyPgOwnerDetails zoyPgOwnerDetails)throws WebServiceException;
	List<String> getNameOfByAmenitiesList(List<String> excelAmeneties)throws WebServiceException;
	ZoyPgPropertyDetails getPropertyById(String propertyId)throws WebServiceException;
	ZoyPgOwnerDetails findPgOwnerById(String ownerId)throws WebServiceException;
	ZoyPgOwnerBookingDetails getBookingDetails(String userBookingsId)throws WebServiceException;
	ZoyPgBedDetails getBedsId(String selectedBed)throws WebServiceException;
	List<String> findPropertyAmenetiesName(String propertyId)throws WebServiceException;
	ZoyPgTermsMaster findTermMaster(String propertyId)throws WebServiceException;
	ZoyPgTokenDetails findTokenDetails() throws WebServiceException;
	ZoyPgTokenDetails saveToken(ZoyPgTokenDetails tokenDetails) throws WebServiceException;
	List<ZoyPgTokenDetails> findAllToken()throws WebServiceException;
	List<ZoyPgDueMaster> findAllDueMaster()throws WebServiceException;
	ZoyPgDueMaster saveUserDueMaster(ZoyPgDueMaster userDueMasters)throws WebServiceException;
	ZoyPgDueMaster findUserDueMaster(String id)throws WebServiceException;
	ZoyPgDueMaster updateDueMaster(ZoyPgDueMaster userDueMasters)throws WebServiceException;
	ZoyPgCancellationDetails findBeforeCancellationDetails(String cancellationId)throws WebServiceException;
	ZoyPgCancellationDetails saveBeforeCancellation(ZoyPgCancellationDetails cancelDetails)throws WebServiceException;
	List<ZoyPgCancellationDetails> findAllBeforeCancellation()throws WebServiceException;
	ZoyShareMaster findZoyShareDetails(String zoyShareId) throws WebServiceException;
	ZoyShareMaster saveZoyShare(ZoyShareMaster shareDetails)throws WebServiceException;
	List<ZoyShareMaster> findAllZoyShare() throws WebServiceException;
	ZoyPgOtherCharges findZoyOtherCharges() throws WebServiceException;
	ZoyPgOtherCharges saveOtherCharges(ZoyPgOtherCharges other) throws WebServiceException;
	ZoyDataGrouping findZoyDataGroup() throws WebServiceException;
	ZoyDataGrouping saveDataGroup(ZoyDataGrouping group) throws WebServiceException;
	List<ZoyDataGrouping> findAllDataGroup()throws WebServiceException;
	ZoyPgSecurityDepositDetails findZoySecurityDeposit() throws WebServiceException;
	ZoyPgSecurityDepositDetails saveZoySecurityDepositLimits(ZoyPgSecurityDepositDetails group) throws WebServiceException;
	ZoyPgSecurityDepositRefundRule findSecurityDepositRefundRuleById() throws WebServiceException;
	ZoyPgAutoCancellationPeriod findAutoCancellationPeriodById() throws WebServiceException;
	ZoyPgSecurityDepositRefundRule saveSecurityDepositRefundRule(ZoyPgSecurityDepositRefundRule rule)throws WebServiceException;
	ZoyPgAutoCancellationPeriod saveAutoCancellationPeriod(ZoyPgAutoCancellationPeriod rule)throws WebServiceException;
	ZoyPgRoomDetails findRoomName(String roomId);
    void deleteBeforeCancellation(String cancellationId);
}