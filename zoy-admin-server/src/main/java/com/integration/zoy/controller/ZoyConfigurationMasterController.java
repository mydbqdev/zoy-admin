package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgSecurityDepositRefundRule;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.entity.ZoyShareMaster;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyOtherCharges;
import com.integration.zoy.model.ZoyShareDetails;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.ZoyAdminConfigDTO;


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


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateToken(ZoyPgTokenDetails details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgTokenDetails tokenDetails = ownerDBImpl.findTokenDetails();
			if (tokenDetails != null) {
				// Update the existing token record
				tokenDetails.setFixedToken(details.getFixedToken() != null ? details.getFixedToken() : BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken() != null ? details.getVariableToken() : BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(tokenDetails);
				response.setMessage("Updated Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				// Create a new token record
				ZoyPgTokenDetails newTokenDetails = new ZoyPgTokenDetails();
				newTokenDetails.setFixedToken(details.getFixedToken() != null ? details.getFixedToken() : BigDecimal.ZERO);
				newTokenDetails.setVariableToken(details.getVariableToken() != null ? details.getVariableToken() : BigDecimal.ZERO);
				ownerDBImpl.saveToken(newTokenDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(newTokenDetails);
				response.setMessage("Saved Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating token details:API:/zoy_admin/config/token_advance.zoyAdminConfigCreateUpdateToken " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(ZoyBeforeCheckInCancellation details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if(details.getCancellationId()!=null && !details.getCancellationId().isEmpty()) {
				ZoyPgCancellationDetails cancelDetails=ownerDBImpl.findBeforeCancellationDetails(details.getCancellationId());
				if(cancelDetails==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get before check in cancellation details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				cancelDetails.setDaysBeforeCheckIn(details.getDaysBeforeCheckIn());
				cancelDetails.setDeductionPercentages(details.getDeductionPercentages());
				ownerDBImpl.saveBeforeCancellation(cancelDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setData(cancelDetails);
				response.setMessage("Updated Before CheckIn details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgCancellationDetails cancelDetails=new ZoyPgCancellationDetails();
				cancelDetails.setDaysBeforeCheckIn(details.getDaysBeforeCheckIn());
				cancelDetails.setDeductionPercentages(details.getDeductionPercentages());
				ownerDBImpl.saveBeforeCancellation(cancelDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(cancelDetails);
				response.setMessage("Saved Before CheckIn details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property details API:/zoy_admin/config/security-deposit-limits.zoyAdminCreateUpadateConfigSecurityDepositLimits " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

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
			log.error("Error saving/updating zoy share details API:/zoy_admin/config/zoy-share.zoyAdminConfigCreateUpdateZoyShare " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
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
			log.error("Error Getting zoy share details API:/zoy_admin/config/zoy-share.zoyAdminConfigGetZoyShare " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateOtherCharges(ZoyOtherCharges details) {
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
				documentCharges = (details.getDocumentCharges() != null) 
						? details.getDocumentCharges() 
								: otherCharges.getDocumentCharges();

				otherGst = (details.getOtherGst() != null) 
						? details.getOtherGst() 
								: otherCharges.getOtherGst();

				otherCharges.setDocumentCharges(documentCharges);
				otherCharges.setOtherGst(otherGst);

				ownerDBImpl.saveOtherCharges(otherCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(otherCharges);
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
				response.setStatus(HttpStatus.OK.value());
				response.setData(newOtherCharges);
				response.setMessage("Saved Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/creating Other Charges details API:/zoy_admin/config/other-charges.zoyAdminConfigCreateUpdateOtherCharges " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}



	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateDataGrouping(ZoyDataGrouping details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyDataGrouping group=ownerDBImpl.findZoyDataGroup();
			if (group != null) {
				group.setConsiderDays(details.getConsiderDays());
				ownerDBImpl.saveDataGroup(group);
				response.setStatus(HttpStatus.OK.value());
				response.setData(group);
				response.setMessage("Updated Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyDataGrouping newGroup=new ZoyDataGrouping();
				newGroup.setConsiderDays(details.getConsiderDays());
				ownerDBImpl.saveDataGroup(newGroup);
				response.setStatus(HttpStatus.OK.value());
				response.setData(newGroup);
				response.setMessage("Saved Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Data Grouping details API:/zoy_admin/config/data-grouping.zoyAdminConfigCreateUpdateDataGrouping " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositLimits(ZoyPgSecurityDepositDetails details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required SecurityDeposit Limit details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgSecurityDepositDetails limits = ownerDBImpl.findZoySecurityDeposit();      
			if (limits != null) {
				// Update the existing record
				limits.setSecurityDepositMax(details.getSecurityDepositMax());
				limits.setSecurityDepositMin(details.getSecurityDepositMin());
				ownerDBImpl.saveZoySecurityDepositLimits(limits);
				response.setStatus(HttpStatus.OK.value());
				response.setData(limits);
				response.setMessage("Updated Security Deposits Limit Details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgSecurityDepositDetails newSecurityLimit = new ZoyPgSecurityDepositDetails();
				newSecurityLimit.setSecurityDepositMax(details.getSecurityDepositMax());
				newSecurityLimit.setSecurityDepositMin(details.getSecurityDepositMin());
				ownerDBImpl.saveZoySecurityDepositLimits(newSecurityLimit);
				response.setStatus(HttpStatus.OK.value());
				response.setData(newSecurityLimit);
				response.setMessage("Saved Security Deposits Limit details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Security Deposits Limit details: API:/zoy_admin/config/security-deposit-limits " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateConfigSecurityDepositRefundRules(ZoyPgSecurityDepositRefundRule ruleDetails) {
		ResponseBody response = new ResponseBody();
		try {
			if (ruleDetails == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Security Deposit Refund Rule details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgSecurityDepositRefundRule existingRule = ownerDBImpl.findSecurityDepositRefundRuleById();

			if (existingRule != null) {
				existingRule.setMaxDaysForRefund(ruleDetails.getMaxDaysForRefund());
				ownerDBImpl.saveSecurityDepositRefundRule(existingRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(existingRule);
				response.setMessage("Updated Security Deposit Refund Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgSecurityDepositRefundRule newRule = new ZoyPgSecurityDepositRefundRule();
				newRule.setMaxDaysForRefund(ruleDetails.getMaxDaysForRefund());
				ownerDBImpl.saveSecurityDepositRefundRule(newRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(newRule);
				response.setMessage("Created new Security Deposit Refund Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Security Deposit Refund Rule API:/zoy_admin/config/security-deposit-refund-rules.zoyAdminCreateUpadateConfigSecurityDepositRefundRules\r\n " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
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
			configDTO.setTokenDetails(tokenDetails);
			configDTO.setDepositDetails(depositDetails);
			configDTO.setCancellationDetails(cancellationDetails);
			configDTO.setRefundRules(refundRules);
			configDTO.setDataGrouping(dataGrouping);
			configDTO.setOtherCharges(otherCharges);
			
			response.setStatus(HttpStatus.OK.value());
			response.setData(configDTO);
			response.setMessage("Successfully fetched all config details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Error fetching config details API:/zoy_admin/config/admin-configuration-details.getAllConfigurationDetails " + e.getMessage(), e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
