package com.integration.zoy.service;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.UserProfile;
import com.integration.zoy.entity.ZoyPgOwnerDetails;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.utils.OtpVerification;
import com.integration.zoy.utils.PropertyList;
import com.integration.zoy.utils.SessionInfo;
import com.integration.zoy.utils.TenantList;
import com.integration.zoy.utils.Whatsapp;

@Service
public class ZoyAdminService {
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminService.class);
	@Autowired
	RestTemplate httpsRestTemplate;

	@Autowired
	CommonDBImpl commonDBImpl;

	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	WhatsAppService whatsAppService;

	@Autowired
	ZoyEmailService zoyEmailService;

	@Autowired
	UploadService uploadService;

	@Autowired
	WebClient webClient;

	@Autowired
	private JobLauncher jobLauncher;

//	@Autowired
//	private Job tenantProcessJob;
//
//	@Autowired
//	private Job propertyProcessJob;
	
	@Autowired
	private Job bulkUploadProcessJob;

//	@Autowired
//	private TenantProcessTasklet tenantProcessTasklet;
//
//	@Autowired
//	private PropertyProcessTasklet propertyProcessTasklet;

	@Autowired
	private BulkUploadProcessTasklet bulkUploadProcessTasklet;
	
	@Value("${app.zoy.server.username}")
	String zoyServerUserName;

	@Value("${app.zoy.server.password}")
	String zoyServerPassword;

	@Value("${app.zoy.server.url}")
	String zoyServerUrl;
	
	@Value("${app.minio.url}")
	private String s3Url;
	
	@Autowired
	ZoyS3Service zoyS3Service;

	private final Map<String, String> otpMap = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final Set<String> blacklistedTokens = new HashSet<>();
	
	private final ConcurrentHashMap<String, SessionInfo> userSingleDeviceLockMap = new ConcurrentHashMap<>();

	public void storeOtp(String userId, String otp) {
		otpMap.put(userId, otp);
		scheduler.schedule(() -> {
			otpMap.remove(userId);
		}, 10, TimeUnit.MINUTES);
	}

	public Map<String, String> getForgotPasswordOtp() throws WebServiceException {
		return otpMap;
	}

	public String validateOtp(OtpVerification otpVerify)throws WebServiceException {
		String otp=otpMap.get(otpVerify.getEmail());
		if (otp != null) {
			if (!otp.equals(otpVerify.getOtp())) {
				return "Invalid OTP";
			}
			otpMap.remove(otpVerify.getEmail());  
			return "OTP validated successfully";
		} else {
			return "Expired OTP";
		}
	}

//	@Async
//	public void processTenant(String ownerId, String propertyId, byte[] file,String fileName,String jobExeId) throws WebServiceException {
//		try {
//
//			tenantProcessTasklet.setParameters(ownerId, propertyId, file);
//			JobParameters jobParameters = new JobParametersBuilder()
//					.addString("ownerId", ownerId)
//					.addString("propertyId", propertyId)
//					.addString("fileName", fileName)
//					.addString("jobExecutionId", jobExeId) 
//					.toJobParameters();
//			jobLauncher.run(tenantProcessJob, jobParameters);
//		} catch (Exception e) {
//
//		}
//
//	}

