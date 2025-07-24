package com.integration.zoy.service;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.PgOwnerUserStatus;
import com.integration.zoy.entity.UserBookingPayment;
import com.integration.zoy.entity.UserBookingPaymentId;
import com.integration.zoy.entity.UserBookings;
import com.integration.zoy.entity.UserDetails;
import com.integration.zoy.entity.UserDues;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.UserNotifications;
import com.integration.zoy.entity.UserPayment;
import com.integration.zoy.entity.UserPaymentDue;
import com.integration.zoy.entity.UserPgDetails;
import com.integration.zoy.entity.ZoyCompanyProfileMaster;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgFloorNameMaster;
import com.integration.zoy.entity.ZoyPgFloorRooms;
import com.integration.zoy.entity.ZoyPgFloorRoomsId;
import com.integration.zoy.entity.ZoyPgRentGst;
import com.integration.zoy.entity.ZoyPgNoRentalAgreement;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.entity.ZoyPgOwnerSettlementSplitUp;
import com.integration.zoy.entity.ZoyPgOwnerSettlementStatus;
import com.integration.zoy.entity.ZoyPgPropertyDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloorDetails;
import com.integration.zoy.entity.ZoyPgPropertyFloors;
import com.integration.zoy.entity.ZoyPgPropertyFloorsId;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomAmeneties;
import com.integration.zoy.entity.ZoyPgRoomBeds;
import com.integration.zoy.entity.ZoyPgRoomBedsId;
import com.integration.zoy.entity.ZoyPgRoomDetails;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.model.RegisterUser;
import com.integration.zoy.repository.ZoyPgRoomAmenetiesId;
import com.integration.zoy.utils.GenerateBulkUploadRentalPdf;
import com.integration.zoy.utils.GstCalc;
import com.integration.zoy.utils.PropertyList;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.TenantList;
import com.integration.zoy.utils.Whatsapp;
import kotlin.Pair;

@Service
public class UploadService {
	@Autowired
	UploadServiceImpl uploadDBImpl;

	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	UserDBService userDBImpl;

	@Autowired 
	WhatsAppService whatsAppService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	PdfGenerateService pdfGenerateService;

	@Autowired
	ZoyS3Service zoyS3Service;

	@Value("${zoy.admin.logo}")
	private String zoyLogoPath;

	@Value("${app.zoy.server.username}")
	String zoyServerUserName;

	@Value("${app.zoy.server.password}")
	String zoyServerPassword;

	@Value("${app.zoy.server.url}")
	String zoyServerUrl;

	@Value("${app.zoy.server.customer.url}")
	String zoyServerCustomerUrl;


	@Autowired
	RestTemplate restTemplate;

	@Autowired
	NumberToWordsService numberToWordsService;
	
	private ZoyPgRentGst charges=new ZoyPgRentGst();
	private ZoyCompanyProfileMaster zoyCompanyProfileMaster = new ZoyCompanyProfileMaster();

