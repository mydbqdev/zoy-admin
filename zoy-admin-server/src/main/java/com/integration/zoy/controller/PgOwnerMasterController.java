package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.PgOwnerMaster;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.BasicPropertyInformation;
import com.integration.zoy.model.Bed;
import com.integration.zoy.model.FloorInformation;
import com.integration.zoy.model.PgOwnerAdditionalInfo;
import com.integration.zoy.model.PgOwnerBusinessInfo;
import com.integration.zoy.model.PgOwnerDetails;
import com.integration.zoy.model.PgOwnerFilter;
import com.integration.zoy.model.PgOwnerMasterModel;
import com.integration.zoy.model.PgOwnerProfile;
import com.integration.zoy.model.PgOwnerPropertyInformation;
import com.integration.zoy.model.PgOwnerbasicInformation;
import com.integration.zoy.model.PgOwnerdetailPortfolio;
import com.integration.zoy.model.Room;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.PgOwnerMaterRepository;
import com.integration.zoy.repository.UserProfileRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;
import com.integration.zoy.service.CommonDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.PdfGenerateService;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.service.ZoyEmailService;
import com.integration.zoy.service.ZoyS3Service;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class PgOwnerMasterController implements PgOwnerMasterImpl {
	private static final Logger log=LoggerFactory.getLogger(PgOwnerMasterController.class);
	@Autowired
	private PgOwnerMaterRepository pgOwnerMaterRepository;
	@Autowired
	private UserProfileRepository profileRepository;
	@Autowired
	private AdminUserMasterRepository adminUserMasterRepository;
	@Autowired
	ZoyPgOwnerDetailsRepository zoyPgOwnerDetailsRepo;

	@Autowired
	ZoyCodeGenerationService zoyCodeGenerationService;
	@Autowired
	EmailService emailService;

	@Autowired
	PasswordDecoder passwordDecoder;

	@Autowired
	ZoyEmailService emailBodyService;

	@Autowired
	ZoyS3Service zoyS3Service;

	@Autowired
	PdfGenerateService  pdfGenerateService;

	@Autowired
	CommonDBImpl commonDBImpl;

	@Value("${qa.signin.link}")
	private String qaRegistrationLink;

	@Value("${app.minio.user.photos.bucket.name}")
	private String userPhotoBucketName;
	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

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
				} catch (Exception e) {
					throw new JsonParseException("Failed to parse Timestamp", e);
				}
			})
			.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
			.create();

	@Override
	public ResponseEntity<String> pgOwnerDetalaisSave(PgOwnerMasterModel model) {
		ResponseBody response = new ResponseBody();
		try {
			List<Object[]> result = pgOwnerMaterRepository.findEmailAndZoyCodeByEmailId(model.getEmailId());

			if (result != null && !result.isEmpty()) {
				Object[] row = result.get(0);
				String existingEmailId = (String) row[0];
				String existingZoyCode = (String) row[1];

				if (existingEmailId != null && !existingEmailId.isEmpty()) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setMessage("Email ID " + existingEmailId + " already exists with Zoy code " + existingZoyCode);
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
			}

			String phoneNumber= pgOwnerMaterRepository.findPhoneNumber(model.getMobileNo());

			if (phoneNumber != null && !phoneNumber.isEmpty()) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setMessage("Phone number " + phoneNumber + " already exists");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}	
			PgOwnerMaster ownerData = new PgOwnerMaster();
			String zoyCode = zoyCodeGenerationService.generateZoyCode(model.getEmailId());
			ownerData.setZoyCode(zoyCode);
			ownerData.setEmailId(model.getEmailId());
			ownerData.setFirstName(model.getFirstName());
			ownerData.setLastName(model.getLastName());
			ownerData.setMobileNo(model.getMobileNo());
			ownerData.setZoyShare(model.getZoyShare());
			pgOwnerMaterRepository.save(ownerData);

			String token = UUID.randomUUID().toString();
			UserProfile user = new UserProfile();
			user.setEmailId(model.getEmailId());
			user.setMobileNo(model.getMobileNo());
			user.setPropertyOwnerName(model.getFirstName() +" "+ model.getLastName());
			user.setZoyCode(zoyCode);
			user.setEnabled(false);
			user.setUserApplicationName("O");
			user.setVerifyToken(token);
			user.setZoyShare(model.getZoyShare());
			commonDBImpl.registerNewUserAccount(user);
			//audit history here
			String historyContent=" has generated the zoy code for,"+user.getPropertyOwnerName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_ZOY_CODE_GENERATE);
			emailBodyService.sendZoyCode(model.getEmailId(),model.getFirstName(),model.getLastName(),zoyCode,token);


			response.setStatus(HttpStatus.OK.value());
			response.setMessage("ZOY code has been generated & sent successfully.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error occurred while saving PG owner details API:/zoy_admin/savePgOwnerData.pgOwnerDetalaisSave ", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> pgOwnerDetalaisresend(String email) {
		ResponseBody response = new ResponseBody();
		try {
			PgOwnerMaster pgOwnerDetails = pgOwnerMaterRepository.getOwnerDetails(email);
			UserProfile profile=profileRepository.findRegisterEmail(email).get();
			emailBodyService.resendPgOwnerDetails(pgOwnerDetails.getEmailId(),pgOwnerDetails.getFirstName(),pgOwnerDetails.getLastName(),pgOwnerDetails.getZoyCode(),profile.getVerifyToken());

			String historyContent=" has resend the zoy code for,"+pgOwnerDetails.getFirstName()+" "+pgOwnerDetails.getLastName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_ZOY_CODE_RESEND);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("ZOY code has been sent successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error occurred while resend PG owner details API:/zoy_admin/resendPgOwnerData.pgOwnerDetalaisresend ", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	public ResponseEntity<String> pgOwnerDetailsGet() {
		ResponseBody response = new ResponseBody();
		try {
			List<Object[]> allPgOwnerDetails = pgOwnerMaterRepository.getAllPgOwnerDetails();

			List<PgOwnerDetails> pgOwnerDetailsList = new ArrayList<>();

			for (Object[] details : allPgOwnerDetails) {
				PgOwnerDetails ownerDetails = new PgOwnerDetails();

				ownerDetails.setZoyCode(details[0] != null ? (String) details[0] : null);
				ownerDetails.setOwnerName(details[1] != null ? (String) details[1] : null);
				ownerDetails.setEmailId(details[2] != null ? (String) details[2] : null);
				ownerDetails.setMobileNo(details[3] != null ? (String) details[3] : null);
				ownerDetails.setCreatedDate(details[4] != null ? (Timestamp) details[4] : null);
				ownerDetails.setStatus(details[5] != null ? (String) details[5] : null);
				ownerDetails.setZoyShare(details[6] != null ? (BigDecimal) details[6] : null);
				pgOwnerDetailsList.add(ownerDetails);
			}

			return new ResponseEntity<>(gson.toJson(pgOwnerDetailsList), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error getting PG Owner details API:/zoy_admin/getAllPgOwnerData.pgOwnerDetailsGet ", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	public ResponseEntity<Object> searchPgOwnerDetails(PgOwnerFilter pgOwnerFilter) {
		ResponseBody response = new ResponseBody();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int pageIndex = pgOwnerFilter.getPageIndex() != null ? pgOwnerFilter.getPageIndex() : 0;
			int pageSize = pgOwnerFilter.getPageSize() != null ? pgOwnerFilter.getPageSize() : 10;
			int offset = pageIndex * pageSize;

			String searchTerm = pgOwnerFilter.getSearchContent();
			searchTerm = (searchTerm == null || searchTerm.trim().isEmpty()) ? "" : searchTerm;

			List<Object[]> searchDetails = pgOwnerMaterRepository
					.PgOwnerDetailsBasedONSearchWithPagination(searchTerm, pageSize, offset);

			List<PgOwnerDetails> pgOwnerDetailsList = new ArrayList<>();
			for (Object[] details : searchDetails) {
				PgOwnerDetails ownerDetails = new PgOwnerDetails();
				ownerDetails.setZoyCode(details[0] instanceof String ? (String) details[0] : null);
				ownerDetails.setOwnerName(details[1] instanceof String ? (String) details[1] : null);
				ownerDetails.setEmailId(details[2] instanceof String ? (String) details[2] : null);
				ownerDetails.setMobileNo(details[3] instanceof String ? (String) details[3] : null);
				ownerDetails.setCreatedDate(details[3] instanceof String ? (Timestamp) details[4] : null);
				ownerDetails.setStatus(details[5] != null ? (String) details[5] : null);
				pgOwnerDetailsList.add(ownerDetails);
			}

			return new ResponseEntity<>(gson.toJson(pgOwnerDetailsList), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error searchPgOwnerDetails details API:/zoy_admin/PgOwnersDataBySearch.searchPgOwnerDetails", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	public ResponseEntity<String> pgOwnerDetailsPortfolio(String ownerId) {
		ResponseBody response = new ResponseBody();

		try {
			List<String[]> ownerPropertyDetails = pgOwnerMaterRepository.getOwnerPropertyDetails(ownerId);
			if (ownerPropertyDetails == null || ownerPropertyDetails.isEmpty()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Owner details not found.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			PgOwnerProfile profile = new PgOwnerProfile();

			String[] details = ownerPropertyDetails.get(0);
			profile.setOwnerID(details[0] != null ? details[0] : null);
			profile.setOwnerName(details[1] != null ? details[1] : null);
			profile.setStatus(details[8] != null ? details[8].toString() : null);
			profile.setProfilePhoto( details[9] != null ? details[9].toString() : null);
			profile.setZoyShare(details[37] != null ? details[37].toString() : null);
			PgOwnerbasicInformation basicInformation = new PgOwnerbasicInformation();
			basicInformation.setFirstName(details[2] != null ? details[2].toString() : null);
			basicInformation.setLastName(details[3] != null ? details[3].toString() : null);
			basicInformation.setEmail(details[4] != null ? details[4].toString() : null);
			basicInformation.setContact(details[5] != null ? details[5].toString() : null);
			basicInformation.setAadhar(details[6] != null ? details[6].toString() : null);
			basicInformation.setAddress(details[7] != null ? details[7].toString() : null);
			Map<String, PgOwnerPropertyInformation> propertyMap = new HashMap<>();

			for (String[] property : ownerPropertyDetails) {
				String propertyId = property[11] != null ? property[11].toString() : null;
				if (propertyId == null) continue;

				PgOwnerPropertyInformation propertyInfo = propertyMap.computeIfAbsent(propertyId, key -> {
					PgOwnerPropertyInformation newProperty = new PgOwnerPropertyInformation();
					newProperty.setPropertyName(property[12] != null ? property[12].toString() : null);
					newProperty.setPropertyId(propertyId);
					newProperty.setStatus(property[13] != null ? property[13].toString() : null);
					BasicPropertyInformation basicPropertyInfo = new BasicPropertyInformation();
					basicPropertyInfo.setPgType(property[14] != null ? property[14].toString() : null);
					basicPropertyInfo.setPgAddress(property[15] != null ? property[15].toString() : null);
					basicPropertyInfo.setManagerName(property[16] != null ? property[16].toString() : null);
					basicPropertyInfo.setManagerContact(property[17] != null ? property[17].toString() : null);
					basicPropertyInfo.setManagerEmailid(property[18] != null ? property[18].toString() : null);
					basicPropertyInfo.setGstNumber(property[19] != null ? property[19].toString() : null);
					basicPropertyInfo.setCin(property[20] != null ? property[20].toString() : null);
					basicPropertyInfo.setGstOwnershiProofNumber(property[21] != null ? property[21].toString() : null);

					PgOwnerAdditionalInfo additionalInfo = new PgOwnerAdditionalInfo();
					additionalInfo.setSecurityDeposit(property[22] != null ? property[22].toString() : null);
					additionalInfo.setNoticePeriod(property[23] != null ? property[23].toString() : null);
					additionalInfo.setRentCycle(property[24] != null ? property[24].toString() : null);
					additionalInfo.setLatePaymentFee(property[25] != null ? property[25].toString() : null);
					additionalInfo.setGracePeriod(property[26] != null ? property[26].toString() : null);
					additionalInfo.setAdditionalCharges(property[27] != null ? property[27].toString() : null);
					additionalInfo.setAgreementCharges(property[28] != null ? property[28].toString() : null);
					additionalInfo.setEkycCharges(property[29] != null ? property[29].toString() : null);
					additionalInfo.setPropertyDescription(property[30] != null ? property[30].toString() : null);

					newProperty.setPgOwnerAdditionalInfo(additionalInfo);
					newProperty.setBasicPropertyInformation(basicPropertyInfo);
					Map<String, String> allFloors = new LinkedHashMap<>(); 
					LinkedHashMap<String, Room> rooms = new LinkedHashMap<>(); 
					int totalBeds = 0;
					for (String[] data : ownerPropertyDetails) {
						String propId = data[11] != null ? data[11].toString() : null;
						if(propertyId.equals(propId)) {
							int totalRoomBeds = 0,totalAvailableBeds = 0, totalOccupiedBeds = 0, totalNoticeBeds = 0;
							String floorName = data[32];
							String floorId = data[31];

							allFloors.putIfAbsent(floorId, floorName);

							if (data[33] == null && data[34] == null) {
								continue; 
							}

							String availableBedsStr = data[35] != null ? data[35] : "";
							String occupiedBedsStr = data[36] != null ? data[36] : "";

							int availableBedsCount = availableBedsStr.isEmpty() ? 0 : availableBedsStr.split(",").length;
							int occupiedBedsCount = occupiedBedsStr.isEmpty() ? 0 : occupiedBedsStr.split(",").length;

							totalAvailableBeds += availableBedsCount;
							totalOccupiedBeds += occupiedBedsCount;
							totalBeds += availableBedsCount + occupiedBedsCount;
							totalRoomBeds+= availableBedsCount + occupiedBedsCount;
							Room roomResponse = new Room();
							roomResponse.setFloorId(floorId);
							roomResponse.setRoomNo(data[34]);
							roomResponse.setBedsAvailable(String.valueOf(totalAvailableBeds));
							roomResponse.setBedsOccupied(String.valueOf(totalOccupiedBeds));
							roomResponse.setNumberOfBeds(String.valueOf(totalRoomBeds));
							List<Bed> beds=new ArrayList<>();
							if(data[35] != null && !data[35].isEmpty()) {
								beds.addAll(data[35] != null ? Arrays.stream(data[35].split(","))
										.map(rating -> rating.split("\\|")).filter(parts -> parts.length == 3)   
										.map(parts -> new Bed(parts[0], parts[1],parts[2]))
										.collect(Collectors.toList()) : new ArrayList<>());
							}
							if(data[36] != null && !data[36].isEmpty()) {
								beds.addAll( data[36] != null ? Arrays.stream(data[36].split(","))
										.map(rating -> rating.split("\\|")).filter(parts -> parts.length == 3)   
										.map(parts -> new Bed(parts[0], parts[1],parts[2]))
										.collect(Collectors.toList()) : new ArrayList<>());
							}
							roomResponse.setBeds(beds);
							rooms.put(roomResponse.getRoomNo(), roomResponse);
						}
					}
					TreeMap<String, FloorInformation> floors = new TreeMap<>(Comparator.nullsLast(String::compareTo));
					for (Map.Entry<String, String> floorEntry : allFloors.entrySet()) {
						String floorId = floorEntry.getKey();
						String floorName = floorEntry.getValue();

						LinkedHashMap<String, Room> filteredRooms = rooms.values().stream()
								.filter(room -> room.getFloorId().equals(floorId))
								.sorted(Comparator.comparing(Room::getRoomNo))
								.collect(Collectors.toMap(Room::getRoomNo, room -> room, (existing, replacement) -> existing, LinkedHashMap::new));

						int floorTotalBeds = 0, floorAvailableBeds = 0, floorOccupiedBeds = 0, floorNoticeBeds = 0;
						for (Room room : filteredRooms.values()) {
							floorTotalBeds += Integer.valueOf(room.getBedsAvailable()) + Integer.valueOf(room.getBedsOccupied());
							floorAvailableBeds += Integer.valueOf(room.getBedsAvailable());
							floorOccupiedBeds += Integer.valueOf(room.getBedsOccupied());
						}

						FloorInformation floorLevelDetails = new FloorInformation();
						floorLevelDetails.setFloorName(floorName);
						floorLevelDetails.setFloorId(floorId);
						floorLevelDetails.setOccupied(String.valueOf(floorOccupiedBeds));
						floorLevelDetails.setTotalOccupancy(String.valueOf(floorTotalBeds));
						floorLevelDetails.setVacant(String.valueOf(floorTotalBeds-floorOccupiedBeds));
						floorLevelDetails.setTotalRooms(String.valueOf(filteredRooms.size()));

						floorLevelDetails.setRooms(new ArrayList<>(filteredRooms.values()));
						floors.put(floorName, floorLevelDetails);
					}
					newProperty.setFloorInformation(new ArrayList<>(floors.values()));
					basicPropertyInfo.setNumberOfFloors(String.valueOf(floors.size()));
					basicPropertyInfo.setTotalOccupancy(String.valueOf(totalBeds));
					return newProperty;
				});
			}

			PgOwnerdetailPortfolio root = new PgOwnerdetailPortfolio();
			root.setProfile(profile);
			root.setPgOwnerbasicInformation(basicInformation);
			root.setPgOwnerBusinessInfo(details[10] != null ? Arrays.stream(details[10].split(","))
					.map(rating -> rating.split("\\|")).filter(parts -> parts.length == 5)   
					.map(parts -> new PgOwnerBusinessInfo(parts[0], parts[1],parts[2],parts[3],parts[4]))
					.collect(Collectors.toList()) : new ArrayList<>());
			root.setPgOwnerPropertyInformation(new ArrayList<>(propertyMap.values()));

			response.setStatus(HttpStatus.OK.value());
			response.setData(root);
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error fetching PG Owner details portfolio API:/zoy_admin/ownerdetailsportfolio.pgOwnerDetailsPortfolio", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> uploadProfilePicture(MultipartFile image) {
		ResponseBody response = new ResponseBody();
		try {
			String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
			String fileName = image.getOriginalFilename();

			long maxSize = 2 * 1024 * 1024; 
			if (image.getSize() > maxSize) {
				log.error("File size exceeds the maximum limit of 2MB.");
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("File size exceeds the maximum limit of 2MB.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if (fileName == null || fileName.isEmpty()) {
				log.error("No file uploaded.");
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("No file uploaded.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			AdminUserMaster userDetail = adminUserMasterRepository.findById(userEmail)
					.orElseThrow(() -> new RuntimeException("User not found"));

			byte[] profilePictureBase64 = pdfGenerateService.imageToBytes(image.getInputStream());
			userDetail.setUserProfilePicture(profilePictureBase64);

			adminUserMasterRepository.save(userDetail);

			String historyContent=" has Updated profile picture";	
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_UPLOAD_PROFILE_PICTURE);
			
			log.info("Profile picture uploaded successfully for user: {}", userEmail);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Profile picture uploaded successfully.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (RuntimeException ex) {
			log.error("User not found or error occurred while uploading profile picture API:/zoy_admin/uploadProfilePicture.uploadProfilePicture", ex.getMessage());
			response.setStatus(HttpStatus.NOT_FOUND.value());
			response.setMessage("User not found.");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);

		} catch (Exception ex) {
			log.error("Unexpected error occurred while uploading the profile pictureAPI:/zoy_admin/uploadProfilePicture.uploadProfilePicture", ex.getMessage(), ex);

			try {
				new ZoyAdminApplicationException(ex, "");
			} catch (Exception e) {
				log.error("Error during custom exception handling API:/zoy_admin/uploadProfilePicture.uploadProfilePicture", e.getMessage(), e);
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(e.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(ex.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	public byte[] getProfilePicture() { 
		byte[] profilePic = null ;
		try {
			String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();  
			profilePic = adminUserMasterRepository.findProfilePhoto(userEmail);  

			if (profilePic == null || profilePic.length == 0) {
				log.info("Profile picture not found for user API:/zoy_admin/getProfilePicture.getProfilePicture", userEmail);
				return profilePic; 
			}

		}catch (Exception ex) {
			log.error("Unexpected error occurred while fetching the profile picture API:/zoy_admin/getProfilePicture.getProfilePicture", ex);
			try {
				new ZoyAdminApplicationException(ex, "");
			} catch (Exception e) {
				log.error("Failed to download Profile Photo API:/zoy_admin/getProfilePicture.getProfilePicture "+ex.getMessage(),ex);
			}
		}
		return profilePic ;
	}

	@Override
	public ResponseEntity<String> updateOwnerZoyShare(String ownerid,BigDecimal newZoyshare) {
		ResponseBody response = new ResponseBody();
		try {
			int rowsUpdated = zoyPgOwnerDetailsRepo.updateZoyShare(ownerid, newZoyshare);
			if (rowsUpdated > 0) {
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Zoy share updated successfully.");
				
				String historyContent=" has Updated Owner ZoyShare Details for" +ownerid;	
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_ZOY_SHARE);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setError("Owner not found.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			log.error("Error occurred while updating zoy share: API:/zoy_admin/updateZoyShare.updateOwnerZoyShare", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}



}