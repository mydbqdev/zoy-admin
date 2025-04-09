package com.integration.zoy.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserDueMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.entity.UserMaster;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.AuditActivitiesLogDTO;
import com.integration.zoy.model.OwnerPropertyDTO;
import com.integration.zoy.model.UserNameDTO;
import com.integration.zoy.repository.NotificationModeMasterRepository;
import com.integration.zoy.repository.UserBillingMasterRepository;
import com.integration.zoy.repository.UserCurrencyMasterRepository;
import com.integration.zoy.repository.UserDueMasterRepository;
import com.integration.zoy.repository.UserEkycTypeMasterRepository;
import com.integration.zoy.repository.UserMasterRepository;
import com.integration.zoy.repository.ZoyPgOwnerDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.TenantDetails;

@Service
public class UserDBService implements UserDBImpl{
	private static final Logger log = LoggerFactory.getLogger(UserDBService.class);
	@Autowired
	private NotificationModeMasterRepository notificationModeMasterRepository;

	@Autowired
	private UserEkycTypeMasterRepository userEkycTypeMasterRepository;

	@Autowired
	private UserBillingMasterRepository userBillingMasterRepository;

	@Autowired
	private UserDueMasterRepository userDueMasterRepository;

	@Autowired
	private UserCurrencyMasterRepository userCurrencyMasterRepository;

	@Autowired
	private ZoyPgOwnerDetailsRepository zoyPgOwnerDetailsRepository;

	@Autowired
	private UserMasterRepository masterRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private UserMasterRepository userRepo;
	
	@Autowired
	private TimestampFormatterUtilService tuService;


	//Notification Mode	
	@Override
	public NotificationModeMaster saveNotificationMode(NotificationModeMaster notificationModeMaster) {
		return notificationModeMasterRepository.save(notificationModeMaster);
	}

	@Override
	public void deleteNotificationMode(String id) {
		notificationModeMasterRepository.deleteById(id);
	}

	@Override
	public List<NotificationModeMaster> findAllNotificationMode() {
		return notificationModeMasterRepository.findAll();
	}

	@Override
	public NotificationModeMaster findNotificationMode(String id) {
		return notificationModeMasterRepository.findById(id).orElse(null);
	}

	@Override
	public NotificationModeMaster updateNotificationMode(NotificationModeMaster notificationModeMaster) {
		Optional<NotificationModeMaster> existingMode = notificationModeMasterRepository.findById(notificationModeMaster.getNotificationModeId());
		if (existingMode.isPresent()) {
			NotificationModeMaster updateMode = existingMode.get();
			updateMode.setNotificationModName(notificationModeMaster.getNotificationModName());
			return notificationModeMasterRepository.save(updateMode);
		}
		return null;
	}


	//User Ekyc
	@Override
	public UserEkycTypeMaster createEkycType(UserEkycTypeMaster ekycType) {
		return userEkycTypeMasterRepository.save(ekycType);
	}

	@Override
	public UserEkycTypeMaster updateEkycType(UserEkycTypeMaster ekycType) {
		Optional<UserEkycTypeMaster> existingType = userEkycTypeMasterRepository.findById(ekycType.getUserEkycTypeId());
		if (existingType.isPresent()) {
			UserEkycTypeMaster updatedType = existingType.get();
			updatedType.setUserEkycTypeName(ekycType.getUserEkycTypeName() != null && !ekycType.getUserEkycTypeName().trim().isEmpty()
					? ekycType.getUserEkycTypeName() : updatedType.getUserEkycTypeName());
			return userEkycTypeMasterRepository.save(updatedType);
		} else {
			new ZoyAdminApplicationException(new Exception(),"Ekyc type not found with id: " + ekycType.getUserEkycTypeId());
		}
		return null;
	}

	@Override
	public void deleteEkycType(String id) {
		userEkycTypeMasterRepository.deleteById(id);
	}

	@Override
	public List<UserEkycTypeMaster> findAllEkycTypes() {
		return userEkycTypeMasterRepository.findAll();
	}
	@Override
	public UserEkycTypeMaster findEkycTypes(String id) {
		return userEkycTypeMasterRepository.findById(id).orElse(null);
	}