	private static final Logger log = LoggerFactory.getLogger(UploadService.class);
	private static final Gson gson = new GsonBuilder()
			.setDateFormat("yyyy-MM-dd HH:mm:ss")
			.registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
				return new JsonPrimitive(dateFormat.format(src)); 
			})
			.registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
				try {
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); 
					return new Timestamp(dateFormat.parse(json.getAsString()).getTime()); 
				} catch (Exception ex) {
					throw new JsonParseException("Failed to parse Timestamp", ex);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();



	public ResponseEntity<String> zoyPartnerBulkUpload(String ownerId, String propertyId,List<PropertyList> propertyList, List<TenantList> tenantList) {
		ResponseBody response = new ResponseBody();
		try{
			this.zoyPartnerUserIdPropertyIdUploadXlsxPost(ownerId,propertyId,propertyList);
			this.tenatantWriteDataPost(ownerId,propertyId,tenantList);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Property & Tenant successfully uploaded from Excel");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Bulk Upload error " + e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	public ResponseEntity<String> zoyPartnerUserIdPropertyIdUploadXlsxPost( String ownerId,
			String propertyId,List<PropertyList> propertyLists) {
		ResponseBody response = new ResponseBody();
		try {
			List<ZoyPgOwnerBookingDetails> bookingDetails=uploadDBImpl.findAllBookingByPropertyId(propertyId);
			if(bookingDetails.size()==0) {
				List<String> existingFloorIds = uploadDBImpl.getFloorIdsByPropertyId(propertyId);
				List<String> existingRoomIds = uploadDBImpl.getAllRoomIdsByPropertyId(propertyId);
				List<String> existingBedIds = uploadDBImpl.getBedIdsByPropertyId(propertyId);
				if (existingBedIds != null && !existingBedIds.isEmpty()) {
					uploadDBImpl.deleteBedsByIds(existingBedIds);
				}
				if (existingRoomIds != null && !existingRoomIds.isEmpty()) {
					uploadDBImpl.deleteRoomsByIds(existingRoomIds);
				}
				if (existingFloorIds != null && !existingFloorIds.isEmpty()) {
					uploadDBImpl.deleteFloorsByIds(existingFloorIds);
				}
			}
			List<String> newFloorIds = new ArrayList<>();
			Map<String, String> allFloors = new LinkedHashMap<>();
			List<ZoyPgFloorNameMaster> zoyPgFloorNameMaster =ownerDBImpl.getAllFloorNames();
			for (PropertyList data : propertyLists) {
				List<String> newRoomIds = new ArrayList<>();
				List<String> newBedIds = new ArrayList<>();

				if (!allFloors.containsValue(data.getFloorName())) {
					Optional<ZoyPgFloorNameMaster> matchedFloor = zoyPgFloorNameMaster.stream()
							.filter(floor -> data.getFloorName().equalsIgnoreCase(floor.getFloorName()))
							.findFirst();
					if(matchedFloor.isPresent()) {
						ZoyPgPropertyFloorDetails newFloorDetails = new ZoyPgPropertyFloorDetails();
						newFloorDetails.setFloorName(data.getFloorName());
						newFloorDetails.setMasterFloorId(matchedFloor.get().getFloorNameId());
						newFloorDetails.setFloorStatus(true);
						ZoyPgPropertyFloorDetails createdFloor = uploadDBImpl.createFloorDetail(newFloorDetails);
						allFloors.put(createdFloor.getFloorId(), data.getFloorName());
					}
				}

				processNewBeds(data.getAvailableBeds(), "available", newBedIds);
				//processNewBeds(occupiedBed!=""?Arrays.asList(occupiedBed.split(",")):new ArrayList<>(), "occupied", newBedIds);

				String roomTypeId = uploadDBImpl.getRoomTypeIdByRoomType(data.getRoomType());
				String shareTypeId = uploadDBImpl.getShareIdByShareType(data.getShareType());

				ZoyPgRoomDetails newRoomDetails = new ZoyPgRoomDetails();
				String floorId=getFloorIdByName(allFloors,data.getFloorName());
				newRoomDetails.setRoomName(uploadDBImpl.checkDuplicateRoomName(data.getRoomName(),floorId,propertyId));
				newRoomDetails.setRoomType(data.getRoomType());
				newRoomDetails.setShareId(shareTypeId);
				newRoomDetails.setRoomArea(Double.valueOf(data.getArea()));
				newRoomDetails.setRoomDailyRent(0.0);//Double.valueOf(dailyRent));
				newRoomDetails.setRoomMonthlyRent(Double.valueOf(data.getMonthlyRent()));
				newRoomDetails.setRoomTypeId(roomTypeId);
				newRoomDetails.setRoomRemarks(data.getRemarks());
				newRoomDetails.setRoomStatus(true);
				ZoyPgRoomDetails createdRoom = uploadDBImpl.createRoom(newRoomDetails);
				mapRoomToBeds(createdRoom.getRoomId(), newBedIds);

				List<String> amenitiesIds = uploadDBImpl.getIdsOfByAmenitiesList(data.getAmenities());
				mapRoomToAmenities(createdRoom.getRoomId(), amenitiesIds);

				newRoomIds.add(createdRoom.getRoomId());

				mapFloorToRooms(floorId, newRoomIds);
				newFloorIds.add(floorId);
			}

			mapPropertyToFloors(propertyId, newFloorIds);

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Property design posted successfully from Excel");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error processing Property: " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private void mapRoomToAmenities(String roomId, List<String> amenitiesIds) {
		if (amenitiesIds != null && !amenitiesIds.isEmpty()) {
			List<ZoyPgRoomAmeneties> roomAmenitiesMappingList = new ArrayList<>();
			amenitiesIds.forEach(amenityId -> {
				ZoyPgRoomAmeneties roomAmenityMapping = new ZoyPgRoomAmeneties();
				ZoyPgRoomAmenetiesId amenityMapping = new ZoyPgRoomAmenetiesId();
				amenityMapping.setAmenetiesId(amenityId);
				amenityMapping.setRoomId(roomId);
				roomAmenityMapping.setId(amenityMapping);
				roomAmenitiesMappingList.add(roomAmenityMapping);
			});
			uploadDBImpl.saveMultipleAmenetiRoomMap(roomAmenitiesMappingList);
		}
	}

	private void mapFloorToRooms(String floorId, List<String> newRoomIds) {
		List<ZoyPgFloorRooms> roomFloorMappingList = new ArrayList<>();
		newRoomIds.forEach(roomId -> {
			ZoyPgFloorRooms roomFloorMapping = new ZoyPgFloorRooms();
			ZoyPgFloorRoomsId roomFloorMappingId = new ZoyPgFloorRoomsId();
			roomFloorMappingId.setFloorId(floorId);
			roomFloorMappingId.setRoomId(roomId);
			roomFloorMapping.setId(roomFloorMappingId);
			roomFloorMappingList.add(roomFloorMapping);
		});
		uploadDBImpl.saveAllFloorRooms(roomFloorMappingList);
	}

	private void mapPropertyToFloors(String propertyId, List<String> newFloorIds) {
		List<ZoyPgPropertyFloors> propertyFloorMappingList = new ArrayList<>();
		newFloorIds.forEach(floorId -> {
			ZoyPgPropertyFloors propertyFloor = new ZoyPgPropertyFloors();
			ZoyPgPropertyFloorsId propertyFloorIdMapping = new ZoyPgPropertyFloorsId();
			propertyFloorIdMapping.setFloorId(floorId);
			propertyFloorIdMapping.setPropertyId(propertyId);
			propertyFloor.setId(propertyFloorIdMapping);
			propertyFloorMappingList.add(propertyFloor);
		});
		uploadDBImpl.saveAllPropertyFloor(propertyFloorMappingList);
	}



	private void mapRoomToBeds(String roomId, List<String> newBedIds) {
		List<ZoyPgRoomBeds> roomBedMappingList = new ArrayList<>();
		newBedIds.forEach(bedId -> {
			ZoyPgRoomBeds roomBedMapping = new ZoyPgRoomBeds();
			ZoyPgRoomBedsId bedMapping = new ZoyPgRoomBedsId();
			bedMapping.setBedId(bedId);
			bedMapping.setRoomId(roomId);
			roomBedMapping.setId(bedMapping);
			roomBedMappingList.add(roomBedMapping);
		});
		uploadDBImpl.saveAllRoomBedIds(roomBedMappingList);
	}

	public static String getFloorIdByName(Map<String, String> allFloors, String floorName) {
		for (Map.Entry<String, String> entry : allFloors.entrySet()) {
			if (entry.getValue().equalsIgnoreCase(floorName)) {
				return entry.getKey();
			}
		}
		return null;
	}

	private void processNewBeds(List<String> bedList, String status, List<String> newBedIds) {
		if (bedList.size() > 0) {
			bedList.forEach(bedName -> {
				ZoyPgBedDetails bed = new ZoyPgBedDetails();
				bed.setBedName(bedName);
				bed.setBedAvailable(status);
				bed.setBedStatus(true);
				ZoyPgBedDetails bedData = uploadDBImpl.createBed(bed);
				newBedIds.add(bedData.getBedId());
			});
		}
	}

	public ResponseEntity<String> tenatantWriteDataPost(String ownerId,String propertyId,List<TenantList> tenantDetailsList) {
		ResponseBody response=new ResponseBody();
		ZoyPgOwnerDetails userMasterOpt = uploadDBImpl.findPgOwnerById(ownerId);
		if (userMasterOpt==null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.setMessage("Owner not found");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
		}
		try {
			charges=ownerDBImpl.findPgRentGst();
			zoyCompanyProfileMaster=ownerDBImpl.getZoyHeadOfficeDetails();
			List<UserDetails> userDetails=new ArrayList<>();
			List<UserMaster> userMasters=new ArrayList<>();
			List<ZoyPgOwnerBookingDetails> zoyPgOwnerBookingDetails = new ArrayList<>();
			List<UserBookings> userBookingDetails=new ArrayList<>();
			List<PgOwnerUserStatus> userStatus=new ArrayList<>();
			List<UserPgDetails> userPgDetails=new ArrayList<>();
			List<ZoyPgBedDetails> bedDetails=new ArrayList<>();
			Map<String,TenantList> userCsvDetails= new HashMap<>();
			for(TenantList tenantDetails:tenantDetailsList) {
				UserMaster userMaster=uploadDBImpl.findUserMaster(tenantDetails.getPhoneNumber());
				if(userMaster==null) {
					String userId=createUser(tenantDetails);
					createUserDetails(tenantDetails,userId,propertyId,userDetails);
					createUserBooking(tenantDetails,userId,propertyId,zoyPgOwnerBookingDetails);
					userCsvDetails.put(userId, tenantDetails);
				} else {
					if(userMaster.getUserStatus()!=null && !userMaster.getUserStatus().equals(ZoyConstant.ACTIVE)) {
						createUserDetails(tenantDetails,userMaster.getUserId(),propertyId,userDetails);
						createUserBooking(tenantDetails,userMaster.getUserId(),propertyId,zoyPgOwnerBookingDetails);
						userCsvDetails.put(userMaster.getUserId(), tenantDetails);
					} else 
						log.debug(userMaster.getUserFirstName() +" "+userMaster.getUserLastName() + 
								" User is already active in some pg, user email is " + userMaster.getUserEmail());
				}
			}
			uploadDBImpl.saveAllUserDetails(userDetails);
			List<ZoyPgOwnerBookingDetails> bookingDetails=uploadDBImpl.saveAllOwnerBooking(zoyPgOwnerBookingDetails);
			List<String[]> dues=ownerDBImpl.findRentDue(propertyId);
			for(ZoyPgOwnerBookingDetails booking:bookingDetails) {
				createWebcheckIn(ownerId,booking,userBookingDetails,userStatus,userPgDetails,bedDetails,userCsvDetails,dues,userMasters);
			}
			uploadDBImpl.saveAllUserBookings(userBookingDetails);
			uploadDBImpl.saveAllUserMaster(userMasters);
			uploadDBImpl.saveAllUserPgDetails(userPgDetails);
			uploadDBImpl.saveAllOwnerUserStatus(userStatus);
			uploadDBImpl.updateAllBeds(bedDetails);
			for(UserBookings userBooking:userBookingDetails) {
				ZoyPgPropertyDetails propertyDetail = ownerDBImpl.getPropertyById(userBooking.getUserBookingsPropertyId());
				ZoyPgOwnerDetails zoyPgOwnerDetails = ownerDBImpl.findPgOwnerById(userBooking.getUserBookingsPgOwnerId());
				ZoyPgOwnerBookingDetails saveMyBookings = ownerDBImpl.getBookingDetails(userBooking.getUserBookingsId());
				UserMaster master=userDBImpl.findUserMaster(userBooking.getUserId());
				Whatsapp whatsapp = new Whatsapp();
				whatsapp.tonumber("+91" + master.getUserMobile());
				whatsapp.templateid(ZoyConstant.ZOY_TENANT_ONLINE_BOOKING);
				Map<Integer, String> params = new HashMap<>();
				params.put(1, master.getUserFirstName() +" "+ master.getUserLastName());
				params.put(2, propertyDetail.getPropertyName());
				params.put(3, String.valueOf(saveMyBookings.getInDate()));
				params.put(4, String.valueOf(saveMyBookings.getOutDate()));
				whatsapp.setParams(params);
				whatsAppService.sendWhatsappMessage(whatsapp);

				ZoyPgShareMaster pgPropertyShareTypes=ownerDBImpl.getShareById(saveMyBookings.getShare());
				ZoyPgBedDetails bedName=ownerDBImpl.getBedsId(saveMyBookings.getSelectedBed());
				ZoyPgRoomDetails roomDetails=ownerDBImpl.findRoomName(saveMyBookings.getRoom());
				zoyEmailService.sendBookingEmail(master.getUserEmail(), saveMyBookings, propertyDetail, zoyPgOwnerDetails,bedName,pgPropertyShareTypes.getShareType(),roomDetails.getRoomName());

				generateSendRentalAgreement(master,propertyDetail,saveMyBookings);
			}

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("File processed successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error processing file" + e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private void createWebcheckIn(String ownerId,ZoyPgOwnerBookingDetails booking, List<UserBookings> userBookingDetails, 
			List<PgOwnerUserStatus> userStatus, List<UserPgDetails> userPgDetails,List<ZoyPgBedDetails> bedDetails,
			Map<String,TenantList> userCsvDetails,List<String[]> duesType,List<UserMaster> userMasters) {
		try {
			//System.out.println(gson.toJson(booking));
			ZoyPgRentCycleMaster rentCycle=uploadDBImpl.findRentCycle(booking.getLockInPeriod());
			if(rentCycle!=null) {
				BigDecimal settleAmount=BigDecimal.ZERO;
				UserPayment paidRentPayment=null;
				UserPayment paidPayment=null;

				UserBookings details=new UserBookings();
				details.setUserId(booking.getTenantId());
				details.setUserBookingsTenantId(booking.getTenantId());
				details.setUserBookingsId(booking.getBookingId());
				details.setUserBookingsPropertyId(booking.getPropertyId());
				details.setUserBookingsPgOwnerId(ownerId);
				details.setUserBookingsWebCheckIn(true);
				details.setUserBookingsDate(Timestamp.valueOf(LocalDateTime.now()));
				details.setUserBookingsWebCheckOut(false);
				details.setUserBookingsIsCancelled(false);
				userBookingDetails.add(details);

				UserMaster master=userDBImpl.findUserMaster(booking.getTenantId());
				if(master!=null) {
					master.setUserStatus(ZoyConstant.ACTIVE);
					userMasters.add(master);
				}

				UserPgDetails userPgDetail=new UserPgDetails();
				userPgDetail.setUserId(booking.getTenantId());
				userPgDetail.setUserPgBookingId(booking.getBookingId());
				userPgDetail.setUserPgRentalNeedDetailsIsAgreed(true);
				userPgDetail.setUserPgRentalNeedDetailsTimestamp(new Timestamp(new Date().getTime()));;
				userPgDetail.setUserPgTenantId(booking.getTenantId());
				userPgDetail.setUserPgOwnerId(ownerId);
				userPgDetail.setUserPgPropertyId(booking.getPropertyId());
				userPgDetails.add(userPgDetail);

				PgOwnerUserStatus ownerUserStatus=new PgOwnerUserStatus();
				ownerUserStatus.setPgOwnerId(ownerId);
				ownerUserStatus.setBedId(booking.getSelectedBed());
				ownerUserStatus.setPropertyId(booking.getPropertyId());
				ownerUserStatus.setUserBookingsId(booking.getBookingId());
				ownerUserStatus.setUserId(booking.getTenantId());
				ownerUserStatus.setPgTenantStatus(true);
				userStatus.add(ownerUserStatus);

				if(booking.getSecurityDeposit()!=null && !booking.getSecurityDeposit().equals(BigDecimal.ZERO)) {
					BigDecimal depositAmount=booking.getSecurityDeposit().setScale(2, RoundingMode.HALF_UP);
					List<String[]> duesId=ownerDBImpl.getPropertyDueDetails(booking.getPropertyId(),ZoyConstant.SECURITY_DEPOSIT);
					UserDues paidDue=saveDuesWithOutGst(booking,depositAmount,duesId,ZoyConstant.SECURITY_DEPOSIT);

					paidPayment=saveUserPayment(booking,paidDue,depositAmount,ZoyConstant.SECURITY_DEPOSIT);
					settleAmount=settleAmount.add(paidPayment.getUserPaymentPayableAmount());

					UserPaymentDue due=new UserPaymentDue();
					due.setUserId(booking.getTenantId());
					due.setUserMoneyDueId(paidDue.getUserMoneyDueId());
					due.setUserPaymentId(paidPayment.getUserPaymentId());
					uploadDBImpl.saveUserPaymentDue(due);

					UserBookingPaymentId userPaymentId = new UserBookingPaymentId();
					userPaymentId.setUserBookingsId(booking.getBookingId());
					userPaymentId.setUserId(booking.getTenantId());
					userPaymentId.setUserPaymentId(paidPayment.getUserPaymentId());

					UserBookingPayment userBookingPayment = new UserBookingPayment();
					userBookingPayment.setId(userPaymentId);
					uploadDBImpl.saveUserBookingPayment(userBookingPayment);
				}

				TenantList tenantDetails=userCsvDetails.get(booking.getTenantId());
				if(tenantDetails.getRentPaid().equals("Yes")) {
					//BigDecimal rentAmount=booking.getCalFixedRent().setScale(2, RoundingMode.HALF_UP);
					BigDecimal rentAmount=booking.getFixedRent().setScale(2, RoundingMode.HALF_UP);
					UserDues paidDue=saveDuesWithOutGst(booking,rentAmount,duesType,ZoyConstant.RENT_DUE);

					paidRentPayment=saveUserPayment(booking,paidDue,rentAmount,ZoyConstant.RENT_DUE);
					settleAmount=settleAmount.add(paidRentPayment.getUserPaymentPayableAmount());

					UserPaymentDue due=new UserPaymentDue();
					due.setUserId(booking.getTenantId());
					due.setUserMoneyDueId(paidDue.getUserMoneyDueId());
					due.setUserPaymentId(paidRentPayment.getUserPaymentId());
					uploadDBImpl.saveUserPaymentDue(due);

					Double balanceRent = booking.getCalFixedRent().doubleValue()-booking.getFixedRent().doubleValue();
					if(balanceRent > 0) {
						BigDecimal balRent=calcBalanceRent(booking,tenantDetails);
						if(balRent.doubleValue() > 0)
							saveDues(booking,balRent,duesType,ZoyConstant.RENT_DUE);
					}
				} else {
					BigDecimal calcFixedRent=calcActualRent(tenantDetails,booking.getFixedRent().doubleValue());
					saveDues(booking,calcFixedRent,duesType,ZoyConstant.RENT_DUE);
				}

				ZoyPgBedDetails pgBedDetails=ownerDBImpl.getBedsId(booking.getSelectedBed());
				pgBedDetails.setBedId(booking.getSelectedBed());
				pgBedDetails.setBedAvailable("occupied");
				bedDetails.add(pgBedDetails);

				if(settleAmount.doubleValue() > 0) {
					ZoyPgOwnerSettlementStatus status=new ZoyPgOwnerSettlementStatus();
					status.setIsApproved(true);
					status.setIsRejected(false);
					status.setOwnerId(ownerId);
					status.setPaymentProcessDate(Timestamp.valueOf(LocalDateTime.now()));
					status.setPropertyId(booking.getPropertyId());
					status.setSettlementAmount(settleAmount);
					ZoyPgOwnerSettlementStatus settlement=uploadDBImpl.saveOwnerSettlementStatus(status);

					if(paidPayment!=null) {
						ZoyPgOwnerSettlementSplitUp firstPayment=new ZoyPgOwnerSettlementSplitUp();
						firstPayment.setPgOwnerSettlementId(settlement.getPgOwnerSettlementId());
						firstPayment.setUserId(booking.getTenantId());
						firstPayment.setBookingId(booking.getBookingId());
						firstPayment.setPaymentId(paidPayment.getUserPaymentId());
						uploadDBImpl.saveOwnerSettlementSlipUp(firstPayment);
					}
					if(paidRentPayment!=null) {
						ZoyPgOwnerSettlementSplitUp rentPayment=new ZoyPgOwnerSettlementSplitUp();
						rentPayment.setPgOwnerSettlementId(settlement.getPgOwnerSettlementId());
						rentPayment.setUserId(booking.getTenantId());
						rentPayment.setBookingId(booking.getBookingId());
						rentPayment.setPaymentId(paidRentPayment.getUserPaymentId());
						uploadDBImpl.saveOwnerSettlementSlipUp(rentPayment);
					}
				}
			}	
		} catch (Exception e) {
			log.error("Unexpected error occurred in createWebcheckIn for bookingId: " + booking.getBookingId(), e);
		}
	}

	private BigDecimal calcBalanceRent(ZoyPgOwnerBookingDetails booking, TenantList tenantDetails) {
		int rentCycleStartDay = Integer.parseInt(tenantDetails.getRentCycle().split("-")[0]);
		LocalDate checkInDate = booking.getInDate().toLocalDateTime().toLocalDate();
		LocalDate rentPaidTill = checkInDate.plusMonths(1);
		LocalDate nextCycleStart = rentPaidTill.withDayOfMonth(rentCycleStartDay);
		long unpaidDays = ChronoUnit.DAYS.between(rentPaidTill, nextCycleStart);
		int daysInMonth = rentPaidTill.lengthOfMonth();
		BigDecimal calRent=new BigDecimal((booking.getFixedRent().doubleValue()/daysInMonth)*unpaidDays).setScale(2, RoundingMode.HALF_UP);
		return calRent;
	}

	private BigDecimal calcActualRent(TenantList tenantDetails,Double fixedRent) {
		int rentCycleStartDay = Integer.parseInt(tenantDetails.getRentCycle().split("-")[0]);
		LocalDate currentDate = LocalDate.now(ZoneId.of(ZoyConstant.IST));
		LocalDate nextMonthDate = currentDate.plusMonths(1).withDayOfMonth(rentCycleStartDay).minusDays(1);
		LocalDateTime resultDateTime = LocalDateTime.of(nextMonthDate, LocalTime.MAX);
		Timestamp rentCycleNextMonthDate = Timestamp.valueOf(resultDateTime);

		long noOfRentCalc=getDiffofTimestamp(tenantDetails.getInDate(),rentCycleNextMonthDate);
		long daysInMonth = tenantDetails.getInDate().toLocalDateTime().toLocalDate().lengthOfMonth();
		//Calculate Month rent
		BigDecimal calRent=new BigDecimal((fixedRent/daysInMonth)*noOfRentCalc).setScale(2, RoundingMode.HALF_UP);
		return calRent;
	}

	private UserPayment saveUserPayment(ZoyPgOwnerBookingDetails booking, UserDues paidDue, BigDecimal rentAmount,String description) {
		UserPayment rentPayment=new UserPayment();
		rentPayment.setUserId(booking.getTenantId());
		rentPayment.setUserPaymentBookingId(booking.getBookingId());
		rentPayment.setUserMoneyDueId(paidDue.getUserMoneyDueId());
		rentPayment.setUserPaymentPayableAmount(rentAmount);
		rentPayment.setUserPaymentTimestamp(new Timestamp(System.currentTimeMillis()));
		rentPayment.setUserPaymentGst(BigDecimal.ZERO);
		rentPayment.setUserPaymentPaymentStatus("success");
		rentPayment.setUserPaymentZoyPaymentMode("Cash");
		rentPayment.setUserPaymentZoyPaymentType(description);
		rentPayment.setUserPaymentSgst(paidDue.getUserCgstAmount());
		rentPayment.setUserPaymentCgst(paidDue.getUserSgstAmount());
		rentPayment.setUserPaymentIgst(paidDue.getUserIgstAmount());
		rentPayment.setUserPaymentSgstPercentage(paidDue.getUserCgstPercentage());
		rentPayment.setUserPaymentCgstPercentage(paidDue.getUserSgstPercentage());
		rentPayment.setUserPaymentIgstPercentage(paidDue.getUserIgstPercentage());
		return uploadDBImpl.saveUserPayment(rentPayment);
	}


	private UserDues saveDues(ZoyPgOwnerBookingDetails booking, BigDecimal rentAmount, List<String[]> duesType,String description) {
		GstCalc gstCalc=this.calcGstChargesPercentage(booking.getFixedRent().doubleValue(), charges, rentAmount.doubleValue(), booking.getPropertyId());
		BigDecimal totalGst = BigDecimal.valueOf(gstCalc.getCgst()).add(BigDecimal.valueOf(gstCalc.getSgst())).add(BigDecimal.valueOf(gstCalc.getIgst()));
		BigDecimal totalCalcRentGst = rentAmount.add(totalGst).setScale(2, RoundingMode.HALF_UP);
		UserDues dues=new UserDues();
		dues.setUserId(booking.getTenantId());
		dues.setUserBookingId(booking.getBookingId());
		dues.setUserMoneyDueAmount(totalCalcRentGst);
		LocalDateTime inDate=LocalDateTime.of(booking.getInDate().toLocalDateTime().toLocalDate().plusMonths(1), LocalTime.MAX);;
		Timestamp endDate = Timestamp.valueOf(inDate);
		dues.setUserMoneyDueBillEndDate(endDate);
		dues.setUserMoneyDueBillStartDate(booking.getInDate());
		dues.setUserMoneyDueDescription(description);
		dues.setUserMoneyDueBillingType(duesType.get(0)[1]);
		dues.setUserMoneyDueTimestamp(booking.getInDate());
		dues.setUserMoneyDueType(duesType.get(0)[0]);
		dues.setUserDueAmount(rentAmount);
		dues.setUserCgstAmount(new BigDecimal(gstCalc.getCgst()));
		dues.setUserSgstAmount(new BigDecimal(gstCalc.getSgst()));
		dues.setUserIgstAmount(new BigDecimal(gstCalc.getIgst()));
		dues.setUserCgstPercentage(new BigDecimal(gstCalc.getCgstPecenatge()));
		dues.setUserSgstPercentage(new BigDecimal(gstCalc.getSgstPecenatge()));
		dues.setUserIgstPercentage(new BigDecimal(gstCalc.getIgstPecenatge()));
		return uploadDBImpl.saveUserDues(dues);
	}
	
	private UserDues saveDuesWithOutGst(ZoyPgOwnerBookingDetails booking, BigDecimal rentAmount, List<String[]> duesType,String description) {
		UserDues dues=new UserDues();
		dues.setUserId(booking.getTenantId());
		dues.setUserBookingId(booking.getBookingId());
		dues.setUserMoneyDueAmount(rentAmount);
		LocalDateTime inDate=LocalDateTime.of(booking.getInDate().toLocalDateTime().toLocalDate().plusMonths(1), LocalTime.MAX);;
		Timestamp endDate = Timestamp.valueOf(inDate);
		dues.setUserMoneyDueBillEndDate(endDate);
		dues.setUserMoneyDueBillStartDate(booking.getInDate());
		dues.setUserMoneyDueDescription(description);
		dues.setUserMoneyDueBillingType(duesType.get(0)[1]);
		dues.setUserMoneyDueTimestamp(booking.getInDate());
		dues.setUserMoneyDueType(duesType.get(0)[0]);
		dues.setUserDueAmount(rentAmount);
		dues.setUserCgstAmount(BigDecimal.ZERO);
		dues.setUserSgstAmount(BigDecimal.ZERO);
		dues.setUserIgstAmount(BigDecimal.ZERO);
		dues.setUserCgstPercentage(BigDecimal.ZERO);
		dues.setUserSgstPercentage(BigDecimal.ZERO);
		dues.setUserIgstPercentage(BigDecimal.ZERO);
		return uploadDBImpl.saveUserDues(dues);
	}

	private String createUser(TenantList tenantDetails) {
		UserMaster master=new UserMaster();
		master.setUserMobile(tenantDetails.getPhoneNumber());
		master.setUserEmail(tenantDetails.getEmail());
		master.setUserGender(tenantDetails.getGender());
		master.setUserFirstName(tenantDetails.getFirstName());
		master.setUserLastName(tenantDetails.getLastName());
		master.setUserEkycIsEkycVerified(false);
		master.setUserEkycIsVideoVerified(false);
		master.setUserEkycPaid(false);
		master.setUserStatus(ZoyConstant.INACTIVE);
		uploadDBImpl.saveUser(master);
		List<NotificationModeMaster> modeMaster=uploadDBImpl.findAllNotificationMode();
		List<UserNotifications> notifications =new ArrayList<>();
		for(NotificationModeMaster mode:modeMaster) {
			UserNotifications notification =new UserNotifications();
			notification.setNotificationModeId(mode.getNotificationModeId());
			notification.setUserId(master.getUserId());
			notifications.add(notification);
		}
		uploadDBImpl.saveAllUserNotification(notifications);

		Whatsapp whatsapp=new Whatsapp();
		whatsapp.tonumber("+91"+tenantDetails.getPhoneNumber());
		whatsapp.templateid(ZoyConstant.ZOY_TENANT_REG_WELCOME_MSG);
		Map<Integer,String> parm=new HashMap<>();
		parm.put(1, tenantDetails.getFirstName());
		whatsapp.setParams(parm);
		whatsAppService.sendWhatsappMessage(whatsapp);

		RegisterUser registerUser=new RegisterUser();
		registerUser.setEmail(tenantDetails.getEmail());
		registerUser.setFirstName(tenantDetails.getFirstName());
		zoyEmailService.sendUserWelcomeMail(registerUser);

		return master.getUserId();
	}

	private void createUserDetails(TenantList tenantDetails, String userId,String propertyId,
			List<UserDetails> userDetails) {
		UserDetails details=uploadDBImpl.findUserDetails(userId);
		if(details!=null) {
			details.setPersonalDob(tenantDetails.getDateOfBirth());
			details.setPersonalEmail(tenantDetails.getEmail());
			details.setPersonalGender(tenantDetails.getGender());
			details.setPersonalName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());
			details.setPersonalPermanentAddress(tenantDetails.getPermanentAddress());
			details.setPersonalPhoneNum(tenantDetails.getPhoneNumber());
			userDetails.add(details);
		} else {
			details=new UserDetails();
			details.setPersonalDob(tenantDetails.getDateOfBirth());
			details.setPersonalEmail(tenantDetails.getEmail());
			details.setPersonalGender(tenantDetails.getGender());
			details.setPersonalName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());
			details.setPersonalPermanentAddress(tenantDetails.getPermanentAddress());
			details.setPersonalPhoneNum(tenantDetails.getPhoneNumber());
			details.setUserId(userId);
			userDetails.add(details);
		}
	}

	private void createUserBooking(TenantList tenantDetails, String userId, String propertyId,
			List<ZoyPgOwnerBookingDetails> zoyPgOwnerBookingDetails) {
		List<String[]> ids=uploadDBImpl.findFloorRoomBedIdsByPropertyName(propertyId,tenantDetails.getRoom(),tenantDetails.getBedNumber());
		String floorId = "" , roomId ="", bedId="",shareId="";
		if(ids.size()>0) {
			floorId=ids.get(0)[0];
			roomId=ids.get(0)[2];
			bedId=ids.get(0)[4];
			shareId=ids.get(0)[6];
		}

		String cycleId=ownerDBImpl.findRentCycleByName(tenantDetails.getRentCycle());
		int rentCycleStartDay = Integer.parseInt(tenantDetails.getRentCycle().split("-")[0]);
		LocalDate currentDate = LocalDate.now(ZoneId.of(ZoyConstant.IST));
		LocalDateTime actualRentDate=LocalDate.of(currentDate.getYear(), currentDate.getMonth(), rentCycleStartDay).atStartOfDay();

		ZoyPgRoomDetails details=uploadDBImpl.getRoomDetails(roomId);
		Pair<Timestamp, Timestamp> date=getMonthStartEndDate();

		ZoyPgOwnerBookingDetails bookingDetails=new ZoyPgOwnerBookingDetails();
		bookingDetails.setCurrMonthEndDate(date.getSecond());
		bookingDetails.setCurrMonthStartDate(date.getFirst());
		bookingDetails.setDue(BigDecimal.ZERO);
		bookingDetails.setActualRent(new BigDecimal(details.getRoomMonthlyRent()));
		if (tenantDetails.getRentAmount().compareTo(BigDecimal.ZERO) > 0) {
			bookingDetails.setFixedRent(tenantDetails.getRentAmount());
			BigDecimal rentDiscount = new BigDecimal(details.getRoomMonthlyRent()-tenantDetails.getRentAmount().doubleValue());
			bookingDetails.setRentDiscount(rentDiscount);
		} else {
			bookingDetails.setFixedRent(new BigDecimal(details.getRoomMonthlyRent()));
			bookingDetails.setRentDiscount(BigDecimal.ZERO);
		}
		bookingDetails.setFloor(floorId);
		bookingDetails.setGender(tenantDetails.getGender());
		bookingDetails.setGst(BigDecimal.ZERO);
		Timestamp checkInDate;
		if(tenantDetails.getInDate()==null) 
			checkInDate=Timestamp.valueOf(actualRentDate);
		else 
			checkInDate=tenantDetails.getInDate();
		bookingDetails.setInDate(checkInDate);
		bookingDetails.setIsTermsAccepted(false);
		bookingDetails.setLockInPeriod(cycleId);
		bookingDetails.setName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());

		long noOfDays=getDiffofTimestamp(checkInDate,tenantDetails.getOutDate());

		//Timestamp checkInOneMonth= Timestamp.valueOf(checkInDate.toLocalDateTime().plusMonths(1).with(LocalTime.of(23, 59, 59)));
		bookingDetails.setNoOfDays(String.valueOf((int)noOfDays));
		//Calculate Month rent
		BigDecimal calRent=calcActualRent(tenantDetails,bookingDetails.getFixedRent().doubleValue());
		
		GstCalc gstCalc=this.calcGstChargesPercentage(bookingDetails.getFixedRent().doubleValue(), charges, calRent.doubleValue(), propertyId);
		
		BigDecimal totalGst = BigDecimal.valueOf(gstCalc.getCgst()).add(BigDecimal.valueOf(gstCalc.getSgst())).add(BigDecimal.valueOf(gstCalc.getIgst()));
		BigDecimal totalCalcRentGst = calRent.add(totalGst).setScale(2, RoundingMode.HALF_UP);

		bookingDetails.setCalFixedRent(totalCalcRentGst);
		bookingDetails.setOutDate(tenantDetails.getOutDate());
		bookingDetails.setPhoneNumber(tenantDetails.getPhoneNumber());
		bookingDetails.setPropertyId(propertyId);
		bookingDetails.setRentCycleEndDate(date.getSecond());
		bookingDetails.setRoom(roomId);
		bookingDetails.setSecurityDeposit(tenantDetails.getDepositPaid());
		bookingDetails.setPaidDeposit(tenantDetails.getDepositPaid());
		bookingDetails.setSelectedBed(bedId);
		bookingDetails.setShare(shareId);
		bookingDetails.setBookingMode("Offline");
		bookingDetails.setTenantId(userId);
		bookingDetails.setDepositPaid(true);
		bookingDetails.setIsTermsAccepted(true);
		bookingDetails.setActualInDate(tenantDetails.getActualInDate());
		if(checkTenantAge(tenantDetails.getDateOfBirth())) {
			if(checkRentalAgreement(noOfDays))
				bookingDetails.setRentalAgreement(true);
		}
		zoyPgOwnerBookingDetails.add(bookingDetails);
	}

	public Boolean checkRentalAgreement(double days) {
		ZoyPgNoRentalAgreement duration=ownerDBImpl.findNoRentAgreementDuration();
		if(duration!=null) {
			if(days >= duration.getNoRentalAgreementDays()) 
				return true;
		}
		return false;
	}

	public Boolean checkTenantAge(Timestamp dob) {
		try {
			LocalDate birthDate = dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();
			int years = Period.between(birthDate, currentDate).getYears();
			return years >= 18;
		} catch (Exception e) {
			return false;
		}
	}


	private long getDiffofTimestamp(Timestamp currentDate, Timestamp outDate) {
		//LocalDateTime startDateTime = currentDate.toLocalDateTime();
		//LocalDateTime endDateTime = outDate.toLocalDateTime();
		//long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
		long daysBetween = ChronoUnit.DAYS.between(currentDate.toLocalDateTime().toLocalDate(),outDate.toLocalDateTime().toLocalDate())+1;
		return daysBetween;
	}


	public static Timestamp getOneMonthBackTimestamp() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime oneMonthBack = currentDateTime.minus(1, ChronoUnit.MONTHS);
		return Timestamp.valueOf(oneMonthBack);
	}

	public Pair<Timestamp,Timestamp> getMonthStartEndDate(){
		LocalDate today = LocalDate.now();
		LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay(ZoneId.of(ZoyConstant.IST)).toLocalDateTime();
		LocalDateTime endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59).atZone(ZoneId.of(ZoyConstant.IST)).toLocalDateTime();
		Timestamp startTimestamp = Timestamp.valueOf(startOfMonth);
		Timestamp endTimestamp = Timestamp.valueOf(endOfMonth);
		return new Pair<Timestamp, Timestamp>(startTimestamp, endTimestamp);
	}

	public GstCalc calcGstChargesPercentage(Double monthRent, ZoyPgRentGst zoyPgRentGst,Double calcFixedRent,String propertyState) {
		GstCalc gstCalc = new GstCalc();
		Double igstPercentage=0.0;
		Double sgstPercentage=0.0;
		Double cgstPercentage=0.0;
		Double igstCharges = 0.0;
		Double sgstCharges = 0.0;
		Double cgstCharges = 0.0;
		if(monthRent >= zoyPgRentGst.getMonthlyRent().doubleValue()) {
			if(propertyState.equalsIgnoreCase(zoyCompanyProfileMaster.getState())){
				sgstPercentage= zoyPgRentGst.getSgstPercentage().doubleValue();
				cgstPercentage = zoyPgRentGst.getCgstPercentage().doubleValue();
				sgstCharges = roundToTwoDecimalPlaces((calcFixedRent) * sgstPercentage/100);
				cgstCharges = roundToTwoDecimalPlaces((calcFixedRent) * cgstPercentage/100);
				gstCalc.setCgst(cgstCharges);
				gstCalc.setSgst(sgstCharges);
				gstCalc.setIgst(igstCharges);
				gstCalc.setCgstPecenatge(cgstPercentage);
				gstCalc.setSgstPecenatge(sgstPercentage);
				gstCalc.setIgstPecenatge(igstPercentage);
			} else {
				igstPercentage= zoyPgRentGst.getIgstPercentage().doubleValue();
				igstCharges = roundToTwoDecimalPlaces((calcFixedRent) * igstPercentage/100);
				gstCalc.setCgst(cgstCharges);
				gstCalc.setSgst(sgstCharges);
				gstCalc.setIgst(igstCharges);
				gstCalc.setCgstPecenatge(cgstPercentage);
				gstCalc.setSgstPecenatge(sgstPercentage);
				gstCalc.setIgstPecenatge(igstPercentage);
			}
		} else {
			gstCalc.setCgst(cgstCharges);
			gstCalc.setSgst(sgstCharges);
			gstCalc.setIgst(igstCharges);
			gstCalc.setCgstPecenatge(cgstPercentage);
			gstCalc.setSgstPecenatge(sgstPercentage);
			gstCalc.setIgstPecenatge(igstPercentage);
		}
		return gstCalc;
	}

	public GstCalc calcGstChargesPercentagePerDay(Double rent,long days,String propertyState,long daysInMonth) {
		GstCalc gstCalc = new GstCalc();
		Double igstPercentage=0.0;
		Double sgstPercentage=0.0;
		Double cgstPercentage=0.0;
		Double igstCharges = 0.0;
		Double sgstCharges = 0.0;
		Double cgstCharges = 0.0;
		if(rent >= charges.getMonthlyRent().doubleValue()/daysInMonth) {
			if(propertyState.equalsIgnoreCase(zoyCompanyProfileMaster.getState())){
				sgstPercentage= charges.getSgstPercentage().doubleValue();
				cgstPercentage = charges.getCgstPercentage().doubleValue();
				sgstCharges = roundToTwoDecimalPlaces((rent) * sgstPercentage/100);
				cgstCharges = roundToTwoDecimalPlaces((rent) * cgstPercentage/100);
				gstCalc.setCgst(cgstCharges*days);
				gstCalc.setSgst(sgstCharges*days);
				gstCalc.setIgst(igstCharges*days);
				gstCalc.setCgstPecenatge(cgstPercentage);
				gstCalc.setSgstPecenatge(sgstPercentage);
				gstCalc.setIgstPecenatge(igstPercentage);
				gstCalc.setGst(gstCalc.getCgst()+gstCalc.getSgst()+gstCalc.getIgst());
				gstCalc.setGstPecenatge(gstCalc.getCgstPecenatge()+gstCalc.getSgstPecenatge()+gstCalc.getIgstPecenatge());
			} else {
				igstPercentage= charges.getIgstPercentage().doubleValue();
				igstCharges = roundToTwoDecimalPlaces((rent) * igstPercentage/100);
				gstCalc.setCgst(cgstCharges*days);
				gstCalc.setSgst(sgstCharges*days);
				gstCalc.setIgst(igstCharges*days);
				gstCalc.setCgstPecenatge(cgstPercentage);
				gstCalc.setSgstPecenatge(sgstPercentage);
				gstCalc.setIgstPecenatge(igstPercentage);
				gstCalc.setGst(gstCalc.getCgst()+gstCalc.getSgst()+gstCalc.getIgst());
				gstCalc.setGstPecenatge(gstCalc.getCgstPecenatge()+gstCalc.getSgstPecenatge()+gstCalc.getIgstPecenatge());
			}
		} else {
			gstCalc.setCgst(cgstCharges*days);
			gstCalc.setSgst(sgstCharges*days);
			gstCalc.setIgst(igstCharges*days);
			gstCalc.setCgstPecenatge(cgstPercentage);
			gstCalc.setSgstPecenatge(sgstPercentage);
			gstCalc.setIgstPecenatge(igstPercentage);
			gstCalc.setGst(gstCalc.getCgst()+gstCalc.getSgst()+gstCalc.getIgst());
			gstCalc.setGstPecenatge(gstCalc.getCgstPecenatge()+gstCalc.getSgstPecenatge()+gstCalc.getIgstPecenatge());
		}
		return gstCalc;
	}
	
	public Double roundToTwoDecimalPlaces(Double value) {
		if(value==null)
			return 0.0;
		return BigDecimal.valueOf(value)
				.setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
	}

	public void generateSendRentalAgreement(UserMaster master,ZoyPgPropertyDetails propertyDetail, ZoyPgOwnerBookingDetails saveMyBookings) {
		try {
			List<String[]> data=ownerDBImpl.getRentalAgreementDetails(saveMyBookings.getBookingId());
			GenerateBulkUploadRentalPdf pdf = new GenerateBulkUploadRentalPdf();
			pdf.setTenantName(data.get(0)[0].toString());
			pdf.setTenantAddress(data.get(0)[1].toString());
			pdf.setOwnerName(data.get(0)[2].toString());
			pdf.setOwnerPhNo(data.get(0)[3].toString());
			pdf.setOwnerAddress(data.get(0)[4].toString());
			pdf.setPgAddress(data.get(0)[4].toString());
			pdf.setPgCity(data.get(0)[5].toString());
			pdf.setShareName(data.get(0)[6].toString());
			pdf.setFloorName(data.get(0)[7].toString());
			pdf.setRoomName(data.get(0)[8].toString());
			pdf.setSecurityWord(numberToWordsService.numberToWords(Double.valueOf(data.get(0)[9].toString())));
			pdf.setRentWord(numberToWordsService.numberToWords(Double.valueOf(data.get(0)[10].toString())));
			pdf.setInDate(data.get(0)[11].toString());
			pdf.setOutDate(data.get(0)[12].toString());
			pdf.setModeOfPayment(data.get(0)[13] != null ? data.get(0)[13].toString() : "Cash");
			pdf.setRentCycle(data.get(0)[14].toString());
			pdf.setNoOfDays(data.get(0)[15].toString());
			pdf.setOfficeAddress(data.get(0)[16].toString());
			pdf.setPgName(propertyDetail.getPropertyName());         
			pdf.setIsRental(saveMyBookings.getRentalAgreement());               
			pdf.setBookingId(saveMyBookings.getBookingId());             

			processRentalPdf(master.getUserId(),master.getUserEmail(), pdf);
		} catch (Exception e) {
			log.error("Error generating renatla pdf " + e);
		}
	}

	public void processRentalPdf(String userId,String userEmail,GenerateBulkUploadRentalPdf generatePDFRental) {
		try {
			String url = zoyServerCustomerUrl + userId +"/"+userEmail+"/generatePdfRental";
			HttpHeaders headers = new HttpHeaders();
			setZoyServerHeader(headers);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<GenerateBulkUploadRentalPdf> entityReq = new HttpEntity<>(generatePDFRental, headers);
			ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entityReq, String.class);
			log.info(resp.getBody());
		} catch (Exception e) {
			log.error("Error in calling/getting generating pdf rental api " + e.getMessage());
		}
	}

	private void setZoyServerHeader(HttpHeaders headers) {
		byte[] credEncoded = Base64.getEncoder().encode((zoyServerUserName+":"+zoyServerPassword).getBytes());
		String authStringEnc = new String(credEncoded);
		headers.add("Authorization", "Basic " + authStringEnc);
		headers.setContentType(MediaType.APPLICATION_JSON);
	}



}
