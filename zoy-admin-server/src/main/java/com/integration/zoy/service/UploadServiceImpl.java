package com.integration.zoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.PgOwnerUserStatus;
import com.integration.zoy.entity.UserBookingPayment;
import com.integration.zoy.entity.UserBookings;
import com.integration.zoy.entity.UserDetails;
import com.integration.zoy.entity.UserDues;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.UserNotifications;
import com.integration.zoy.entity.UserPayment;
import com.integration.zoy.entity.UserPaymentDue;
import com.integration.zoy.entity.UserPgDetails;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgFloorRooms;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.entity.ZoyPgOwnerSettlementSplitUp;
import com.integration.zoy.entity.ZoyPgOwnerSettlementStatus;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloors;
import com.integration.zoy.entity.ZoyPgPropertyRentCycle;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomAmeneties;
import com.integration.zoy.entity.ZoyPgRoomBeds;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.repository.NotificationModeMasterRepository;
import com.integration.zoy.repository.PgOwnerUserStatusRepository;
import com.integration.zoy.repository.UserBookingPaymentRepository;
import com.integration.zoy.repository.UserBookingsRepository;
import com.integration.zoy.repository.UserDetailsRepository;
import com.integration.zoy.repository.UserDuesRepository;
import com.integration.zoy.repository.UserMasterRepository;
import com.integration.zoy.repository.UserNotificationsRepository;
import com.integration.zoy.repository.UserPaymentDueRepository;
import com.integration.zoy.repository.UserPaymentRepository;
import com.integration.zoy.repository.UserPgDetailsRepository;
import com.integration.zoy.repository.ZoyPgAmenetiesMasterRepository;
import com.integration.zoy.repository.ZoyPgBedDetailsRepository;
import com.integration.zoy.repository.ZoyPgFloorRoomsRepository;
import com.integration.zoy.repository.ZoyPgOwnerBookingDetailsRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;
import com.integration.zoy.repository.ZoyPgOwnerSettlementSplitUpRepository;
import com.integration.zoy.repository.ZoyPgOwnerSettlementStatusRepository;
import com.integration.zoy.repository.ZoyPgPropertyFloorDetailsRepository;
import com.integration.zoy.repository.ZoyPgPropertyFloorsRepository;
import com.integration.zoy.repository.ZoyPgPropertyRentCycleRepository;
import com.integration.zoy.repository.ZoyPgRentCycleMasterRepository;
import com.integration.zoy.repository.ZoyPgRoomAmenetiesRepository;
import com.integration.zoy.repository.ZoyPgRoomBedsRepository;
import com.integration.zoy.repository.ZoyPgRoomDetailsRepository;
import com.integration.zoy.repository.ZoyPgRoomTypeMasterRepository;
import com.integration.zoy.repository.ZoyPgShareMasterRepository;

@Service
public class UploadServiceImpl {
	@Autowired
	private ZoyPgOwnerDetailsRepository repository;
	
	@Autowired
	private UserMasterRepository userMasterRepository;
	
	@Autowired
	private UserDetailsRepository userDetailsRepository;
	
	@Autowired
	private ZoyPgOwnerBookingDetailsRepository zoyPgOwnerBookingDetailsRepository;
	
	@Autowired
	private UserBookingsRepository userBookingsRepository;
	
	@Autowired
	private UserPgDetailsRepository userPgDetailsRepository;
	
	@Autowired
	private PgOwnerUserStatusRepository ownerUserStatusRepository;
	
	@Autowired
	private ZoyPgRentCycleMasterRepository zoyPgRentCycleMasterRepository;
	
	@Autowired
	private NotificationModeMasterRepository notificationModeMasterRepository;
	
	@Autowired
	private UserNotificationsRepository userNotificationsRepository;
	
	@Autowired
	private ZoyPgRoomDetailsRepository zoyPgRoomDetailsRepository;
	
	@Autowired
	private ZoyPgPropertyFloorsRepository floorsRepository;
	
	@Autowired
	private ZoyPgBedDetailsRepository zoyPgBedDetailsRepository;
	
	@Autowired
	private ZoyPgPropertyFloorDetailsRepository floorDetailsRepository;
	
	@Autowired
	private ZoyPgRoomTypeMasterRepository roomTypeRepository;
	
	@Autowired
	private ZoyPgShareMasterRepository zoyPgShareMasterRepository;
	
	@Autowired
	private ZoyPgRoomBedsRepository zoyPgRoomBedsRepository;
	
	@Autowired
	private ZoyPgAmenetiesMasterRepository zoyPgAmenetiesMasterRepository;
	
	@Autowired
	private ZoyPgRoomAmenetiesRepository zoyPgRoomAmenetiesRepository;
	
	@Autowired
	private ZoyPgFloorRoomsRepository zoyPgFloorRoomsRepository;
	