	//User bIlling master
	@Override
	public UserBillingMaster saveUserBillingMaster(UserBillingMaster master) {
		return userBillingMasterRepository.save(master);
	}

	@Override
	public List<UserBillingMaster> findAllUserBillingMaster() {
		return userBillingMasterRepository.findAll();
	}

	@Override
	public UserBillingMaster findUserBillingMaster(String billingId) {
		return userBillingMasterRepository.findById(billingId).orElse(null);
	}

	@Override
	public UserBillingMaster updateUserBillingMaster(UserBillingMaster master) {
		Optional<UserBillingMaster> existingMaster = userBillingMasterRepository.findById(master.getBillingTypeId());
		if (existingMaster.isPresent()) {
			UserBillingMaster updatedMaster = existingMaster.get();
			updatedMaster.setBillingTypeName(master.getBillingTypeName() != null && !master.getBillingTypeName().trim().isEmpty() ? master.getBillingTypeName() : updatedMaster.getBillingTypeName());
			return userBillingMasterRepository.save(updatedMaster);
		} else {
			throw new RuntimeException("Billing type not found with id: " + master.getBillingTypeId());
		}
	}

	@Override
	public void deleteUserBillingMaster(String id) {
		userBillingMasterRepository.deleteById(id);
	}

	//User Due Master
	@Override
	public UserDueMaster saveUserDueMaster(UserDueMaster master) {
		return userDueMasterRepository.save(master);
	}

	@Override
	public List<UserDueMaster> findAllUserDueMaster() {
		return userDueMasterRepository.findAll();
	}

	@Override
	public UserDueMaster findUserDueMaster(String dueTypeId) {
		return userDueMasterRepository.findById(dueTypeId).orElse(null);
	}

	@Override
	public UserDueMaster updateUserDueMaster(UserDueMaster master) {
		Optional<UserDueMaster> existingMaster = userDueMasterRepository.findById(master.getDueTypeId());
		if (existingMaster.isPresent()) {
			UserDueMaster updatedMaster = existingMaster.get();
			updatedMaster.setDueTypeName(master.getDueTypeName() != null && !master.getDueTypeName().trim().isEmpty() ? master.getDueTypeName() : updatedMaster.getDueTypeName());
			return userDueMasterRepository.save(updatedMaster);
		} else {
			throw new RuntimeException("Due type not found with id: " + master.getDueTypeId());
		}
	}

	@Override
	public void deleteUserDueMaster(String id) {
		userDueMasterRepository.deleteById(id);
	}

	//User Currency 
	@Override
	public UserCurrencyMaster saveUserCurrency(UserCurrencyMaster currency) {
		return userCurrencyMasterRepository.save(currency);
	}

	@Override
	public void deleteUserCurrency(String id) {
		userCurrencyMasterRepository.deleteById(id);
	}

	@Override
	public UserCurrencyMaster updateUserCurrency(UserCurrencyMaster currency) {
		return userCurrencyMasterRepository.findById(currency.getCurrencyId())
				.map(existingCurrency -> {
					existingCurrency.setCurrencyName(currency.getCurrencyName() != null && !currency.getCurrencyName().trim().isEmpty() ? currency.getCurrencyName() : existingCurrency.getCurrencyName());
					return userCurrencyMasterRepository.save(existingCurrency);
				}).orElseThrow(() -> new RuntimeException("UserCurrencyMaster not found with id: " + currency.getCurrencyId()));
	}

	@Override
	public List<UserCurrencyMaster> findAllUserCurrency() {
		return userCurrencyMasterRepository.findAll();
	}

	@Override
	public UserCurrencyMaster findCurrency(String currencyId) {
		return userCurrencyMasterRepository.findById(currencyId).orElse(null);
	}

