package com.integration.zoy.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
import com.integration.zoy.repository.ZoyDataGroupingRepository;
import com.integration.zoy.repository.ZoyPgAmenetiesMasterRepository;
import com.integration.zoy.repository.ZoyPgBedDetailsRepository;
import com.integration.zoy.repository.ZoyPgCancellationDetailsRepository;
import com.integration.zoy.repository.ZoyPgDueFactorMasterRepository;
import com.integration.zoy.repository.ZoyPgDueMasterRepository;
import com.integration.zoy.repository.ZoyPgDueTypeMasterRepository;
import com.integration.zoy.repository.ZoyPgOtherChargesRepository;
import com.integration.zoy.repository.ZoyPgOwnerBookingDetailsRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;
import com.integration.zoy.repository.ZoyPgPropertyAmenetiesRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.repository.ZoyPgPropertyFloorDetailsRepository;
import com.integration.zoy.repository.ZoyPgRentCycleMasterRepository;
import com.integration.zoy.repository.ZoyPgRoomDetailsRepository;
import com.integration.zoy.repository.ZoyPgRoomTypeMasterRepository;
import com.integration.zoy.repository.ZoyPgSecurityDepositDetailsRepository;
import com.integration.zoy.repository.ZoyPgSecurityDepositRefundRuleRepository;
import com.integration.zoy.repository.ZoyPgShareMasterRepository;
import com.integration.zoy.repository.ZoyPgTermsMasterRepository;
import com.integration.zoy.repository.ZoyPgTimeMasterRepository;
import com.integration.zoy.repository.ZoyPgTokenDetailsRepository;
import com.integration.zoy.repository.ZoyPgTypeMasterRepository;
import com.integration.zoy.repository.ZoyShareMasterRepository;

@Service
public class OwnerDBService implements OwnerDBImpl{

	@Autowired
	private ZoyPgOwnerDetailsRepository repository;

	@Autowired
	private ZoyPgTypeMasterRepository zoyPgTypeMasterRepository;

	@Autowired
	private ZoyPgShareMasterRepository zoyPgShareMasterRepository;

	@Autowired
	private ZoyPgAmenetiesMasterRepository zoyPgAmenetiesMasterRepository;

	@Autowired
	private ZoyPgRoomTypeMasterRepository roomTypeRepository;

	@Autowired
	private ZoyPgDueFactorMasterRepository zoyPgDueFactorMasterRepository;

	@Autowired
	private ZoyPgDueTypeMasterRepository zoyPgDueTypeMasterRepository;

	@Autowired
	private ZoyPgRentCycleMasterRepository zoyPgRentCycleMasterRepository;

	@Autowired
	private ZoyPgTimeMasterRepository zoyPgTimeMasterRepository;

	@Autowired
	private ZoyPgPropertyDetailsRepository zoyPgPropertyDetailsRepository;

	@Autowired
	private ZoyPgPropertyFloorDetailsRepository zoyPgPropertyFloorDetailsRepository;

	@Autowired
	private ZoyPgRoomDetailsRepository zoyPgRoomDetailsRepository;

	@Autowired
	private ZoyPgBedDetailsRepository zoyPgBedDetailsRepository;

	@Autowired
	private ZoyPgOwnerBookingDetailsRepository bookingDetailsRepository;

	@Autowired
	private ZoyPgPropertyAmenetiesRepository zoyPgPropertyAmenetiesRepository;

	@Autowired
	private ZoyPgTermsMasterRepository zoyPgTermsMasterRepository;

	@Autowired
	private ZoyPgTokenDetailsRepository zoyPgTokenDetailsRepository;

	@Autowired
	private ZoyPgDueMasterRepository zoyPgDueMasterRepository;

	@Autowired
	private ZoyPgCancellationDetailsRepository zoyPgCancellationDetailsRepository;

	@Autowired
	private ZoyShareMasterRepository zoyShareMasterRepository;