	@Autowired
	private ZoyPgPropertyRentCycleRepository zoyPgPropertyRentCycleRepository;

	@Autowired
	private ZoyPgRoomDetailsRepository detailsRepository;
	
	@Autowired
	private UserPaymentRepository userPaymentRepository;
	
	@Autowired
	private UserDuesRepository userDuesRepository;
	
	@Autowired
	private ZoyPgOwnerSettlementStatusRepository zoyPgOwnerSettlementStatusRepository;
	
	@Autowired
	private ZoyPgOwnerSettlementSplitUpRepository zoyPgOwnerSettlementSplitUpRepository;
	
	@Autowired
	private UserPaymentDueRepository userPaymentDueRepository;
	
	@Autowired
	private UserBookingPaymentRepository userBookingPaymentRepository;
	
	public ZoyPgOwnerDetails findPgOwnerById(String ownerId) {
		return repository.findById(ownerId).orElse(null);
	}
	
	public UserMaster findUserMaster(String mobile,String email) {
		return userMasterRepository.findUserMaster(mobile,email).orElse(null);
	}

	public List<UserDetails> saveAllUserDetails(List<UserDetails> userDetails){
		return userDetailsRepository.saveAll(userDetails);
	}

	public List<ZoyPgOwnerBookingDetails> saveAllOwnerBooking(List<ZoyPgOwnerBookingDetails> tenantBooking){
		return zoyPgOwnerBookingDetailsRepository.saveAll(tenantBooking);
	}
	
	public List<UserBookings> saveAllUserBookings(List<UserBookings> bookings){
		return userBookingsRepository.saveAll(bookings);
	}
	
	public List<UserPgDetails> saveAllUserPgDetails(List<UserPgDetails> details){
		return userPgDetailsRepository.saveAll(details);
	}
	
	public List<PgOwnerUserStatus> saveAllOwnerUserStatus(List<PgOwnerUserStatus> userStatus) {
		return ownerUserStatusRepository.saveAll(userStatus);
	}
	
	public ZoyPgRentCycleMaster findRentCycle(String rentCycelId) {
		return zoyPgRentCycleMasterRepository.findById(rentCycelId).orElse(null);
	}
	
	public UserMaster saveUser(UserMaster userMaster) {
		return userMasterRepository.save(userMaster);
	}
	
	public List<NotificationModeMaster> findAllNotificationMode() {
		return notificationModeMasterRepository.findAll();
	}
	
	public List<UserNotifications> saveAllUserNotification(List<UserNotifications> userNotifications) {
		return userNotificationsRepository.saveAll(userNotifications);
	}
	
	public UserDetails findUserDetails(String userId) {
		return userDetailsRepository.findById(userId).orElse(null);
	}

	public List<String[]> findFloorRoomBedIdsByPropertyName(String propertyId, String room, String selectedBed,String lockInPeriod) {
		return zoyPgOwnerBookingDetailsRepository.findFloorRoomBedIdsByPropertyName(propertyId, room, selectedBed,lockInPeriod);
	}
	
	public List<String[]> findFloorRoomBedNameByPropertyName(String propertyId, String room, String selectedBed,String lockInPeriod) {
		return zoyPgOwnerBookingDetailsRepository.findFloorRoomBedNameByPropertyName(propertyId, room, selectedBed,lockInPeriod);
	}
	
	public List<ZoyPgOwnerBookingDetails> findAllBookingByPropertyId(String propertyId) {
		return zoyPgOwnerBookingDetailsRepository.findAllBookingByPropertyId(propertyId);
	}
	
	public List<String> getFloorIdsByPropertyId(String propertyId) {
		return floorsRepository.findAllFloorIdsByPropertyId(propertyId);
	}
	public List<String> getAllRoomIdsByPropertyId(String propertyId){
		return	zoyPgRoomDetailsRepository.findAllRoomIdsByPropertyId(propertyId);
	}

	public List<String> getBedIdsByPropertyId(String propertyId) {
		return zoyPgBedDetailsRepository.findAllBedIdsByPropertyId(propertyId);
	}
	
	public void deleteBedsByIds(List<String> bedIds) {
		if (bedIds != null && !bedIds.isEmpty()) {
			zoyPgBedDetailsRepository.deleteByBedIds(bedIds);
		}
	};
	
	public void deleteRoomsByIds(List<String> roomIds) {
		if (roomIds != null && !roomIds.isEmpty()) {
			zoyPgRoomDetailsRepository.deleteByRoomIds(roomIds);
		}
	}
	
	public void deleteFloorsByIds(List<String> floorIds) {
		if (floorIds != null && !floorIds.isEmpty()) {
			floorsRepository.deleteByFloorIds(floorIds);
		}
	}

