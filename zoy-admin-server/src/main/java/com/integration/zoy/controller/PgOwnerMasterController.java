package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.entity.PgOwnerMaster;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.model.BasicPropertyInformation;
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
import com.integration.zoy.repository.PgOwnerMaterRepository;
import com.integration.zoy.repository.UserProfileRepository;
import com.integration.zoy.service.CommonDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.service.ZoyEmailService;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class PgOwnerMasterController implements PgOwnerMasterImpl {

    @Autowired
    private PgOwnerMaterRepository pgOwnerMaterRepository;
    
    @Autowired
    private UserProfileRepository profileRepository;
    @Autowired
    ZoyCodeGenerationService zoyCodeGenerationService;
	@Autowired
	EmailService emailService;

	@Autowired
	PasswordDecoder passwordDecoder;
	
	@Autowired
	ZoyEmailService emailBodyService;
	
	@Autowired
	CommonDBImpl commonDBImpl;
	
	@Value("${qa.signin.link}")
	private String qaRegistrationLink;


    private static final Logger log = LoggerFactory.getLogger(PgOwnerMasterController.class);  // Fixed logger reference
    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(Timestamp.class, (JsonSerializer<Timestamp>) (src, typeOfSrc, context) -> {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                return new JsonPrimitive(dateFormat.format(src));
            })
            .registerTypeAdapter(Timestamp.class, (JsonDeserializer<Timestamp>) (json, typeOfT, context) -> {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
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

            PgOwnerMaster ownerData = new PgOwnerMaster();
            String zoyCode = zoyCodeGenerationService.generateZoyCode(model.getEmailId());
            ownerData.setZoyCode(zoyCode);
            ownerData.setEmailId(model.getEmailId());
            ownerData.setFirstName(model.getFirstName());
            ownerData.setLastName(model.getLastName());
            ownerData.setMobileNo(model.getMobileNo());
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
			commonDBImpl.registerNewUserAccount(user);
			
            emailBodyService.sendZoyCode(model.getEmailId(),model.getFirstName(),model.getLastName(),zoyCode,token);
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("ZOY code has been generated & sent successfully.");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while saving PG owner details", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Internal server error. Please try again later.");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
        
    @Override
    public ResponseEntity<String> pgOwnerDetalaisresend(String email) {
        ResponseBody response = new ResponseBody();
        try {
        	PgOwnerMaster pgOwnerDetails = pgOwnerMaterRepository.getOwnerDetails(email);
            UserProfile profile=profileRepository.findRegisterEmail(email).get();
            emailBodyService.resendPgOwnerDetails(pgOwnerDetails.getEmailId(),pgOwnerDetails.getFirstName(),pgOwnerDetails.getLastName(),pgOwnerDetails.getZoyCode(),profile.getVerifyToken());
            
            response.setStatus(HttpStatus.OK.value());
            response.setMessage("ZOY code has been sent successfully");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error occurred while saving PG owner details", e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Internal server error. Please try again later.");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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

                pgOwnerDetailsList.add(ownerDetails);
            }

            return new ResponseEntity<>(gson.toJson(pgOwnerDetailsList), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error getting PG Owner details: " + e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Internal server error");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
            log.error("Error fetching details: {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Internal server error: " + e.getMessage());
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    public ResponseEntity<String> pgOwnerDetailsPortfolio(String ownerId) {
        ResponseBody response = new ResponseBody();

        try {
            List<Object[]> pgOwnerProfileDetails = pgOwnerMaterRepository.getPgOwnerProfileDetails(ownerId);
            if (pgOwnerProfileDetails == null || pgOwnerProfileDetails.isEmpty()) {
                response.setStatus(HttpStatus.BAD_REQUEST.value());
                response.setError("Owner details not found.");
                return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
            }

            PgOwnerProfile profile = new PgOwnerProfile();
            for (Object[] details : pgOwnerProfileDetails) {
                profile.setOwnerID(details[0] != null ? details[0].toString() : null);
                profile.setOwnerName(details[1] != null ? details[1].toString() : null);
                profile.setStatus(details[2] != null ? details[2].toString() : null);
                profile.setProfilePhoto( details[3] != null ? details[2].toString() : null);
            }

            List<Object[]> pgOwnerBasicDetails = pgOwnerMaterRepository.getPgOwnerBasicInfo(ownerId);
            PgOwnerbasicInformation basicInformation = new PgOwnerbasicInformation();
            for (Object[] details : pgOwnerBasicDetails) {
                basicInformation.setFirstName(details[0] != null ? details[0].toString() : null);
                basicInformation.setLastName(details[1] != null ? details[1].toString() : null);
                basicInformation.setEmail(details[2] != null ? details[2].toString() : null);
                basicInformation.setContact(details[3] != null ? details[3].toString() : null);
                basicInformation.setAadhar(details[4] != null ? details[4].toString() : null);
                basicInformation.setAddress(details[5] != null ? details[5].toString() : null);
            }

            List<Object[]> pgOwnerBusinessDetails = pgOwnerMaterRepository.getPgOwnerBusinessInfo(ownerId);
            List<PgOwnerBusinessInfo> businessInfoList = new ArrayList<>();
            for (Object[] details : pgOwnerBusinessDetails) {
                PgOwnerBusinessInfo businessInfo = new PgOwnerBusinessInfo();
                businessInfo.setAccountNumber(details[0] != null ? details[0].toString() : null);
                businessInfo.setBankName(details[1] != null ? details[1].toString() : null);
                businessInfo.setBankBranch(details[2] != null ? details[2].toString() : null);
                businessInfo.setIfscCode(details[3] != null ? details[3].toString() : null);
                businessInfo.setAccountType(details[4] != null ? details[4].toString() : null);
                businessInfoList.add(businessInfo);
            }

            List<Object[]> propertyDetailsList = pgOwnerMaterRepository.getPropertyDetailsByOwnerId(ownerId);
            Map<String, PgOwnerPropertyInformation> propertyMap = new HashMap<>();

            for (Object[] property : propertyDetailsList) {
                String propertyId = property[29] != null ? property[29].toString() : null;
                if (propertyId == null) continue;

                PgOwnerPropertyInformation propertyInfo = propertyMap.computeIfAbsent(propertyId, key -> {
                    PgOwnerPropertyInformation newProperty = new PgOwnerPropertyInformation();
                    newProperty.setPropertyName(property[0] != null ? property[0].toString() : null);
                    newProperty.setStatus(property[1] != null ? property[1].toString() : null);

                    BasicPropertyInformation basicPropertyInfo = new BasicPropertyInformation();
                    basicPropertyInfo.setPgType(property[2] != null ? property[2].toString() : null);
                    basicPropertyInfo.setPgAddress(property[3] != null ? property[3].toString() : null);
                    basicPropertyInfo.setManagerName(property[4] != null ? property[4].toString() : null);
                    basicPropertyInfo.setManagerContact(property[5] != null ? property[5].toString() : null);
                    basicPropertyInfo.setManagerEmailid(property[6] != null ? property[6].toString() : null);
                    basicPropertyInfo.setNumberOfFloors(property[7] != null ? property[7].toString() : "0");
                    basicPropertyInfo.setTotalOccupancy(property[8] != null ? property[8].toString() : "0");
                    basicPropertyInfo.setGstNumber(property[9] != null ? property[9].toString() : null);
                    basicPropertyInfo.setCin(property[10] != null ? property[10].toString() : null);
                    basicPropertyInfo.setGstOwnershiProofNumber(property[11] != null ? property[11].toString() : null);
                    
                    PgOwnerAdditionalInfo additionalInfo = new PgOwnerAdditionalInfo();
                    additionalInfo.setSecurityDeposit(property[20] != null ? property[20].toString() : null);
                    additionalInfo.setNoticePeriod(property[21] != null ? property[21].toString() : null);
                    additionalInfo.setRentCycle(property[22] != null ? property[22].toString() : null);
                    additionalInfo.setLatePaymentFee(property[23] != null ? property[23].toString() : null);
                    additionalInfo.setGracePeriod(property[24] != null ? property[24].toString() : null);
                    additionalInfo.setAdditionalCharges(property[25] != null ? property[25].toString() : null);
                    additionalInfo.setAgreementCharges(property[26] != null ? property[26].toString() : null);
                    additionalInfo.setEkycCharges(property[27] != null ? property[27].toString() : null);
                    additionalInfo.setPropertyDescription(property[28] != null ? property[28].toString() : null);
                    
                    newProperty.setPgOwnerAdditionalInfo(additionalInfo);
                    newProperty.setBasicPropertyInformation(basicPropertyInfo);
                    newProperty.setFloorInformation(new ArrayList<>());
                    
                    return newProperty;
                });

                String floorNumber = property[12] != null ? property[12].toString() : null;
                if (floorNumber != null) {
                    FloorInformation floorInfo = propertyInfo.getFloorInformation().stream()
                            .filter(floor -> floor.getFloorNumber().equals(floorNumber))
                            .findFirst()
                            .orElseGet(() -> {
                                FloorInformation newFloor = new FloorInformation();
                                newFloor.setFloorNumber(floorNumber);
                                newFloor.setTotalRooms(property[13] != null ? property[13].toString() : "0");
                                newFloor.setTotalOccupancy(property[14] != null ? property[14].toString() : "0");
                                newFloor.setVacant(property[15] != null ? property[15].toString() : "0");
                                newFloor.setOccupied(property[16] != null ? property[16].toString() : "0");
                                newFloor.setRooms(new ArrayList<>());
                                propertyInfo.getFloorInformation().add(newFloor);
                                return newFloor;
                            });

                    String roomNo = property[17] != null ? property[17].toString() : null;
                    if (roomNo != null) {
                        Room room = new Room();
                        room.setRoomNo(roomNo);
                        room.setNumberOfBeds(property[18] != null ? property[18].toString() : "0");
                        room.setBedsAvailable(property[19] != null ? property[19].toString() : "0");
                        floorInfo.getRooms().add(room);
                    }
                }

                PgOwnerAdditionalInfo additionalInfo = new PgOwnerAdditionalInfo();
                additionalInfo.setSecurityDeposit(property[20] != null ? property[20].toString() : null);
                additionalInfo.setNoticePeriod(property[21] != null ? property[21].toString() : null);
                additionalInfo.setRentCycle(property[22] != null ? property[22].toString() : null);
                additionalInfo.setLatePaymentFee(property[23] != null ? property[23].toString() : null);
                additionalInfo.setGracePeriod(property[24] != null ? property[24].toString() : null);
                additionalInfo.setAdditionalCharges(property[25] != null ? property[25].toString() : null);
                additionalInfo.setAgreementCharges(property[26] != null ? property[26].toString() : null);
                additionalInfo.setEkycCharges(property[27] != null ? property[27].toString() : null);
                additionalInfo.setPropertyDescription(property[28] != null ? property[28].toString() : null);
                propertyInfo.setPgOwnerAdditionalInfo(additionalInfo);
            }

            PgOwnerdetailPortfolio root = new PgOwnerdetailPortfolio();
            root.setProfile(profile);
            root.setPgOwnerbasicInformation(basicInformation);
            root.setPgOwnerBusinessInfo(businessInfoList);
            root.setPgOwnerPropertyInformation(new ArrayList<>(propertyMap.values()));

            response.setStatus(HttpStatus.OK.value());
            response.setData(root);
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error fetching PG Owner details portfolio: {}", e.getMessage(), e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setError("Internal server error");
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