//	@Async
//	public void processProperty(String ownerId, String propertyId, byte[] file,String fileName,String jobExecutionId) throws WebServiceException {
//		try {
//			propertyProcessTasklet.setParameters(ownerId, propertyId, file);
//			JobParameters jobParameters = new JobParametersBuilder()
//					.addString("ownerId", ownerId)
//					.addString("propertyId", propertyId)
//					.addString("fileName", fileName)
//					.addString("jobExecutionId", jobExecutionId) 
//					.toJobParameters();
//			jobLauncher.run(propertyProcessJob, jobParameters);
//
//		} catch (Exception e) {
//			log.error("error:::"+e);
//		}
//	}
	
	@Async
	public void processBulkUpload(String ownerId, String propertyId, byte[] file,String fileName,String jobExecutionId) throws WebServiceException {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file);
				Workbook workbook = new XSSFWorkbook(inputStream)){
			List<PropertyList> propertyList = parsePropertySheet(workbook.getSheet("Property"));
			List<TenantList> tenantList = parseTenantSheet(workbook.getSheet("Tenant"));
			bulkUploadProcessTasklet.setParameters(ownerId, propertyId, propertyList,tenantList);
			JobParameters jobParameters = new JobParametersBuilder()
					.addString("ownerId", ownerId)
					.addString("propertyId", propertyId)
					.addString("fileName", fileName)
					.addString("jobExecutionId", jobExecutionId) 
					.toJobParameters();
			jobLauncher.run(bulkUploadProcessJob, jobParameters);
		} catch (Exception e) {
			log.error("error:::"+e);
		}
	}

	public List<PropertyList> parsePropertySheet(Sheet sheet) {
		List<PropertyList> propertyLists = new ArrayList<>();
		for (Row row : sheet) {
			if (row.getRowNum() < 2) {
				continue;
			}
			String cellValue = getStringCellValue(row, 0);
			if (cellValue != null && !cellValue.trim().isEmpty()) {
			    PropertyList propertyList = new PropertyList();
			    propertyList.setFloorName(cellValue);  
				propertyList.setRoomName(getStringCellValue(row, 1));   
				propertyList.setRoomType(getStringCellValue(row, 2));   
				propertyList.setShareType(getStringCellValue(row, 3));  
				propertyList.setArea(getDoubleCellValue(row, 4));       
				propertyList.setAvailableBeds(parseAvailableBeds(row, 5));  
				propertyList.setMonthlyRent(getDoubleCellValue(row, 6));   
				propertyList.setAmenities(parseAmenities(row, 7));        
				propertyList.setRemarks(getStringCellValue(row, 8));      

				propertyLists.add(propertyList);
			}
		}
		return propertyLists;
	}