	public String checkDuplicateFloorName(String floorName, String propertyId) {
		return floorDetailsRepository.checkDuplicateFloorName(floorName, propertyId);
	}
	
	public ZoyPgPropertyFloorDetails createFloorDetail(ZoyPgPropertyFloorDetails floorDetail) {
		return floorDetailsRepository.save(floorDetail);
	}
	
	public ZoyPgBedDetails createBed(ZoyPgBedDetails bedDetails) {
		return zoyPgBedDetailsRepository.save(bedDetails);
	}
	
	public String getRoomTypeIdByRoomType(String roomTypeName) {
		return roomTypeRepository.findRoomTypeIdByName(roomTypeName);
	}
	
	public String getShareIdByShareType(String ShareType) {
		String id=zoyPgShareMasterRepository.findShareIdByShareType(ShareType);
		return id ;
	}
	
	public String checkDuplicateRoomName(String roomName, String floorId, String propertyId) {
		return zoyPgRoomDetailsRepository.checkDuplicateRoomName(roomName, floorId, propertyId);
	}
	public ZoyPgRoomDetails createRoom(ZoyPgRoomDetails roomDetails) {
		return zoyPgRoomDetailsRepository.save(roomDetails);
	}
	
	public void saveAllRoomBedIds(List<ZoyPgRoomBeds>  multipleData) {
		zoyPgRoomBedsRepository.saveAll(multipleData);
	}
	
	public List<String>  getIdsOfByAmenitiesList(List<String> amenities){
		return zoyPgAmenetiesMasterRepository.getAmenityIdsByNames(amenities);
	}	
	
	public void saveMultipleAmenetiRoomMap(List<ZoyPgRoomAmeneties> multiAmenetiRoom) {
		zoyPgRoomAmenetiesRepository.saveAll(multiAmenetiRoom);
	};

	public void saveAllFloorRooms(List<ZoyPgFloorRooms>  floorRoomMap) {
		zoyPgFloorRoomsRepository.saveAll(floorRoomMap);
	}
	
	public  List<ZoyPgPropertyFloors> saveAllPropertyFloor(List<ZoyPgPropertyFloors> multipleData) {
		return floorsRepository.saveAll(multipleData);
	}

	public String getRentCycleId(String rentName) {
		return zoyPgRentCycleMasterRepository.getRentCycleId(rentName);
	}

	public ZoyPgPropertyRentCycle saveRentCycle(ZoyPgPropertyRentCycle cycle) {
		return zoyPgPropertyRentCycleRepository.save(cycle);
		
	}

	public ZoyPgRoomDetails getRoomDetails(String roomId) {
		return detailsRepository.findById(roomId).orElse(null);
	}

	public List<UserPayment> saveAllUserPayment(List<UserPayment> payment) {
		return userPaymentRepository.saveAll(payment);
		
	}

	public List<ZoyPgBedDetails> updateAllBeds(List<ZoyPgBedDetails> bedDetails) {
		return zoyPgBedDetailsRepository.saveAll(bedDetails);
	}

	public List<String[]> findFloorRoomBedIdsByPropertyName(String propertyId, String room, String selectedBed) {
		return zoyPgOwnerBookingDetailsRepository.findFloorRoomBedIdsByPropertyName(propertyId, room, selectedBed);
	}

	public List<UserDues> saveAllUserDues(List<UserDues> userDues) {
		return userDuesRepository.saveAll(userDues);
		
	}

	public List<ZoyPgOwnerSettlementStatus> saveAllOwnerSettlementStatus(List<ZoyPgOwnerSettlementStatus> settlementStatus) {
		return zoyPgOwnerSettlementStatusRepository.saveAll(settlementStatus);		
	}

	public UserPayment saveUserPayment(UserPayment payment) {
		return userPaymentRepository.save(payment);
	}

	public UserDues saveUserDues(UserDues dues) {
		return userDuesRepository.save(dues);
	}

	public ZoyPgOwnerSettlementStatus saveOwnerSettlementStatus(ZoyPgOwnerSettlementStatus settlementStatus) {
		return zoyPgOwnerSettlementStatusRepository.save(settlementStatus);
		
	}

	public ZoyPgOwnerSettlementSplitUp saveOwnerSettlementSlipUp(ZoyPgOwnerSettlementSplitUp firstPayment) {
		return zoyPgOwnerSettlementSplitUpRepository.save(firstPayment);
		
	}

	public UserPaymentDue saveUserPaymentDue(UserPaymentDue due) {
		return userPaymentDueRepository.save(due);
		
	}

	public UserBookingPayment saveUserBookingPayment(UserBookingPayment userBookingPayment) {
		return userBookingPaymentRepository.save(userBookingPayment);
		
	}

	public UserMaster findUserMaster(String phoneNumber) {
		 return userMasterRepository.findUserMaster(phoneNumber).orElse(null);
	}

}
