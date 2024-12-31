package com.integration.zoy.service;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.integration.zoy.entity.UserBookings;
import com.integration.zoy.entity.UserDetails;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.entity.UserNotifications;
import com.integration.zoy.entity.UserPayment;
import com.integration.zoy.entity.UserPgDetails;
import com.integration.zoy.entity.ZoyPgBedDetails;
import com.integration.zoy.entity.ZoyPgFloorRooms;
import com.integration.zoy.entity.ZoyPgFloorRoomsId;
import com.integration.zoy.entity.ZoyPgOwnerBookingDetails;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
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
import com.integration.zoy.entity.ZoyPgTermsMaster;
import com.integration.zoy.model.PropertyRentalDetails;
import com.integration.zoy.model.RegisterUser;
import com.integration.zoy.repository.ZoyPgRoomAmenetiesId;
import com.integration.zoy.utils.CsvTenantDetails;
import com.integration.zoy.utils.GeneratePDFRental;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.Whatsapp;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvValidationException;

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


	public ResponseEntity<String> tenatantWriteDataPost(String ownerId,String propertyId,byte[] file) throws CsvValidationException {
		ResponseBody response=new ResponseBody();
		if (file.length==0) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setMessage("File is empty");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
		ZoyPgOwnerDetails userMasterOpt = uploadDBImpl.findPgOwnerById(ownerId);
		if (userMasterOpt==null) {
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.setMessage("Owner not found");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
		}
		try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(byteArrayInputStream);
				CSVReader csvReader = new CSVReaderBuilder(inputStreamReader).withSkipLines(2).build()) {
			CsvToBean<CsvTenantDetails> csvToBean = new CsvToBeanBuilder<CsvTenantDetails>(csvReader)
					.withType(CsvTenantDetails.class)
					.withIgnoreLeadingWhiteSpace(true)
					.build();
			List<UserDetails> userDetails=new ArrayList<>();
			List<ZoyPgOwnerBookingDetails> zoyPgOwnerBookingDetails = new ArrayList<>();
			List<UserBookings> userBookingDetails=new ArrayList<>();
			List<PgOwnerUserStatus> userStatus=new ArrayList<>();
			List<UserPgDetails> userPgDetails=new ArrayList<>();
			List<UserPayment> userPayment=new ArrayList<>();
			List<ZoyPgBedDetails> bedDetails=new ArrayList<>();
			List<CsvTenantDetails> tenantDetailsList = csvToBean.parse();
			for(CsvTenantDetails tenantDetails:tenantDetailsList) {
				UserMaster userMaster=uploadDBImpl.findUserMaster(tenantDetails.getPhoneNumber(),tenantDetails.getEmail());
				if(userMaster==null) {
					String userId=createUser(tenantDetails);
					createUserDetails(tenantDetails,userId,propertyId,userDetails);
					createUserBooking(tenantDetails,userId,propertyId,zoyPgOwnerBookingDetails);
				} else {
					createUserDetails(tenantDetails,userMaster.getUserId(),propertyId,userDetails);
					createUserBooking(tenantDetails,userMaster.getUserId(),propertyId,zoyPgOwnerBookingDetails);

				}
			}
			uploadDBImpl.saveAllUserDetails(userDetails);
			List<ZoyPgOwnerBookingDetails> bookingDetails=uploadDBImpl.saveAllOwnerBooking(zoyPgOwnerBookingDetails);
			for(ZoyPgOwnerBookingDetails booking:bookingDetails) {
				createWebcheckIn(ownerId,booking,userBookingDetails,userStatus,userPgDetails,userPayment,bedDetails);
			}
			uploadDBImpl.saveAllUserBookings(userBookingDetails);
			uploadDBImpl.saveAllUserPgDetails(userPgDetails);
			uploadDBImpl.saveAllOwnerUserStatus(userStatus);
			uploadDBImpl.saveAllUserPayment(userPayment);
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

				generateSendRentalAgreement(master,zoyPgOwnerDetails,propertyDetail,saveMyBookings);
			}

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("File processed successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (IOException e) {
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Error processing file" + e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	private void createWebcheckIn(String ownerId,ZoyPgOwnerBookingDetails booking, List<UserBookings> userBookingDetails, 
			List<PgOwnerUserStatus> userStatus, List<UserPgDetails> userPgDetails,List<UserPayment> userPayment,List<ZoyPgBedDetails> bedDetails) {
		try {
			ZoyPgRentCycleMaster rentCycle=uploadDBImpl.findRentCycle(booking.getLockInPeriod());
			if(rentCycle!=null) {
				UserBookings details=new UserBookings();
				details.setUserId(booking.getTenantId());
				details.setUserBookingsTenantId(booking.getTenantId());
				details.setUserBookingsId(booking.getBookingId());
				details.setUserBookingsPropertyId(booking.getPropertyId());
				details.setUserBookingsPgOwnerId(ownerId);
				details.setUserBookingsWebCheckIn(true);
				details.setUserBookingsDate(new Timestamp(System.currentTimeMillis()));
				details.setUserBookingsWebCheckOut(false);
				details.setUserBookingsIsCancelled(false);
				userBookingDetails.add(details);

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

//				UserPayment payment=new UserPayment();
//				payment.setUserId(booking.getTenantId());
//				payment.setUserPaymentBookingId(booking.getBookingId());
//				if(booking.getPaidDeposit()!=null && !booking.getPaidDeposit().equals(BigDecimal.ZERO))
//					payment.setUserPaymentPayableAmount(booking.getPaidDeposit());
//				else 
//					payment.setUserPaymentPayableAmount(booking.getFixedRent());
//				payment.setUserPaymentPaymentStatus("success");
//				payment.setUserPaymentZoyPaymentMode("Cash");
//				payment.setUserPaymentZoyPaymentType("Deposit");
//				userPayment.add(payment);

				ZoyPgBedDetails pgBedDetails=ownerDBImpl.getBedsId(booking.getSelectedBed());
				pgBedDetails.setBedId(booking.getSelectedBed());
				pgBedDetails.setBedAvailable("occupied");
				bedDetails.add(pgBedDetails);
			}	
		}catch (Exception e) {
	        log.error("Unexpected error occurred in createWebcheckIn for bookingId: " + booking.getBookingId(), e);
	    }
	}

	private Timestamp getPreviousDate(String inputDate) {
		int dayOfMonth = Integer.valueOf(inputDate);
		LocalDate currentDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), dayOfMonth);
		LocalDate previousMonthDate = currentDate.minusMonths(1);
		LocalDateTime previousMonthDateTime = previousMonthDate.atStartOfDay();
		return Timestamp.valueOf(previousMonthDateTime);
	}

	private String createUser(CsvTenantDetails tenantDetails) {
		UserMaster master=new UserMaster();
		master.setUserMobile(tenantDetails.getPhoneNumber());
		master.setUserEmail(tenantDetails.getEmail());
		master.setUserGender(tenantDetails.getGender());
		master.setUserFirstName(tenantDetails.getFirstName());
		master.setUserLastName(tenantDetails.getLastName());
		master.setUserEkycIsEkycVerified(false);
		master.setUserEkycIsVideoVerified(false);
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

	private void createUserDetails(CsvTenantDetails tenantDetails, String userId,String propertyId,
			List<UserDetails> userDetails) {
		UserDetails details=uploadDBImpl.findUserDetails(userId);
		if(details!=null) {
			details.setPersonalDob(tenantDetails.getDob());
			details.setPersonalEmail(tenantDetails.getEmail());
			details.setPersonalGender(tenantDetails.getGender());
			details.setPersonalName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());
			details.setPersonalPermanentAddress(tenantDetails.getPermanentAddress());
			details.setPersonalPhoneNum(tenantDetails.getPhoneNumber());
			userDetails.add(details);
		} else {
			details=new UserDetails();
			details.setPersonalDob(tenantDetails.getDob());
			details.setPersonalEmail(tenantDetails.getEmail());
			details.setPersonalGender(tenantDetails.getGender());
			details.setPersonalName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());
			details.setPersonalPermanentAddress(tenantDetails.getPermanentAddress());
			details.setPersonalPhoneNum(tenantDetails.getPhoneNumber());
			details.setUserId(userId);
			userDetails.add(details);
		}
	}

	private void createUserBooking(CsvTenantDetails tenantDetails, String userId, String propertyId,
			List<ZoyPgOwnerBookingDetails> zoyPgOwnerBookingDetails) {
		List<String[]> ids=uploadDBImpl.findFloorRoomBedIdsByPropertyName(propertyId,tenantDetails.getRoom(),tenantDetails.getBedNumber());
		String floorId = "" , floorName="",roomId ="", roomName="",bedId="",bedName="",shareId="",shareName="",rentCycleName="",rentCycleId="";
		if(ids.size()>0) {
			floorId=ids.get(0)[0];
			floorName=ids.get(0)[1];
			roomId=ids.get(0)[2];
			roomName=ids.get(0)[3];
			bedId=ids.get(0)[4];
			bedName=ids.get(0)[5];
			shareId=ids.get(0)[6];
			shareName=ids.get(0)[7];
			rentCycleId=ids.get(0)[8];
			rentCycleName=ids.get(0)[9];
		}
		String cycleId=ownerDBImpl.findRentCycleByName("01-01");
		ZoyPgOwnerBookingDetails bookingDetails=new ZoyPgOwnerBookingDetails();
		ZoyPgRoomDetails details=uploadDBImpl.getRoomDetails(roomId);
		Pair<Timestamp, Timestamp> date=getMonthStartEndDate();
		bookingDetails.setCalFixedRent(new BigDecimal(details.getRoomDailyRent()));
		bookingDetails.setCurrMonthEndDate(date.getSecond());
		bookingDetails.setCurrMonthStartDate(date.getFirst());
		bookingDetails.setDue(BigDecimal.ZERO);
		bookingDetails.setFixedRent(new BigDecimal(details.getRoomMonthlyRent()));
		bookingDetails.setFloor(floorId);
		bookingDetails.setGender(tenantDetails.getGender());
		bookingDetails.setGst(BigDecimal.ZERO);
		Timestamp currentDate=new Timestamp(System.currentTimeMillis());
		bookingDetails.setInDate(currentDate);
		bookingDetails.setIsTermsAccepted(false);
		bookingDetails.setLockInPeriod(cycleId);
		bookingDetails.setName(tenantDetails.getFirstName()+" "+ tenantDetails.getLastName());
		long noOfDays=getDiffofTimestamp(currentDate,tenantDetails.getOutDate());
		bookingDetails.setNoOfDays(String.valueOf(noOfDays));
		bookingDetails.setOutDate(tenantDetails.getOutDate());
		bookingDetails.setPhoneNumber(tenantDetails.getPhoneNumber());
		bookingDetails.setPropertyId(propertyId);
		bookingDetails.setRentCycleEndDate(date.getSecond());
		bookingDetails.setRoom(roomId);
		bookingDetails.setSecurityDeposit(BigDecimal.ZERO);
		//bookingDetails.setPaidDeposit(tenantDetails.getDepositPaid());
		bookingDetails.setSelectedBed(bedId);
		bookingDetails.setShare(shareId);
		bookingDetails.setBookingMode("Offline");
		bookingDetails.setTenantId(userId);
		zoyPgOwnerBookingDetails.add(bookingDetails);
	}

	private long getDiffofTimestamp(Timestamp currentDate, Timestamp outDate) {
		LocalDateTime startDateTime = currentDate.toLocalDateTime();
		LocalDateTime endDateTime = outDate.toLocalDateTime();
		long daysBetween = ChronoUnit.DAYS.between(startDateTime.toLocalDate(), endDateTime.toLocalDate());
		return daysBetween;
	}


	public static Timestamp getOneMonthBackTimestamp() {
		LocalDateTime currentDateTime = LocalDateTime.now();
		LocalDateTime oneMonthBack = currentDateTime.minus(1, ChronoUnit.MONTHS);
		return Timestamp.valueOf(oneMonthBack);
	}

	public Pair<Timestamp,Timestamp> getMonthStartEndDate(){
		LocalDate today = LocalDate.now();
		LocalDateTime startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
		LocalDateTime endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59);
		Timestamp startTimestamp = Timestamp.valueOf(startOfMonth);
		Timestamp endTimestamp = Timestamp.valueOf(endOfMonth);
		return new Pair<Timestamp, Timestamp>(startTimestamp, endTimestamp);
	}


	public ResponseEntity<String> zoyPartnerUserIdPropertyIdUploadXlsxPost( String ownerId,
			String propertyId,byte[] fileBytes) throws CsvValidationException {
		ResponseBody response = new ResponseBody();
		if (fileBytes.length==0) {
			return new ResponseEntity<>(gson.toJson("File is empty"), HttpStatus.BAD_REQUEST);
		}

		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes); Workbook workbook = new XSSFWorkbook(inputStream)) {
			// Fetch existing data
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

			Sheet sheet = workbook.getSheetAt(0); 
			for (Row row : sheet) {
				if (row.getRowNum() == 0 || row.getRowNum() == 1) 
					continue; 

				String floorName = getCellValue(row.getCell(0));
				String roomName = getCellValue(row.getCell(1));
				String roomType = getCellValue(row.getCell(2));
				String shareType = getCellValue(row.getCell(3));
				String area = getCellValue(row.getCell(4));
				String availableBed = getCellValue(row.getCell(5));
				String dailyRent = getCellValue(row.getCell(6));
				String monthlyRent = getCellValue(row.getCell(7));
				String amenities = getCellValue(row.getCell(8));
				String occupiedBed = getCellValue(row.getCell(9));
				String remarks = getCellValue(row.getCell(10));

				List<String> newRoomIds = new ArrayList<>();
				List<String> newBedIds = new ArrayList<>();

				if (!allFloors.containsValue(floorName)) {
					ZoyPgPropertyFloorDetails newFloorDetails = new ZoyPgPropertyFloorDetails();
					newFloorDetails.setFloorName(uploadDBImpl.checkDuplicateFloorName(floorName,propertyId));
					newFloorDetails.setFloorStatus(true);
					ZoyPgPropertyFloorDetails createdFloor = uploadDBImpl.createFloorDetail(newFloorDetails);
					allFloors.put(createdFloor.getFloorId(), floorName);
				}

				processNewBeds(availableBed!=""?Arrays.asList(availableBed.split(",")):new ArrayList<>(), "available", newBedIds);
				processNewBeds(occupiedBed!=""?Arrays.asList(occupiedBed.split(",")):new ArrayList<>(), "occupied", newBedIds);

				String roomTypeId = uploadDBImpl.getRoomTypeIdByRoomType(roomType);
				String shareTypeId = uploadDBImpl.getShareIdByShareType(shareType);

				ZoyPgRoomDetails newRoomDetails = new ZoyPgRoomDetails();
				String floorId=getFloorIdByName(allFloors,floorName);
				newRoomDetails.setRoomName(uploadDBImpl.checkDuplicateRoomName(roomName,floorId,propertyId));
				newRoomDetails.setRoomType(roomType);
				newRoomDetails.setShareId(shareTypeId);
				newRoomDetails.setRoomArea(Double.valueOf(area));
				newRoomDetails.setRoomDailyRent(Double.valueOf(dailyRent));
				newRoomDetails.setRoomMonthlyRent(Double.valueOf(monthlyRent));
				newRoomDetails.setRoomTypeId(roomTypeId);
				newRoomDetails.setRoomRemarks(remarks);
				newRoomDetails.setRoomStatus(true);
				ZoyPgRoomDetails createdRoom = uploadDBImpl.createRoom(newRoomDetails);
				mapRoomToBeds(createdRoom.getRoomId(), newBedIds);

				List<String> amenitiesIds = uploadDBImpl.getIdsOfByAmenitiesList(Arrays.asList(amenities.split(",")));
				mapRoomToAmenities(createdRoom.getRoomId(), amenitiesIds);

				//mapPropertyRentCycle(propertyId);			
				newRoomIds.add(createdRoom.getRoomId());

				mapFloorToRooms(floorId, newRoomIds);
				newFloorIds.add(floorId);
			}

			mapPropertyToFloors(propertyId, newFloorIds);

			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Property design posted successfully from Excel");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error processing Excel: " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setMessage("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	private void mapPropertyRentCycle(String propertyId) {
//		String rentId = uploadDBImpl.getRentCycleId("01-01");
//		ZoyPgPropertyRentCycle cycle=new ZoyPgPropertyRentCycle();
//		ZoyPgPropertyRentCycleId cycleId=new ZoyPgPropertyRentCycleId();
//		cycleId.setCycleId(rentId);
//		cycleId.setPropertyId(propertyId);
//		cycle.setId(cycleId);
//		uploadDBImpl.saveRentCycle(cycle);	
//	}

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

	private String getCellValue(Cell cell) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue().toString();
			} else {
				return Double.toString(cell.getNumericCellValue());
			}
		case BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case FORMULA:
			return cell.getCellFormula();
		default:
			return "";
		}
	}

	public void generateSendRentalAgreement(UserMaster master, ZoyPgOwnerDetails zoyPgOwnerDetails,ZoyPgPropertyDetails propertyDetail, ZoyPgOwnerBookingDetails saveMyBookings) {
		try {
			UserDetails userDetails=uploadDBImpl.findUserDetails(master.getUserId());
			List<String[]> ids=uploadDBImpl.findFloorRoomBedNameByPropertyName(propertyDetail.getPropertyId(),saveMyBookings.getRoom(),saveMyBookings.getSelectedBed(),saveMyBookings.getLockInPeriod());
			String floorId = "" , floorName="",roomId ="", roomName="",bedId="",bedName="",shareId="",shareName="",rentCycleName="",rentCycleId="";
			if(ids.size()>0) {
				floorId=ids.get(0)[0];
				floorName=ids.get(0)[1];
				roomId=ids.get(0)[2];
				roomName=ids.get(0)[3];
				bedId=ids.get(0)[4];
				bedName=ids.get(0)[5];
				shareId=ids.get(0)[6];
				shareName=ids.get(0)[7];
				rentCycleId=ids.get(0)[8];
				rentCycleName=ids.get(0)[9];
			}
//			Map<String, Object> data = new HashMap<>();
//			PropertyRentalDetails details = new PropertyRentalDetails();
//			details.setPgOwnerId(zoyPgOwnerDetails.getPgOwnerId());
//			details.setPgName(propertyDetail.getPropertyName());
//			details.setPgAddress(propertyDetail.getPropertyHouseArea());
//			details.setPropertyId(propertyDetail.getPropertyId());
//			details.setShareType(shareName);
//			details.setCustomerAddress(userDetails.getPersonalPermanentAddress());
//			List<String> propertyAmenities=ownerDBImpl.findPropertyAmenetiesName(propertyDetail.getPropertyId());
//			ZoyPgTermsMaster zoyPgTermsMaster=ownerDBImpl.findTermMaster(propertyDetail.getPropertyId());
//			details.setPropertyAmenities(propertyAmenities);
//			InputStream inputStreamImg =getClass().getResourceAsStream(zoyLogoPath);
//			String base64Image = pdfGenerateService.imageToBase64(inputStreamImg);
//			details.setBase64Images(base64Image);
//			details.setRoom(roomName);
//			details.setMoveInDate(saveMyBookings.getInDate());
//			details.setMoveOutDate(saveMyBookings.getOutDate());
//			details.setNoOfDays(saveMyBookings.getNoOfDays()!=null?Integer.valueOf(saveMyBookings.getNoOfDays()):0);
//			details.setBookingId(saveMyBookings.getBookingId());
//			details.setFloor(floorName);
//			details.setBed(bedName);
//			details.setFixedRent(saveMyBookings.getFixedRent().intValue());
//			details.setSecurityDeposit(saveMyBookings.getSecurityDeposit().intValue());
//			details.setNoticePeriod(zoyPgTermsMaster.getNoticePeriod()); 
//			details.setTenantName(saveMyBookings.getName());
//			details.setTimeStamp(new Timestamp(System.currentTimeMillis()));
//			details.setManagerName(propertyDetail.getPropertyManagerName());
//			data.put("data", details);
//			pdfGenerateService.generateRenatalPdfFile("pdfRental", data, master.getUserId(),saveMyBookings.getBookingId());
//
//			//Email
//			InputStream is = zoyS3Service.downloadRentalAgreement(master.getUserId(), saveMyBookings.getBookingId());
//			zoyEmailService.sendRentalAgreementToTenant(master,propertyDetail.getPropertyName(),is);
			GeneratePDFRental generatePDFRental=new GeneratePDFRental();
			generatePDFRental.setPgOwnerId(zoyPgOwnerDetails.getPgOwnerId());
			generatePDFRental.setPgName(propertyDetail.getPropertyName());
			generatePDFRental.setPgAddress(propertyDetail.getPropertyHouseArea());
			generatePDFRental.setPropertyId(propertyDetail.getPropertyId());
			generatePDFRental.setShareType(shareName);
			generatePDFRental.setCustomerAddress(userDetails.getPersonalPermanentAddress());
			List<String> propertyAmenities=ownerDBImpl.findPropertyAmenetiesName(propertyDetail.getPropertyId());
			ZoyPgTermsMaster zoyPgTermsMaster=ownerDBImpl.findTermMaster(propertyDetail.getPropertyId());
			generatePDFRental.setPropertyAmenities(propertyAmenities);
			generatePDFRental.setRoom(roomName);
			generatePDFRental.setMoveInDate(saveMyBookings.getInDate());
			generatePDFRental.setMoveOutDate(saveMyBookings.getOutDate());
			generatePDFRental.setNoOfDays(saveMyBookings.getNoOfDays()!=null?Integer.valueOf(saveMyBookings.getNoOfDays()):0);
			generatePDFRental.setBookingId(saveMyBookings.getBookingId());
			generatePDFRental.setFloor(floorName);
			generatePDFRental.setBed(bedName);
			generatePDFRental.setFixedRent(saveMyBookings.getFixedRent().intValue());
			generatePDFRental.setSecurityDeposit(saveMyBookings.getSecurityDeposit().intValue());
			generatePDFRental.setNoticePeriod(Integer.valueOf(zoyPgTermsMaster.getNoticePeriod().split(" ")[0])); 
			generatePDFRental.setTenantName(saveMyBookings.getName());
			generatePDFRental.setTimeStamp(new Timestamp(System.currentTimeMillis()));
			generatePDFRental.setManagerName(propertyDetail.getPropertyManagerName());
			
			processRentalPdf(master.getUserId(),master.getUserEmail(), generatePDFRental);


		} catch (Exception e) {
			log.error("Error occured while generating pdf rental: " + e.getMessage());
		}
	}
	public void processRentalPdf(String userId,String userEmail,GeneratePDFRental generatePDFRental) {
		try {
			String url = zoyServerCustomerUrl + userId +"/"+userEmail+"/generatePdfRental";
			HttpHeaders headers = new HttpHeaders();
			setZoyServerHeader(headers);
			headers.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<GeneratePDFRental> entityReq = new HttpEntity<>(generatePDFRental, headers);
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