	@Override
	public Page<OwnerPropertyDTO> findAllOwnerWithPropertyCount(PaginationRequest paginationRequest) {
		Map<String, String> sortFieldMapping = new HashMap<>();
		sortFieldMapping.put("owner_name", "pg_owner_name");
		sortFieldMapping.put("owner_email", "pg_owner_email");
		sortFieldMapping.put("owner_contact", "pg_owner_mobile");
		sortFieldMapping.put("number_of_properties", "numberOfProperties");
		sortFieldMapping.put("status", "status");

		String sortColumn = sortFieldMapping.getOrDefault(paginationRequest.getSortActive(), "pg_owner_name");
		Sort sort = Sort.by(Sort.Order.by(sortColumn)
				.with(Sort.Direction.fromString(paginationRequest.getSortDirection()))
				.ignoreCase());

		Pageable pageable = PageRequest.of(paginationRequest.getPageIndex(), paginationRequest.getPageSize(), sort);

		Page<Object[]> results = zoyPgOwnerDetailsRepository.findAllOwnerWithPropertyCountRaw(
				pageable, 
				paginationRequest.getFilter().getSearchText()
				);

		return results.map(result -> new OwnerPropertyDTO(
				(String) result[0], 
				(String) result[1], 
				(String) result[2],  
				(String) result[3],  
				((BigInteger) result[4]).longValue(), 
				(String) result[5]  
				));
	}

	public UserMaster findUserMaster(String userId) {
		return masterRepository.findById(userId).orElse(null);
	}




	@Override
	public CommonResponseDTO<AuditActivitiesLogDTO> getAuditActivitiesLogCount(PaginationRequest paginationRequest) throws WebServiceException{
		StringBuilder queryBuilder = new StringBuilder();
		try {
			queryBuilder.append("select concat(um.first_name,' ',um.last_name )as userName, ah.created_on, ah.history_data, ah.operation , ah.user_email  FROM audit_history ah\r\n"
					+ "	join user_master um  on ah.user_email = um.user_email Where 1=1 ");

			if(!"".equals(paginationRequest.getUserEmail()) && null != paginationRequest.getUserEmail()) {
				queryBuilder.append("AND um.user_email = '"+paginationRequest.getUserEmail()+"' ");
			}

			if(!"".equals(paginationRequest.getActivity()) && null != paginationRequest.getActivity()) {
				queryBuilder.append("AND ah.operation = '"+paginationRequest.getActivity()+"' ");
			}

			if(!"".equals(paginationRequest.getSearchText()) && null != paginationRequest.getSearchText()) {
				queryBuilder.append(" AND ((concat(' ',ah.created_on) LIKE '%"+paginationRequest.getSearchText().toLowerCase() +"%' ) or (LOWER(ah.history_data) LIKE '%"+paginationRequest.getSearchText().toLowerCase() +"%' ) "
						+ " or (LOWER( concat(um.first_name,' ',um.last_name )) LIKE '%"+paginationRequest.getSearchText().toLowerCase() +"%' ) or (LOWER(ah.operation) LIKE '%"+paginationRequest.getSearchText().toLowerCase() +"%' )  ) ");
			}	     

			if("created_on".equals(paginationRequest.getSortActive())) {
				queryBuilder.append(" order by ah.created_on "+paginationRequest.getSortDirection());

			}else if("history_data".equals(paginationRequest.getSortActive())) {
				queryBuilder.append(" order by ah.history_data "+paginationRequest.getSortDirection());

			}else if ("user_name".equals(paginationRequest.getSortActive())) {
				queryBuilder.append(" order by concat(um.first_name, ' ', um.last_name) " + paginationRequest.getSortDirection());
			} else if ("type".equals(paginationRequest.getSortActive())) {
				queryBuilder.append(" order by ah.operation " + paginationRequest.getSortDirection());
			} else {
				queryBuilder.append(" order by ah.created_on DESC ");
			}

			Query query = entityManager.createNativeQuery(queryBuilder.toString());

			int count=query.getResultList().size();

			if(null == paginationRequest.getDownloadType() || paginationRequest.getDownloadType().equals("")) {
				query.setFirstResult(paginationRequest.getPageIndex() * paginationRequest.getPageSize());
				query.setMaxResults(paginationRequest.getPageSize());
			}

			List<Object[]> activityLogData = query.getResultList();
			List<AuditActivitiesLogDTO> list = new ArrayList<>(activityLogData.size());

			for (Object[] details : activityLogData) {
				AuditActivitiesLogDTO auditActivityData = new AuditActivitiesLogDTO();
				auditActivityData.setUserName(String.valueOf(details[0]));
				auditActivityData.setCreatedOn(String.valueOf(details[1])!=null?Timestamp.valueOf(String.valueOf(details[1])):null);
				auditActivityData.setHistoryData(String.valueOf(details[2]));
				auditActivityData.setType(String.valueOf(details[3]));
				list.add(auditActivityData);
			}
			return new CommonResponseDTO<>(list, count);
		}catch (Exception e) {
			log.error("Error in getAuditActivitiesLogCount() service :"+e);
			new ZoyAdminApplicationException(e, "");
		}
		return null;
	}