	@Autowired
	private ZoyDataGroupingRepository zoyDataGroupingRepository;

	@Autowired
	private ZoyPgSecurityDepositDetailsRepository zoySecurityDepositRepo;

	@Autowired
	private ZoyPgOtherChargesRepository zoyPgOtherChargesRepo;

	@Autowired
	private ZoyPgSecurityDepositRefundRuleRepository  zoySecurityDepositRefundRuleRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public ZoyPgTypeMaster createPgType(ZoyPgTypeMaster pgType) {
		return zoyPgTypeMasterRepository.save(pgType);
	}

	@Override
	public ZoyPgTypeMaster getPgTypeById(String id) {
		return zoyPgTypeMasterRepository.findById(id).orElse(null);
	}

	@Override
	public List<ZoyPgTypeMaster> getAllPgTypes() {
		return zoyPgTypeMasterRepository.findAll();
	}


	@Override
	public ZoyPgTypeMaster updatePgType(ZoyPgTypeMaster pgType) {
		Optional<ZoyPgTypeMaster> existingPgType = zoyPgTypeMasterRepository.findById(pgType.getPgTypeId());
		if (existingPgType.isPresent()) {
			ZoyPgTypeMaster updatedPgType = existingPgType.get();
			updatedPgType.setPgTypeName((pgType.getPgTypeName() != null && !pgType.getPgTypeName().trim().isEmpty()) ? pgType.getPgTypeName() : updatedPgType.getPgTypeName());
			return zoyPgTypeMasterRepository.save(updatedPgType);
		}
		return null;
	}

	@Override
	public void deletePgType(String id) {
		zoyPgTypeMasterRepository.deleteById(id);
	}

	//PG Share
	@Override
	public ZoyPgShareMaster createShare(ZoyPgShareMaster share) {
		return zoyPgShareMasterRepository.save(share);
	}

	@Override
	public ZoyPgShareMaster updateShare(ZoyPgShareMaster share) {
		Optional<ZoyPgShareMaster> existingShare = zoyPgShareMasterRepository.findById(share.getShareId());
		if (existingShare.isPresent()) {
			ZoyPgShareMaster updateShare = existingShare.get();
			updateShare.setShareType((share.getShareType() != null && !share.getShareType().trim().isEmpty()) ? share.getShareType() : updateShare.getShareType());
			updateShare.setShareOccupancyCount((share.getShareOccupancyCount() != null) ? share.getShareOccupancyCount() : updateShare.getShareOccupancyCount());
			return zoyPgShareMasterRepository.save(updateShare);
		} else {
			throw new RuntimeException("Share not found with id: " + share.getShareId());
		}
	}

	@Override
	public void deleteShare(String shareId) {
		zoyPgShareMasterRepository.deleteById(shareId);
	}

	@Override
	public List<ZoyPgShareMaster> getAllShares() {
		return zoyPgShareMasterRepository.findAll();
	}

	@Override
	public ZoyPgShareMaster getShareById(String shareId) {
		return zoyPgShareMasterRepository.findById(shareId).orElse(null);
	}

	@Override
	public String getShareIdByShareType(String ShareType) {
		String id=zoyPgShareMasterRepository.findShareIdByShareType(ShareType);
		return id ;
	}


	@Override
	public ZoyPgAmenetiesMaster createAmeneties(ZoyPgAmenetiesMaster ameneties) {
		return zoyPgAmenetiesMasterRepository.save(ameneties);
	}

	@Override
	public List<ZoyPgAmenetiesMaster> createAllAmeneties(List<ZoyPgAmenetiesMaster> ameneties) {
		return zoyPgAmenetiesMasterRepository.saveAll(ameneties);
	}



	@Override
	public List<ZoyPgAmenetiesMaster> getAllAmeneties() {
		return zoyPgAmenetiesMasterRepository.findAll();
	}

