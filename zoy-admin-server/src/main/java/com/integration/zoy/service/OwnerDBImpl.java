package com.integration.zoy.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgDueTypeMaster;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgTimeMaster;
import com.integration.zoy.entity.ZoyPgTypeMaster;

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
	
	

}