	//User name List 
	@Override
	public List<UserNameDTO> getUserNameList() throws WebServiceException {
		List<UserNameDTO> userList = new ArrayList<>();
		try {
			List<Object[]> list = masterRepository.getUsersNameList();

			for (Object[] row : list) {
				String username = (String) row[0];  
				String useremail = (String) row[1]; 

				UserNameDTO userNameDTO = new UserNameDTO(username, useremail);
				userList.add(userNameDTO);
			}
		} catch(Exception e) {
			log.error("Error in getUserNameList() service :"+e);
			new ZoyAdminApplicationException(e, "");
		}

		return userList;
	}

	//User Audit download
	@Override
	public byte[] generateDynamicReport(PaginationRequest paginationRequest ) throws WebServiceException {
		try {
			String[] headers= {" USER NAME","CREATED ON","TYPE","HISTORY DATA"};
			CommonResponseDTO<AuditActivitiesLogDTO> data = getAuditActivitiesLogCount(paginationRequest);

			if(paginationRequest.getDownloadType().equals("csv")) {
				return generateCsvFile(data, headers ,paginationRequest.getIsUserActivity() );
			}else if(paginationRequest.getDownloadType().equals("excel")){
				return generateExcelFile(data, headers,paginationRequest.getIsUserActivity() );
			}else {
				return new byte[0];
			}

		}catch (Exception e) {
			new ZoyAdminApplicationException(e, "");
		}
		return  new byte[0];
	}

	public byte[] generateExcelFile(CommonResponseDTO<AuditActivitiesLogDTO> data,String[] headers ,boolean isUserActivity) {

		if (data == null || null == data.getData() || data.getData().size()<1 || data.getCount()<1) {
			return new byte[0];
		}
		List <AuditActivitiesLogDTO> reportData = data.getData();

		try (XSSFWorkbook workbook = new XSSFWorkbook()) {			
			Sheet sheet = workbook.createSheet("User Audit Report");
			Row headerRow = sheet.createRow(0);
			
			if(isUserActivity) {
				headerRow.createCell(0).setCellValue("CREATED ON");
				headerRow.createCell(1).setCellValue("HISTORY DATA");

				for (int i = 0; i < reportData.size(); i++) {
					Row dataRow = sheet.createRow(i + 1);
					dataRow.createCell(0).setCellValue(nullSafe(tuService.formatTimestamp(reportData.get(i).getCreatedOn().toInstant())));
					dataRow.createCell(1).setCellValue(nullSafe(reportData.get(i).getHistoryData()));
				}
			}else {
				for (int i = 0; i < headers.length; i++) {
					headerRow.createCell(i).setCellValue(headers[i]);
				}				
				for (int i = 0; i < reportData.size(); i++) {
					Row dataRow = sheet.createRow(i + 1);
					dataRow.createCell(0).setCellValue(nullSafe(reportData.get(i).getUserName()));
					dataRow.createCell(1).setCellValue(nullSafe(tuService.formatTimestamp(reportData.get(i).getCreatedOn().toInstant())));
					dataRow.createCell(2).setCellValue(nullSafe(reportData.get(i).getType()));
					dataRow.createCell(3).setCellValue(nullSafe(reportData.get(i).getHistoryData()));
				}
			}

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			workbook.write(outputStream);
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Error generating Excel file", e);
		}
	}
	