	@Override
	public ZoyPgAmenetiesMaster updateAmeneties(String id, ZoyPgAmenetiesMaster ameneties) {
		Optional<ZoyPgAmenetiesMaster> existingAmeneties = zoyPgAmenetiesMasterRepository.findById(id);
		if (existingAmeneties.isPresent()) {
			ZoyPgAmenetiesMaster updatedAmeneties = existingAmeneties.get();
			updatedAmeneties.setAmenetiesName(ameneties.getAmenetiesName() != null && !ameneties.getAmenetiesName().trim().isEmpty()? ameneties.getAmenetiesName() : updatedAmeneties.getAmenetiesName());
			return zoyPgAmenetiesMasterRepository.save(updatedAmeneties);
		} else {
			throw new RuntimeException("Ameneties not found with id: " + id);
		}
	}

	@Override
	public ZoyPgAmenetiesMaster findAmeneties(String id) {
		return zoyPgAmenetiesMasterRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteAmeneties(String id) {
		if (!repository.existsById(id)) {
			throw new RuntimeException("Ameneties not found with id: " + id);
		}
		zoyPgAmenetiesMasterRepository.deleteById(id);
	}

	@Override
	public List<String>  getIdsOfByAmenitiesList(List<String> amenities){
		return zoyPgAmenetiesMasterRepository.getAmenityIdsByNames(amenities);
	}


	// Room Types methods
	@Override
	public ZoyPgRoomTypeMaster createRoomType(ZoyPgRoomTypeMaster roomType) {
		return roomTypeRepository.save(roomType);
	}

	@Override
	public void deleteRoomType(String roomTypeId) {
		roomTypeRepository.deleteById(roomTypeId);
	}

	@Override
	public ZoyPgRoomTypeMaster updateRoomType(ZoyPgRoomTypeMaster roomType) {
		Optional<ZoyPgRoomTypeMaster> existingRoom = roomTypeRepository.findById(roomType.getRoomTypeId());
		if (existingRoom.isPresent()) {
			ZoyPgRoomTypeMaster updatedRoom = existingRoom.get();
			updatedRoom.setRoomTypeName((roomType.getRoomTypeName()!=null && !roomType.getRoomTypeName().trim().isBlank())? roomType.getRoomTypeName() : updatedRoom.getRoomTypeName());

			return roomTypeRepository.save(updatedRoom);
		} else {
			throw new RuntimeException("Room Type not found with id: " + roomType.getRoomTypeId());
		}
	}

	@Override
	public List<ZoyPgRoomTypeMaster> getAllRoomTypes() {
		return roomTypeRepository.findAll();
	}

	@Override
	public String getRoomTypeIdByRoomType(String roomTypeName) {
		return roomTypeRepository.findRoomTypeIdByName(roomTypeName);
	}

	@Override
	public ZoyPgRoomTypeMaster findRoomType(String id) {
		return roomTypeRepository.findById(id).orElse(null);
	}

	//Pg due dueType
	@Override
	public ZoyPgDueFactorMaster createDueFactor(ZoyPgDueFactorMaster dueFactor) {
		return zoyPgDueFactorMasterRepository.save(dueFactor);
	}

	@Override
	public void deleteDueFactor(String factorId) {
		zoyPgDueFactorMasterRepository.deleteById(factorId);
	}

	@Override
	public ZoyPgDueFactorMaster updateDueFactor(ZoyPgDueFactorMaster dueFactor) {
		Optional<ZoyPgDueFactorMaster> existingDue = zoyPgDueFactorMasterRepository.findById(dueFactor.getFactorId());
		if (existingDue.isPresent()) {
			ZoyPgDueFactorMaster updatedDue = existingDue.get();
			updatedDue.setFactorName(dueFactor.getFactorName() != null && !dueFactor.getFactorName().trim().isEmpty() ? dueFactor.getFactorName() : updatedDue.getFactorName());
			return zoyPgDueFactorMasterRepository.save(dueFactor);
		}
		throw new RuntimeException("Due not found with id: " + dueFactor.getFactorId());
	}

	@Override
	public List<ZoyPgDueFactorMaster> getAllDueFactors() {
		return zoyPgDueFactorMasterRepository.findAll();
	}

	@Override
	public String getFactorIdbyFactorName(String factorName) {
		String dueTypeId=	zoyPgDueFactorMasterRepository.findFactorIdByFactorName(factorName);
		return dueTypeId;
	}

	@Override
	public ZoyPgDueFactorMaster findDueFactor(String id) {
		return zoyPgDueFactorMasterRepository.findById(id).orElse(null);
	}


	//Pg Due Type
	@Override
	public ZoyPgDueTypeMaster createDueType(ZoyPgDueTypeMaster dueType) {
		return zoyPgDueTypeMasterRepository.save(dueType);
	}

	@Override
	public List<ZoyPgDueTypeMaster> createAllDueType(List<ZoyPgDueTypeMaster> dueType) {
		return zoyPgDueTypeMasterRepository.saveAll(dueType);
	}


	@Override
	public void deleteDueType(String dueId) {
		zoyPgDueTypeMasterRepository.deleteById(dueId);
	}

	@Override
	public ZoyPgDueTypeMaster updateDueType(ZoyPgDueTypeMaster dueType) {
		Optional<ZoyPgDueTypeMaster> existingDue = zoyPgDueTypeMasterRepository.findById(dueType.getDueId());
		if (existingDue.isPresent()) {
			ZoyPgDueTypeMaster updatedDue = existingDue.get();
			updatedDue.setDueType(dueType.getDueType() != null && !dueType.getDueType().trim().isEmpty() ? dueType.getDueType() : updatedDue.getDueType());
			return zoyPgDueTypeMasterRepository.save(dueType);
		}
		throw new RuntimeException("Bed not found with id: " + dueType.getDueId());
	}

	@Override
	public List<ZoyPgDueTypeMaster> getAllDueTypes() {
		return zoyPgDueTypeMasterRepository.findAll();
	}


	//Pg Rent Cycle
	@Override
	public ZoyPgRentCycleMaster saveRentCycle(ZoyPgRentCycleMaster rentCycle) {
		return zoyPgRentCycleMasterRepository.save(rentCycle);
	}

	@Override
	public ZoyPgRentCycleMaster updateRentCycle(ZoyPgRentCycleMaster rentCycle) {
		Optional<ZoyPgRentCycleMaster> existingRentCycle = zoyPgRentCycleMasterRepository.findById(rentCycle.getCycleId());
		if (existingRentCycle.isPresent()) {
			ZoyPgRentCycleMaster updatedRentCycle = existingRentCycle.get();
			updatedRentCycle.setCycleName((rentCycle.getCycleName()!= null && !rentCycle.getCycleName().trim().isEmpty()) ? rentCycle.getCycleName() : updatedRentCycle.getCycleName());
			zoyPgRentCycleMasterRepository.save(updatedRentCycle);

		}
		throw new RuntimeException("Rent Cycle not found with id: " + rentCycle.getCycleId());
	}

	@Override
	public void deleteRentCycle(String cycleId) {
		zoyPgRentCycleMasterRepository.deleteById(cycleId);
	}

	@Override
	public List<ZoyPgRentCycleMaster> getAllRentCycle() {
		return zoyPgRentCycleMasterRepository.findAll();
	}

	@Override
	public List<String[]> findRentCycleName(String propertyId) {
		return zoyPgRentCycleMasterRepository.findRentCycleName(propertyId);
	}

	@Override
	public ZoyPgRentCycleMaster findRentCycle(String rentCycelId) {
		return zoyPgRentCycleMasterRepository.findById(rentCycelId).orElse(null);
	}

	//Pg Time
	@Override
	public ZoyPgTimeMaster saveTime(ZoyPgTimeMaster time) {
		return zoyPgTimeMasterRepository.save(time);
	}

	@Override
	public void deleteTime(String timeId) {
		zoyPgTimeMasterRepository.deleteById(timeId);
	}

	@Override
	public ZoyPgTimeMaster updateTime(ZoyPgTimeMaster time) {
		Optional<ZoyPgTimeMaster> existingTime = zoyPgTimeMasterRepository.findById(time.getTimeId());
		if (existingTime.isPresent()) {
			ZoyPgTimeMaster updatedTime = existingTime.get();
			updatedTime.setTimeName((time.getTimeName()!= null && !time.getTimeName().trim().isEmpty()) ? time.getTimeName() : updatedTime.getTimeName());
			zoyPgTimeMasterRepository.save(updatedTime);

		}
		throw new RuntimeException("Term not found with id: " + time.getTimeId());
	}

	@Override
	public List<ZoyPgTimeMaster> getAllTime() {
		return zoyPgTimeMasterRepository.findAll();
	}

	@Override
	public List<String[]> getOwnerPropertyDetails() {
		return zoyPgPropertyDetailsRepository.getOwnerPropertyDetails();
	}

	@Override
	public ZoyPgPropertyFloorDetails findFloorDetails(String propertyId, String floorName) {
		return zoyPgPropertyFloorDetailsRepository.findFloorDetails(propertyId, floorName);
	}

	@Override
	public ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName) {
		return zoyPgRoomDetailsRepository.findRoomDetails(propertyId, roomName);
	}

