package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;
import com.integration.zoy.entity.ZoyPgShortTermMaster;
import com.integration.zoy.entity.ZoyPgShortTermRentingDuration;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.ShortTerm;
import com.integration.zoy.model.ZoyAfterCheckInCancellation;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyCompanyMasterModal;
import com.integration.zoy.model.ZoyCompanyProfileMasterModal;
import com.integration.zoy.model.ZoyPgEarlyCheckOutRule;
import com.integration.zoy.model.ZoySecurityDeadLine;
import com.integration.zoy.service.AdminDBImpl;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.PdfGenerateService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.UploadTenant;
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
import com.integration.zoy.utils.ZoyPgSecurityDepositDetailsDTO;
import com.integration.zoy.utils.ZoyPgTokenDetailsDTO;
import com.integration.zoy.utils.ZoyRentingDuration;
import com.integration.zoy.utils.ZoySecurityDepositDeadLineDto;
import com.integration.zoy.utils.ZoyShortTermDto;


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
	AdminDBImpl adminDBImpl;

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

	@Autowired
	PdfGenerateService pdfGenerateService;


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
	public ResponseEntity<String> zoyAdminConfigCreateUpdateBeforeCheckIn(List<ZoyBeforeCheckInCancellation> zoyBeforeCheckInCancellations) {
		ResponseBody response = new ResponseBody();
		try {
			if (zoyBeforeCheckInCancellations == null || zoyBeforeCheckInCancellations.size() <= 0) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			for(ZoyBeforeCheckInCancellation details:zoyBeforeCheckInCancellations) {
				if (details.getCancellationId() != null && !details.getCancellationId().isEmpty()) {
					if(details.getIsEdit()) {
						ZoyPgCancellationDetails cancelDetails = ownerDBImpl.findBeforeCancellationDetails(details.getCancellationId());
						if (cancelDetails == null) {
							response.setStatus(HttpStatus.CONFLICT.value());
							response.setError("Unable to get before check-in cancellation details");
							return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
						}
						final int oldFixed=cancelDetails.getBeforeCheckinDays();
						final BigDecimal oldVariable=cancelDetails.getDeductionPercentage();
						cancelDetails.setPriority(details.getPriority());
						cancelDetails.setTriggerOn(details.getTriggerOn());
						cancelDetails.setTriggerCondition(details.getTriggerCondition());
						cancelDetails.setBeforeCheckinDays(details.getBeforeCheckinDays());
						cancelDetails.setDeductionPercentage(details.getDeductionPercentage());
						cancelDetails.setCond(details.getTriggerOn() +" "+ details.getTriggerCondition() +" "+ details.getBeforeCheckinDays());
						cancelDetails.setTriggerValue(details.getTriggerValue());
						ownerDBImpl.saveBeforeCancellation(cancelDetails);
						//audit history here
						StringBuffer historyContent=new StringBuffer(" has updated the Cancellation And Refund Policy for");
						if(oldFixed!=details.getBeforeCheckinDays()) {
							historyContent.append(", Days before check in from "+oldFixed+" to "+details.getBeforeCheckinDays());
						}
						if(oldVariable!=details.getDeductionPercentage()) {
							historyContent.append(" , Deduction percentage from "+oldVariable+" to "+details.getDeductionPercentage());
						}
						auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
					} else if (details.getIsDelete()) {
						ZoyPgCancellationDetails cancelDetails = ownerDBImpl.findBeforeCancellationDetails(details.getCancellationId());
						if (cancelDetails == null) {
							response.setStatus(HttpStatus.NOT_FOUND.value());
							response.setError("Cancellation details not found for the given ID");
							return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
						}
						ownerDBImpl.deleteBeforeCancellation(cancelDetails.getCancellationId());
						//audit history here
						String historyContent=" has deleted the Cancellation And Refund Policy for, Days before check in = "+cancelDetails.getBeforeCheckinDays()+" , Deduction percentage ="+cancelDetails.getDeductionPercentage();
						auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);

					}
				} else {
					ZoyPgCancellationDetails newCancelDetails = new ZoyPgCancellationDetails();
					newCancelDetails.setPriority(details.getPriority());
					newCancelDetails.setTriggerOn(details.getTriggerOn());
					newCancelDetails.setTriggerCondition(details.getTriggerCondition());
					newCancelDetails.setBeforeCheckinDays(details.getBeforeCheckinDays());
					newCancelDetails.setDeductionPercentage(details.getDeductionPercentage());
					newCancelDetails.setCond(details.getTriggerOn() +" "+ details.getTriggerCondition() +" "+ details.getBeforeCheckinDays());
					newCancelDetails.setTriggerValue(details.getTriggerValue());
					ownerDBImpl.saveBeforeCancellation(newCancelDetails);
					//audit history here
					String historyContent=" has created the Cancellation And Refund Policy for, Days before check in = "+details.getBeforeCheckinDays()+" , Deduction percentage ="+details.getDeductionPercentage();
					auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				}
			}

			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
			List<ZoyBeforeCheckInCancellationDto> dtoList = cancellationDetails.stream()
					.map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dtoList);
			response.setMessage("Retrieved all Before CheckIn details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error("Error creating/updating before check-in API:/zoy_admin/config/before-check-in.zoyAdminConfigCreateUpdateBeforeCheckIn", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
		dto.setCond(details.getTriggerOn() +" "+ details.getTriggerCondition() +" "+ details.getBeforeCheckinDays());
		dto.setTriggerValue(details.getTriggerValue());
		dto.setCreateAt(details.getCreateAt());
		return dto;
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

			BigDecimal ownerCharges;
			BigDecimal tenantCharges;
			BigDecimal tenantEkycCharges;
			BigDecimal ownerEkycCharges;
			if (otherCharges != null) {
				final BigDecimal oldOwnerCharges=otherCharges.getOwnerDocumentCharges();
				final BigDecimal oldTenantChares=otherCharges.getTenantDocumentCharges();
				final BigDecimal oldOwnerEkycCharges=otherCharges.getOwnerEkycCharges();
				final BigDecimal oldTenantEkycCharges=otherCharges.getTenantEkycCharges();
				ownerCharges = (details.getOwnerDocumentCharges() != null) 
						? details.getOwnerDocumentCharges() 
								: otherCharges.getOwnerDocumentCharges();

				ownerEkycCharges = (details.getOwnerEkycCharges() != null) 
						? details.getOwnerEkycCharges() 
								: otherCharges.getOwnerEkycCharges();

				tenantCharges = (details.getTenantDocumentCharges() != null) 
						? details.getTenantDocumentCharges() 
								: otherCharges.getTenantDocumentCharges();

				tenantEkycCharges = (details.getTenantEkycCharges() != null) 
						? details.getTenantEkycCharges() 
								: otherCharges.getTenantEkycCharges();

				otherCharges.setOwnerDocumentCharges(ownerCharges);
				otherCharges.setTenantDocumentCharges(tenantCharges);
				otherCharges.setOwnerEkycCharges(ownerEkycCharges);
				otherCharges.setTenantEkycCharges(tenantEkycCharges);
				ownerDBImpl.saveOtherCharges(otherCharges);

				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Other Changes for");
				if(oldOwnerCharges!=otherCharges.getOwnerDocumentCharges()) {
					historyContent.append(", owner document charges from "+oldOwnerCharges+" to "+otherCharges.getOwnerDocumentCharges());
				}
				if(oldOwnerCharges!=otherCharges.getOwnerEkycCharges()) {
					historyContent.append(", owner Ekyc charges from "+oldOwnerEkycCharges+" to "+otherCharges.getOwnerEkycCharges());
				}
				if(oldTenantChares!=otherCharges.getTenantDocumentCharges()) {
					historyContent.append(" ,  tenant document charges from "+oldTenantChares+" to "+otherCharges.getTenantDocumentCharges());
				}
				if(oldTenantChares!=otherCharges.getTenantEkycCharges()) {
					historyContent.append(" ,  tenant Ekyc charges from "+oldTenantEkycCharges+" to "+otherCharges.getTenantEkycCharges());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyOtherChargesDto dto =convertToDTO(otherCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

			} else {
				ownerCharges = (details.getOwnerDocumentCharges() != null) 
						? details.getOwnerDocumentCharges() 
								: BigDecimal.ZERO;
				ownerEkycCharges = (details.getOwnerEkycCharges() != null) 
						? details.getOwnerEkycCharges() 
								: BigDecimal.ZERO;
				tenantCharges = (details.getTenantDocumentCharges() != null) 
						? details.getTenantDocumentCharges() 
								: BigDecimal.ZERO;
				tenantEkycCharges = (details.getTenantEkycCharges() != null) 
						? details.getTenantEkycCharges() 
								: BigDecimal.ZERO;


				ZoyPgOtherCharges newOtherCharges = new ZoyPgOtherCharges();
				newOtherCharges.setOwnerDocumentCharges(ownerCharges);
				newOtherCharges.setTenantDocumentCharges(tenantCharges);
				newOtherCharges.setOwnerEkycCharges(ownerEkycCharges);
				newOtherCharges.setTenantEkycCharges(tenantEkycCharges);

				ownerDBImpl.saveOtherCharges(newOtherCharges);

				//audit history here
				String historyContent=" has created the Other Charges for, Owner Document Charges = "+newOtherCharges.getOwnerDocumentCharges()+" , Tenant Document Charges ="+newOtherCharges.getTenantDocumentCharges()+" , Tenant Ekyc Charges ="+newOtherCharges.getTenantEkycCharges()+" , Owner Ekyc Charges ="+newOtherCharges.getOwnerEkycCharges();
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

	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateGstCharges(ZoyGstChargesDto details) {
		ResponseBody response = new ResponseBody();
		try {
			if (details == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required GST Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgGstCharges gstCharges = ownerDBImpl.findZoyGstCharges();

			BigDecimal cgstPercentage;
			BigDecimal sgstPercentage;
			BigDecimal igstPercentage;
			BigDecimal monthlyRent;
			if (gstCharges != null) {
				final BigDecimal oldCGstPercentage=gstCharges.getCgstPercentage();
				final BigDecimal oldSGstPercentage=gstCharges.getSgstPercentage();
				final BigDecimal oldIGstPercentage=gstCharges.getIgstPercentage();
				final BigDecimal oldMonthlyRent=gstCharges.getMonthlyRent();

				cgstPercentage = (details.getCgstPercentage() != null) 
						? details.getCgstPercentage() 
								: gstCharges.getCgstPercentage();

				sgstPercentage = (details.getSgstPercentage() != null) 
						? details.getSgstPercentage() 
								: gstCharges.getSgstPercentage();

				igstPercentage = (details.getIgstPercentage() != null) 
						? details.getIgstPercentage() 
								: gstCharges.getIgstPercentage();

				monthlyRent = (details.getMonthlyRent() != null) 
						? details.getMonthlyRent() 
								: gstCharges.getMonthlyRent();

				gstCharges.setCgstPercentage(cgstPercentage);
				gstCharges.setSgstPercentage(sgstPercentage);
				gstCharges.setIgstPercentage(igstPercentage);
				gstCharges.setMonthlyRent(monthlyRent);
				gstCharges.setComponentName("RENT");
				ownerDBImpl.saveGstCharges(gstCharges);

				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Gst Changes for");
				if(oldCGstPercentage!=gstCharges.getCgstPercentage()) {
					historyContent.append(", CGST percentage charges from "+oldCGstPercentage+" to "+gstCharges.getCgstPercentage());
				}
				if(oldSGstPercentage!=gstCharges.getSgstPercentage()) {
					historyContent.append(", SGST percentage charges from "+oldSGstPercentage+" to "+gstCharges.getSgstPercentage());
				}
				if(oldIGstPercentage!=gstCharges.getIgstPercentage()) {
					historyContent.append(", IGST percentage charges from "+oldIGstPercentage+" to "+gstCharges.getIgstPercentage());
				}
				if(oldMonthlyRent!=gstCharges.getMonthlyRent()) {
					historyContent.append(", monthly Rent charges from "+oldMonthlyRent+" to "+gstCharges.getMonthlyRent());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyGstChargesDto dto =convertToDTO(gstCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated GST Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

			} else {
				cgstPercentage = (details.getCgstPercentage() != null) 
						? details.getCgstPercentage() 
								: BigDecimal.ZERO;

				sgstPercentage = (details.getSgstPercentage() != null) 
						? details.getSgstPercentage() 
								: BigDecimal.ZERO;

				igstPercentage = (details.getIgstPercentage() != null) 
						? details.getIgstPercentage() 
								: BigDecimal.ZERO;

				monthlyRent = (details.getMonthlyRent() != null) 
						? details.getMonthlyRent() 
								: BigDecimal.ZERO;

				ZoyPgGstCharges newGstCharges = new ZoyPgGstCharges();

				newGstCharges.setCgstPercentage(cgstPercentage);
				newGstCharges.setSgstPercentage(sgstPercentage);
				newGstCharges.setIgstPercentage(igstPercentage);
				newGstCharges.setMonthlyRent(monthlyRent);
				newGstCharges.setComponentName("RENT");

				ownerDBImpl.saveGstCharges(newGstCharges);

				//audit history here
				String historyContent=" has created the GST Charges for, CGST Charges = "+newGstCharges.getCgstPercentage()+
						", SGST Charges = "+newGstCharges.getSgstPercentage()+ 
						", IGST Charges = "+newGstCharges.getIgstPercentage()+
						", Monthly Rent ="+newGstCharges.getMonthlyRent();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyGstChargesDto dto =convertToDTO(newGstCharges);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved GST Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/creating GST Charges details API:/zoy_admin/config/gst-charges.zoyAdminConfigCreateUpdateGstCharges ", e);
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
		return dto;
	}

	private ZoyGstChargesDto convertToDTO(ZoyPgGstCharges entity) {
		ZoyGstChargesDto dto = new ZoyGstChargesDto();
		dto.setRentId(entity.getRentId());
		dto.setCgstPercentage(entity.getCgstPercentage());
		dto.setSgstPercentage(entity.getSgstPercentage());
		dto.setIgstPercentage(entity.getIgstPercentage());
		dto.setMonthlyRent(entity.getMonthlyRent());
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
	public ResponseEntity<String> zoyAdminCreateUpadateEarlyCheckOutRules(ZoyPgEarlyCheckOutRule zoyPgEarlyCheckOut) {
		ResponseBody response = new ResponseBody();
		try {
			if (zoyPgEarlyCheckOut == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Early Check out Rule details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgEarlyCheckOut existingRule = ownerDBImpl.findEarlyCheckOutRule(zoyPgEarlyCheckOut.getEarlyCheckOutId());

			if (existingRule != null) {
				final Long oldFixed=existingRule.getCheckOutDay();
				final BigDecimal oldVariable=existingRule.getDeductionPercentage();
				existingRule.setTriggerOn(zoyPgEarlyCheckOut.getTriggerOn());
				existingRule.setTriggerCondition(zoyPgEarlyCheckOut.getTriggerCondition());
				existingRule.setCheckOutDay(zoyPgEarlyCheckOut.getCheckOutDay());
				existingRule.setDeductionPercentage(zoyPgEarlyCheckOut.getDeductionPercentage());
				existingRule.setCond(zoyPgEarlyCheckOut.getTriggerOn() +" "+ zoyPgEarlyCheckOut.getTriggerCondition() +" "+ zoyPgEarlyCheckOut.getCheckOutDay());
				existingRule.setTriggerValue(zoyPgEarlyCheckOut.getTriggerValue());
				ownerDBImpl.saveEarlyCheckOut(existingRule);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Early Check out for");
				if(oldFixed!=zoyPgEarlyCheckOut.getCheckOutDay()) {
					historyContent.append(", Chek out Days from "+oldFixed+" to "+zoyPgEarlyCheckOut.getCheckOutDay());
				}
				if(oldVariable!=zoyPgEarlyCheckOut.getDeductionPercentage()) {
					historyContent.append(" ,  Deduction percentage from "+oldVariable+" to "+zoyPgEarlyCheckOut.getDeductionPercentage());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyPgEarlyCheckOutRuleDto dto =convertToDTO(existingRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Early Check out Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgEarlyCheckOut newRule = new ZoyPgEarlyCheckOut();
				newRule.setTriggerOn(zoyPgEarlyCheckOut.getTriggerOn());
				newRule.setTriggerCondition(zoyPgEarlyCheckOut.getTriggerCondition());
				newRule.setCheckOutDay(zoyPgEarlyCheckOut.getCheckOutDay());
				newRule.setDeductionPercentage(zoyPgEarlyCheckOut.getDeductionPercentage());
				newRule.setCond(zoyPgEarlyCheckOut.getTriggerOn() +" "+ zoyPgEarlyCheckOut.getTriggerCondition() +" "+ zoyPgEarlyCheckOut.getCheckOutDay());
				newRule.setTriggerValue(zoyPgEarlyCheckOut.getTriggerValue());
				ownerDBImpl.saveEarlyCheckOut(newRule);

				//audit history here
				String historyContent=" has created the Early check out for, Check out Days = "+newRule.getCheckOutDay()+" , Deduction Percentage ="+newRule.getDeductionPercentage();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyPgEarlyCheckOutRuleDto dto =convertToDTO(newRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Created new Early Check out Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Early check out  Rule API:/zoy_admin/config/early-checkout-rules.zoyAdminCreateUpadateEarlyCheckOutRules\r\n ", e);
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
		dto.setCond(entity.getTriggerOn() +" "+ entity.getTriggerCondition() +" "+ entity.getCheckOutDay());
		dto.setTriggerValue(entity.getTriggerValue());
		return dto;
	}


	private List<ZoyBeforeCheckInCancellationDto> convertToDTO(List<ZoyPgCancellationDetails> cancellationDetailsList) {
		return cancellationDetailsList.stream()
				.map(this::convertToDTO) 
				.collect(Collectors.toList());
	}




	//	@Override
	//	@Transactional
	//	public ResponseEntity<String> zoyAdminConfigDeleteBeforeCheckIn(@RequestBody ZoyBeforeCheckInCancellation cancellationID) {
	//		ResponseBody response = new ResponseBody();
	//		try {
	//			if (cancellationID.getCancellationId() == null || cancellationID.getCancellationId().isEmpty()) {
	//				response.setStatus(HttpStatus.BAD_REQUEST.value());
	//				response.setError("Cancellation ID is required");
	//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	//			}
	//
	//			final ZoyPgCancellationDetails cancelDetails = ownerDBImpl.findBeforeCancellationDetails(cancellationID.getCancellationId());
	//			if (cancelDetails == null) {
	//				response.setStatus(HttpStatus.NOT_FOUND.value());
	//				response.setError("Cancellation details not found for the given ID");
	//				return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	//			}
	//			ownerDBImpl.deleteBeforeCancellation(cancellationID.getCancellationId());
	//			//audit history here
	//			String historyContent=" has deleted the Cancellation And Refund Policy for, Days before check in = "+cancelDetails.getBeforeCheckinDays()+" , Deduction percentage ="+cancelDetails.getDeductionPercentage();
	//			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
	//
	//			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
	//			List<ZoyBeforeCheckInCancellationDto> dtoList = cancellationDetails.stream()
	//					.map(this::convertToDTO)
	//					.collect(Collectors.toList());
	//			response.setStatus(HttpStatus.OK.value());
	//			response.setData(dtoList);
	//			response.setMessage("Cancellation details successfully deleted");
	//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	//
	//		} catch (Exception e) {
	//			log.error("Error deleting cancellation details API: /zoy_admin/config/before-check-in/{cancellationId}", e);
	//			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	//			response.setError("Internal server error");
	//			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	//		}
	//	}

	private ZoyAfterCheckInCancellationDto convertToDTO(ZoyPgAutoCancellationAfterCheckIn entity) {
		ZoyAfterCheckInCancellationDto dto = new ZoyAfterCheckInCancellationDto();
		dto.setAutoCancellationId(entity.getAutoCancellationId());
		dto.setTriggerOn(entity.getTriggerOn());
		dto.setTriggerCondition(entity.getTriggerCondition());
		dto.setAutoCancellationDay(entity.getAutoCancellationDay());
		dto.setDeductionPercentage(entity.getDeductionPercentage());
		dto.setCond(entity.getTriggerOn() +" "+ entity.getTriggerCondition() +" "+ entity.getAutoCancellationDay());
		dto.setTriggerValue(entity.getTriggerValue());
		return dto;
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateAfterCheckIn(ZoyAfterCheckInCancellation zoyAfterCheckInCancellation) {

		ResponseBody response = new ResponseBody();
		try {
			if (zoyAfterCheckInCancellation == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required After Check In details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}

			ZoyPgAutoCancellationAfterCheckIn existingRule = ownerDBImpl.findAutoCancellationAfterCheckIn(zoyAfterCheckInCancellation.getAutoCancellationId());

			if (existingRule != null) {
				final Long oldFixed=existingRule.getAutoCancellationDay();
				final BigDecimal oldVariable=existingRule.getDeductionPercentage();
				existingRule.setTriggerOn(zoyAfterCheckInCancellation.getTriggerOn());
				existingRule.setTriggerCondition(zoyAfterCheckInCancellation.getTriggerCondition());
				existingRule.setAutoCancellationDay(zoyAfterCheckInCancellation.getAutoCancellationDay());
				existingRule.setDeductionPercentage(zoyAfterCheckInCancellation.getDeductionPercentage());
				existingRule.setCond(zoyAfterCheckInCancellation.getTriggerOn() +" "+ zoyAfterCheckInCancellation.getTriggerCondition() +" "+ zoyAfterCheckInCancellation.getAutoCancellationDay());
				existingRule.setTriggerValue(zoyAfterCheckInCancellation.getTriggerValue());
				ownerDBImpl.saveAutoCancellationAfterCheckIn(existingRule);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Cancellation After Check in for");
				if(oldFixed!=zoyAfterCheckInCancellation.getAutoCancellationDay()) {
					historyContent.append(", Cancellation Days from "+oldFixed+" to "+zoyAfterCheckInCancellation.getAutoCancellationDay());
				}
				if(oldVariable!=zoyAfterCheckInCancellation.getDeductionPercentage()) {
					historyContent.append(" ,  Deduction Percentage from "+oldVariable+" to "+zoyAfterCheckInCancellation.getDeductionPercentage());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyAfterCheckInCancellationDto dto =convertToDTO(existingRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Cancellation After Check In Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgAutoCancellationAfterCheckIn newRule = new ZoyPgAutoCancellationAfterCheckIn();
				newRule.setTriggerOn(zoyAfterCheckInCancellation.getTriggerOn());
				newRule.setTriggerCondition(zoyAfterCheckInCancellation.getTriggerCondition());
				newRule.setAutoCancellationDay(zoyAfterCheckInCancellation.getAutoCancellationDay());
				newRule.setDeductionPercentage(zoyAfterCheckInCancellation.getDeductionPercentage());
				newRule.setCond(zoyAfterCheckInCancellation.getTriggerOn() +" "+ zoyAfterCheckInCancellation.getTriggerCondition() +" "+ zoyAfterCheckInCancellation.getAutoCancellationDay());
				newRule.setTriggerValue(zoyAfterCheckInCancellation.getTriggerValue());
				ownerDBImpl.saveAutoCancellationAfterCheckIn(newRule);

				//audit history here
				String historyContent=" has created the Cancellation After Check in for, Cancellation Days = "+newRule.getAutoCancellationDay()+" , Deduction Percentage ="+newRule.getDeductionPercentage();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyAfterCheckInCancellationDto dto =convertToDTO(newRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Created new Cancellation After Check In Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Cancellation After Check in Rule API:/zoy_admin/config/after-check-in.zoyAdminCreateUpadateAfterCheckIn ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> getAllConfigurationDetails() {
		ResponseBody response = new ResponseBody();
		try {
			ZoyPgTokenDetails tokenDetails = ownerDBImpl.findTokenDetails();
			ZoyPgSecurityDepositDetails depositDetails = ownerDBImpl.findZoySecurityDeposit();
			List<ZoyPgCancellationDetails> cancellationDetails = ownerDBImpl.findAllBeforeCancellation();
			ZoyPgEarlyCheckOut earlyCheckOutDetails = ownerDBImpl.findEarlyCheckOutRule();
			ZoyPgAutoCancellationAfterCheckIn cancellationAfterCheckIn = ownerDBImpl.findAutoCancellationAfterCheckIn();
			ZoyPgAutoCancellationMaster securityDepositDeadLine = ownerDBImpl.findSecurityDepositDeadLine();
			ZoyDataGrouping dataGrouping=ownerDBImpl.findZoyDataGroup();
			ZoyPgOtherCharges otherCharges = ownerDBImpl.findZoyOtherCharges();
			ZoyPgGstCharges gstCharges=ownerDBImpl.findZoyGstCharges();
			List<ZoyPgShortTermMaster> shortTermMaster=ownerDBImpl.findAllShortTerm();
			ZoyPgForceCheckOut forceCheckOut=ownerDBImpl.findZoyForceCheckOut();
			ZoyPgShortTermRentingDuration rentingDuration=ownerDBImpl.findZoyRentingDuration();
			ZoyAdminConfigDTO configDTO = new ZoyAdminConfigDTO();
			if (tokenDetails != null) 
				configDTO.setTokenDetails(convertToDTO(tokenDetails));
			if (depositDetails != null)
				configDTO.setDepositDetails(convertToDTO(depositDetails));
			if (cancellationDetails != null)
				configDTO.setCancellationBeforeCheckInDetails(convertToDTO(cancellationDetails));
			if (earlyCheckOutDetails != null)
				configDTO.setEarlyCheckOutRuleDetails(convertToDTO(earlyCheckOutDetails));
			if (cancellationAfterCheckIn != null)
				configDTO.setCancellationAfterCheckInDetails(convertToDTO(cancellationAfterCheckIn));
			if (securityDepositDeadLine != null)
				configDTO.setSecurityDepositDeadLineDetails(convertToDTO(securityDepositDeadLine));
			if (dataGrouping != null)
				configDTO.setDataGrouping(convertToDTO(dataGrouping));
			if (otherCharges != null)
				configDTO.setOtherCharges(convertToDTO(otherCharges));
			if (gstCharges != null)
				configDTO.setGstCharges(convertToDTO(gstCharges));
			if (shortTermMaster != null)
				configDTO.setZoyShortTermDtos(convertShortToDTO(shortTermMaster));
			if (forceCheckOut != null)
				configDTO.setZoyForceCheckOutDto(convertToDTO(forceCheckOut));
			if (rentingDuration != null) 
				configDTO.setShortTermRentingDuration(convertToDTO(rentingDuration));
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

	private List<ZoyShortTermDto> convertShortToDTO(List<ZoyPgShortTermMaster> shortTermMaster) {
		List<ZoyShortTermDto> dtos=new ArrayList<>();
		for(ZoyPgShortTermMaster master:shortTermMaster) {
			ZoyShortTermDto dto=new ZoyShortTermDto();
			dto.setShortTermId(master.getZoyPgShortTermMasterId());
			dto.setDays(master.getStartDay()+"-"+master.getEndDay());
			dto.setPercentage(master.getPercentage());
			dtos.add(dto);
		}
		return dtos;
	}

	@Override
	public ResponseEntity<String> zoyAdminCreateUpadateSecurityDepositDeadline(ZoySecurityDeadLine zoySecurityDeadLine) {
		ResponseBody response = new ResponseBody();
		try {
			if (zoySecurityDeadLine == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Security Deposit Refund Rule details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgAutoCancellationMaster existingRule = ownerDBImpl.findSecurityDepositDeadLine(zoySecurityDeadLine.getAutoCancellationId());
			if (existingRule != null) {
				final Long oldFixed=existingRule.getAutoCancellationDay();
				final BigDecimal oldVariable=existingRule.getDeductionPercentage();
				existingRule.setTriggerOn(zoySecurityDeadLine.getTriggerOn());
				existingRule.setTriggerCondition(zoySecurityDeadLine.getTriggerCondition());
				existingRule.setAutoCancellationDay(zoySecurityDeadLine.getAutoCancellationDay());
				existingRule.setDeductionPercentage(zoySecurityDeadLine.getDeductionPercentage());
				existingRule.setCond(zoySecurityDeadLine.getTriggerOn() +" "+ zoySecurityDeadLine.getTriggerCondition() +" "+ zoySecurityDeadLine.getAutoCancellationDay());
				existingRule.setTriggerValue(zoySecurityDeadLine.getTriggerValue());
				ownerDBImpl.saveSecurityDepositDeadLine(existingRule);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Security Deposit DeadLine for");
				if(oldFixed!=zoySecurityDeadLine.getAutoCancellationDay()) {
					historyContent.append(", Cancellation Days For Refund from "+oldFixed+" to "+zoySecurityDeadLine.getAutoCancellationDay());
				}
				if(oldVariable!=zoySecurityDeadLine.getDeductionPercentage()) {
					historyContent.append(" , Deduction Percentage from "+oldVariable+" to "+zoySecurityDeadLine.getDeductionPercentage());
				}

				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoySecurityDepositDeadLineDto dto =convertToDTO(existingRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Security Deposit DeadLine Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgAutoCancellationMaster newRule = new ZoyPgAutoCancellationMaster();
				newRule.setTriggerOn(zoySecurityDeadLine.getTriggerOn());
				newRule.setTriggerCondition(zoySecurityDeadLine.getTriggerCondition());
				newRule.setAutoCancellationDay(zoySecurityDeadLine.getAutoCancellationDay());
				newRule.setDeductionPercentage(zoySecurityDeadLine.getDeductionPercentage());
				newRule.setCond(zoySecurityDeadLine.getTriggerOn() +" "+ zoySecurityDeadLine.getTriggerCondition() +" "+ zoySecurityDeadLine.getAutoCancellationDay());
				newRule.setTriggerValue(zoySecurityDeadLine.getTriggerValue());
				ownerDBImpl.saveSecurityDepositDeadLine(newRule);

				//audit history here
				String historyContent=" has created the Security Deposit DeadLine for, Cancellation Days For Refund = "+newRule.getAutoCancellationDay()+" , Deduction Percentage ="+newRule.getDeductionPercentage();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoySecurityDepositDeadLineDto dto =convertToDTO(newRule);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Created new Security Deposit DeadLine Rule");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Security Deposit DeadLine Rule API:/zoy_admin/config/security-deposit-deadline.zoyAdminCreateUpadateSecurityDepositDeadline ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private ZoySecurityDepositDeadLineDto convertToDTO(ZoyPgAutoCancellationMaster entity) {
		ZoySecurityDepositDeadLineDto dto = new ZoySecurityDepositDeadLineDto();
		dto.setAutoCancellationId(entity.getAutoCancellationId());
		dto.setTriggerOn(entity.getTriggerOn());
		dto.setTriggerCondition(entity.getTriggerCondition());
		dto.setAutoCancellationDay(entity.getAutoCancellationDay());
		dto.setDeductionPercentage(entity.getDeductionPercentage());
		dto.setCond(entity.getTriggerOn() +" "+entity.getTriggerCondition() +" "+ entity.getAutoCancellationDay());
		dto.setTriggerValue(entity.getTriggerValue());
		return dto;
	}

	@Override
	public ResponseEntity<String> getAllTriggeredCond() {
		ResponseBody response = new ResponseBody();
		try {
			List<TriggeredCond> cond=adminDBImpl.findTriggeredCond();
			if(cond.size() > 0)
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
			List<TriggeredOn> cond=adminDBImpl.findTriggeredOn();
			if(cond.size() > 0)
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
			List<TriggeredValue> cond=adminDBImpl.findTriggeredValue();
			if(cond.size() > 0)
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

	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateShortTerm(ZoyShortTermDto shortTerm) {
		ResponseBody response = new ResponseBody();
		try {
			if (shortTerm == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Short Term details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgShortTermMaster zoyPgShortTermMaster = ownerDBImpl.findShortTerm(shortTerm.getShortTermId());
			if (zoyPgShortTermMaster != null) {
				final BigDecimal oldFixed=zoyPgShortTermMaster.getPercentage();
				zoyPgShortTermMaster.setPercentage(shortTerm.getPercentage());
				ownerDBImpl.createShortTerm(zoyPgShortTermMaster);
				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Short Term for Percentage from "+ oldFixed +" to " + shortTerm.getPercentage());
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				List<ZoyPgShortTermMaster> shortTermMaster=ownerDBImpl.findAllShortTerm();

				List<ZoyShortTermDto> dto =convertShortToDTO(shortTermMaster);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Short Term");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get Short term id " + shortTerm.getShortTermId());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Short Term API:/zoy_admin/config/short-term.zoyAdminConfigUpdateShortTerm ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateForceCheckOut(ZoyForceCheckOutDto forceCheckOut) {

		ResponseBody response=new ResponseBody();
		try {
			if(forceCheckOut==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Force Checkout details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgForceCheckOut force=ownerDBImpl.findZoyForceCheckOut();
			if(force!=null) {
				final int oldFixed=force.getForceCheckOutDays();
				force.setForceCheckOutDays(forceCheckOut.getForceCheckOutDays());
				ownerDBImpl.saveForceCheckOut(force);

				//audit history here
				String historyContent=" has updated the Force Check Out for, Considering days from "+oldFixed+" to "+forceCheckOut.getForceCheckOutDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyForceCheckOutDto dto=convertToDTO(force);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Force Check Out details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgForceCheckOut newForceCheckOut=new ZoyPgForceCheckOut();
				newForceCheckOut.setForceCheckOutDays(forceCheckOut.getForceCheckOutDays());
				ownerDBImpl.saveForceCheckOut(newForceCheckOut);
				//audit history here
				String historyContent=" has created the Force Check Out for, Considering days = "+forceCheckOut.getForceCheckOutDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyForceCheckOutDto dto=convertToDTO(newForceCheckOut);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Force Check Out details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Force Check Out details API:/zoy_admin/config/force-checkout.zoyAdminConfigUpdateForceCheckOut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}
	private ZoyForceCheckOutDto convertToDTO(ZoyPgForceCheckOut entity) {
		ZoyForceCheckOutDto dto = new ZoyForceCheckOutDto();
		dto.setForceCheckOutId(entity.getForceCheckOutId());
		dto.setForceCheckOutDays(entity.getForceCheckOutDays());
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
				ownerDBImpl.createOrUpdateCompanyProfile(zoyCompanyProfileMaster);

				// Audit history here
				StringBuffer historyContent = new StringBuffer(" has updated the Company Profile for " + companyProfile.getAddressLineOne()+" this address");
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

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

				ownerDBImpl.createOrUpdateCompanyProfile(newZoyCompanyProfile);

				// Audit history here
				StringBuffer historyContent = new StringBuffer(" has created a new " + companyProfile.getType());
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				List<ZoyCompanyProfileMaster> companyProfiles = ownerDBImpl.findAllCompanyProfiles();
				List<ZoyCompanyProfileMasterDto> dto = convertsToDTO(companyProfiles);

				response.setStatus(HttpStatus.CREATED.value());
				response.setData(dto);
				response.setMessage("Created Company Profile");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
			}

		} catch (Exception e) {
			log.error("Error saving/updating Company Profile API:/zoy_admin/config/company-profile.zoyAdminConfigUpdateCompany", e);
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
		}catch (Exception e) {
			log.error("Error while Fetching all  Company Profile details API:/zoy_admin/config/fetch-company-profiles.zoyAdminCompanyProfiles", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminConfigShortTermRentingDuration(ZoyRentingDuration rentingDuration) {
		ResponseBody response=new ResponseBody();
		try {
			if(rentingDuration==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Short Term Renting Duration Details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyPgShortTermRentingDuration force = null;
			if (rentingDuration.getRentingDurationId() != null) {
				force=ownerDBImpl.findZoyRentingDuration();
			}

			if(force!=null) {
				final int oldFixed=force.getRentingDurationDays();
				force.setRentingDurationDays(rentingDuration.getRentingDurationDays());
				ownerDBImpl.saveRentingDuration(force);

				//audit history here
				String historyContent=" has updated the Renting Duration  for, Considering days from "+oldFixed+" to "+rentingDuration.getRentingDurationDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);

				ZoyRentingDuration dto=convertToDTO(force);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Updated Short Term Renting Duration  details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgShortTermRentingDuration newRentingDuration=new ZoyPgShortTermRentingDuration();
				newRentingDuration.setRentingDurationDays(rentingDuration.getRentingDurationDays());
				ownerDBImpl.saveRentingDuration(newRentingDuration);
				//audit history here
				String historyContent=" has created the Short Term Renting Duration for, Considering days = "+rentingDuration.getRentingDurationDays();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);

				ZoyRentingDuration dto=convertToDTO(newRentingDuration);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Short Term Renting Duration details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Short Term Renting Duration API:/zoy_admin/config/renting-duration.zoyAdminConfigShortTermRentingDuration ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private ZoyRentingDuration convertToDTO(ZoyPgShortTermRentingDuration force) {
		ZoyRentingDuration rentingDuration = new ZoyRentingDuration();
		rentingDuration.setRentingDurationId(force.getRentingDurationId());
		rentingDuration.setRentingDurationDays(force.getRentingDurationDays());
		return rentingDuration;
	}

	@Override
	public ResponseEntity<String> zoyAdminRemoveCompanyProfile(String profileId) {
		ResponseBody response = new ResponseBody();
		try {
			ownerDBImpl.deleteCompanyProfile(profileId);
			//audit history here
			String historyContent=" has Deleted the Company Profile Details ";
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_DELETE);
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Deleted  Company Profile details");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.CREATED);
		}catch (Exception e) {
			log.error("Error while Deleting   Company Profile details API:/zoy_admin/config/remove-company-profiles.zoyAdminRemoveCompanyProfile", e);
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
	public ResponseEntity<String> zoyAdminCompanyProfileMaster(String  data,
			MultipartFile companyLogo) {
		ResponseBody response = new ResponseBody();
		
		try {
			ZoyCompanyMasterModal companyMaster=gson2.fromJson(data,ZoyCompanyMasterModal.class);
			if (companyMaster == null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			ZoyCompanyMaster company=ownerDBImpl.findcompanyMaster();
			if(company!= null) {
				byte[] profilePictureBase64 = pdfGenerateService.imageToBytes(companyLogo.getInputStream());
				company.setCompanyLogo(profilePictureBase64);
				company.setCompanyName(companyMaster.getCompanyName());
				company.setGstNumber(companyMaster.getGstNumber());
				company.setPanNumber(companyMaster.getPanNumber());
				company.setUrl(companyMaster.getUrl());
				ZoyCompanyMaster companyDetails=ownerDBImpl.saveCompanyMaster(company);

				String historyContent=" has Updated Master Company Profile Details ";
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyCompanyMasterDto dto =convertToDTO(companyDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}else {
				ZoyCompanyMaster newCompanyMaster= new ZoyCompanyMaster();
				newCompanyMaster.setCompanyName(companyMaster.getCompanyName());
				byte[] profilePictureBase64 = pdfGenerateService.imageToBytes(companyLogo.getInputStream());
				newCompanyMaster.setCompanyLogo(profilePictureBase64);
				newCompanyMaster.setGstNumber(companyMaster.getGstNumber());
				newCompanyMaster.setPanNumber(companyMaster.getPanNumber());
				newCompanyMaster.setUrl(companyMaster.getUrl());
				ZoyCompanyMaster companyDetails=ownerDBImpl.saveCompanyMaster(newCompanyMaster);

				String historyContent=" has Updated Master Company Profile Details ";
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
				
				ZoyCompanyMasterDto dto =convertToDTO(companyDetails);
				response.setStatus(HttpStatus.OK.value());
				response.setData(dto);
				response.setMessage("Saved Company Master details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Master company details API:/zoy_admin/config/company-master-profile.zoyAdminCompanyProfileMaster", e);
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
		}catch (Exception e) {
			log.error("Error while Fetching all  Company Profile details API:/zoy_admin/config/fetch-master-profile.fetchCompanyProfileMaster", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
}