	private String nullSafe(Object value) {
		return (value == null) ? "N/A" : value.toString();
	}

	public byte[] generateCsvFile(CommonResponseDTO<AuditActivitiesLogDTO> data,String[] headers ,boolean isUserActivity ) {

		if (data == null || null == data.getData() || data.getData().size()<1 || data.getCount()<1) {
			return new byte[0];
		}
		List <AuditActivitiesLogDTO> reportData = data.getData();

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {

			if(isUserActivity) {
				writer.println( "CREATED ON,HISTORY DATA");

				for (AuditActivitiesLogDTO dto : reportData) {
					writer.printf("\"%s\",\"%s\"%n",
							 tuService.formatTimestamp(dto.getCreatedOn().toInstant()),
							dto.getHistoryData()

							);
				}
			}else {
				writer.println( String.join(",", headers));
				for (AuditActivitiesLogDTO dto : reportData) {
					writer.printf("\"%s\",\"%s\",\"%s\",\"%s\"%n",
							dto.getUserName(),
							 tuService.formatTimestamp(dto.getCreatedOn().toInstant()),
							dto.getType(),
							dto.getHistoryData()

							);
				}
			}


			writer.flush();
			return outputStream.toByteArray();

		} catch (Exception e) {
			throw new RuntimeException("Error generating CSV file", e);
		}
	}

	@Override
	public Page<TenantDetails> findAllTenantDetails(PaginationRequest paginationRequest) {
		String sortColumn = paginationRequest.getSortActive() != null ? paginationRequest.getSortActive() : "user_created_at";
		String sortDirection = paginationRequest.getSortDirection() != null ? paginationRequest.getSortDirection().toUpperCase() : "ASC";

		if (sortColumn == null || sortColumn.isEmpty()) {
			sortColumn = "user_created_at"; 
		}

		Sort sort;
		if (sortColumn.equals("user_created_at")) {
			sort = Sort.by(Sort.Order.by(sortColumn).with(Sort.Direction.fromString(sortDirection)));
		} else {
			sort = Sort.by(Sort.Order.by(sortColumn).with(Sort.Direction.fromString(sortDirection)).ignoreCase());
		}

		Pageable pageable = PageRequest.of(paginationRequest.getPageIndex(), paginationRequest.getPageSize(), sort);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Page<Object[]> results = userRepo.getTenantDetails(
					pageable, 
					Optional.ofNullable(paginationRequest.getFilter().getSearchText()).orElse(""), 
					Optional.ofNullable(paginationRequest.getFilter().getStartDate()).orElse("1970-01-01 00:00:00"), 
					Optional.ofNullable(paginationRequest.getFilter().getEndDate()).orElse("9999-12-31 23:59:59"),
					(List<String>) Optional.ofNullable(paginationRequest.getFilter().getStatus()!=null ?
							Arrays.asList(paginationRequest.getFilter().getStatus().split(",")): 
								Arrays.asList(ZoyConstant.ACTIVE,ZoyConstant.INACTIVE,ZoyConstant.REGISTER,ZoyConstant.SUSPENDED)).orElse(new ArrayList<>())
					);
			return results.map(result -> new TenantDetails(
					result[0] != null ? (String) result[0] : "",
					result[1] != null ? (String) result[1] : "",
					result[2] != null ? (String) result[2] : "",
					result[3] != null ? (String) result[3] : "",
					result[4] != null ? (String) result[4] : "",
					result[5] != null ? (String) result[5] : "",
					result[6] != null ? dateFormat.format((Timestamp) result[6]) : null,
					result[7] != null ? (String) result[7] : ""
					));
		} catch (Exception e) {
			e.printStackTrace();
			return Page.empty();
		}
	}


}
