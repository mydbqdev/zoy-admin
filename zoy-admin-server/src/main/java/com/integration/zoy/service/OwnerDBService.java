package com.integration.zoy.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.RentalAgreementDoc;
import com.integration.zoy.entity.ZoyCompanyMaster;
import com.integration.zoy.entity.ZoyCompanyProfileMaster;
import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgAutoCancellationAfterCheckIn;
import com.integration.zoy.entity.ZoyPgAutoCancellationMaster;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgDueMaster;
import com.integration.zoy.entity.ZoyPgDueTypeMaster;
import com.integration.zoy.entity.ZoyPgEarlyCheckOut;
import com.integration.zoy.entity.ZoyPgForceCheckOut;
import com.integration.zoy.entity.ZoyPgGstCharges;
import com.integration.zoy.entity.ZoyPgNoRentalAgreement;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.entity.ZoyPgPropertyDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgShortTermMaster;
import com.integration.zoy.entity.ZoyPgShortTermRentingDuration;
import com.integration.zoy.entity.ZoyPgTermsMaster;
import com.integration.zoy.entity.ZoyPgTimeMaster;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.entity.ZoyShareMaster;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.repository.RentalAgreementDocRepository;
import com.integration.zoy.repository.ZoyCompanyMasterRepository;
import com.integration.zoy.repository.ZoyCompanyProfileMasterRepository;
import com.integration.zoy.repository.ZoyDataGroupingRepository;
import com.integration.zoy.repository.ZoyPgAmenetiesMasterRepository;
import com.integration.zoy.repository.ZoyPgAutoCancellationAfterCheckInRepository;
import com.integration.zoy.repository.ZoyPgAutoCancellationMasterRepository;
import com.integration.zoy.repository.ZoyPgBedDetailsRepository;
import com.integration.zoy.repository.ZoyPgCancellationDetailsRepository;
import com.integration.zoy.repository.ZoyPgDueFactorMasterRepository;
import com.integration.zoy.repository.ZoyPgDueMasterRepository;
import com.integration.zoy.repository.ZoyPgDueTypeMasterRepository;
import com.integration.zoy.repository.ZoyPgEarlyCheckOutRepository;
import com.integration.zoy.repository.ZoyPgForceCheckOutRepository;
import com.integration.zoy.repository.ZoyPgGstChargesRepository;
import com.integration.zoy.repository.ZoyPgNoRentalAgreementRespository;
import com.integration.zoy.repository.ZoyPgOtherChargesRepository;
import com.integration.zoy.repository.ZoyPgOwnerBookingDetailsRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;
import com.integration.zoy.repository.ZoyPgPropertyAmenetiesRepository;
import com.integration.zoy.repository.ZoyPgPropertyDetailsRepository;
import com.integration.zoy.repository.ZoyPgPropertyDuesRepository;
import com.integration.zoy.repository.ZoyPgPropertyFloorDetailsRepository;
import com.integration.zoy.repository.ZoyPgRentCycleMasterRepository;
import com.integration.zoy.repository.ZoyPgRentingDurationRepository;
import com.integration.zoy.repository.ZoyPgRoomDetailsRepository;
import com.integration.zoy.repository.ZoyPgRoomTypeMasterRepository;
import com.integration.zoy.repository.ZoyPgSecurityDepositDetailsRepository;
import com.integration.zoy.repository.ZoyPgShareMasterRepository;
import com.integration.zoy.repository.ZoyPgShortTermMasterRepository;
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
	private ZoyPgGstChargesRepository zoyPgGstChargesRepo;

	@Autowired
	private ZoyPgEarlyCheckOutRepository zoyPgEarlyCheckOutRepository;

	@Autowired
	private ZoyPgAutoCancellationAfterCheckInRepository zoyPgAutoCancellationAfterCheckInRepository;
	
	@Autowired
	private ZoyPgAutoCancellationMasterRepository zoyPgAutoCancellationMasterRepository;
	
	@Autowired
	private ZoyPgShortTermMasterRepository zoyPgShortTermMasterRepository;
	
	@Autowired
	private ZoyPgForceCheckOutRepository zoyPgForceCheckOutRepository; 
	
	@Autowired
	private ZoyPgNoRentalAgreementRespository zoyPgNoRentalAgreementRespository;
	
	@Autowired
	private ZoyCompanyProfileMasterRepository zoyCompanyProfileMasterRepository;
	
	@Autowired
	private ZoyPgRentingDurationRepository rentingDurationRepo;
	
	@Autowired
	private ZoyPgPropertyDuesRepository zoyPgPropertyDuesRepository;
	
	@Autowired
	private RentalAgreementDocRepository rentalAgreementDocRepository;
	
	@Autowired
	ZoyCompanyMasterRepository zoyCompanyMasterRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public ZoyPgTypeMaster createPgType(ZoyPgTypeMaster pgType)throws WebServiceException {
		return zoyPgTypeMasterRepository.save(pgType);
	}

	@Override
	public ZoyPgTypeMaster getPgTypeById(String id) throws WebServiceException{
		return zoyPgTypeMasterRepository.findById(id).orElse(null);
	}

	@Override
	public List<ZoyPgTypeMaster> getAllPgTypes() throws WebServiceException{
		return zoyPgTypeMasterRepository.findAll();
	}


	@Override
	public ZoyPgTypeMaster updatePgType(ZoyPgTypeMaster pgType) throws WebServiceException{
		Optional<ZoyPgTypeMaster> existingPgType = zoyPgTypeMasterRepository.findById(pgType.getPgTypeId());
		if (existingPgType.isPresent()) {
			ZoyPgTypeMaster updatedPgType = existingPgType.get();
			updatedPgType.setPgTypeName((pgType.getPgTypeName() != null && !pgType.getPgTypeName().trim().isEmpty()) ? pgType.getPgTypeName() : updatedPgType.getPgTypeName());
			return zoyPgTypeMasterRepository.save(updatedPgType);
		}
		return null;
	}

	@Override
	public void deletePgType(String id) throws WebServiceException{
		zoyPgTypeMasterRepository.deleteById(id);
	}

	//PG Share
	@Override
	public ZoyPgShareMaster createShare(ZoyPgShareMaster share) throws WebServiceException{
		return zoyPgShareMasterRepository.save(share);
	}

	@Override
	public ZoyPgShareMaster updateShare(ZoyPgShareMaster share)throws WebServiceException {
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
	public void deleteShare(String shareId) throws WebServiceException{
		zoyPgShareMasterRepository.deleteById(shareId);
	}

	@Override
	public List<ZoyPgShareMaster> getAllShares()throws WebServiceException {
		return zoyPgShareMasterRepository.findAll();
	}

	@Override
	public ZoyPgShareMaster getShareById(String shareId)throws WebServiceException {
		return zoyPgShareMasterRepository.findById(shareId).orElse(null);
	}

	@Override
	public String getShareIdByShareType(String ShareType)throws WebServiceException {
		String id=zoyPgShareMasterRepository.findShareIdByShareType(ShareType);
		return id ;
	}


	@Override
	public ZoyPgAmenetiesMaster createAmeneties(ZoyPgAmenetiesMaster ameneties) throws WebServiceException{
		return zoyPgAmenetiesMasterRepository.save(ameneties);
	}

	@Override
	public List<ZoyPgAmenetiesMaster> createAllAmeneties(List<ZoyPgAmenetiesMaster> ameneties)throws WebServiceException {
		return zoyPgAmenetiesMasterRepository.saveAll(ameneties);
	}



	@Override
	public List<ZoyPgAmenetiesMaster> getAllAmeneties() throws WebServiceException{
		return zoyPgAmenetiesMasterRepository.findAll();
	}

	@Override
	public ZoyPgAmenetiesMaster updateAmeneties(String id, ZoyPgAmenetiesMaster ameneties) throws WebServiceException{
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
	public ZoyPgAmenetiesMaster findAmeneties(String id) throws WebServiceException {
		return zoyPgAmenetiesMasterRepository.findById(id).orElse(null);
	}

	@Override
	public void deleteAmeneties(String id) throws WebServiceException{
		if (!repository.existsById(id)) {
			throw new RuntimeException("Ameneties not found with id: " + id);
		}
		zoyPgAmenetiesMasterRepository.deleteById(id);
	}

	@Override
	public List<String>  getIdsOfByAmenitiesList(List<String> amenities)throws WebServiceException{
		return zoyPgAmenetiesMasterRepository.getAmenityIdsByNames(amenities);
	}


	// Room Types methods
	@Override
	public ZoyPgRoomTypeMaster createRoomType(ZoyPgRoomTypeMaster roomType)throws WebServiceException {
		return roomTypeRepository.save(roomType);
	}

	@Override
	public void deleteRoomType(String roomTypeId) throws WebServiceException{
		roomTypeRepository.deleteById(roomTypeId);
	}

	@Override
	public ZoyPgRoomTypeMaster updateRoomType(ZoyPgRoomTypeMaster roomType)throws WebServiceException {
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
	public List<ZoyPgRoomTypeMaster> getAllRoomTypes()throws WebServiceException {
		return roomTypeRepository.findAll();
	}

	@Override
	public String getRoomTypeIdByRoomType(String roomTypeName) throws WebServiceException{
		return roomTypeRepository.findRoomTypeIdByName(roomTypeName);
	}

	@Override
	public ZoyPgRoomTypeMaster findRoomType(String id) throws WebServiceException{
		return roomTypeRepository.findById(id).orElse(null);
	}

	//Pg due dueType
	@Override
	public ZoyPgDueFactorMaster createDueFactor(ZoyPgDueFactorMaster dueFactor)throws WebServiceException {
		return zoyPgDueFactorMasterRepository.save(dueFactor);
	}

	@Override
	public void deleteDueFactor(String factorId) throws WebServiceException{
		zoyPgDueFactorMasterRepository.deleteById(factorId);
	}

	@Override
	public ZoyPgDueFactorMaster updateDueFactor(ZoyPgDueFactorMaster dueFactor)throws WebServiceException {
		Optional<ZoyPgDueFactorMaster> existingDue = zoyPgDueFactorMasterRepository.findById(dueFactor.getFactorId());
		if (existingDue.isPresent()) {
			ZoyPgDueFactorMaster updatedDue = existingDue.get();
			updatedDue.setFactorName(dueFactor.getFactorName() != null && !dueFactor.getFactorName().trim().isEmpty() ? dueFactor.getFactorName() : updatedDue.getFactorName());
			return zoyPgDueFactorMasterRepository.save(dueFactor);
		}
		throw new RuntimeException("Due not found with id: " + dueFactor.getFactorId());
	}

	@Override
	public List<ZoyPgDueFactorMaster> getAllDueFactors() throws WebServiceException{
		return zoyPgDueFactorMasterRepository.findAll();
	}

	@Override
	public String getFactorIdbyFactorName(String factorName)throws WebServiceException {
		String dueTypeId=	zoyPgDueFactorMasterRepository.findFactorIdByFactorName(factorName);
		return dueTypeId;
	}

	@Override
	public ZoyPgDueFactorMaster findDueFactor(String id)throws WebServiceException {
		return zoyPgDueFactorMasterRepository.findById(id).orElse(null);
	}


	//Pg Due Type
	@Override
	public ZoyPgDueTypeMaster createDueType(ZoyPgDueTypeMaster dueType)throws WebServiceException {
		return zoyPgDueTypeMasterRepository.save(dueType);
	}

	@Override
	public List<ZoyPgDueTypeMaster> createAllDueType(List<ZoyPgDueTypeMaster> dueType) throws WebServiceException{
		return zoyPgDueTypeMasterRepository.saveAll(dueType);
	}


	@Override
	public void deleteDueType(String dueId)throws WebServiceException {
		zoyPgDueTypeMasterRepository.deleteById(dueId);
	}

	@Override
	public ZoyPgDueTypeMaster updateDueType(ZoyPgDueTypeMaster dueType) throws WebServiceException{
		Optional<ZoyPgDueTypeMaster> existingDue = zoyPgDueTypeMasterRepository.findById(dueType.getDueId());
		if (existingDue.isPresent()) {
			ZoyPgDueTypeMaster updatedDue = existingDue.get();
			updatedDue.setDueType(dueType.getDueType() != null && !dueType.getDueType().trim().isEmpty() ? dueType.getDueType() : updatedDue.getDueType());
			return zoyPgDueTypeMasterRepository.save(dueType);
		}
		throw new RuntimeException("Bed not found with id: " + dueType.getDueId());
	}

	@Override
	public List<ZoyPgDueTypeMaster> getAllDueTypes() throws WebServiceException{
		return zoyPgDueTypeMasterRepository.findAll();
	}


	//Pg Rent Cycle
	@Override
	public ZoyPgRentCycleMaster saveRentCycle(ZoyPgRentCycleMaster rentCycle) throws WebServiceException{
		return zoyPgRentCycleMasterRepository.save(rentCycle);
	}

	@Override
	public ZoyPgRentCycleMaster updateRentCycle(ZoyPgRentCycleMaster rentCycle)throws WebServiceException {
		Optional<ZoyPgRentCycleMaster> existingRentCycle = zoyPgRentCycleMasterRepository.findById(rentCycle.getCycleId());
		if (existingRentCycle.isPresent()) {
			ZoyPgRentCycleMaster updatedRentCycle = existingRentCycle.get();
			updatedRentCycle.setCycleName((rentCycle.getCycleName()!= null && !rentCycle.getCycleName().trim().isEmpty()) ? rentCycle.getCycleName() : updatedRentCycle.getCycleName());
			zoyPgRentCycleMasterRepository.save(updatedRentCycle);

		}
		throw new RuntimeException("Rent Cycle not found with id: " + rentCycle.getCycleId());
	}

	@Override
	public void deleteRentCycle(String cycleId) throws WebServiceException{
		zoyPgRentCycleMasterRepository.deleteById(cycleId);
	}

	@Override
	public List<ZoyPgRentCycleMaster> getAllRentCycle()throws WebServiceException {
		return zoyPgRentCycleMasterRepository.findAll();
	}

	@Override
	public List<String[]> findRentCycleName(String propertyId) throws WebServiceException{
		return zoyPgRentCycleMasterRepository.findRentCycleName(propertyId);
	}

	@Override
	public ZoyPgRentCycleMaster findRentCycle(String rentCycelId) throws WebServiceException{
		return zoyPgRentCycleMasterRepository.findById(rentCycelId).orElse(null);
	}

	@Override
	public String findRentCycleByName(String rentCycelName)throws WebServiceException{
		return zoyPgRentCycleMasterRepository.getRentCycleId(rentCycelName);
	}
	
	//Pg Time
	@Override
	public ZoyPgTimeMaster saveTime(ZoyPgTimeMaster time) throws WebServiceException{
		return zoyPgTimeMasterRepository.save(time);
	}

	@Override
	public void deleteTime(String timeId) throws WebServiceException{
		zoyPgTimeMasterRepository.deleteById(timeId);
	}

	@Override
	public ZoyPgTimeMaster updateTime(ZoyPgTimeMaster time) throws WebServiceException{
		Optional<ZoyPgTimeMaster> existingTime = zoyPgTimeMasterRepository.findById(time.getTimeId());
		if (existingTime.isPresent()) {
			ZoyPgTimeMaster updatedTime = existingTime.get();
			updatedTime.setTimeName((time.getTimeName()!= null && !time.getTimeName().trim().isEmpty()) ? time.getTimeName() : updatedTime.getTimeName());
			zoyPgTimeMasterRepository.save(updatedTime);

		}
		throw new RuntimeException("Term not found with id: " + time.getTimeId());
	}

	@Override
	public List<ZoyPgTimeMaster> getAllTime() throws WebServiceException{
		return zoyPgTimeMasterRepository.findAll();
	}

	@Override
	public List<String[]> getOwnerPropertyDetails() throws WebServiceException {
		return zoyPgPropertyDetailsRepository.getOwnerPropertyDetails();
	}

	@Override
	public ZoyPgPropertyFloorDetails findFloorDetails(String propertyId, String floorName)throws WebServiceException {
		return zoyPgPropertyFloorDetailsRepository.findFloorDetails(propertyId, floorName);
	}

	@Override
	public ZoyPgRoomDetails findRoomDetails(String propertyId, String roomName,String floorName)throws WebServiceException {
		return zoyPgRoomDetailsRepository.findRoomDetails(propertyId, roomName,floorName);
	}

	@Override
	public List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName,String roomName,String floorName)throws WebServiceException {
		return zoyPgBedDetailsRepository.findBedDetails(propertyId, bedName, roomName, floorName);
	}

	@Override
	public ZoyPgRentCycleMaster findRentCycleName(String propertyId, String rentCycle) throws WebServiceException{
		return zoyPgRentCycleMasterRepository.findRentCycleName(propertyId,rentCycle);
	}

	@Override
	public ZoyPgOwnerDetails savePgOwner(ZoyPgOwnerDetails zoyPgOwnerDetails) throws WebServiceException{
		return repository.save(zoyPgOwnerDetails);
	}

	@Override
	public List<String> getNameOfByAmenitiesList(List<String> excelAmeneties)throws WebServiceException {
		return	zoyPgAmenetiesMasterRepository.getAmenityNameByNames(excelAmeneties);

	}

	@Override
	public ZoyPgPropertyDetails getPropertyById(String id)throws WebServiceException {
		return zoyPgPropertyDetailsRepository.findById(id).orElse(null); 
	}

	@Override
	public ZoyPgOwnerDetails findPgOwnerById(String ownerId)throws WebServiceException {
		return repository.findById(ownerId).orElse(null);
	}

	@Override
	public ZoyPgOwnerBookingDetails getBookingDetails(String userBookingsId)throws WebServiceException {
		return bookingDetailsRepository.findById(userBookingsId).orElse(null);
	}

	@Override
	public	ZoyPgBedDetails getBedsId(String bedId) throws WebServiceException{
		return zoyPgBedDetailsRepository.findById(bedId).orElse(null);
	}

	@Override
	public List<String> findPropertyAmenetiesName(String propertyId)throws WebServiceException {
		return zoyPgPropertyAmenetiesRepository.findPropertyAmenetiesName(propertyId);
	}

	@Override
	public ZoyPgTermsMaster findTermMaster(String propertyId) throws WebServiceException{
		return zoyPgTermsMasterRepository.findTermMaster(propertyId) ;
	}

	@Override
	public ZoyPgTokenDetails findTokenDetails()throws WebServiceException {
		List<ZoyPgTokenDetails> results = zoyPgTokenDetailsRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public List<ZoyPgTokenDetails> findAllTokenDetailsSorted() throws WebServiceException {
	    List<ZoyPgTokenDetails> results = zoyPgTokenDetailsRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	

	@Override
	public ZoyPgTokenDetails saveToken(ZoyPgTokenDetails tokenDetails) throws WebServiceException{
		return zoyPgTokenDetailsRepository.save(tokenDetails);
	}

	@Override
	public List<ZoyPgTokenDetails> findAllToken() throws WebServiceException{
		return zoyPgTokenDetailsRepository.findAll();
	}

	@Override
	public List<ZoyPgDueMaster> findAllDueMaster() throws WebServiceException{
		return zoyPgDueMasterRepository.findAll();
	}

	@Override
	public ZoyPgDueMaster saveUserDueMaster(ZoyPgDueMaster userDueMasters)throws WebServiceException {
		return zoyPgDueMasterRepository.save(userDueMasters);
	}

	@Override
	public ZoyPgDueMaster findUserDueMaster(String id)throws WebServiceException {
		return zoyPgDueMasterRepository.findById(id).orElse(null);
	}

	@Override
	public ZoyPgDueMaster updateDueMaster(ZoyPgDueMaster userDueMasters)throws WebServiceException {
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
	public ZoyPgCancellationDetails findBeforeCancellationDetails(String cancellationId) throws WebServiceException {
		return zoyPgCancellationDetailsRepository.findById(cancellationId).orElse(null);
	}

	@Override
	public ZoyPgCancellationDetails saveBeforeCancellation(ZoyPgCancellationDetails cancelDetails) throws WebServiceException {
		return zoyPgCancellationDetailsRepository.save(cancelDetails);
	}

	@Override
	public List<ZoyPgCancellationDetails> findAllBeforeCancellation() throws WebServiceException {
		return zoyPgCancellationDetailsRepository.findAllByOrderByCreatedAtDesc();
	}

	@Override
	public ZoyShareMaster findZoyShareDetails(String zoyShareId) throws WebServiceException {
		return zoyShareMasterRepository.findById(zoyShareId).orElse(null);
	}

	@Override
	public ZoyShareMaster saveZoyShare(ZoyShareMaster shareDetails) throws WebServiceException {
		return zoyShareMasterRepository.save(shareDetails);
	}

	@Override
	public List<ZoyShareMaster> findAllZoyShare() throws WebServiceException{
		return zoyShareMasterRepository.findAll();
	}

	@Override
	public ZoyPgOtherCharges findZoyOtherCharges() throws WebServiceException{
		List<ZoyPgOtherCharges> results =zoyPgOtherChargesRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public ZoyPgGstCharges findZoyGstCharges() throws WebServiceException {
		List<ZoyPgGstCharges> results =zoyPgGstChargesRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgOtherCharges saveOtherCharges(ZoyPgOtherCharges other) throws WebServiceException{
		return zoyPgOtherChargesRepo.save(other);
	}
	
	@Override
	public ZoyPgGstCharges saveGstCharges(ZoyPgGstCharges gst) throws WebServiceException {
		return zoyPgGstChargesRepo.save(gst);
	}


	@Override
	public ZoyDataGrouping findZoyDataGroup() throws WebServiceException{
		List<ZoyDataGrouping> results=zoyDataGroupingRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public List<ZoyDataGrouping> findAllDataGroupingSorted() throws WebServiceException {
	    List<ZoyDataGrouping> results = zoyDataGroupingRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public ZoyDataGrouping saveDataGroup(ZoyDataGrouping group) throws WebServiceException {
		return zoyDataGroupingRepository.save(group);
	}

	@Override
	public List<ZoyDataGrouping> findAllDataGroup()throws WebServiceException {
		return zoyDataGroupingRepository.findAll();
	}

	@Override
	public ZoyPgSecurityDepositDetails findZoySecurityDeposit() throws WebServiceException {
		List<ZoyPgSecurityDepositDetails> results = zoySecurityDepositRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgSecurityDepositDetails saveZoySecurityDepositLimits(ZoyPgSecurityDepositDetails depositLimits) throws WebServiceException {
		return zoySecurityDepositRepo.save(depositLimits);
	}

	@Override
	public List<ZoyPgSecurityDepositDetails> findAllSortedByEffectiveDate() throws WebServiceException {
	    List<ZoyPgSecurityDepositDetails> results = zoySecurityDepositRepo.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgEarlyCheckOut> findAllEarlyCheckOutRulesSorted() throws WebServiceException {
	    List<ZoyPgEarlyCheckOut> results = zoyPgEarlyCheckOutRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgAutoCancellationAfterCheckIn> findAllAfterCheckInDatesSorted() throws WebServiceException {
	    List<ZoyPgAutoCancellationAfterCheckIn> results = zoyPgAutoCancellationAfterCheckInRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgAutoCancellationMaster> findAllSecurityDepositDeadlineSorted() throws WebServiceException {
	    List<ZoyPgAutoCancellationMaster> results = zoyPgAutoCancellationMasterRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	@Override
	public ZoyPgRoomDetails findRoomName(String roomId) {
		return zoyPgRoomDetailsRepository.findRoomNameByRoomId(roomId);
	}

	@Override
	@Transactional
	public void deleteBeforeCancellation(String cancellationId) {
		zoyPgCancellationDetailsRepository.deleteById(cancellationId);
	}

	@Override
	public ZoyPgEarlyCheckOut findEarlyCheckOutRule(String earlyCheckOutId) throws WebServiceException {
		return zoyPgEarlyCheckOutRepository.findById(earlyCheckOutId).orElse(null);
	}

	@Override
	public ZoyPgEarlyCheckOut saveEarlyCheckOut(ZoyPgEarlyCheckOut existingRule) throws WebServiceException {
		return zoyPgEarlyCheckOutRepository.save(existingRule);
	}

	@Override
	public ZoyPgEarlyCheckOut findEarlyCheckOutRule() throws WebServiceException {
		List<ZoyPgEarlyCheckOut> results=zoyPgEarlyCheckOutRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgAutoCancellationAfterCheckIn findAutoCancellationAfterCheckIn(String autoCancellationId) throws WebServiceException {
		return zoyPgAutoCancellationAfterCheckInRepository.findById(autoCancellationId).orElse(null);
	}

	@Override
	public ZoyPgAutoCancellationAfterCheckIn findAutoCancellationAfterCheckIn() throws WebServiceException {
		List<ZoyPgAutoCancellationAfterCheckIn> results=zoyPgAutoCancellationAfterCheckInRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);

	}

	@Override
	public void saveAutoCancellationAfterCheckIn(ZoyPgAutoCancellationAfterCheckIn existingRule) throws WebServiceException {
		zoyPgAutoCancellationAfterCheckInRepository.save(existingRule);		
	}

	@Override
	public ZoyPgAutoCancellationMaster findSecurityDepositDeadLine(String autoCancellationId) throws WebServiceException {
		return zoyPgAutoCancellationMasterRepository.findById(autoCancellationId).orElse(null);
	}

	@Override
	public ZoyPgAutoCancellationMaster findSecurityDepositDeadLine() throws WebServiceException {
		List<ZoyPgAutoCancellationMaster> results=zoyPgAutoCancellationMasterRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgAutoCancellationMaster saveSecurityDepositDeadLine(ZoyPgAutoCancellationMaster autoCancellationMaster) throws WebServiceException {
		return zoyPgAutoCancellationMasterRepository.save(autoCancellationMaster);
	}

	@Override
	public List<String[]> findRentDue(String propertyId) {
		return zoyPgDueMasterRepository.findRentDue(propertyId);
	}

	@Override
	public List<ZoyPgShortTermMaster> findAllShortTerm() {
		return zoyPgShortTermMasterRepository.findAll();
	}

	@Override
	public ZoyPgShortTermMaster createShortTerm(ZoyPgShortTermMaster zoyPgShortTermMaster) {
		return zoyPgShortTermMasterRepository.save(zoyPgShortTermMaster);
	}

	@Override
	public ZoyPgShortTermMaster findShortTerm(String zoyPgShortTermMasterId) {
		return zoyPgShortTermMasterRepository.findById(zoyPgShortTermMasterId).orElse(null);
	}

	@Override
	public ZoyPgForceCheckOut findZoyForceCheckOut(String forceCheckOutId) {
		return zoyPgForceCheckOutRepository.findById(forceCheckOutId).orElse(null);
	}

	@Override
	public ZoyPgForceCheckOut saveForceCheckOut(ZoyPgForceCheckOut force) {
		return zoyPgForceCheckOutRepository.save(force);
	}

	@Override
	public ZoyPgForceCheckOut findZoyForceCheckOut() {
		List<ZoyPgForceCheckOut> results=zoyPgForceCheckOutRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);

	}
	
	@Override
	public List<ZoyPgForceCheckOut> findAllForceCheckOutDetailsSorted() throws WebServiceException {
	    List<ZoyPgForceCheckOut> results = zoyPgForceCheckOutRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgGstCharges> findAllGstChargesDetailsSorted() throws WebServiceException {
	    List<ZoyPgGstCharges> results = zoyPgGstChargesRepo.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgOtherCharges> findAllOtherChargesDetailsSorted() throws WebServiceException {
	    List<ZoyPgOtherCharges> results = zoyPgOtherChargesRepo.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<ZoyPgNoRentalAgreement> findAllNoRentalAgreementDetailsSorted() throws WebServiceException {
	    List<ZoyPgNoRentalAgreement> results = zoyPgNoRentalAgreementRespository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	
	@Override
	public List<RentalAgreementDoc> findAllRentalAgreementDetailsSorted() throws WebServiceException {
	    List<RentalAgreementDoc> results = rentalAgreementDocRepository.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}

	@Override
	public List<ZoyPgShortTermRentingDuration> findAllShortTermRentingDurationDetailsSorted() throws WebServiceException {
	    List<ZoyPgShortTermRentingDuration> results = rentingDurationRepo.findAll(Sort.by(Sort.Order.desc("effectiveDate")));  
	    return results;
	}
	@Override
	public ZoyCompanyProfileMaster findCompanyProfile(String profileId) {
		return zoyCompanyProfileMasterRepository.findById(profileId).orElse(null);
	}

	@Override
	public ZoyCompanyProfileMaster createOrUpdateCompanyProfile(ZoyCompanyProfileMaster companyProfile) {
		return zoyCompanyProfileMasterRepository.save(companyProfile);
	}

	@Override
	public ZoyPgShortTermRentingDuration findZoyRentingDuration() {
		List<ZoyPgShortTermRentingDuration> results=rentingDurationRepo.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyPgShortTermRentingDuration saveRentingDuration(ZoyPgShortTermRentingDuration rentingDuration) {
		return rentingDurationRepo.save(rentingDuration);
	}

	@Override
	public List<ZoyCompanyProfileMaster> findAllCompanyProfiles() {
		return zoyCompanyProfileMasterRepository.findAll();
	}

	@Override
	@Transactional
	public void deleteCompanyProfile(String profileiD) throws WebServiceException {
		zoyCompanyProfileMasterRepository.deleteById(profileiD);
		
	}

	@Override
	public ZoyCompanyMaster findcompanyMaster() throws WebServiceException {
		List<ZoyCompanyMaster> results=zoyCompanyMasterRepository.findAll(PageRequest.of(0, 1)).getContent();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public ZoyCompanyMaster saveCompanyMaster(ZoyCompanyMaster master) throws WebServiceException {
		return zoyCompanyMasterRepository.save(master);
	}

	@Override
	public List<String[]> getPropertyDueDetails(String propertyId, String dueName) {
		return zoyPgPropertyDuesRepository.getPropertyDueDetails(propertyId, dueName) ;
	}
	
}
