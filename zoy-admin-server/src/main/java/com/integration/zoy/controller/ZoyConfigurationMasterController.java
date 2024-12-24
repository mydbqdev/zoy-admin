package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.constants.ZoyConstant;
import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgAutoCancellationPeriod;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgSecurityDepositRefundRule;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.entity.ZoyShareMaster;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyPgAutoCancellationPeriodDto;
import com.integration.zoy.model.ZoyShareDetails;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.ZoyAdminConfigDTO;
import com.integration.zoy.utils.ZoyDataGroupingDto;
import com.integration.zoy.utils.ZoyOtherChargesDto;
import com.integration.zoy.utils.ZoyPgSecurityDepositDetailsDTO;
import com.integration.zoy.utils.ZoyPgSecurityDepositRefundRuleDto;
import com.integration.zoy.utils.ZoyPgTokenDetailsDTO;


@RestController
@RequestMapping("")
public class ZoyConfigurationMasterController implements ZoyConfigurationMasterImpl{

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
	AuditHistoryUtilities auditHistoryUtilities;


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateToken(ZoyPgTokenDetailsDTO details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgTokenDetails tokenDetails = ownerDBImpl.findTokenDetails();
			if (tokenDetails != null) {
				final BigDecimal oldFixed=tokenDetails.getFixedToken();
				final BigDecimal oldVariable=tokenDetails.getVariableToken();
				// Update the existing token record
				tokenDetails.setFixedToken(details.getFixedToken() != null ? details.getFixedToken() : BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken() != null ? details.getVariableToken() : BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);
				
				//audit history here
				 StringBuffer historyContent=new StringBuffer(" has updated the Token for");
				if(oldFixed!=tokenDetails.getFixedToken()) {
					historyContent.append(", Fixed from "+oldFixed+" to "+tokenDetails.getFixedToken());
				}
				if(oldVariable!=tokenDetails.getVariableToken()) {
					historyContent.append(" , Variable from "+oldVariable+" to "+tokenDetails.getVariableToken());
				}
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyPgTokenDetailsDTO dto = convertToDTO(tokenDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				// Create a new token record
				ZoyPgTokenDetails newTokenDetails = new ZoyPgTokenDetails();
				newTokenDetails.setFixedToken(details.getFixedToken() != null ? details.getFixedToken() : BigDecimal.ZERO);
				newTokenDetails.setVariableToken(details.getVariableToken() != null ? details.getVariableToken() : BigDecimal.ZERO);
				ownerDBImpl.saveToken(newTokenDetails);
				//audit history here
				String historyContent=" has created the Token for, Fixed = "+newTokenDetails.getFixedToken()+" , Variable ="+newTokenDetails.getVariableToken();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyPgTokenDetailsDTO dto = convertToDTO(newTokenDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating token details:API:/zoy_admin/config/token_advance.zoyAdminConfigCreateUpdateToken ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private ZoyPgTokenDetailsDTO convertToDTO(ZoyPgTokenDetails entity) {
	    ZoyPgTokenDetailsDTO dto = new ZoyPgTokenDetailsDTO();
	    dto.setTokenId(entity.getTokenId());
	    dto.setFixedToken(entity.getFixedToken());
	    dto.setVariableToken(entity.getVariableToken());
	    return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(ZoyBeforeCheckInCancellation details) {
	    ResponseBody response = new ResponseBody();
	    try {
	        if (details == null) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Required cancellation details");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        if (details.getCancellationId() != null && !details.getCancellationId().isEmpty()) {
	            ZoyPgCancellationDetails cancelDetails = ownerDBImpl.findBeforeCancellationDetails(details.getCancellationId());
	            if (cancelDetails == null) {
	                response.setStatus(HttpStatus.CONFLICT.value());
	                response.setError("Unable to get before check-in cancellation details");
	                return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
	            }
	            final int oldFixed=cancelDetails.getDaysBeforeCheckIn();
	            final BigDecimal oldVariable=cancelDetails.getDeductionPercentages();
	            cancelDetails.setDaysBeforeCheckIn(details.getDaysBeforeCheckIn());
	            cancelDetails.setDeductionPercentages(details.getDeductionPercentages());
	            ownerDBImpl.saveBeforeCancellation(cancelDetails);
	            //audit history here
	            StringBuffer historyContent=new StringBuffer(" has updated the Cancellation And Refund Policy for");
				if(oldFixed!=details.getDaysBeforeCheckIn()) {
					historyContent.append(", Days before check in from "+oldFixed+" to "+details.getDaysBeforeCheckIn());
				}
				if(oldVariable!=details.getDeductionPercentages()) {
					historyContent.append(" , Deduction percentage from "+oldVariable+" to "+details.getDeductionPercentages());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
	        } else {
	            ZoyPgCancellationDetails newCancelDetails = new ZoyPgCancellationDetails();
	            newCancelDetails.setDaysBeforeCheckIn(details.getDaysBeforeCheckIn());
	            newCancelDetails.setDeductionPercentages(details.getDeductionPercentages());
	            ownerDBImpl.saveBeforeCancellation(newCancelDetails);
	            
	          //audit history here
				String historyContent=" has created the Cancellation And Refund Policy for, Days before check in = "+details.getDaysBeforeCheckIn()+" , Deduction percentage ="+details.getDeductionPercentages();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
	        }

	        List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
	        List<ZoyBeforeCheckInCancellation> dtoList = cancellationDetails.stream()
	                .map(this::convertToDTO)
	                .collect(Collectors.toList());

	        response.setStatus(HttpStatus.OK.value());
	        response.setData(dtoList);
	        response.setMessage("Retrieved all Before CheckIn details");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error("Error uploading property details API:/zoy_admin/config/security-deposit-limits.zoyAdminCreateUpdateConfigSecurityDepositLimits", e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError("Internal server error");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}

	
	private ZoyBeforeCheckInCancellation convertToDTO(ZoyPgCancellationDetails entity) {
		ZoyBeforeCheckInCancellation dto = new ZoyBeforeCheckInCancellation();
	    dto.setCancellationId(entity.getCancellationId());
	    dto.setDaysBeforeCheckIn(entity.getDaysBeforeCheckIn());
	    dto.setDeductionPercentages(entity.getDeductionPercentages());
	    return dto;
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateZoyShare(ZoyShareDetails details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if(details.getZoyShareId()!=null && !details.getZoyShareId().isEmpty()) {
				ZoyShareMaster shareDetails=ownerDBImpl.findZoyShareDetails(details.getZoyShareId());
				if(shareDetails==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get zoy share details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				shareDetails.setPropertyShare(new BigDecimal(details.getPropertyShare()));
				shareDetails.setZoyShare(new BigDecimal(details.getZoyShare()));
				ownerDBImpl.saveZoyShare(shareDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Updated Zoy Share details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyShareMaster shareDetails=new ZoyShareMaster();
				shareDetails.setPropertyShare(new BigDecimal(details.getPropertyShare()));
				shareDetails.setZoyShare(new BigDecimal(details.getZoyShare()));
				ownerDBImpl.saveZoyShare(shareDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Saved Zoy Share details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating zoy share details API:/zoy_admin/config/zoy-share.zoyAdminConfigCreateUpdateZoyShare ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}


	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetZoyShare() {
		ResponseBody response=new ResponseBody();
		try {
			ZoyShareMaster zoyShareDetails=ownerDBImpl.findAllZoyShare().get(0);
			if(zoyShareDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get zoy share");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyShareDetails zoyShare=new ZoyShareDetails();
			zoyShare.setZoyShareId(zoyShareDetails.getZoyShareId());
			zoyShare.setZoyShare(zoyShareDetails.getZoyShare().doubleValue());
			zoyShare.setPropertyShare(zoyShareDetails.getPropertyShare().doubleValue());
			return new ResponseEntity<>(gson.toJson(zoyShare), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error Getting zoy share details API:/zoy_admin/config/zoy-share.zoyAdminConfigGetZoyShare ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateOtherCharges(ZoyOtherChargesDto details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgOtherCharges otherCharges = ownerDBImpl.findZoyOtherCharges();

			BigDecimal documentCharges;
			BigDecimal otherGst;

			if (otherCharges != null) {
				final BigDecimal oldFixed=otherCharges.getOtherGst();
				final BigDecimal oldVariable=otherCharges.getDocumentCharges();
				documentCharges = (details.getDocumentCharges() != null) 
						? details.getDocumentCharges() 
								: otherCharges.getDocumentCharges();

				otherGst = (details.getOtherGst() != null) 
						? details.getOtherGst() 
								: otherCharges.getOtherGst();

				otherCharges.setDocumentCharges(documentCharges);
				otherCharges.setOtherGst(otherGst);

				ownerDBImpl.saveOtherCharges(otherCharges);
				
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Other Changes for");
				if(oldFixed!=otherCharges.getOtherGst()) {
					historyContent.append(", GST from "+oldFixed+" to "+otherCharges.getOtherGst());
				}
				if(oldVariable!=otherCharges.getDocumentCharges()) {
					historyContent.append(" ,  Document from "+oldVariable+" to "+otherCharges.getDocumentCharges());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyOtherChargesDto dto =convertToDTO(otherCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

			} else {
				documentCharges = (details.getDocumentCharges() != null) 
						? details.getDocumentCharges() 
								: BigDecimal.ZERO; 

				otherGst = (details.getOtherGst() != null) 
						? details.getOtherGst() 
								: BigDecimal.ZERO; 


				ZoyPgOtherCharges newOtherCharges = new ZoyPgOtherCharges();
				newOtherCharges.setDocumentCharges(documentCharges);
				newOtherCharges.setOtherGst(otherGst);

				ownerDBImpl.saveOtherCharges(newOtherCharges);
				
				//audit history here
				String historyContent=" has created the Other Charges for, GST = "+newOtherCharges.getOtherGst()+" , Document ="+newOtherCharges.getDocumentCharges();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyOtherChargesDto dto =convertToDTO(newOtherCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/creating Other Charges details API:/zoy_admin/config/other-charges.zoyAdminConfigCreateUpdateOtherCharges ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	
	private ZoyOtherChargesDto convertToDTO(ZoyPgOtherCharges entity) {
		ZoyOtherChargesDto dto = new ZoyOtherChargesDto();
	    dto.setOtherChargesId(entity.getOtherChargesId());
	    dto.setDocumentCharges(entity.getDocumentCharges());
	    dto.setOtherGst(entity.getOtherGst());
	    return dto;
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateDataGrouping(ZoyDataGroupingDto details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyDataGrouping group=ownerDBImpl.findZoyDataGroup();
			if (group != null) {
				final int oldFixed=group.getConsiderDays();
				group.setConsiderDays(details.getConsiderDays());
				ownerDBImpl.saveDataGroup(group);
				
				//audit history here
				String historyContent=" has updated the Data Grouping for, Considering days from "+oldFixed+" to "+details.getConsiderDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyDataGroupingDto dto=convertToDTO(group);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyDataGrouping newGroup=new ZoyDataGrouping();
				newGroup.setConsiderDays(details.getConsiderDays());
				ownerDBImpl.saveDataGroup(newGroup);
				//audit history here
				String historyContent=" has created the Data Grouping for, Considering days = "+details.getConsiderDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyDataGroupingDto dto=convertToDTO(newGroup);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Data Grouping details API:/zoy_admin/config/data-grouping.zoyAdminConfigCreateUpdateDataGrouping ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	private ZoyDataGroupingDto convertToDTO(ZoyDataGrouping entity) {
		ZoyDataGroupingDto dto = new ZoyDataGroupingDto();
	    dto.setDataGroupingId(entity.getDataGroupingId());
	    dto.setConsiderDays(entity.getConsiderDays());
	    return dto;
	}


	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositLimits(ZoyPgSecurityDepositDetailsDTO details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required SecurityDeposit Limit details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgSecurityDepositDetails limits = ownerDBImpl.findZoySecurityDeposit();      
			if (limits != null) {
				final BigDecimal oldFixed=limits.getSecurityDepositMax();
				final BigDecimal oldVariable=limits.getSecurityDepositMin();
				// Update the existing record
				limits.setSecurityDepositMax(details.getMaximumDeposit());
				limits.setSecurityDepositMin(details.getMinimumDeposit());
				ownerDBImpl.saveZoySecurityDepositLimits(limits);
				
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Security Deposit Limit for");
				if(oldFixed!=details.getMaximumDeposit()) {
					historyContent.append(", Max from "+oldFixed+" to "+details.getMaximumDeposit());
				}
				if(oldVariable!=details.getMinimumDeposit()) {
					historyContent.append(" ,  Min from "+oldVariable+" to "+details.getMinimumDeposit());
				}
				
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyPgSecurityDepositDetailsDTO dto = convertToDTO(limits);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Security Deposits Limit Details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgSecurityDepositDetails newSecurityLimit = new ZoyPgSecurityDepositDetails();
				newSecurityLimit.setSecurityDepositMax(details.getMaximumDeposit());
				newSecurityLimit.setSecurityDepositMin(details.getMinimumDeposit());
				ownerDBImpl.saveZoySecurityDepositLimits(newSecurityLimit);
				//audit history here
				String historyContent=" has created the Security Deposit Limit for, Max = "+details.getMaximumDeposit()+" , Min="+details.getMinimumDeposit();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyPgSecurityDepositDetailsDTO dto = convertToDTO(newSecurityLimit);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Security Deposits Limit details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Security Deposits Limit details: API:/zoy_admin/config/security-deposit-limits ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	private ZoyPgSecurityDepositDetailsDTO convertToDTO(ZoyPgSecurityDepositDetails entity) {
	    ZoyPgSecurityDepositDetailsDTO dto = new ZoyPgSecurityDepositDetailsDTO();
	    dto.setDepositId(entity.getSecurityDepositId());
	    dto.setMinimumDeposit(entity.getSecurityDepositMin());
	    dto.setMaximumDeposit(entity.getSecurityDepositMax());
	    return dto;
	}




	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositRefundRules(ZoyPgSecurityDepositRefundRuleDto ruleDetails) {
		ResponseBody response = new ResponseBody();
		try {
			if (ruleDetails == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Security Deposit Refund Rule details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgSecurityDepositRefundRule existingRule = ownerDBImpl.findSecurityDepositRefundRuleById();

			if (existingRule != null) {
				final int oldFixed=existingRule.getMaxDaysForRefund();
				final BigDecimal oldVariable=existingRule.getPlotformCharges();
				existingRule.setMaxDaysForRefund(ruleDetails.getMaximumDays());
				existingRule.setPlotformCharges(ruleDetails.getPlotformCharges());
				ownerDBImpl.saveSecurityDepositRefundRule(existingRule);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Security Deposit Refund for");
				if(oldFixed!=ruleDetails.getMaximumDays()) {
					historyContent.append(", Max Days For Refund from "+oldFixed+" to "+ruleDetails.getMaximumDays());
				}
				if(oldVariable!=ruleDetails.getPlotformCharges()) {
					historyContent.append(" ,  Plot form Charges from "+oldVariable+" to "+ruleDetails.getPlotformCharges());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyPgSecurityDepositRefundRuleDto dto =convertToDTO(existingRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Security Deposit Refund Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgSecurityDepositRefundRule newRule = new ZoyPgSecurityDepositRefundRule();
				newRule.setMaxDaysForRefund(ruleDetails.getMaximumDays());
				newRule.setPlotformCharges(ruleDetails.getPlotformCharges());
				ownerDBImpl.saveSecurityDepositRefundRule(newRule);
				
				//audit history here
				String historyContent=" has created the Security Deposit Refund for, Max Days For Refund = "+newRule.getMaxDaysForRefund()+" , Plot form Charges="+newRule.getPlotformCharges();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyPgSecurityDepositRefundRuleDto dto =convertToDTO(newRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Created new Security Deposit Refund Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Security Deposit Refund Rule API:/zoy_admin/config/security-deposit-refund-rules.zoyAdminCreateUpadateConfigSecurityDepositRefundRules\r\n ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	private ZoyPgSecurityDepositRefundRuleDto convertToDTO(ZoyPgSecurityDepositRefundRule entity) {
		ZoyPgSecurityDepositRefundRuleDto dto = new ZoyPgSecurityDepositRefundRuleDto();
	    dto.setRuleId(entity.getRuleId());
	    dto.setMaximumDays(entity.getMaxDaysForRefund());
	    dto.setPlotformCharges(entity.getPlotformCharges());
	    return dto;
	}

	
	private List<ZoyBeforeCheckInCancellation> convertToDTO(List<ZoyPgCancellationDetails> cancellationDetailsList) {
	    return cancellationDetailsList.stream()
	        .map(this::convertToDTO) 
	        .collect(Collectors.toList());
	}


	@Override
	public ResponseEntity<String> getAllConfigurationDetails() {
		ResponseBody response = new ResponseBody();
		try {
			ZoyPgTokenDetails tokenDetails = ownerDBImpl.findTokenDetails();
			ZoyPgSecurityDepositDetails depositDetails = ownerDBImpl.findZoySecurityDeposit();
			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
			ZoyPgSecurityDepositRefundRule refundRules = ownerDBImpl.findSecurityDepositRefundRuleById();
			ZoyDataGrouping dataGrouping=ownerDBImpl.findZoyDataGroup();
			ZoyPgOtherCharges otherCharges = ownerDBImpl.findZoyOtherCharges();

			ZoyAdminConfigDTO configDTO = new ZoyAdminConfigDTO();
			configDTO.setTokenDetails(convertToDTO(tokenDetails));
			configDTO.setDepositDetails(convertToDTO(depositDetails));
			configDTO.setCancellationDetails(convertToDTO(cancellationDetails));
			configDTO.setRefundRules(convertToDTO(refundRules));
			configDTO.setDataGrouping(convertToDTO(dataGrouping));
			configDTO.setOtherCharges(convertToDTO(otherCharges));
			
			response.setStatus(HttpStatus.OK.value());
			response.setData(configDTO);
			response.setMessage("Successfully fetched all config details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Error fetching config details API:/zoy_admin/config/admin-configuration-details.getAllConfigurationDetails ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	@Transactional
	public ResponseEntity<String> zoyAdminConfigDeleteBeforeCheckIn(@RequestBody ZoyBeforeCheckInCancellation cancellationID) {
		 ResponseBody response = new ResponseBody();
		    try {
		        if (cancellationID.getCancellationId() == null || cancellationID.getCancellationId().isEmpty()) {
		            response.setStatus(HttpStatus.BAD_REQUEST.value());
		            response.setError("Cancellation ID is required");
		            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		        }

		        final ZoyPgCancellationDetails cancelDetails = ownerDBImpl.findBeforeCancellationDetails(cancellationID.getCancellationId());
		        if (cancelDetails == null) {
		            response.setStatus(HttpStatus.NOT_FOUND.value());
		            response.setError("Cancellation details not found for the given ID");
		            return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
		        }
		        ownerDBImpl.deleteBeforeCancellation(cancellationID.getCancellationId());
		      //audit history here
				String historyContent=" has deleted the Cancellation And Refund Policy for, Days before check in = "+cancelDetails.getDaysBeforeCheckIn()+" , Deduction percentage ="+cancelDetails.getDeductionPercentages();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
				
		        List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
		        List<ZoyBeforeCheckInCancellation> dtoList = cancellationDetails.stream()
		                .map(this::convertToDTO)
		                .collect(Collectors.toList());
		        response.setStatus(HttpStatus.OK.value());
		        response.setData(dtoList);
		        response.setMessage("Cancellation details successfully deleted");
		        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		    } catch (Exception e) {
		        log.error("Error deleting cancellation details API: /zoy_admin/config/before-check-in/{cancellationId}", e);
		        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		        response.setError("Internal server error");
		        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigAutoCancellationperiod(ZoyPgAutoCancellationPeriodDto autoCancellation) {
		ResponseBody response = new ResponseBody();
		try {
			if (autoCancellation == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Auto Cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgAutoCancellationPeriod cancellationPeriod = ownerDBImpl.findAutoCancellationPeriodById();

			if (cancellationPeriod != null) {
				final int oldFixed=cancellationPeriod.getDaysToCancel();
				final BigDecimal oldVariable=cancellationPeriod.getDeductionPercentage();
				cancellationPeriod.setDaysToCancel(autoCancellation.getDaysToCancel());
				cancellationPeriod.setDeductionPercentage(autoCancellation.getDeductionPercentage());
				ownerDBImpl.saveAutoCancellationPeriod(cancellationPeriod);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the auto cancellation period for");
				if(oldFixed!=autoCancellation.getDaysToCancel()) {
					historyContent.append(", number of days from "+oldFixed+" to "+autoCancellation.getDaysToCancel());
				}
				if(oldVariable!=autoCancellation.getDeductionPercentage()) {
					historyContent.append(" ,   from "+oldVariable+" to "+autoCancellation.getDeductionPercentage());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				
				ZoyPgAutoCancellationPeriodDto dto =convertToDTO(cancellationPeriod);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated auto cancellation period");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgAutoCancellationPeriod newCancellationPeriod = new ZoyPgAutoCancellationPeriod();
				newCancellationPeriod.setDaysToCancel(autoCancellation.getDaysToCancel());
				newCancellationPeriod.setDeductionPercentage(autoCancellation.getDeductionPercentage());

				ownerDBImpl.saveAutoCancellationPeriod(newCancellationPeriod);
				//audit history here
				String historyContent=" has created the autodeduction details for, Max Days For Refund = "+newCancellationPeriod.getDaysToCancel()+" , deduction percentage="+newCancellationPeriod.getDeductionPercentage();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyPgAutoCancellationPeriodDto dto =convertToDTO(newCancellationPeriod);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Created new auto cancellation period ");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Auto cancellation Period Rule API:/zoy_admin/config/auto-cancellation-period.zoyAdminCreateUpadateConfigAutoCancellationperiod\r\n ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	private ZoyPgAutoCancellationPeriodDto convertToDTO(ZoyPgAutoCancellationPeriod entity) {
		ZoyPgAutoCancellationPeriodDto dto = new ZoyPgAutoCancellationPeriodDto();
	    dto.setAutoCancellationId(entity.getAutoCancellationId());
	    dto.setDaysToCancel(entity.getDaysToCancel());
	    dto.setDeductionPercentage(entity.getDeductionPercentage());
	    return dto;
	}

}
