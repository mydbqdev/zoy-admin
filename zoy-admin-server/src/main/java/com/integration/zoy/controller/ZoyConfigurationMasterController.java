package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.integration.zoy.entity.TriggeredCond;
import com.integration.zoy.entity.TriggeredOn;
import com.integration.zoy.entity.TriggeredValue;
import com.integration.zoy.entity.ZoyCompanyMaster;
import com.integration.zoy.entity.ZoyCompanyProfileMaster;
import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgAutoCancellationAfterCheckIn;
import com.integration.zoy.entity.ZoyPgAutoCancellationMaster;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgEarlyCheckOut;
import com.integration.zoy.entity.ZoyPgForceCheckOut;
import com.integration.zoy.entity.ZoyPgGstCharges;
import com.integration.zoy.entity.ZoyPgNoRentalAgreement;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgShortTermMaster;
import com.integration.zoy.entity.ZoyPgShortTermRentingDuration;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.ZoyAfterCheckInCancellation;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyBeforeCheckInCancellationModel;
import com.integration.zoy.model.ZoyCompanyMasterModal;
import com.integration.zoy.model.ZoyCompanyProfileMasterModal;
import com.integration.zoy.model.ZoyPgEarlyCheckOutRule;
import com.integration.zoy.model.ZoySecurityDeadLine;
import com.integration.zoy.model.oldNewConfigRequest;
import com.integration.zoy.repository.ZoyDataGroupingRepository;
import com.integration.zoy.repository.ZoyPgCancellationDetailsRepository;
import com.integration.zoy.repository.ZoyPgEarlyCheckOutRepository;
import com.integration.zoy.repository.ZoyPgForceCheckOutRepository;
import com.integration.zoy.repository.ZoyPgGstChargesRepository;
import com.integration.zoy.repository.ZoyPgNoRentalAgreementRespository;
import com.integration.zoy.repository.ZoyPgOtherChargesRepository;
import com.integration.zoy.repository.ZoyPgRentingDurationRepository;
import com.integration.zoy.repository.ZoyPgSecurityDepositDetailsRepository;
import com.integration.zoy.repository.ZoyPgShortTermMasterRepository;
import com.integration.zoy.repository.ZoyPgTokenDetailsRepository;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.NotificationsAndAlertsService;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.PdfGenerateService;
import com.integration.zoy.service.ZoyEmailService;
import com.integration.zoy.service.ZoyS3Service;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.ZoyAdminConfigDTO;
import com.integration.zoy.utils.ZoyAfterCheckInCancellationDto;
import com.integration.zoy.utils.ZoyBeforeCheckInCancellationDto;
import com.integration.zoy.utils.ZoyCompanyMasterDto;
import com.integration.zoy.utils.ZoyCompanyProfileMasterDto;
import com.integration.zoy.utils.ZoyDataGroupingDto;
import com.integration.zoy.utils.ZoyForceCheckOutDto;
import com.integration.zoy.utils.ZoyGstChargesDto;
import com.integration.zoy.utils.ZoyOtherChargesDto;
import com.integration.zoy.utils.ZoyPgEarlyCheckOutRuleDto;
import com.integration.zoy.utils.ZoyPgNoRentalAgreementDto;
import com.integration.zoy.utils.ZoyPgSecurityDepositDetailsDTO;
import com.integration.zoy.utils.ZoyPgTokenDetailsDTO;
import com.integration.zoy.utils.ZoyRentingDuration;
import com.integration.zoy.utils.ZoySecurityDepositDeadLineDto;
import com.integration.zoy.utils.ZoyShortTermDetails;
import com.integration.zoy.utils.ZoyShortTermDto;


@RestController
@RequestMapping("")
public class ZoyConfigurationMasterController implements ZoyConfigurationMasterImpl {

	private static final Logger log = LoggerFactory.getLogger(ZoyConfigurationMasterController.class);
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
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
    
	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	AdminDBImpl adminDBImpl;
	
	@Autowired
	ZoyS3Service zoyS3Service;

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

	@Autowired
	PdfGenerateService pdfGenerateService;
	
	@Autowired
	ZoyPgSecurityDepositDetailsRepository zoyPgSecurityDepositDetailsRepository;

	@Autowired
	ZoyPgTokenDetailsRepository zoyPgTokenDetailsRepository;

	@Autowired
	ZoyPgEarlyCheckOutRepository zoyPgEarlyCheckOutRepository;

	@Autowired
	ZoyDataGroupingRepository zoyDataGroupingRepository;

	@Autowired
	ZoyPgForceCheckOutRepository zoyPgForceCheckOutRepository;

	@Autowired
	ZoyPgNoRentalAgreementRespository zoyPgNoRentalAgreementRespository;

	@Autowired
	ZoyPgRentingDurationRepository zoyPgRentingDurationRepository;

	@Autowired
	ZoyPgGstChargesRepository zoyPgGstChargesRepository;

	@Autowired
	ZoyPgOtherChargesRepository zoyPgOtherChargesRepository;

	@Autowired
    ZoyPgShortTermMasterRepository zoyPgShortTermMasterRepository;
	
	@Autowired
	ZoyPgCancellationDetailsRepository ZoyPgCancellationDetailsRepo;
	
	@Autowired
	ZoyEmailService zoyEmailService;
	
	@Autowired
	NotificationsAndAlertsService notificationsAndAlertsService;
	