	@Override
	public List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName) {
		return zoyPgBedDetailsRepository.findBedDetails(propertyId, bedName);
	}

	@Override
	public ZoyPgRentCycleMaster findRentCycleName(String propertyId, String rentCycle) {
		return zoyPgRentCycleMasterRepository.findRentCycleName(propertyId,rentCycle);
	}

	@Override
	public ZoyPgOwnerDetails savePgOwner(ZoyPgOwnerDetails zoyPgOwnerDetails) {
		return repository.save(zoyPgOwnerDetails);
	}

	@Override
	public List<String> getNameOfByAmenitiesList(List<String> excelAmeneties) {
		return	zoyPgAmenetiesMasterRepository.getAmenityNameByNames(excelAmeneties);

	}

	@Override
	public ZoyPgPropertyDetails getPropertyById(String id) {
		return zoyPgPropertyDetailsRepository.findById(id).orElse(null); 
	}

	@Override
	public ZoyPgOwnerDetails findPgOwnerById(String ownerId) {
		return repository.findById(ownerId).orElse(null);
	}

	@Override
	public ZoyPgOwnerBookingDetails getBookingDetails(String userBookingsId) {
		return bookingDetailsRepository.findById(userBookingsId).orElse(null);
	}

	@Override
	public	ZoyPgBedDetails getBedsId(String bedId) {
		return zoyPgBedDetailsRepository.findById(bedId).orElse(null);
	}

	@Override
	public List<String> findPropertyAmenetiesName(String propertyId) {
		return zoyPgPropertyAmenetiesRepository.findPropertyAmenetiesName(propertyId);
	}

	@Override
	public ZoyPgTermsMaster findTermMaster(String propertyId) {
		return zoyPgTermsMasterRepository.findTermMaster(propertyId) ;
	}

	@Override
	public ZoyPgTokenDetails findTokenDetails() {
		List<ZoyPgTokenDetails> results = zoyPgTokenDetailsRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgTokenDetails saveToken(ZoyPgTokenDetails tokenDetails) {
		return zoyPgTokenDetailsRepository.save(tokenDetails);
	}

	@Override
	public List<ZoyPgTokenDetails> findAllToken() {
		return zoyPgTokenDetailsRepository.findAll();
	}

	@Override
	public List<ZoyPgDueMaster> findAllDueMaster() {
		return zoyPgDueMasterRepository.findAll();
	}

	@Override
	public ZoyPgDueMaster saveUserDueMaster(ZoyPgDueMaster userDueMasters) {
		return zoyPgDueMasterRepository.save(userDueMasters);
	}

	@Override
	public ZoyPgDueMaster findUserDueMaster(String id) {
		return zoyPgDueMasterRepository.findById(id).orElse(null);
	}

	@Override
	public ZoyPgDueMaster updateDueMaster(ZoyPgDueMaster userDueMasters) {
		Optional<ZoyPgDueMaster> existingMaster = zoyPgDueMasterRepository.findById(userDueMasters.getDueTypeId());
		if (existingMaster.isPresent()) {
			ZoyPgDueMaster updatedMaster = existingMaster.get();
			updatedMaster.setDueName(userDueMasters.getDueName() != null && !userDueMasters.getDueName().trim().isEmpty() ? userDueMasters.getDueName() : updatedMaster.getDueName());
			return zoyPgDueMasterRepository.save(updatedMaster);
		} else {
			throw new RuntimeException("Due type not found with id: " + userDueMasters.getDueTypeId());
		}
	}

	@Override
	public ZoyPgCancellationDetails findBeforeCancellationDetails(String cancellationId) {
		return zoyPgCancellationDetailsRepository.findById(cancellationId).orElse(null);
	}

	@Override
	public ZoyPgCancellationDetails saveBeforeCancellation(ZoyPgCancellationDetails cancelDetails) {
		return zoyPgCancellationDetailsRepository.save(cancelDetails);
	}

	@Override
	public List<ZoyPgCancellationDetails> findAllBeforeCancellation() {
		return zoyPgCancellationDetailsRepository.findAll();
	}

	@Override
	public ZoyShareMaster findZoyShareDetails(String zoyShareId) {
		return zoyShareMasterRepository.findById(zoyShareId).orElse(null);
	}

	@Override
	public ZoyShareMaster saveZoyShare(ZoyShareMaster shareDetails) {
		return zoyShareMasterRepository.save(shareDetails);
	}

	@Override
	public List<ZoyShareMaster> findAllZoyShare() {
		return zoyShareMasterRepository.findAll();
	}

	@Override
	public ZoyPgOtherCharges findZoyOtherCharges() {
		List<ZoyPgOtherCharges> results =zoyPgOtherChargesRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgOtherCharges saveOtherCharges(ZoyPgOtherCharges other) {
		return zoyPgOtherChargesRepo.save(other);
	}

	@Override
	public ZoyDataGrouping findZoyDataGroup() {
		List<ZoyDataGrouping> results=zoyDataGroupingRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyDataGrouping saveDataGroup(ZoyDataGrouping group) {
		return zoyDataGroupingRepository.save(group);
	}

	@Override
	public List<ZoyDataGrouping> findAllDataGroup() {
		return zoyDataGroupingRepository.findAll();
	}

	@Override
	public ZoyPgSecurityDepositDetails findZoySecurityDeposit() {
		List<ZoyPgSecurityDepositDetails> results = zoySecurityDepositRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgSecurityDepositDetails saveZoySecurityDepositLimits(ZoyPgSecurityDepositDetails depositLimits) {
		return zoySecurityDepositRepo.save(depositLimits);
	}



	@Override
	public ZoyPgSecurityDepositRefundRule saveSecurityDepositRefundRule(ZoyPgSecurityDepositRefundRule refundRule) {
		return zoySecurityDepositRefundRuleRepo.save(refundRule);
	}

	@Override
	public ZoyPgSecurityDepositRefundRule findSecurityDepositRefundRuleById() {
		List<ZoyPgSecurityDepositRefundRule> results= zoySecurityDepositRefundRuleRepo.findAll(PageRequest.of(0, 1)).getContent();;
		return results.isEmpty() ? null : results.get(0);
	}

}
