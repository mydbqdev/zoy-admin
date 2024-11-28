package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
import com.integration.zoy.model.PgOwnerDetails;
import com.integration.zoy.model.PgOwnerFilter;
import com.integration.zoy.model.PgOwnerMasterModel;
import com.integration.zoy.repository.PgOwnerMaterRepository;
import com.integration.zoy.service.CommonDBImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.PasswordDecoder;
import com.integration.zoy.service.ZoyCodeGenerationService;
import com.integration.zoy.service.ZoyEmailService;
import com.integration.zoy.utils.Email;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class PgOwnerMasterController implements PgOwnerMasterImpl {

    @Autowired
    private PgOwnerMaterRepository pgOwnerMaterRepository;
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
            
            emailBodyService.resendPgOwnerDetails(pgOwnerDetails.getEmailId(),pgOwnerDetails.getFirstName(),pgOwnerDetails.getLastName(),pgOwnerDetails.getZoyCode());
            
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

}