	@Value("${spring.jackson.time-zone}")
	private String currentTimeZone;
	
	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateToken(ZoyPgTokenDetailsDTO details) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Token Advance";
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (details.getTokenId() != null && !details.getTokenId().isEmpty()) {
				Optional<ZoyPgTokenDetails> tokenDetails = zoyPgTokenDetailsRepository.findById(details.getTokenId());

				if (tokenDetails.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required token advance details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyPgTokenDetails oldDetails = tokenDetails.get();
					BigDecimal oldFixed = oldDetails.getFixedToken();
					BigDecimal oldVariable = oldDetails.getVariableToken();

					oldDetails.setFixedToken(details.getFixedToken());
					oldDetails.setVariableToken(details.getVariableToken());
					oldDetails.setEffectiveDate(details.getEffectiveDate());
					oldDetails.setIsApproved(details.getIsApproved());

					 if (details.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
     					zoyEmailService.sendTokenAdvanceRuleChangeEmail(details.getEffectiveDate(),oldFixed,oldVariable,details.getFixedToken(),details.getVariableToken());
     					
     					// audit 
     					StringBuffer historyContent = new StringBuffer(" has "+flag+" the Token for");
    					if (!oldFixed.equals(details.getFixedToken())) {
    						historyContent.append(", Fixed from ").append(oldFixed.stripTrailingZeros().toPlainString()).append(" to ")
    								.append(details.getFixedToken());
    					}
    					if (!oldVariable.equals(details.getVariableToken())) {
    						historyContent.append(" , Variable from ").append(oldVariable.stripTrailingZeros().toPlainString()).append(" to ")
    								.append(details.getVariableToken());
    					}
    					auditHistoryUtilities.auditForCommon(
    							SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
    							ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
    					
					} else if ( null != details.getComments()&& details.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(details.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, details.getEffectiveDate(), details.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Token for");
    					if (!oldFixed.equals(details.getFixedToken())) {
    						historyContent.append(", Fixed from ").append(oldFixed.stripTrailingZeros().toPlainString()).append(" to ")
    								.append(details.getFixedToken());
    					}
    					if (!oldVariable.equals(details.getVariableToken())) {
    						historyContent.append(" , Variable from ").append(oldVariable.stripTrailingZeros().toPlainString()).append(" to ")
    								.append(details.getVariableToken());
    					}
    					auditHistoryUtilities.auditForCommon(
    							SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
    							ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						oldDetails.setCreatedBy(currentUser);	
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					zoyPgTokenDetailsRepository.save(oldDetails);

				
				}
			} else {
				ZoyPgTokenDetails newTokenDetails = new ZoyPgTokenDetails();
				newTokenDetails
						.setFixedToken(details.getFixedToken() != null ? details.getFixedToken() : BigDecimal.ZERO);
				newTokenDetails.setVariableToken(
						details.getVariableToken() != null ? details.getVariableToken() : BigDecimal.ZERO);
				newTokenDetails.setEffectiveDate(details.getEffectiveDate());
				newTokenDetails.setCreatedBy(currentUser);
				newTokenDetails.setIsApproved(false);
				zoyPgTokenDetailsRepository.save(newTokenDetails);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
				
			}

			List<ZoyPgTokenDetails> allDetails = ownerDBImpl.findAllTokenDetailsSorted();
			List<ZoyPgTokenDetailsDTO> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Token Advance details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Token Advance details: API:/zoy_admin/config/token_advance.zoyAdminConfigCreateUpdateToken ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoyPgTokenDetailsDTO convertToDTO(ZoyPgTokenDetails entity) {
		if (entity == null)
			return null;

		ZoyPgTokenDetailsDTO dto = new ZoyPgTokenDetailsDTO();
		dto.setTokenId(entity.getTokenId());
		dto.setFixedToken(entity.getFixedToken() != null ? entity.getFixedToken() : BigDecimal.ZERO);
		dto.setVariableToken(entity.getVariableToken() != null ? entity.getVariableToken() : BigDecimal.ZERO);
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

//	@Override
//	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(
//			List<ZoyBeforeCheckInCancellation> zoyBeforeCheckInCancellations) {
//		ResponseBody response = new ResponseBody();
//		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//		try {
//			if (zoyBeforeCheckInCancellations == null || zoyBeforeCheckInCancellations.size() <= 0) {
//				response.setStatus(HttpStatus.BAD_REQUEST.value());
//				response.setError("Required cancellation details");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//			}
//			for (ZoyBeforeCheckInCancellation details : zoyBeforeCheckInCancellations) {
//				if (details.getCancellationId() != null && !details.getCancellationId().isEmpty()) {
//					if (ObjectUtils.isNotEmpty(details.getIsDelete()) && details.getIsDelete()) {
//						ZoyPgCancellationDetails cancelDetails = ownerDBImpl
//								.findBeforeCancellationDetails(details.getCancellationId());
//						if (cancelDetails == null) {
//							response.setStatus(HttpStatus.NOT_FOUND.value());
//							response.setError("Cancellation details not found for the given ID");
//							return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
//						}
//						ownerDBImpl.deleteBeforeCancellation(cancelDetails.getCancellationId());
//						// audit history here
//						String historyContent = " has deleted the Cancellation And Refund Policy for, Days before check in = "
//								+ cancelDetails.getBeforeCheckinDays() + " , Deduction percentage ="
//								+ cancelDetails.getDeductionPercentage();
//						auditHistoryUtilities.auditForCommon(
//								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,
//								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
//					} else {
//						ZoyPgCancellationDetails cancelDetails = ownerDBImpl
//								.findBeforeCancellationDetails(details.getCancellationId());
//						if (cancelDetails == null) {
//							response.setStatus(HttpStatus.CONFLICT.value());
//							response.setError("Unable to get before check-in cancellation details");
//							return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
//						}
//						final int oldFixed = cancelDetails.getBeforeCheckinDays();
//						final BigDecimal oldVariable = cancelDetails.getDeductionPercentage();
//						cancelDetails.setPriority(details.getPriority());
//						cancelDetails.setTriggerOn(details.getTriggerOn());
//						cancelDetails.setTriggerCondition(details.getTriggerCondition());
//						cancelDetails.setBeforeCheckinDays(details.getBeforeCheckinDays());
//						cancelDetails.setDeductionPercentage(details.getDeductionPercentage());
//						cancelDetails.setCond(details.getTriggerOn() + " " + details.getTriggerCondition() + " "
//								+ details.getBeforeCheckinDays());
//						cancelDetails.setTriggerValue(details.getTriggerValue());
//						ownerDBImpl.saveBeforeCancellation(cancelDetails);
//						// audit history here
//						StringBuffer historyContent = new StringBuffer(
//								" has updated the Cancellation And Refund Policy for");
//						if (oldFixed != details.getBeforeCheckinDays()) {
//							historyContent.append(", Days before check in from " + oldFixed + " to "
//									+ details.getBeforeCheckinDays());
//						}
//						if (oldVariable != details.getDeductionPercentage()) {
//							historyContent.append(" , Deduction percentage from " + oldVariable + " to "
//									+ details.getDeductionPercentage());
//						}
//						auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
//								historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
//
//					}
//				} else {
//					ZoyPgCancellationDetails newCancelDetails = new ZoyPgCancellationDetails();
//					newCancelDetails.setPriority(details.getPriority());
//					newCancelDetails.setTriggerOn(details.getTriggerOn());
//					newCancelDetails.setTriggerCondition(details.getTriggerCondition());
//					newCancelDetails.setBeforeCheckinDays(details.getBeforeCheckinDays());
//					newCancelDetails.setDeductionPercentage(details.getDeductionPercentage());
//					newCancelDetails.setCond(details.getTriggerOn() + " " + details.getTriggerCondition() + " "
//							+ details.getBeforeCheckinDays());
//					newCancelDetails.setTriggerValue(details.getTriggerValue());
//					ownerDBImpl.saveBeforeCancellation(newCancelDetails);
//					// audit history here
//					String historyContent = " has created the Cancellation And Refund Policy for, Days before check in = "
//							+ details.getBeforeCheckinDays() + " , Deduction percentage ="
//							+ details.getDeductionPercentage();
//					auditHistoryUtilities.auditForCommon(
//							SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,
//							ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
//				}
//			}
//
//			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
//			List<ZoyBeforeCheckInCancellationDto> dtoList = cancellationDetails.stream().map(this::convertToDTO)
//					.collect(Collectors.toList());
//
//			response.setStatus(HttpStatus.OK.value());
//			response.setData(dtoList);
//			response.setMessage("Retrieved all Before CheckIn details");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
//
//		} catch (Exception e) {
//			log.error(
//					"Error creating/updating before check-in API:/zoy_admin/config/before-check-in.zoyAdminConfigCreateUpdateBeforeCheckIn",
//					e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError("Internal server error");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//	}
//	  oldNewConfigRequest oldNewConfigReq
	    
	@Transactional
	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(oldNewConfigRequest oldNewConfigReq) {

	    ResponseBody response = new ResponseBody();
	    String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
	    String flag = "";
	    String ruleName = "Cancellation And Refund Policy (Before check in)";
	    
	    ZoyBeforeCheckInCancellationModel zoyBeforeCheckInCancellation = oldNewConfigReq.getNewBCCRule();
	    ZoyBeforeCheckInCancellationModel previousZoyBeforeCheckInCancellation = oldNewConfigReq.getOldBCCRule();

	    try {
	        if (zoyBeforeCheckInCancellation == null || 
	            zoyBeforeCheckInCancellation.getZoyBeforeCheckInCancellationInfo() == null) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Required Data Grouping are missing");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        List<String> deleteList = new ArrayList<>();
	        List<ZoyPgCancellationDetails> cancellationDetailsList = new ArrayList<>();

	        for (ZoyBeforeCheckInCancellation cancellation : zoyBeforeCheckInCancellation.getZoyBeforeCheckInCancellationInfo()) {
	        
	        	System.out.println("Boolean.TRUE.equals(cancellation.getIsDelete()"+Boolean.TRUE.equals(cancellation.getIsDelete()));
	        	if (Boolean.TRUE.equals(cancellation.getIsDelete())) {
	                deleteList.add(cancellation.getCancellationId());
	            } else {
	                ZoyPgCancellationDetails entity = new ZoyPgCancellationDetails();
	                entity.setPriority(cancellation.getPriority());
	                entity.setTriggerOn(cancellation.getTriggerOn());
	                entity.setTriggerCondition(cancellation.getTriggerCondition());
	                entity.setBeforeCheckinDays(cancellation.getBeforeCheckinDays());
	                entity.setDeductionPercentage(cancellation.getDeductionPercentage());
	                entity.setCond(cancellation.getTriggerOn() + " " + cancellation.getTriggerCondition() + " "
	                        + cancellation.getBeforeCheckinDays());
	                entity.setTriggerValue(cancellation.getTriggerValue());
	                entity.setEffectiveDate(zoyBeforeCheckInCancellation.getEffectiveDate());
	                entity.setPgType(zoyBeforeCheckInCancellation.getPgType());

	                if (zoyBeforeCheckInCancellation.getIscreate()) {
	                    entity.setCreatedBy(currentUser);
	                    entity.setIsApproved(false);
	                    flag = "Created";
	                } else if (zoyBeforeCheckInCancellation.getIsApproved()) {
	                    entity.setCancellationId(cancellation.getCancellationId());
	                    entity.setCreatedBy(zoyBeforeCheckInCancellation.getCreatedBy());
	                    entity.setIsApproved(true);
	                    entity.setApprovedBy(currentUser);
	                    flag = "Approved";
	                } else if (zoyBeforeCheckInCancellation.getComments() != null && !zoyBeforeCheckInCancellation.getComments().isEmpty()) {
	                    entity.setCancellationId(cancellation.getCancellationId());
	                    entity.setApprovedBy(currentUser);
	                    entity.setIsApproved(false);
	                    entity.setComments(zoyBeforeCheckInCancellation.getComments());
	                    flag = "Rejected";
	                } else {
	                    entity.setCancellationId(cancellation.getCancellationId());
	                    entity.setCreatedBy(zoyBeforeCheckInCancellation.getCreatedBy());
	                    entity.setIsApproved(false);
	                    flag = "Edited";
	                }

	                cancellationDetailsList.add(entity);
	            }
	        } 

	        if ("Created".equals(flag)) {
	            zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyBeforeCheckInCancellation.getEffectiveDate());
	            notificationsAndAlertsService.masterConfigurationRulechange(currentUser, ruleName);
	        } else if ("Approved".equals(flag)) {
	            zoyEmailService.sendApprovalNotificationEmail(currentUser, ruleName);
	            notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
	        	
	            zoyEmailService.sendCancellationRefundPolicyChangeEmail(previousZoyBeforeCheckInCancellation, zoyBeforeCheckInCancellation);
	            
				// audit history here
				String historyContent =zoyEmailService.sendCancellationRefundPolicyChange(previousZoyBeforeCheckInCancellation, zoyBeforeCheckInCancellation,flag);
		            		
				auditHistoryUtilities.auditForCommon(
				SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
	        } else if ("Rejected".equals(flag)) {
	            zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, zoyBeforeCheckInCancellation.getEffectiveDate(), zoyBeforeCheckInCancellation.getComments());
	            notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser, ruleName);
	            
				// audit history here
	            String historyContent =zoyEmailService.sendCancellationRefundPolicyChange(previousZoyBeforeCheckInCancellation, zoyBeforeCheckInCancellation,flag);
        		
				auditHistoryUtilities.auditForCommon(
				SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
	        } else if ("Edited".equals(flag)) {
	            zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyBeforeCheckInCancellation.getEffectiveDate());
	            notificationsAndAlertsService.masterConfigurationRulechange(currentUser, ruleName);
	        }

	        if (!cancellationDetailsList.isEmpty()) {
	            ZoyPgCancellationDetailsRepo.saveAll(cancellationDetailsList);
	        }

	        if (!deleteList.isEmpty() && deleteList.size()>0) {
	        	
	            String[] todeleteList = deleteList.toArray(new String[0]);
	            ZoyPgCancellationDetailsRepo.deleteBeforeCheckInCancellationbyIds(todeleteList);
	        }

	        response.setStatus(HttpStatus.OK.value());
			response.setMessage(flag+" Cancellation And Refund Policy details successfully");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error(
	            "Error creating/updating before check-in API:/zoy_admin/config/before-check-in.zoyAdminConfigCreateUpdateBeforeCheckIn",
	            e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError("Internal server error");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckInGetDetails(ZoyBeforeCheckInCancellationModel zoyBeforeCheckInCancellation) {
	    ResponseBody response = new ResponseBody();
	    
	    List<ZoyBeforeCheckInCancellationModel> beforeCheckinDetailsList = new ArrayList<>();
	    try {
	        if (zoyBeforeCheckInCancellation == null || zoyBeforeCheckInCancellation.getPgType() == null || zoyBeforeCheckInCancellation.getPgType().trim().isEmpty()) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Invalid request: pgType is required.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        List<ZoyPgCancellationDetails> cancellationList = ZoyPgCancellationDetailsRepo.findAllByOrderByCreatedAtDesc1(zoyBeforeCheckInCancellation.getPgType());

	        if (cancellationList == null || cancellationList.isEmpty()) {
	            response.setStatus(HttpStatus.OK.value());
	            response.setData(beforeCheckinDetailsList);
	            response.setError("No cancellation details found.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	        }
	        ZoyBeforeCheckInCancellationModel beforeCheckInCancellation = new ZoyBeforeCheckInCancellationModel();
	        List<ZoyBeforeCheckInCancellation> checkInCancellationDetailsList = new ArrayList<>();
	        
	        String effectiveDate="";
	        String comments ="";

	        for (ZoyPgCancellationDetails details : cancellationList) {
	            if (!(Objects.toString(effectiveDate, "") + Objects.toString(comments, ""))
	                    .equals(Objects.toString(details.getEffectiveDate(), "") + Objects.toString(details.getComments(), ""))) {
	            	
	        		beforeCheckInCancellation = new ZoyBeforeCheckInCancellationModel();
	        		beforeCheckInCancellation.setApproved(details.getIsApproved());
		            beforeCheckInCancellation.setApprovedBy(details.getApprovedBy());
		            beforeCheckInCancellation.setCreatedBy(details.getCreatedBy());
		            beforeCheckInCancellation.setEffectiveDate(details.getEffectiveDate());
		            beforeCheckInCancellation.setPgType(details.getPgType());
		            beforeCheckInCancellation.setComments(details.getComments());

		            checkInCancellationDetailsList = new ArrayList<>();
	        	}
	        	
	        	
	            
	            ZoyBeforeCheckInCancellation checkInCancellationDetails = new ZoyBeforeCheckInCancellation();
	            checkInCancellationDetails.setBeforeCheckinDays(details.getBeforeCheckinDays());
	            checkInCancellationDetails.setCancellationId(details.getCancellationId());
	            checkInCancellationDetails.setCond(details.getCond());
	            checkInCancellationDetails.setCreateAt(details.getCreateAt());
	            checkInCancellationDetails.setDeductionPercentage(details.getDeductionPercentage());
	            checkInCancellationDetails.setPriority(details.getPriority());
	            checkInCancellationDetails.setTriggerCondition(details.getTriggerCondition());
	            checkInCancellationDetails.setTriggerOn(details.getTriggerOn());
	            checkInCancellationDetails.setTriggerValue(details.getTriggerValue());

	            checkInCancellationDetailsList.add(checkInCancellationDetails);
	            beforeCheckInCancellation.setZoyBeforeCheckInCancellationInfo(checkInCancellationDetailsList);
	         
	            if (!(Objects.toString(effectiveDate, "") + Objects.toString(comments, ""))
	                    .equals(Objects.toString(details.getEffectiveDate(), "") + Objects.toString(details.getComments(), ""))) {
	            	  beforeCheckinDetailsList.add(beforeCheckInCancellation);
	            }
	            effectiveDate=details.getEffectiveDate();
	            comments=details.getComments();
	          
	        }
	        response.setStatus(HttpStatus.OK.value());
            response.setData(beforeCheckinDetailsList);
            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
      	    } catch (Exception e) {
	        log.error("Error in API :/zoy_admin/config/fetch-Cancellation-And-Refund-Policy-details.zoyAdminConfigCreateUpdateBeforeCheckInGetDetails", e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	private ZoyBeforeCheckInCancellationDto convertToDTO(ZoyPgCancellationDetails details) {
		ZoyBeforeCheckInCancellationDto dto = new ZoyBeforeCheckInCancellationDto();
		dto.setCancellationId(details.getCancellationId());
		dto.setPriority(details.getPriority());
		dto.setTriggerOn(details.getTriggerOn());
		dto.setTriggerCondition(details.getTriggerCondition());
		dto.setBeforeCheckinDays(details.getBeforeCheckinDays());
		dto.setDeductionPercentage(details.getDeductionPercentage());
		dto.setCond(
				details.getTriggerOn() + " " + details.getTriggerCondition() + " " + details.getBeforeCheckinDays());
		dto.setTriggerValue(details.getTriggerValue());
		dto.setCreateAt(details.getCreateAt());
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateOtherCharges(ZoyOtherChargesDto details) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Other Charges";

		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			BigDecimal ownerCharges;
			BigDecimal tenantCharges;
			BigDecimal tenantEkycCharges;
			BigDecimal ownerEkycCharges;

			if (details.getOtherChargesId() != null && !details.getOtherChargesId().isEmpty()) {
				Optional<ZoyPgOtherCharges> otherChargesDetails = zoyPgOtherChargesRepository
						.findById(details.getOtherChargesId());

				if (!otherChargesDetails.isPresent()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required Other Charges details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyPgOtherCharges oldDetails = otherChargesDetails.get();

					final BigDecimal oldOwnerCharges = oldDetails.getOwnerDocumentCharges();
					final BigDecimal oldTenantCharges = oldDetails.getTenantDocumentCharges();
					final BigDecimal oldOwnerEkycCharges = oldDetails.getOwnerEkycCharges();
					final BigDecimal oldTenantEkycCharges = oldDetails.getTenantEkycCharges();

					ownerCharges = (details.getOwnerDocumentCharges() != null) ? details.getOwnerDocumentCharges()
							: oldDetails.getOwnerDocumentCharges();
					ownerEkycCharges = (details.getOwnerEkycCharges() != null) ? details.getOwnerEkycCharges()
							: oldDetails.getOwnerEkycCharges();
					tenantCharges = (details.getTenantDocumentCharges() != null) ? details.getTenantDocumentCharges()
							: oldDetails.getTenantDocumentCharges();
					tenantEkycCharges = (details.getTenantEkycCharges() != null) ? details.getTenantEkycCharges()
							: oldDetails.getTenantEkycCharges();

					oldDetails.setOwnerDocumentCharges(ownerCharges);
					oldDetails.setTenantDocumentCharges(tenantCharges);
					oldDetails.setOwnerEkycCharges(ownerEkycCharges);
					oldDetails.setTenantEkycCharges(tenantEkycCharges);
					oldDetails.setIsApproved(details.getIsApproved());
					oldDetails.setEffectiveDate(details.getEffectiveDate());

					if (details.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendOtherChargesChangeEmailForOwners(details.getEffectiveDate(),oldOwnerCharges,oldOwnerEkycCharges,ownerCharges,ownerEkycCharges);
						zoyEmailService.sendOtherChargesChangeEmailForUsers(details.getEffectiveDate(),oldTenantCharges,oldTenantEkycCharges,tenantCharges,tenantEkycCharges);
						
						// Audit history here
						StringBuilder historyContent = new StringBuilder(" has "+flag+" the Other Charges for");
						if (oldOwnerCharges != oldDetails.getOwnerDocumentCharges()) {
							historyContent.append(", owner document charges from ").append(oldOwnerCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getOwnerDocumentCharges());
						}
						if (oldOwnerEkycCharges != oldDetails.getOwnerEkycCharges()) {
							historyContent.append(", owner eKYC charges from ").append(oldOwnerEkycCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getOwnerEkycCharges());
						}
						if (oldTenantCharges != oldDetails.getTenantDocumentCharges()) {
							historyContent.append(", tenant document charges from ").append(oldTenantCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getTenantDocumentCharges());
						}
						if (oldTenantEkycCharges != oldDetails.getTenantEkycCharges()) {
							historyContent.append(", tenant eKYC charges from ").append(oldTenantEkycCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getTenantEkycCharges());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

					} else if ( null != details.getComments()&& details.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(details.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, details.getEffectiveDate(), details.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit history here
						StringBuilder historyContent = new StringBuilder(" has "+flag+" the Other Charges for");
						if (oldOwnerCharges != oldDetails.getOwnerDocumentCharges()) {
							historyContent.append(", owner document charges from ").append(oldOwnerCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getOwnerDocumentCharges());
						}
						if (oldOwnerEkycCharges != oldDetails.getOwnerEkycCharges()) {
							historyContent.append(", owner eKYC charges from ").append(oldOwnerEkycCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getOwnerEkycCharges());
						}
						if (oldTenantCharges != oldDetails.getTenantDocumentCharges()) {
							historyContent.append(", tenant document charges from ").append(oldTenantCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getTenantDocumentCharges());
						}
						if (oldTenantEkycCharges != oldDetails.getTenantEkycCharges()) {
							historyContent.append(", tenant eKYC charges from ").append(oldTenantEkycCharges.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getTenantEkycCharges());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

					}else{
						oldDetails.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}				 

					ownerDBImpl.saveOtherCharges(oldDetails);
					
				}
			} else {
				ownerCharges = (details.getOwnerDocumentCharges() != null) ? details.getOwnerDocumentCharges()
						: BigDecimal.ZERO;
				ownerEkycCharges = (details.getOwnerEkycCharges() != null) ? details.getOwnerEkycCharges()
						: BigDecimal.ZERO;
				tenantCharges = (details.getTenantDocumentCharges() != null) ? details.getTenantDocumentCharges()
						: BigDecimal.ZERO;
				tenantEkycCharges = (details.getTenantEkycCharges() != null) ? details.getTenantEkycCharges()
						: BigDecimal.ZERO;

				ZoyPgOtherCharges newOtherCharges = new ZoyPgOtherCharges();
				newOtherCharges.setOwnerDocumentCharges(ownerCharges);
				newOtherCharges.setTenantDocumentCharges(tenantCharges);
				newOtherCharges.setOwnerEkycCharges(ownerEkycCharges);
				newOtherCharges.setTenantEkycCharges(tenantEkycCharges);

				newOtherCharges.setEffectiveDate(details.getEffectiveDate());
				newOtherCharges.setCreatedBy(currentUser);
				newOtherCharges.setIsApproved(false);
				ownerDBImpl.saveOtherCharges(newOtherCharges);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgOtherCharges> allDetails = ownerDBImpl.findAllOtherChargesDetailsSorted();
			List<ZoyOtherChargesDto> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Other Charges details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error(
					"Error saving/creating Other Charges details API:/zoy_admin/config/other-charges.zoyAdminConfigCreateUpdateOtherCharges ",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateGstCharges(ZoyGstChargesDto details) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="GST Charges";
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required GST Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			BigDecimal cgstPercentage;
			BigDecimal sgstPercentage;
			BigDecimal igstPercentage;
			BigDecimal monthlyRent;

			if (details.getRentId() != null && !details.getRentId().isEmpty()) {
				Optional<ZoyPgGstCharges> PgGstChargesDetails = zoyPgGstChargesRepository.findById(details.getRentId());

				if (!PgGstChargesDetails.isPresent()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required GST Charges details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyPgGstCharges oldDetails = PgGstChargesDetails.get();

					final BigDecimal oldCGstPercentage = oldDetails.getCgstPercentage();
					final BigDecimal oldSGstPercentage = oldDetails.getSgstPercentage();
					final BigDecimal oldIGstPercentage = oldDetails.getIgstPercentage();
					final BigDecimal oldMonthlyRent = oldDetails.getMonthlyRent();

					cgstPercentage = (details.getCgstPercentage() != null) ? details.getCgstPercentage()
							: oldDetails.getCgstPercentage();
					sgstPercentage = (details.getSgstPercentage() != null) ? details.getSgstPercentage()
							: oldDetails.getSgstPercentage();
					igstPercentage = (details.getIgstPercentage() != null) ? details.getIgstPercentage()
							: oldDetails.getIgstPercentage();
					monthlyRent = (details.getMonthlyRent() != null) ? details.getMonthlyRent()
							: oldDetails.getMonthlyRent();

					oldDetails.setCgstPercentage(cgstPercentage);
					oldDetails.setSgstPercentage(sgstPercentage);
					oldDetails.setIgstPercentage(igstPercentage);
					oldDetails.setMonthlyRent(monthlyRent);
					oldDetails.setComponentName("RENT");
					oldDetails.setIsApproved(details.getIsApproved());
					oldDetails.setEffectiveDate(details.getEffectiveDate());

					if (details.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);	
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendGstChargesChangeEmail(details.getEffectiveDate(),oldCGstPercentage,oldMonthlyRent,cgstPercentage,monthlyRent);
						
						// Audit history here
						StringBuilder historyContent = new StringBuilder(" has "+flag+" the Gst Changes for");
						if (oldCGstPercentage != oldDetails.getCgstPercentage()) {
							historyContent.append(", CGST percentage charges from ").append(oldCGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getCgstPercentage());
						}
						if (oldSGstPercentage != oldDetails.getSgstPercentage()) {
							historyContent.append(", SGST percentage charges from ").append(oldSGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getSgstPercentage());
						}
						if (oldIGstPercentage != oldDetails.getIgstPercentage()) {
							historyContent.append(", IGST percentage charges from ").append(oldIGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getIgstPercentage());
						}
						if (oldMonthlyRent != oldDetails.getMonthlyRent()) {
							historyContent.append(", monthly Rent charges from ").append(oldMonthlyRent.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getMonthlyRent());
						}
					
						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
						
					} else if ( null != details.getComments()&& details.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(details.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, details.getEffectiveDate(), details.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit history here
						StringBuilder historyContent = new StringBuilder(" has "+flag+" the Gst Changes for");
						if (oldCGstPercentage != oldDetails.getCgstPercentage()) {
							historyContent.append(", CGST percentage charges from ").append(oldCGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getCgstPercentage());
						}
						if (oldSGstPercentage != oldDetails.getSgstPercentage()) {
							historyContent.append(", SGST percentage charges from ").append(oldSGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getSgstPercentage());
						}
						if (oldIGstPercentage != oldDetails.getIgstPercentage()) {
							historyContent.append(", IGST percentage charges from ").append(oldIGstPercentage.stripTrailingZeros().toPlainString())
									.append(" to ").append(oldDetails.getIgstPercentage());
						}
						if (oldMonthlyRent != oldDetails.getMonthlyRent()) {
							historyContent.append(", monthly Rent charges from ").append(oldMonthlyRent.stripTrailingZeros().toPlainString()).append(" to ")
									.append(oldDetails.getMonthlyRent());
						}
					
						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						oldDetails.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					ownerDBImpl.saveGstCharges(oldDetails);
				}
			} else {
				cgstPercentage = (details.getCgstPercentage() != null) ? details.getCgstPercentage() : BigDecimal.ZERO;
				sgstPercentage = (details.getSgstPercentage() != null) ? details.getSgstPercentage() : BigDecimal.ZERO;
				igstPercentage = (details.getIgstPercentage() != null) ? details.getIgstPercentage() : BigDecimal.ZERO;
				monthlyRent = (details.getMonthlyRent() != null) ? details.getMonthlyRent() : BigDecimal.ZERO;

				ZoyPgGstCharges newGstCharges = new ZoyPgGstCharges();

				newGstCharges.setCgstPercentage(cgstPercentage);
				newGstCharges.setSgstPercentage(sgstPercentage);
				newGstCharges.setIgstPercentage(igstPercentage);
				newGstCharges.setMonthlyRent(monthlyRent);
				newGstCharges.setComponentName("RENT");

				newGstCharges.setEffectiveDate(details.getEffectiveDate());
				newGstCharges.setCreatedBy(currentUser);
				newGstCharges.setIsApproved(false);
				ownerDBImpl.saveGstCharges(newGstCharges);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgGstCharges> allDetails = ownerDBImpl.findAllGstChargesDetailsSorted();
			List<ZoyGstChargesDto> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" GST Charges details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error(
					"Error saving/creating GST Charges details API:/zoy_admin/config/gst-charges.zoyAdminConfigCreateUpdateGstCharges ",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private ZoyOtherChargesDto convertToDTO(ZoyPgOtherCharges entity) {
		ZoyOtherChargesDto dto = new ZoyOtherChargesDto();
		dto.setOtherChargesId(entity.getOtherChargesId());
		dto.setOwnerDocumentCharges(entity.getOwnerDocumentCharges());
		dto.setTenantDocumentCharges(entity.getTenantDocumentCharges());
		dto.setTenantEkycCharges(entity.getTenantEkycCharges());
		dto.setOwnerEkycCharges(entity.getOwnerEkycCharges());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	private ZoyGstChargesDto convertToDTO(ZoyPgGstCharges entity) {
		ZoyGstChargesDto dto = new ZoyGstChargesDto();
		dto.setRentId(entity.getRentId());
		dto.setCgstPercentage(entity.getCgstPercentage());
		dto.setSgstPercentage(entity.getSgstPercentage());
		dto.setIgstPercentage(entity.getIgstPercentage());
		dto.setMonthlyRent(entity.getMonthlyRent());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateDataGrouping(ZoyDataGroupingDto details) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Data Grouping";
		try {
			currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Data Grouping are missing");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (details.getDataGroupingId() != null && !details.getDataGroupingId().trim().isEmpty()) {
				Optional<ZoyDataGrouping> dataGroupingOptional = zoyDataGroupingRepository
						.findById(details.getDataGroupingId());

				if (dataGroupingOptional.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Data Grouping details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyDataGrouping existingGroup = dataGroupingOptional.get();
					int oldFixed = existingGroup.getConsiderDays();

					existingGroup.setConsiderDays(details.getConsiderDays());
					existingGroup.setEffectiveDate(details.getEffectiveDate());
					existingGroup.setIsApproved(details.getIsApproved());

					if (details.getIsApproved()) {
						existingGroup.setApprovedBy(currentUser);	
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						
						// Audit history for update
						String historyContent = String.format(
								"has "+flag+" the Data Grouping. Considering days from %d to %d", oldFixed,
								details.getConsiderDays());
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != details.getComments()&& details.getComments()!="") {
						existingGroup.setApprovedBy(currentUser);
						existingGroup.setComments(details.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, details.getEffectiveDate(), details.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit history for update
						String historyContent = String.format(
								"has "+flag+" the Data Grouping. Considering days from %d to %d", oldFixed,
								details.getConsiderDays());
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						existingGroup.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					zoyDataGroupingRepository.save(existingGroup);

					
				}
			} else {
				// Create a new Data Grouping
				ZoyDataGrouping newGroup = new ZoyDataGrouping();
				newGroup.setConsiderDays(details.getConsiderDays());
				newGroup.setEffectiveDate(details.getEffectiveDate());
				newGroup.setCreatedBy(currentUser);
				newGroup.setIsApproved(false);

				zoyDataGroupingRepository.save(newGroup);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyDataGrouping> allDetails = ownerDBImpl.findAllDataGroupingSorted();
			List<ZoyDataGroupingDto> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Data Grouping details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Data Grouping details API:/zoy_admin/config/data-grouping.zoyAdminConfigCreateUpdateDataGrouping",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoyDataGroupingDto convertToDTO(ZoyDataGrouping entity) {
		ZoyDataGroupingDto dto = new ZoyDataGroupingDto();
		dto.setDataGroupingId(entity.getDataGroupingId());
		dto.setConsiderDays(entity.getConsiderDays());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositLimits(
			ZoyPgSecurityDepositDetailsDTO details) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Security Deposit Limits";
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required SecurityDeposit Limit details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (details.getDepositId() != null && !"".equals(details.getDepositId())) {

				Optional<ZoyPgSecurityDepositDetails> depositDetails = zoyPgSecurityDepositDetailsRepository
						.findById(details.getDepositId());

				if (depositDetails.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required SecurityDeposit Limit details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyPgSecurityDepositDetails oldDetails = depositDetails.get();
					BigDecimal oldFixed = oldDetails.getSecurityDepositMax();
					BigDecimal oldVariable = oldDetails.getSecurityDepositMin();

					oldDetails.setEffectiveDate(details.getEffectiveDate());
					oldDetails.setIsApproved(details.getIsApproved());
					oldDetails.setSecurityDepositMax(details.getMaximumDeposit());
					oldDetails.setSecurityDepositMin(details.getMinimumDeposit());
				
					if (details.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						
						// Build history for audit
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Security Deposit Limit for ");
						if (oldFixed != details.getMaximumDeposit()) {
							historyContent.append(" Max from ").append(oldFixed.stripTrailingZeros().toPlainString()).append(" to ")
									.append(details.getMaximumDeposit());
						}
						if (oldVariable != details.getMinimumDeposit()) {
							historyContent.append(" , Min from ").append(oldVariable.stripTrailingZeros().toPlainString()).append(" to ")
									.append(details.getMinimumDeposit());
						}

						// Audit history
						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
						
					} else if ( null != details.getComments()&& details.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(details.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, details.getEffectiveDate(), details.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						zoyEmailService.sendSecurityDepositLimitsChangeEmail(details.getEffectiveDate(),oldVariable,oldFixed,details.getMinimumDeposit(),details.getMaximumDeposit());
						
						// Build history for audit
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Security Deposit Limit for ");
						if (oldFixed != details.getMaximumDeposit()) {
							historyContent.append("Max from ").append(oldFixed).append(" to ")
									.append(details.getMaximumDeposit());
						}
						if (oldVariable != details.getMinimumDeposit()) {
							historyContent.append(" , Min from ").append(oldVariable).append(" to ")
									.append(details.getMinimumDeposit());
						}

						// Audit history
						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						oldDetails.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					zoyPgSecurityDepositDetailsRepository.save(oldDetails);

				}

			} else {
				ZoyPgSecurityDepositDetails newDetails = new ZoyPgSecurityDepositDetails();
				newDetails.setSecurityDepositMax(details.getMaximumDeposit());
				newDetails.setSecurityDepositMin(details.getMinimumDeposit());
				newDetails.setEffectiveDate(details.getEffectiveDate());
				newDetails.setCreatedBy(SecurityContextHolder.getContext().getAuthentication().getName());
				newDetails.setIsApproved(false);

				zoyPgSecurityDepositDetailsRepository.save(newDetails);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), details.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgSecurityDepositDetails> allDetails = ownerDBImpl.findAllSortedByEffectiveDate();
			List<ZoyPgSecurityDepositDetailsDTO> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Security Deposit Limit details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Security Deposits Limit details: API:/zoy_admin/config/security-deposit-limits ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoyPgSecurityDepositDetailsDTO convertToDTO(ZoyPgSecurityDepositDetails entity) {
		ZoyPgSecurityDepositDetailsDTO dto = new ZoyPgSecurityDepositDetailsDTO();
		dto.setDepositId(entity != null && entity.getSecurityDepositId() != null ? entity.getSecurityDepositId() : "");
		dto.setMinimumDeposit(entity != null && entity.getSecurityDepositMin() != null ? entity.getSecurityDepositMin()
				: BigDecimal.ZERO);
		dto.setMaximumDeposit(entity != null && entity.getSecurityDepositMax() != null ? entity.getSecurityDepositMax()
				: BigDecimal.ZERO);
		dto.setEffectiveDate(entity.getEffectiveDate());
		dto.setIsApproved(entity.getIsApproved()!= null ? entity.getIsApproved():false);
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateEarlyCheckOutRules(ZoyPgEarlyCheckOutRule zoyPgEarlyCheckOut) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Cancellation And Refund Policy (Before check in)";
		try {
			if (zoyPgEarlyCheckOut == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Early Check out Rules details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (zoyPgEarlyCheckOut.getEarlyCheckOutId() != null
					&& !"".equals(zoyPgEarlyCheckOut.getEarlyCheckOutId())) {

				ZoyPgEarlyCheckOut existingRule = ownerDBImpl
						.findEarlyCheckOutRule(zoyPgEarlyCheckOut.getEarlyCheckOutId());

				if (existingRule.getEarlyCheckOutId() == null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required Early Check-out Rules details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					final Long oldFixed = existingRule.getCheckOutDay();
					final BigDecimal oldVariable = existingRule.getDeductionPercentage();
					final String oldcondition=existingRule.getTriggerCondition();
					
					existingRule.setTriggerOn(zoyPgEarlyCheckOut.getTriggerOn());
					existingRule.setTriggerCondition(zoyPgEarlyCheckOut.getTriggerCondition());
					existingRule.setCheckOutDay(zoyPgEarlyCheckOut.getCheckOutDay());
					existingRule.setDeductionPercentage(zoyPgEarlyCheckOut.getDeductionPercentage());
					existingRule.setCond(zoyPgEarlyCheckOut.getTriggerOn() + " "
							+ zoyPgEarlyCheckOut.getTriggerCondition() + " " + zoyPgEarlyCheckOut.getCheckOutDay());
					existingRule.setTriggerValue(zoyPgEarlyCheckOut.getTriggerValue());

					existingRule.setEffectiveDate(zoyPgEarlyCheckOut.getEffectiveDate());
					existingRule.setIsApproved(zoyPgEarlyCheckOut.getIsApproved());
					
					if (zoyPgEarlyCheckOut.getIsApproved()) {
						existingRule.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendEarlyCheckoutChangeEmail(zoyPgEarlyCheckOut.getEffectiveDate(),oldFixed,zoyPgEarlyCheckOut.getCheckOutDay(),oldcondition,zoyPgEarlyCheckOut.getTriggerCondition());
						
						//audit history
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Early Check out for");
						if (oldFixed != zoyPgEarlyCheckOut.getCheckOutDay()) {
							historyContent.append(
									", Chek out Days from " + oldFixed + " to " + zoyPgEarlyCheckOut.getCheckOutDay());
						}
						if (oldVariable != zoyPgEarlyCheckOut.getDeductionPercentage()) {
							historyContent.append(" ,  Deduction percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoyPgEarlyCheckOut.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != zoyPgEarlyCheckOut.getComments()&& zoyPgEarlyCheckOut.getComments()!="") {
						existingRule.setApprovedBy(currentUser);
						existingRule.setComments(zoyPgEarlyCheckOut.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, zoyPgEarlyCheckOut.getEffectiveDate(), zoyPgEarlyCheckOut.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						//audit history
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Early Check out for");
						if (oldFixed != zoyPgEarlyCheckOut.getCheckOutDay()) {
							historyContent.append(
									", Chek out Days from " + oldFixed + " to " + zoyPgEarlyCheckOut.getCheckOutDay());
						}
						if (oldVariable != zoyPgEarlyCheckOut.getDeductionPercentage()) {
							historyContent.append(" ,  Deduction percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoyPgEarlyCheckOut.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(
								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					}else{
						existingRule.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyPgEarlyCheckOut.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					ownerDBImpl.saveEarlyCheckOut(existingRule);
					
				}
			} else {
				ZoyPgEarlyCheckOut newRule = new ZoyPgEarlyCheckOut();
				newRule.setTriggerOn(zoyPgEarlyCheckOut.getTriggerOn());
				newRule.setTriggerCondition(zoyPgEarlyCheckOut.getTriggerCondition());
				newRule.setCheckOutDay(zoyPgEarlyCheckOut.getCheckOutDay());
				newRule.setDeductionPercentage(zoyPgEarlyCheckOut.getDeductionPercentage());
				newRule.setCond(zoyPgEarlyCheckOut.getTriggerOn() + " " + zoyPgEarlyCheckOut.getTriggerCondition() + " "
						+ zoyPgEarlyCheckOut.getCheckOutDay());
				newRule.setTriggerValue(zoyPgEarlyCheckOut.getTriggerValue());
				newRule.setIsApproved(false);
				newRule.setEffectiveDate(zoyPgEarlyCheckOut.getEffectiveDate());
				newRule.setCreatedBy(currentUser);
				ownerDBImpl.saveEarlyCheckOut(newRule);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyPgEarlyCheckOut.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgEarlyCheckOut> allDetails = ownerDBImpl.findAllEarlyCheckOutRulesSorted();
			List<ZoyPgEarlyCheckOutRuleDto> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Early Check-out Rules details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Early check out  Rule API:/zoy_admin/config/early-checkout-rules.zoyAdminCreateUpadateEarlyCheckOutRules\r\n ",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private ZoyPgEarlyCheckOutRuleDto convertToDTO(ZoyPgEarlyCheckOut entity) {
		ZoyPgEarlyCheckOutRuleDto dto = new ZoyPgEarlyCheckOutRuleDto();
		dto.setEarlyCheckOutId(entity.getEarlyCheckOutId());
		dto.setTriggerOn(entity.getTriggerOn());
		dto.setTriggerCondition(entity.getTriggerCondition());
		dto.setCheckOutDay(entity.getCheckOutDay());
		dto.setDeductionPercentage(entity.getDeductionPercentage());
		dto.setCond(entity.getTriggerOn() + " " + entity.getTriggerCondition() + " " + entity.getCheckOutDay());
		dto.setTriggerValue(entity.getTriggerValue());
		dto.setEffectiveDate(entity.getEffectiveDate());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

//	private List<ZoyBeforeCheckInCancellationDto> convertToDTO(List<ZoyPgCancellationDetails> cancellationDetailsList) {
//		return cancellationDetailsList.stream().map(this::convertToDTO).collect(Collectors.toList());
//	}

	// @Override
	// @Transactional
	// public ResponseEntity<String> zoyAdminConfigDeleteBeforeCheckIn(@RequestBody
	// ZoyBeforeCheckInCancellation cancellationID) {
	// ResponseBody response = new ResponseBody();
	// try {
	// if (cancellationID.getCancellationId() == null ||
	// cancellationID.getCancellationId().isEmpty()) {
	// response.setStatus(HttpStatus.BAD_REQUEST.value());
	// response.setError("Cancellation ID is required");
	// return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	// }
	//
	// final ZoyPgCancellationDetails cancelDetails =
	// ownerDBImpl.findBeforeCancellationDetails(cancellationID.getCancellationId());
	// if (cancelDetails == null) {
	// response.setStatus(HttpStatus.NOT_FOUND.value());
	// response.setError("Cancellation details not found for the given ID");
	// return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	// }
	// ownerDBImpl.deleteBeforeCancellation(cancellationID.getCancellationId());
	// //audit history here
	// String historyContent=" has deleted the Cancellation And Refund Policy for,
	// Days before check in = "+cancelDetails.getBeforeCheckinDays()+" , Deduction
	// percentage ="+cancelDetails.getDeductionPercentage();
	// auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
	// historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
	//
	// List<ZoyPgCancellationDetails> cancellationDetails =
	// ownerDBImpl.findAllBeforeCancellation();
	// List<ZoyBeforeCheckInCancellationDto> dtoList = cancellationDetails.stream()
	// .map(this::convertToDTO)
	// .collect(Collectors.toList());
	// response.setStatus(HttpStatus.OK.value());
	// response.setData(dtoList);
	// response.setMessage("Cancellation details successfully deleted");
	// return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	//
	// } catch (Exception e) {
	// log.error("Error deleting cancellation details API:
	// /zoy_admin/config/before-check-in/{cancellationId}", e);
	// response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	// response.setError("Internal server error");
	// return new ResponseEntity<>(gson.toJson(response),
	// HttpStatus.INTERNAL_SERVER_ERROR);
	// }
	// }

	private ZoyAfterCheckInCancellationDto convertToDTO(ZoyPgAutoCancellationAfterCheckIn entity) {
		ZoyAfterCheckInCancellationDto dto = new ZoyAfterCheckInCancellationDto();
		dto.setAutoCancellationId(entity.getAutoCancellationId());
		dto.setTriggerOn(entity.getTriggerOn());
		dto.setTriggerCondition(entity.getTriggerCondition());
		dto.setAutoCancellationDay(entity.getAutoCancellationDay());
		dto.setDeductionPercentage(entity.getDeductionPercentage());
		dto.setCond(entity.getTriggerOn() + " " + entity.getTriggerCondition() + " " + entity.getAutoCancellationDay());
		dto.setTriggerValue(entity.getTriggerValue());
		dto.setEffectiveDate(entity.getEffectiveDate());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateAfterCheckIn(
			ZoyAfterCheckInCancellation zoyAfterCheckInCancellation) {

		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="After check-in Date";
		try {
			if (zoyAfterCheckInCancellation == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required After Check In details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (zoyAfterCheckInCancellation.getAutoCancellationId() != null
					&& !"".equals(zoyAfterCheckInCancellation.getAutoCancellationId())) {

				ZoyPgAutoCancellationAfterCheckIn existingRule = ownerDBImpl
						.findAutoCancellationAfterCheckIn(zoyAfterCheckInCancellation.getAutoCancellationId());

				if (existingRule.getAutoCancellationId() == null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required After check-in Date details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					final Long oldFixed = existingRule.getAutoCancellationDay();
					final BigDecimal oldVariable = existingRule.getDeductionPercentage();
					final String oldcondition=existingRule.getTriggerCondition();

					// Update values
					existingRule.setTriggerOn(zoyAfterCheckInCancellation.getTriggerOn());
					existingRule.setTriggerCondition(zoyAfterCheckInCancellation.getTriggerCondition());
					existingRule.setAutoCancellationDay(zoyAfterCheckInCancellation.getAutoCancellationDay());
					existingRule.setDeductionPercentage(zoyAfterCheckInCancellation.getDeductionPercentage());
					existingRule.setCond(zoyAfterCheckInCancellation.getTriggerOn() + " "
							+ zoyAfterCheckInCancellation.getTriggerCondition() + " "
							+ zoyAfterCheckInCancellation.getAutoCancellationDay());
					existingRule.setTriggerValue(zoyAfterCheckInCancellation.getTriggerValue());
					existingRule.setEffectiveDate(zoyAfterCheckInCancellation.getEffectiveDate());
					existingRule.setIsApproved(zoyAfterCheckInCancellation.getIsApproved());

					if (zoyAfterCheckInCancellation.getIsApproved()) {
						existingRule.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendAutoCancellationAfterCheckinChangeEmail(zoyAfterCheckInCancellation.getEffectiveDate(), oldFixed, zoyAfterCheckInCancellation.getAutoCancellationDay(),oldcondition,zoyAfterCheckInCancellation.getTriggerCondition());
					
						// Audit history
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Early Check out for");
						if (oldFixed != zoyAfterCheckInCancellation.getAutoCancellationDay()) {
							historyContent.append(", Check out Days from " + oldFixed + " to "
									+ zoyAfterCheckInCancellation.getAutoCancellationDay());
						}
						if (oldVariable != zoyAfterCheckInCancellation.getDeductionPercentage()) {
							historyContent.append(" , Deduction percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoyAfterCheckInCancellation.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != zoyAfterCheckInCancellation.getComments()&& zoyAfterCheckInCancellation.getComments()!="") {
						existingRule.setApprovedBy(currentUser);
						existingRule.setComments(zoyAfterCheckInCancellation.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, zoyAfterCheckInCancellation.getEffectiveDate(), zoyAfterCheckInCancellation.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit history
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Early Check out for");
						if (oldFixed != zoyAfterCheckInCancellation.getAutoCancellationDay()) {
							historyContent.append(", Check out Days from " + oldFixed + " to "
									+ zoyAfterCheckInCancellation.getAutoCancellationDay());
						}
						if (oldVariable != zoyAfterCheckInCancellation.getDeductionPercentage()) {
							historyContent.append(" , Deduction percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoyAfterCheckInCancellation.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						existingRule.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyAfterCheckInCancellation.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					ownerDBImpl.saveAutoCancellationAfterCheckIn(existingRule);

				}
			} else {
				ZoyPgAutoCancellationAfterCheckIn newRule = new ZoyPgAutoCancellationAfterCheckIn();
				newRule.setTriggerOn(zoyAfterCheckInCancellation.getTriggerOn());
				newRule.setTriggerCondition(zoyAfterCheckInCancellation.getTriggerCondition());
				newRule.setAutoCancellationDay(zoyAfterCheckInCancellation.getAutoCancellationDay());
				newRule.setDeductionPercentage(zoyAfterCheckInCancellation.getDeductionPercentage());
				newRule.setCond(zoyAfterCheckInCancellation.getTriggerOn() + " "
						+ zoyAfterCheckInCancellation.getTriggerCondition() + " "
						+ zoyAfterCheckInCancellation.getAutoCancellationDay());
				newRule.setTriggerValue(zoyAfterCheckInCancellation.getTriggerValue());
				newRule.setIsApproved(false);
				newRule.setEffectiveDate(zoyAfterCheckInCancellation.getEffectiveDate());
				newRule.setCreatedBy(currentUser);

				ownerDBImpl.saveAutoCancellationAfterCheckIn(newRule);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoyAfterCheckInCancellation.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
			}

			// Return updated data
			List<ZoyPgAutoCancellationAfterCheckIn> allDetails = ownerDBImpl.findAllAfterCheckInDatesSorted();
			List<ZoyAfterCheckInCancellationDto> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" After check-in Date details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Cancellation After Check in Rule API:/zoy_admin/config/after-check-in.zoyAdminCreateUpadateAfterCheckIn ",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


//	private List<ZoyShortTermDto> convertShortToDTO(List<ZoyPgShortTermMaster> shortTermMaster) {
//		List<ZoyShortTermDto> dtos = new ArrayList<>();
//		for (ZoyPgShortTermMaster master : shortTermMaster) {
//			ZoyShortTermDto dto = new ZoyShortTermDto();
//			dto.setShortTermId(master.getZoyPgShortTermMasterId());
//			dto.setDays(master.getStartDay() + "-" + master.getEndDay());
//			dto.setPercentage(master.getPercentage());
//			dtos.add(dto);
//		}
//		return dtos;
//	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateSecurityDepositDeadline(
			ZoySecurityDeadLine zoySecurityDeadLine) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Security Deposit Deadline";

		try {
			if (zoySecurityDeadLine == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Security Deposit Deadline details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (zoySecurityDeadLine.getAutoCancellationId() != null
					&& !"".equals(zoySecurityDeadLine.getAutoCancellationId())) {
				ZoyPgAutoCancellationMaster existingRule = ownerDBImpl
						.findSecurityDepositDeadLine(zoySecurityDeadLine.getAutoCancellationId());

				if (existingRule == null) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required Security Deposit Deadline details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					final Long oldFixed = existingRule.getAutoCancellationDay();
					final BigDecimal oldVariable = existingRule.getDeductionPercentage();
					final String oldcondition =existingRule.getTriggerCondition();
					
					existingRule.setTriggerOn(zoySecurityDeadLine.getTriggerOn());
					existingRule.setTriggerCondition(zoySecurityDeadLine.getTriggerCondition());
					existingRule.setAutoCancellationDay(zoySecurityDeadLine.getAutoCancellationDay());
					existingRule.setDeductionPercentage(zoySecurityDeadLine.getDeductionPercentage());
					existingRule.setCond(
							zoySecurityDeadLine.getTriggerOn() + " " + zoySecurityDeadLine.getTriggerCondition() + " "
									+ zoySecurityDeadLine.getAutoCancellationDay());
					existingRule.setTriggerValue(zoySecurityDeadLine.getTriggerValue());
					existingRule.setEffectiveDate(zoySecurityDeadLine.getEffectiveDate());
					existingRule.setIsApproved(zoySecurityDeadLine.getIsApproved());
				
					if (zoySecurityDeadLine.getIsApproved()) {
						existingRule.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendSecurityDepositDeadlineChangeEmail(zoySecurityDeadLine.getEffectiveDate(),oldFixed,oldVariable,zoySecurityDeadLine.getAutoCancellationDay(),zoySecurityDeadLine.getDeductionPercentage(),oldcondition,zoySecurityDeadLine.getTriggerCondition());
						
						// Audit history for the update
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Security Deposit DeadLine for");
						if (oldFixed != zoySecurityDeadLine.getAutoCancellationDay()) {
							historyContent.append(", Cancellation Days For Refund from " + oldFixed + " to "
									+ zoySecurityDeadLine.getAutoCancellationDay());
						}
						if (oldVariable != zoySecurityDeadLine.getDeductionPercentage()) {
							historyContent.append(" , Deduction Percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoySecurityDeadLine.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != zoySecurityDeadLine.getComments()&& zoySecurityDeadLine.getComments()!="") {
						existingRule.setApprovedBy(currentUser);
						existingRule.setComments(zoySecurityDeadLine.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, zoySecurityDeadLine.getEffectiveDate(), zoySecurityDeadLine.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit history for the update
						StringBuffer historyContent = new StringBuffer(" has "+flag+" the Security Deposit DeadLine for");
						if (oldFixed != zoySecurityDeadLine.getAutoCancellationDay()) {
							historyContent.append(", Cancellation Days For Refund from " + oldFixed + " to "
									+ zoySecurityDeadLine.getAutoCancellationDay());
						}
						if (oldVariable != zoySecurityDeadLine.getDeductionPercentage()) {
							historyContent.append(" , Deduction Percentage from " + oldVariable.stripTrailingZeros().toPlainString() + " to "
									+ zoySecurityDeadLine.getDeductionPercentage());
						}

						auditHistoryUtilities.auditForCommon(currentUser, historyContent.toString(),
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						existingRule.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoySecurityDeadLine.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}
					
					ownerDBImpl.saveSecurityDepositDeadLine(existingRule);

				}
			} else {
				ZoyPgAutoCancellationMaster newRule = new ZoyPgAutoCancellationMaster();
				newRule.setTriggerOn(zoySecurityDeadLine.getTriggerOn());
				newRule.setTriggerCondition(zoySecurityDeadLine.getTriggerCondition());
				newRule.setAutoCancellationDay(zoySecurityDeadLine.getAutoCancellationDay());
				newRule.setDeductionPercentage(zoySecurityDeadLine.getDeductionPercentage());
				newRule.setCond(zoySecurityDeadLine.getTriggerOn() + " " + zoySecurityDeadLine.getTriggerCondition()
						+ " " + zoySecurityDeadLine.getAutoCancellationDay());
				newRule.setTriggerValue(zoySecurityDeadLine.getTriggerValue());
				newRule.setIsApproved(false);
				newRule.setEffectiveDate(zoySecurityDeadLine.getEffectiveDate());
				newRule.setCreatedBy(currentUser);

				ownerDBImpl.saveSecurityDepositDeadLine(newRule);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), zoySecurityDeadLine.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgAutoCancellationMaster> allDetails = ownerDBImpl.findAllSecurityDepositDeadlineSorted();
			List<ZoySecurityDepositDeadLineDto> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());
			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Security Deposit Deadline details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Security Deposit DeadLine Rule API:/zoy_admin/config/security-deposit-deadline.zoyAdminCreateUpadateSecurityDepositDeadline ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoySecurityDepositDeadLineDto convertToDTO(ZoyPgAutoCancellationMaster entity) {
		ZoySecurityDepositDeadLineDto dto = new ZoySecurityDepositDeadLineDto();
		dto.setAutoCancellationId(entity.getAutoCancellationId());
		dto.setTriggerOn(entity.getTriggerOn());
		dto.setTriggerCondition(entity.getTriggerCondition());
		dto.setAutoCancellationDay(entity.getAutoCancellationDay());
		dto.setDeductionPercentage(entity.getDeductionPercentage());
		dto.setCond(entity.getTriggerOn() + " " + entity.getTriggerCondition() + " " + entity.getAutoCancellationDay());
		dto.setTriggerValue(entity.getTriggerValue());
		dto.setEffectiveDate(entity.getEffectiveDate());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> getAllTriggeredCond() {
		ResponseBody response = new ResponseBody();
		try {
			List<TriggeredCond> cond = adminDBImpl.findTriggeredCond();
			if (cond.size() > 0)
				return new ResponseEntity<>(gson.toJson(cond), HttpStatus.OK);
			else
				return new ResponseEntity<>(gson.toJson(""), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error getting trigered cond Rule API:/zoy_admin/config/triggered-cond.getAllTriggeredCond ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllTriggeredOn() {
		ResponseBody response = new ResponseBody();
		try {
			List<TriggeredOn> cond = adminDBImpl.findTriggeredOn();
			if (cond.size() > 0)
				return new ResponseEntity<>(gson.toJson(cond), HttpStatus.OK);
			else
				return new ResponseEntity<>(gson.toJson(""), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error getting trigered on  API:/zoy_admin/config/triggered-on.getAllTriggeredOn ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllTriggeredValue() {
		ResponseBody response = new ResponseBody();
		try {
			List<TriggeredValue> cond = adminDBImpl.findTriggeredValue();
			if (cond.size() > 0)
				return new ResponseEntity<>(gson.toJson(cond), HttpStatus.OK);
			else
				return new ResponseEntity<>(gson.toJson(""), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			log.error("Error getting trigered value API:/zoy_admin/config/triggered-value.getAllTriggeredValue ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

//	@Override
//	public ResponseEntity<String> zoyAdminConfigUpdateShortTerm(ZoyShortTermDto shortTerm) {
//		ResponseBody response = new ResponseBody();
//		try {
//			if (shortTerm == null) {
//				response.setStatus(HttpStatus.BAD_REQUEST.value());
//				response.setError("Required Short Term details");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//			}
//			ZoyPgShortTermMaster zoyPgShortTermMaster = ownerDBImpl.findShortTerm(shortTerm.getShortTermId());
//			if (zoyPgShortTermMaster != null) {
//				final BigDecimal oldFixed = zoyPgShortTermMaster.getPercentage();
//				zoyPgShortTermMaster.setPercentage(shortTerm.getPercentage());
//				ownerDBImpl.createShortTerm(zoyPgShortTermMaster);
//				// audit history here
//				StringBuffer historyContent = new StringBuffer(" has updated the Short Term for Percentage from "
//						+ oldFixed + " to " + shortTerm.getPercentage());
//				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
//						historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
//
//				List<ZoyPgShortTermMaster> shortTermMaster = ownerDBImpl.findAllShortTerm();
//
//				List<ZoyShortTermDto> dto = convertShortToDTO(shortTermMaster);
//				response.setStatus(HttpStatus.OK.value());
//				response.setData(dto);
//				response.setMessage("Updated Short Term");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
//			} else {
//				response.setStatus(HttpStatus.CONFLICT.value());
//				response.setError("Unable to get Short term id " + shortTerm.getShortTermId());
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
//			}
//
//		} catch (Exception e) {
//			log.error(
//					"Error saving/updating Short Term API:/zoy_admin/config/short-term.zoyAdminConfigUpdateShortTerm ",
//					e);
//			response.setStatus(HttpStatus.BAD_REQUEST.value());
//			response.setError("Internal server error");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//		}
//	}
//	
//	
//	
	
	@Transactional
	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateShortTerm(oldNewConfigRequest oldNewConfigReq) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag = "";
		String ruleName = "Short term duration period";

		ZoyShortTermDetails shortTerm = oldNewConfigReq.getNewSTDRule();
		ZoyShortTermDetails previousShortTerm = oldNewConfigReq.getOldSTDRule();

		try {
			if (shortTerm == null || shortTerm.getZoyShortTermDtoInfo() == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Short term duration period is missing");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			List<String> deleteList = new ArrayList<>();
			List<ZoyPgShortTermMaster> shortTermDetailsList = new ArrayList<>();

			for (ZoyShortTermDto termDetails : shortTerm.getZoyShortTermDtoInfo()) {
				if (Boolean.TRUE.equals(termDetails.getDelete())) {
					deleteList.add(termDetails.getShortTermId());
				} else {
					ZoyPgShortTermMaster entity = new ZoyPgShortTermMaster();
					entity.setPercentage(termDetails.getPercentage());
					entity.setStartDay(termDetails.getStartDay());
					entity.setEndDay(termDetails.getEndDay());
					entity.setEffectiveDate(shortTerm.getEffectiveDate());

					if (Boolean.TRUE.equals(shortTerm.getIscreate())) {
						entity.setCreatedBy(currentUser);
						entity.setIsApproved(false);
						flag = "created";
					} else if (Boolean.TRUE.equals(shortTerm.getIsApproved())) {
						entity.setZoyPgShortTermMasterId(termDetails.getShortTermId());
						entity.setCreatedBy(shortTerm.getCreatedBy());
						entity.setIsApproved(true);
						entity.setApprovedBy(currentUser);
						flag = "Approved";
					} else if (shortTerm.getComments() != null && !shortTerm.getComments().isEmpty()) {
						entity.setZoyPgShortTermMasterId(termDetails.getShortTermId());
						entity.setApprovedBy(currentUser);
						entity.setIsApproved(false);
						entity.setComments(shortTerm.getComments());
						flag = "Rejected";
					} else {
						entity.setZoyPgShortTermMasterId(termDetails.getShortTermId());
						entity.setCreatedBy(currentUser);
						entity.setIsApproved(false);
						flag = "Edited";
					}

					shortTermDetailsList.add(entity);
				}
			}

			if ("Created".equals(flag)) {
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(),
						shortTerm.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser, ruleName);
			} else if ("Approved".equals(flag)) {

				zoyEmailService.sendApprovalNotificationEmail(currentUser, ruleName);
				notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
				zoyEmailService.sendEmailNotificationsForShortTerm(previousShortTerm, shortTerm);

				// audit history here
				String historyContent = zoyEmailService.sendAuditNotificationsForShortTerm(previousShortTerm, shortTerm,
						flag);

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
			} else if ("Rejected".equals(flag)) {
				zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, shortTerm.getEffectiveDate(),
						shortTerm.getComments());
				notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser, ruleName);

				// audit history here
				String historyContent = zoyEmailService.sendAuditNotificationsForShortTerm(previousShortTerm, shortTerm,
						flag);

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

			} else if ("Edited".equals(flag)) {
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName,getCurrentTimestampString(), shortTerm.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser, ruleName);
			}

			if (!shortTermDetailsList.isEmpty()) {
				zoyPgShortTermMasterRepository.saveAll(shortTermDetailsList);
			}

			if (!deleteList.isEmpty() && deleteList.size() > 0) {
				String[] todeleteList = deleteList.toArray(new String[0]);
				zoyPgShortTermMasterRepository.deleteShortTermDetailsbyIds(todeleteList);
			}

			response.setStatus(HttpStatus.OK.value());
			response.setMessage(flag + " Short term duration period details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error creating/updating short term config: /zoy_admin/config/short-term.zoyAdminConfigUpdateShortTerm",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateForceCheckOut(ZoyForceCheckOutDto forceCheckOut) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		String flag ="";
		String ruleName ="Force Checkout";
		try {

			if (forceCheckOut == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Force Checkout details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			if (forceCheckOut.getForceCheckOutId() != null && !forceCheckOut.getForceCheckOutId().isEmpty()) {
				Optional<ZoyPgForceCheckOut> forceCheckOutDetails = zoyPgForceCheckOutRepository
						.findById(forceCheckOut.getForceCheckOutId());

				if (forceCheckOutDetails.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required Force CheckOut details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
				} else {
					ZoyPgForceCheckOut oldDetails = forceCheckOutDetails.get();

					int oldFixed = oldDetails.getForceCheckOutDays();
					
					oldDetails.setForceCheckOutDays(forceCheckOut.getForceCheckOutDays());
					oldDetails.setEffectiveDate(forceCheckOut.getEffectiveDate());
					oldDetails.setIsApproved(forceCheckOut.getIsApproved());

					if (forceCheckOut.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendForceCheckoutChangeEmail(forceCheckOut.getEffectiveDate(),oldFixed,forceCheckOut.getForceCheckOutDays());
						
						// Audit the update action
						String historyContent = " has "+flag+" the Force Check Out for, Considering days from " + oldFixed
								+ " to " + forceCheckOut.getForceCheckOutDays();
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != forceCheckOut.getComments()&& forceCheckOut.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(forceCheckOut.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, forceCheckOut.getEffectiveDate(), forceCheckOut.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit the update action
						String historyContent = " has "+flag+" the Force Check Out for, Considering days from " + oldFixed
								+ " to " + forceCheckOut.getForceCheckOutDays();
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						oldDetails.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), forceCheckOut.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}
					
					zoyPgForceCheckOutRepository.save(oldDetails);

				}
			} else {
				ZoyPgForceCheckOut newForceCheckOut = new ZoyPgForceCheckOut();
				newForceCheckOut.setForceCheckOutDays(forceCheckOut.getForceCheckOutDays());
				newForceCheckOut.setEffectiveDate(forceCheckOut.getEffectiveDate());
				newForceCheckOut.setCreatedBy(currentUser);
				newForceCheckOut.setIsApproved(false);

				zoyPgForceCheckOutRepository.save(newForceCheckOut);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), forceCheckOut.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgForceCheckOut> allDetails = ownerDBImpl.findAllForceCheckOutDetailsSorted();
			List<ZoyForceCheckOutDto> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" Force CheckOut details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Force Check Out details API:/zoy_admin/config/force-checkout.zoyAdminConfigUpdateForceCheckOut ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("An internal error occurred while processing the request");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoyForceCheckOutDto convertToDTO(ZoyPgForceCheckOut entity) {
		ZoyForceCheckOutDto dto = new ZoyForceCheckOutDto();
		dto.setForceCheckOutId(entity.getForceCheckOutId());
		dto.setForceCheckOutDays(entity.getForceCheckOutDays());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateNoRentalAgreement(ZoyPgNoRentalAgreementDto NoRentalAgreement) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();

		String flag ="";
		String ruleName ="No Rental Agreement";	
		if (NoRentalAgreement == null) {
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Required No Rental Agreement details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
		try {
			if (NoRentalAgreement.getNoRentalAgreementId() != null
					&& !NoRentalAgreement.getNoRentalAgreementId().isEmpty()) {

				Optional<ZoyPgNoRentalAgreement> forceCheckOutDetails = zoyPgNoRentalAgreementRespository
						.findById(NoRentalAgreement.getNoRentalAgreementId());
				if (forceCheckOutDetails.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required No Rental Agreement details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
					
				} else {
					ZoyPgNoRentalAgreement oldDetails = forceCheckOutDetails.get();

					int oldFixed = oldDetails.getNoRentalAgreementDays();

					oldDetails.setNoRentalAgreementDays(NoRentalAgreement.getNoRentalAgreementDays());
					oldDetails.setEffectiveDate(NoRentalAgreement.getEffectiveDate());
					oldDetails.setIsApproved(NoRentalAgreement.getIsApproved());
					
					if (NoRentalAgreement.getIsApproved()) {
						oldDetails.setApprovedBy(currentUser);
						flag="Approved";
						zoyEmailService.sendApprovalNotificationEmail(currentUser,ruleName);
						notificationsAndAlertsService.masterConfigurationRuleApproval(currentUser, ruleName);
						zoyEmailService.sendNoRentalAgreementChangeEmail(NoRentalAgreement.getEffectiveDate(),oldFixed,NoRentalAgreement.getNoRentalAgreementDays());
						
						// Audit the update action
						String historyContent = " has "+flag+" the No Rental Agreement for, Considering days from "
								+ oldFixed + " to " + NoRentalAgreement.getNoRentalAgreementDays();
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
						
					} else if ( null != NoRentalAgreement.getComments()&& NoRentalAgreement.getComments()!="") {
						oldDetails.setApprovedBy(currentUser);
						oldDetails.setComments(NoRentalAgreement.getComments());
						flag="Rejected";
						zoyEmailService.sendApprovalRejectionEmail(ruleName, currentUser, NoRentalAgreement.getEffectiveDate(), NoRentalAgreement.getComments());
						notificationsAndAlertsService.masterConfigurationRuleRejection(currentUser,ruleName);
						
						// Audit the update action
						String historyContent = " has "+flag+" the No Rental Agreement for, Considering days from "
								+ oldFixed + " to " + NoRentalAgreement.getNoRentalAgreementDays();
						auditHistoryUtilities.auditForCommon(currentUser, historyContent,
								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					}else{
						oldDetails.setCreatedBy(currentUser);
						flag="Edited";
						zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), NoRentalAgreement.getEffectiveDate());
						notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);
					}

					zoyPgNoRentalAgreementRespository.save(oldDetails);

				}
			} else {
				ZoyPgNoRentalAgreement newForceCheckOut = new ZoyPgNoRentalAgreement();
				newForceCheckOut.setNoRentalAgreementDays(NoRentalAgreement.getNoRentalAgreementDays());
				newForceCheckOut.setEffectiveDate(NoRentalAgreement.getEffectiveDate());
				newForceCheckOut.setCreatedBy(currentUser);
				newForceCheckOut.setIsApproved(false);
				zoyPgNoRentalAgreementRespository.save(newForceCheckOut);
				flag="created";
				zoyEmailService.sendApprovalRequestRaisedEmail(currentUser, ruleName, getCurrentTimestampString(), NoRentalAgreement.getEffectiveDate());
				notificationsAndAlertsService.masterConfigurationRulechange(currentUser,ruleName);

			}

			List<ZoyPgNoRentalAgreement> allDetails = ownerDBImpl.findAllNoRentalAgreementDetailsSorted();
			List<ZoyPgNoRentalAgreementDto> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage(flag+" No Rental Agreement details successfully");

			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating No Rental Agreement details API:/zoy_admin/config/force-checkout.zoyAdminConfigUpdateForceCheckOut ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("An internal error occurred while processing the request");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	private ZoyPgNoRentalAgreementDto convertToDTO(ZoyPgNoRentalAgreement entity) {
		ZoyPgNoRentalAgreementDto dto = new ZoyPgNoRentalAgreementDto();
		dto.setNoRentalAgreementId(entity.getNoRentalAgreementId());
		dto.setNoRentalAgreementDays(entity.getNoRentalAgreementDays());
		dto.setIsApproved(entity.getIsApproved() != null ? entity.getIsApproved() : false);
		dto.setEffectiveDate(entity.getEffectiveDate() != null ? entity.getEffectiveDate() : "");
		dto.setApprovedBy(entity.getApprovedBy() != null ? entity.getApprovedBy() : "");
		dto.setCreatedBy(entity.getCreatedBy() != null ? entity.getCreatedBy() : "");
		dto.setComments(entity.getComments()!=null ? entity.getComments():"");
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminCompanyProfileMaster(ZoyCompanyProfileMasterModal companyProfile) {
		ResponseBody response = new ResponseBody();
		try {
			if (companyProfile == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Company Profile details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyCompanyProfileMaster zoyCompanyProfileMaster = null;
			if (companyProfile.getCompanyProfileId() != null) {
				zoyCompanyProfileMaster = ownerDBImpl.findCompanyProfile(companyProfile.getCompanyProfileId());
			}
			if (zoyCompanyProfileMaster != null) {
				// Update existing company profile
				zoyCompanyProfileMaster.setType(companyProfile.getType());
				zoyCompanyProfileMaster.setPinCode(companyProfile.getPinCode());
				zoyCompanyProfileMaster.setState(companyProfile.getState());
				zoyCompanyProfileMaster.setCity(companyProfile.getCity());
				zoyCompanyProfileMaster.setAddressLineOne(companyProfile.getAddressLineOne());
				zoyCompanyProfileMaster.setAddressLineTwo(companyProfile.getAddressLineTwo());
				zoyCompanyProfileMaster.setAddressLineThree(companyProfile.getAddressLineThree());
				zoyCompanyProfileMaster.setContactNumberOne(companyProfile.getContactNumberOne());
				zoyCompanyProfileMaster.setContactNumbertwo(companyProfile.getContactNumberTwo());
				zoyCompanyProfileMaster.setEmailIdOne(companyProfile.getEmailOne());
				zoyCompanyProfileMaster.setEmailIdTwo(companyProfile.getEmailTwo());
				zoyCompanyProfileMaster.setStatus(companyProfile.getStatus());
				ownerDBImpl.createOrUpdateCompanyProfile(zoyCompanyProfileMaster);

				// Audit history here
				StringBuffer historyContent = new StringBuffer(
						" has updated the Company Profile for " + companyProfile.getAddressLineOne() + " this address");
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				List<ZoyCompanyProfileMaster> companyProfiles = ownerDBImpl.findAllCompanyProfiles();
				List<ZoyCompanyProfileMasterDto> dto = convertsToDTO(companyProfiles);

				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Company Profile");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				// Create new company profile
				ZoyCompanyProfileMaster newZoyCompanyProfile = new ZoyCompanyProfileMaster();
				newZoyCompanyProfile.setType(companyProfile.getType());
				newZoyCompanyProfile.setPinCode(companyProfile.getPinCode());
				newZoyCompanyProfile.setState(companyProfile.getState());
				newZoyCompanyProfile.setCity(companyProfile.getCity());
				newZoyCompanyProfile.setAddressLineOne(companyProfile.getAddressLineOne());
				newZoyCompanyProfile.setAddressLineTwo(companyProfile.getAddressLineTwo());
				newZoyCompanyProfile.setAddressLineThree(companyProfile.getAddressLineThree());
				newZoyCompanyProfile.setContactNumberOne(companyProfile.getContactNumberOne());
				newZoyCompanyProfile.setContactNumbertwo(companyProfile.getContactNumberTwo());
				newZoyCompanyProfile.setEmailIdOne(companyProfile.getEmailOne());
				newZoyCompanyProfile.setEmailIdTwo(companyProfile.getEmailTwo());
				newZoyCompanyProfile.setStatus(companyProfile.getStatus());

				ownerDBImpl.createOrUpdateCompanyProfile(newZoyCompanyProfile);

				// Audit history here
				StringBuffer historyContent = new StringBuffer(" has created a new " + companyProfile.getType());
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				List<ZoyCompanyProfileMaster> companyProfiles = ownerDBImpl.findAllCompanyProfiles();
				List<ZoyCompanyProfileMasterDto> dto = convertsToDTO(companyProfiles);

				response.setStatus(HttpStatus.CREATED.value());
				response.setData(dto);
				response.setMessage("Created Company Profile");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
			}

		} catch (Exception e) {
			log.error(
					"Error saving/updating Company Profile API:/zoy_admin/config/company-profile.zoyAdminConfigUpdateCompany",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	public static List<ZoyCompanyProfileMasterDto> convertsToDTO(List<ZoyCompanyProfileMaster> companyProfiles) {
		List<ZoyCompanyProfileMasterDto> dtoList = new ArrayList<>();
		for (ZoyCompanyProfileMaster profile : companyProfiles) {
			ZoyCompanyProfileMasterDto dto = new ZoyCompanyProfileMasterDto();
			dto.setCompanyProfileId(profile.getCompanyProfileId());
			dto.setType(profile.getType());
			dto.setPinCode(profile.getPinCode());
			dto.setState(profile.getState());
			dto.setCity(profile.getCity());
			dto.setAddressLineOne(profile.getAddressLineOne());
			dto.setAddressLineTwo(profile.getAddressLineTwo());
			dto.setAddressLineThree(profile.getAddressLineThree());
			dto.setContactNumberOne(profile.getContactNumberOne());
			dto.setContactNumberTwo(profile.getContactNumbertwo());
			dto.setEmailOne(profile.getEmailIdOne());
			dto.setEmailTwo(profile.getEmailIdTwo());
			dto.setStatus(profile.getStatus());
			dtoList.add(dto);
		}
		return dtoList;
	}

	@Override
	public ResponseEntity<String> zoyAdminCompanyProfiles() {
		ResponseBody response = new ResponseBody();
		try {
			List<ZoyCompanyProfileMaster> companyProfiles = ownerDBImpl.findAllCompanyProfiles();
			List<ZoyCompanyProfileMasterDto> dto = convertsToDTO(companyProfiles);
			response.setStatus(HttpStatus.CREATED.value());
			response.setData(dto);
			response.setMessage("Fetched all  Company Profile details successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(
					"Error while Fetching all  Company Profile details API:/zoy_admin/config/fetch-company-profiles.zoyAdminCompanyProfiles",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

//	@Override
//	public ResponseEntity<String> zoyAdminConfigShortTermRentingDuration(ZoyRentingDuration rentingDuration) {
//		ResponseBody response = new ResponseBody();
//		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
//
//		try {
//			if (rentingDuration == null) {
//				response.setStatus(HttpStatus.BAD_REQUEST.value());
//				response.setError("Required Short Term Renting Duration Details");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//			}
//
//			try {
//				if (rentingDuration.getRentingDurationId() != null
//						&& !rentingDuration.getRentingDurationId().isEmpty()) {
//
//					Optional<ZoyPgShortTermRentingDuration> shortTermRentingDurationDetails = zoyPgRentingDurationRepository
//							.findById(rentingDuration.getRentingDurationId());
//					if (shortTermRentingDurationDetails.isEmpty()) {
//						response.setStatus(HttpStatus.BAD_REQUEST.value());
//						response.setError("Required short Term Renting Duration Details not found");
//						return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
//					} else {
//						ZoyPgShortTermRentingDuration oldDetails = shortTermRentingDurationDetails.get();
//
//						int oldFixed = oldDetails.getRentingDurationDays();
//
//						oldDetails.setEffectiveDate(rentingDuration.getEffectiveDate());
//						oldDetails.setIsApproved(rentingDuration.getIsApproved());
//
//						if (rentingDuration.getIsApproved()) {
//							oldDetails.setApprovedBy(currentUser);
//						} else {
//							oldDetails.setCreatedBy(currentUser);
//						}
//
//						zoyPgRentingDurationRepository.save(oldDetails);
//
//						// Audit history
//						String historyContent = " has updated the Renting Duration for, Considering days from "
//								+ oldFixed + " to " + rentingDuration.getRentingDurationDays();
//						auditHistoryUtilities.auditForCommon(
//								SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,
//								ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
//					}
//				} else {
//					ZoyPgShortTermRentingDuration newShortTermRentingDuration = new ZoyPgShortTermRentingDuration();
//					newShortTermRentingDuration.setRentingDurationDays(rentingDuration.getRentingDurationDays());
//					newShortTermRentingDuration.setEffectiveDate(rentingDuration.getEffectiveDate()); // Removed
//																										// unnecessary
//																										// semicolon
//					newShortTermRentingDuration.setCreatedBy(currentUser);
//					newShortTermRentingDuration.setIsApproved(false);
//
//					zoyPgRentingDurationRepository.save(newShortTermRentingDuration);
//
//					// Audit history
//					String historyContent = " has created the Short Term Renting Duration for, Considering days = "
//							+ rentingDuration.getRentingDurationDays();
//					auditHistoryUtilities.auditForCommon(
//							SecurityContextHolder.getContext().getAuthentication().getName(), historyContent,
//							ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
//				}
//
//				List<ZoyPgShortTermRentingDuration> allDetails = ownerDBImpl
//						.findAllShortTermRentingDurationDetailsSorted();
//				List<ZoyRentingDuration> dto = allDetails.stream().map(this::convertToDTO).collect(Collectors.toList());
//
//				response.setStatus(HttpStatus.OK.value());
//				response.setData(dto);
//				response.setMessage("Short Term Renting Duration details successfully saved/updated");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
//
//			} catch (Exception e) {
//				log.error(
//						"Error saving/updating Short Term Renting Duration API:/zoy_admin/config/renting-duration.zoyAdminConfigShortTermRentingDuration ",
//						e);
//				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//				response.setError("Internal server error");
//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
//			}
//		} catch (Exception e) {
//			log.error("Unexpected error in zoyAdminConfigShortTermRentingDuration", e);
//			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
//			response.setError("Unexpected error occurred");
//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}

	private ZoyRentingDuration convertToDTO(ZoyPgShortTermRentingDuration force) {
		ZoyRentingDuration rentingDuration = new ZoyRentingDuration();
		rentingDuration.setRentingDurationId(force.getRentingDurationId());
		rentingDuration.setRentingDurationDays(force.getRentingDurationDays());
		rentingDuration.setIsApproved(force.getIsApproved() != null ? force.getIsApproved() : false);
		rentingDuration.setEffectiveDate(force.getEffectiveDate() != null ? force.getEffectiveDate() : "");
		rentingDuration.setApprovedBy(force.getApprovedBy() != null ? force.getApprovedBy() : "");
		rentingDuration.setCreatedBy(force.getCreatedBy() != null ? force.getCreatedBy() : "");
		return rentingDuration;
	}

	@Override
	public ResponseEntity<String> zoyAdminRemoveCompanyProfile(String profileId) {
		ResponseBody response = new ResponseBody();
		try {
			ownerDBImpl.deleteCompanyProfile(profileId);
			// audit history here
			String historyContent = " has Deleted the Company Profile Details ";
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
					historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Deleted  Company Profile details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(
					"Error while Deleting   Company Profile details API:/zoy_admin/config/remove-company-profiles.zoyAdminRemoveCompanyProfile",
					e);
			try {
				new ZoyAdminApplicationException(e, "");
			} catch (Exception ex) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError(ex.getMessage());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@Override
	public ResponseEntity<String> zoyAdminCompanyProfileMaster(String data, MultipartFile companyLogo) {
		ResponseBody response = new ResponseBody();

		try {
			ZoyCompanyMasterModal companyMaster = gson2.fromJson(data, ZoyCompanyMasterModal.class);
			if (companyMaster == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyCompanyMaster company = ownerDBImpl.findcompanyMaster();
			if (company != null) {
				byte[] profilePictureBase64 = pdfGenerateService.imageToBytes(companyLogo.getInputStream());
				company.setCompanyLogo(profilePictureBase64);
				company.setCompanyName(companyMaster.getCompanyName());
				company.setGstNumber(companyMaster.getGstNumber());
				company.setPanNumber(companyMaster.getPanNumber());
				company.setUrl(companyMaster.getUrl());
				ZoyCompanyMaster companyDetails = ownerDBImpl.saveCompanyMaster(company);

				String historyContent = " has Updated Master Company Profile Details ";
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyCompanyMasterDto dto = convertToDTO(companyDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyCompanyMaster newCompanyMaster = new ZoyCompanyMaster();
				newCompanyMaster.setCompanyName(companyMaster.getCompanyName());
				byte[] profilePictureBase64 = pdfGenerateService.imageToBytes(companyLogo.getInputStream());
				newCompanyMaster.setCompanyLogo(profilePictureBase64);
				newCompanyMaster.setGstNumber(companyMaster.getGstNumber());
				newCompanyMaster.setPanNumber(companyMaster.getPanNumber());
				newCompanyMaster.setUrl(companyMaster.getUrl());
				ZoyCompanyMaster companyDetails = ownerDBImpl.saveCompanyMaster(newCompanyMaster);

				String historyContent = " has Updated Master Company Profile Details ";
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(),
						historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyCompanyMasterDto dto = convertToDTO(companyDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error(
					"Error saving/updating Master company details API:/zoy_admin/config/company-master-profile.zoyAdminCompanyProfileMaster",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	public static ZoyCompanyMasterDto convertToDTO(ZoyCompanyMaster company) {
		ZoyCompanyMasterDto dto = new ZoyCompanyMasterDto();
		dto.setCompanyMasterId(company.getCompanyMasterId());
		dto.setCompanyName(company.getCompanyName());
		dto.setGstNumber(company.getGstNumber());
		dto.setPanNumber(company.getPanNumber());
		dto.setUrl(company.getUrl());
		dto.setLogo(company.getCompanyLogo());
		return dto;
	}

	@Override
	public ResponseEntity<String> fetchCompanyProfileMaster() {
		ResponseBody response = new ResponseBody();
		try {
			ZoyCompanyMaster company = ownerDBImpl.findcompanyMaster();
			ZoyCompanyMasterDto dto = null;

			if (company != null) {
				dto = convertToDTO(company);
			}

			if (dto == null) {
				response.setData(new ZoyCompanyMasterDto());
				response.setMessage("Company profile details not found");
				response.setStatus(HttpStatus.NOT_FOUND.value());
			} else {
				response.setData(dto);
				response.setMessage("Fetched master Company Profile details successfully");
				response.setStatus(HttpStatus.CREATED.value());
			}

			return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
		} catch (Exception e) {
			log.error(
					"Error while Fetching all  Company Profile details API:/zoy_admin/config/fetch-master-profile.fetchCompanyProfileMaster",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllConfigurationDetails() {
		ResponseBody response = new ResponseBody();
		try {
			List<ZoyPgTokenDetails> tokenDetails = ownerDBImpl.findAllTokenDetailsSorted();
			List<ZoyPgSecurityDepositDetails> depositDetails = ownerDBImpl.findAllSortedByEffectiveDate();
//			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
			List<ZoyPgEarlyCheckOut> earlyCheckOutDetails = ownerDBImpl.findAllEarlyCheckOutRulesSorted();
			List<ZoyPgAutoCancellationAfterCheckIn> cancellationAfterCheckIn = ownerDBImpl
					.findAllAfterCheckInDatesSorted();
			List<ZoyPgAutoCancellationMaster> securityDepositDeadLine = ownerDBImpl
					.findAllSecurityDepositDeadlineSorted();
			List<ZoyDataGrouping> dataGrouping = ownerDBImpl.findAllDataGroupingSorted();
			List<ZoyPgOtherCharges> otherCharges = ownerDBImpl.findAllOtherChargesDetailsSorted();
			List<ZoyPgGstCharges> gstCharges = ownerDBImpl.findAllGstChargesDetailsSorted();
//			List<ZoyPgShortTermMaster> shortTermMaster = ownerDBImpl.findAllShortTerm();
			List<ZoyPgForceCheckOut> forceCheckOut = ownerDBImpl.findAllForceCheckOutDetailsSorted();
			List<ZoyPgNoRentalAgreement> noRentalAgreement = ownerDBImpl.findAllNoRentalAgreementDetailsSorted();
//			List<ZoyPgShortTermRentingDuration> rentingDuration = ownerDBImpl
//					.findAllShortTermRentingDurationDetailsSorted();
			ZoyAdminConfigDTO configDTO = new ZoyAdminConfigDTO();
			if (tokenDetails != null) {
				List<ZoyPgTokenDetailsDTO> listToken = new ArrayList<>();
				for (ZoyPgTokenDetails tokenDetail : tokenDetails) {
					listToken.add(convertToDTO(tokenDetail));
				}
				configDTO.setTokenDetails(listToken);
			}
			if (depositDetails != null) {
				List<ZoyPgSecurityDepositDetailsDTO> listDepositDetails = new ArrayList<>();
				for (ZoyPgSecurityDepositDetails depositDetail : depositDetails) {
					listDepositDetails.add(convertToDTO(depositDetail));
				}
				configDTO.setDepositDetails(listDepositDetails);
			}
			if (depositDetails != null) {
				List<ZoyPgSecurityDepositDetailsDTO> listDepositDetails = new ArrayList<>();
				for (ZoyPgSecurityDepositDetails depositDetail : depositDetails) {
					listDepositDetails.add(convertToDTO(depositDetail));
				}
				configDTO.setDepositDetails(listDepositDetails);
			}
//			if (cancellationDetails != null)
//				configDTO.setCancellationBeforeCheckInDetails(convertToDTO(cancellationDetails));

			if (earlyCheckOutDetails != null) {
				List<ZoyPgEarlyCheckOutRuleDto> listZoyearlyCheckOutDetails = new ArrayList<>();
				for (ZoyPgEarlyCheckOut detail : earlyCheckOutDetails) {
					listZoyearlyCheckOutDetails.add(convertToDTO(detail));
				}
				configDTO.setEarlyCheckOutRuleDetails(listZoyearlyCheckOutDetails);
			}

			if (cancellationAfterCheckIn != null) {
				List<ZoyAfterCheckInCancellationDto> listZoycancellationAfterCheckIn = new ArrayList<>();
				for (ZoyPgAutoCancellationAfterCheckIn detail : cancellationAfterCheckIn) {
					listZoycancellationAfterCheckIn.add(convertToDTO(detail));
				}
				configDTO.setCancellationAfterCheckInDetails(listZoycancellationAfterCheckIn);
			}
			if (securityDepositDeadLine != null) {
				List<ZoySecurityDepositDeadLineDto> listZoysecurityDepositDeadLine = new ArrayList<>();
				for (ZoyPgAutoCancellationMaster detail : securityDepositDeadLine) {
					listZoysecurityDepositDeadLine.add(convertToDTO(detail));
				}
				configDTO.setSecurityDepositDeadLineDetails(listZoysecurityDepositDeadLine);
			}

			if (dataGrouping != null) {
				List<ZoyDataGroupingDto> listDataGrouping = new ArrayList<>();
				for (ZoyDataGrouping grouping : dataGrouping) {
					listDataGrouping.add(convertToDTO(grouping));
				}
				configDTO.setDataGrouping(listDataGrouping);
			}
			if (otherCharges != null) {
				List<ZoyOtherChargesDto> listZoyOtherChargesDto = new ArrayList<>();
				for (ZoyPgOtherCharges detail : otherCharges) {
					listZoyOtherChargesDto.add(convertToDTO(detail));
				}
				configDTO.setOtherCharges(listZoyOtherChargesDto);
			}

			if (gstCharges != null) {
				List<ZoyGstChargesDto> listZoyGstChargesDto = new ArrayList<>();
				for (ZoyPgGstCharges detail : gstCharges) {
					listZoyGstChargesDto.add(convertToDTO(detail));
				}
				configDTO.setGstCharges(listZoyGstChargesDto);
			}

//			if (shortTermMaster != null) {
//				configDTO.setZoyShortTermDtos(convertShortToDTO(shortTermMaster));
//
//			}
			if (forceCheckOut != null) {
				List<ZoyForceCheckOutDto> listZoyForceCheckOutDto = new ArrayList<>();
				for (ZoyPgForceCheckOut checkOut : forceCheckOut) {
					listZoyForceCheckOutDto.add(convertToDTO(checkOut));
				}
				configDTO.setZoyForceCheckOutDto(listZoyForceCheckOutDto);
			}
			if (noRentalAgreement != null) {
				List<ZoyPgNoRentalAgreementDto> listAgreement = new ArrayList<>();
				for (ZoyPgNoRentalAgreement agreementDetail : noRentalAgreement) {
					listAgreement.add(convertToDTO(agreementDetail));
				}
				configDTO.setNoRentalAgreement(listAgreement);
			}
//			if (rentingDuration != null) {
//				List<ZoyRentingDuration> listZoyRentingDuration = new ArrayList<>();
//				for (ZoyPgShortTermRentingDuration detail : rentingDuration) {
//					listZoyRentingDuration.add(convertToDTO(detail));
//				}
//				configDTO.setShortTermRentingDuration(listZoyRentingDuration);
//			}

			response.setStatus(HttpStatus.OK.value());
			response.setData(configDTO);
			response.setMessage("Successfully fetched all config details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error(
					"Error fetching config details API:/zoy_admin/config/admin-configuration-details.getAllConfigurationDetails ",
					e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	@Override
	public ResponseEntity<String> zoyAdminConfigShortTermDetails() {
	    ResponseBody response = new ResponseBody();
	    List<ZoyShortTermDetails> ZoyShortTermDetailsList = new ArrayList<>();

	    try {
	        List<ZoyPgShortTermMaster> ShortTermList = zoyPgShortTermMasterRepository.findAllShortTermDetails();

	        if (ShortTermList == null || ShortTermList.isEmpty()) {
	            response.setStatus(HttpStatus.OK.value());
	            response.setData(ZoyShortTermDetailsList);
	            response.setError("No Short Term details found.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	        }

	        String effectiveDate = null;
	        String comments ="";
	        ZoyShortTermDetails termDetails = null;
	        List<ZoyShortTermDto> zoyShortTermDtoDetailsList = new ArrayList<>();

	        for (ZoyPgShortTermMaster details : ShortTermList) {
	            if (!(Objects.toString(effectiveDate, "") + Objects.toString(comments, ""))
	                    .equals(Objects.toString(details.getEffectiveDate(), "") + Objects.toString(details.getComments(), ""))) {
	                if (termDetails != null) {
	                    termDetails.setZoyShortTermDtoInfo(zoyShortTermDtoDetailsList);
	                    ZoyShortTermDetailsList.add(termDetails);
	                }
	                termDetails = new ZoyShortTermDetails();
	                termDetails.setApproved(details.getIsApproved() != null ? details.getIsApproved() : false);
	                termDetails.setApprovedBy(details.getApprovedBy());
	                termDetails.setCreatedBy(details.getCreatedBy());
	                termDetails.setEffectiveDate(details.getEffectiveDate());
	                termDetails.setComments(details.getComments());
	                zoyShortTermDtoDetailsList = new ArrayList<>();
	                effectiveDate = details.getEffectiveDate();
	                comments = details.getComments();
	            }

	            ZoyShortTermDto zoyShortTermDtoDetails = new ZoyShortTermDto();
	            zoyShortTermDtoDetails.setEndDay(details.getEndDay());
	            zoyShortTermDtoDetails.setPercentage(details.getPercentage());
	            zoyShortTermDtoDetails.setShortTermId(details.getZoyPgShortTermMasterId());
	            zoyShortTermDtoDetails.setStartDay(details.getStartDay());

	            zoyShortTermDtoDetailsList.add(zoyShortTermDtoDetails);
	            
	            if (!(Objects.toString(effectiveDate, "") + Objects.toString(comments, ""))
		                .equals(Objects.toString(details.getEffectiveDate(), "") + Objects.toString(details.getComments(), ""))) {
		        if (termDetails != null) {
		            termDetails.setZoyShortTermDtoInfo(zoyShortTermDtoDetailsList);
		            ZoyShortTermDetailsList.add(termDetails);
		        }
		        }
	        }
	       

	        response.setStatus(HttpStatus.OK.value());
	        response.setData(ZoyShortTermDetailsList);
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error("Error in API :/zoy_admin/config/fetchzoyAdminConfigShortTermDetails.zoyAdminConfigShortTermDetails", e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

	
	@Override
	public ResponseEntity<String> FetchShortTermRentingDuration() {
		ResponseBody response = new ResponseBody();
		try {
			ZoyPgShortTermRentingDuration rentingDuration = ownerDBImpl.findZoyRentingDuration();
			ZoyRentingDuration dto = null;

			if (rentingDuration != null) {
				dto = convertToDTO(rentingDuration);
			}

			if (dto == null) {
				response.setData(new ZoyRentingDuration()); 
				response.setMessage("short term renting duration details not found");
				response.setStatus(HttpStatus.NOT_FOUND.value());
			} else {
				response.setData(dto);
				response.setMessage("Fetched admin short term renting duration details successfully");
				response.setStatus(HttpStatus.CREATED.value());
			}
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
		}catch(Exception e) {
			log.error("Error while Fetching all  Company Profile details API:/zoy_admin/config/fetch-renting-duration.FetchShortTermRentingDuration", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
		
	}

	public String getCurrentTimestampString() {
		TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
		Calendar calendar = Calendar.getInstance(timeZone);
		Timestamp currentTimestamp = new Timestamp(calendar.getTimeInMillis());

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(timeZone);

		return sdf.format(currentTimestamp);
	}

}