//    private String getStringCellValue(Row row, int cellIndex) {
//        Cell cell = row.getCell(cellIndex);
//        return (cell != null) ? cell.getStringCellValue() : "";
//    }
    private Double getDoubleCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        return (cell != null && cell.getCellType() == CellType.NUMERIC) ? cell.getNumericCellValue() : 0.0;
    }
    private List<String> parseAvailableBeds(Row row, int cellIndex) {
        List<String> availableBeds = new ArrayList<>();
        String bedsData = getStringCellValue(row, cellIndex);
        if (!bedsData.isEmpty()) {
            String[] beds = bedsData.split(",");
            for (String bed : beds) {
                availableBeds.add(bed.trim());
            }
        }
        return availableBeds;
    }

    private List<String> parseAmenities(Row row, int cellIndex) {
        List<String> amenities = new ArrayList<>();
        String amenitiesData = getStringCellValue(row, cellIndex);
        if (!amenitiesData.isEmpty()) {
            String[] amenitiesArray = amenitiesData.split(",");
            for (String amenity : amenitiesArray) {
                amenities.add(amenity.trim());
            }
        }
        return amenities;
    }
	
    private List<TenantList> parseTenantSheet(Sheet sheet) {
    	List<TenantList> tenantList = new ArrayList<>();
    	for (Row row : sheet) {
    		if (row.getRowNum() < 2) { 
    			continue;
    		}
    		String cellValue = getStringCellValue(row, 0);
    		if (cellValue != null && !cellValue.trim().isEmpty()) {
    			TenantList tenant = new TenantList();
    			tenant.setFirstName(cellValue);     
    			tenant.setLastName(getStringCellValue(row, 1));      
    			tenant.setPhoneNumber(getStringCellValue(row, 2));   
    			tenant.setEmail(getStringCellValue(row, 3));       
    			tenant.setDateOfBirth(getTimestampCellValue(row, 4));   
    			tenant.setGender(getStringCellValue(row, 5));        
    			tenant.setPermanentAddress(getStringCellValue(row, 6)); 
    			tenant.setInDate(getTimestampCellValue(row, 7));        
    			tenant.setOutDate(getTimestampCellValue(row, 8));       
    			tenant.setFloor(getStringCellValue(row, 9));         
    			tenant.setRoom(getStringCellValue(row, 10));         
    			tenant.setBedNumber(getStringCellValue(row, 11));    
    			tenant.setDepositPaid(new BigDecimal(getStringCellValue(row, 12)));  
    			tenant.setRentPaid(getStringCellValue(row, 13));     
    			tenantList.add(tenant);
    		}
    	}
    	return tenantList;
    }
    
    private Timestamp getTimestampCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell != null && cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return new Timestamp(cell.getDateCellValue().getTime());
        } else if (cell != null && cell.getCellType() == CellType.STRING) {
            try {
                String value = cell.getStringCellValue().trim();
                if (!value.isEmpty()) {
                    // Try parsing known date format (adjust as needed)
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = sdf.parse(value);
                    return new Timestamp(parsedDate.getTime());
                }
            } catch (Exception e) {
                System.err.println("Failed to parse date string: " + e.getMessage());
            }
        }
        return null;
    }
    private String getStringCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = cell.getDateCellValue();
                    return dateFormat.format(date);
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

	public String validateVerificationToken(String token) {
		UserProfile user = commonDBImpl.findByVerifyToken(token);
		if (user == null) {
			return "invalid";
		}

		if(!user.isEnabled()) {
			user.setEnabled(true);
			commonDBImpl.registerNewUserAccount(user);
			ZoyPgOwnerDetails zoyPgOwnerDetails=new ZoyPgOwnerDetails();
			zoyPgOwnerDetails.setPgOwnerEmail(user.getEmailId());
			zoyPgOwnerDetails.setPgOwnerMobile(user.getMobileNo());
			zoyPgOwnerDetails.setPgOwnerName(user.getPropertyOwnerName());
			zoyPgOwnerDetails.setZoyCode(user.getZoyCode());
			zoyPgOwnerDetails.setPgOwnerEncryptedAadhar(user.getEncryptedAadhar());
			zoyPgOwnerDetails.setZoyShare(user.getZoyShare());
			ownerDBImpl.savePgOwner(zoyPgOwnerDetails);

			Whatsapp whatsapp = new Whatsapp();
			whatsapp.tonumber("+91" + user.getMobileNo());
			whatsapp.templateid(ZoyConstant.ZOY_OWNER_REG_WELCOME_MSG);
			Map<Integer, String> params = new HashMap<>();
			params.put(1, user.getPropertyOwnerName());
			whatsapp.setParams(params);
			whatsAppService.sendWhatsappMessage(whatsapp);

			zoyEmailService.sendRegistrationMail(user);

			return "valid";
		} else {
			return "Email already verified";
		}
	}
	
	public void addToBlacklist(String token) {
        blacklistedTokens.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }

    @Scheduled(fixedRate = 5*60*1000)
	public void removeExpiredSessions() {
		long currentTime = System.currentTimeMillis();
	    long sessionTimeout = 15 * 60 * 1000;
	    userSingleDeviceLockMap.entrySet().removeIf(entry -> 
	        (currentTime - entry.getValue().getLoginTime()) >= sessionTimeout
	    );
	}

	public ConcurrentHashMap<String, SessionInfo> getUserSingleDeviceLockMap() {
		return userSingleDeviceLockMap;
	}
	
	
	public String generatePreSignedUrl(String bucketName,String objectName) {
		String signedUrl="";
		if(objectName != null) {
			if(objectName.startsWith(s3Url+"/"+bucketName+"/")) 
				objectName = objectName.replace(s3Url+"/"+bucketName+"/", "");
			signedUrl= zoyS3Service.generatePreSignedUrl(bucketName,objectName);
		}
		return signedUrl;
	}
	
	
}