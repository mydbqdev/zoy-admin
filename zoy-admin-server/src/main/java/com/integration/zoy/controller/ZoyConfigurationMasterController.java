package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
import com.integration.zoy.entity.BulkUploadDetails;
import com.integration.zoy.entity.ZoyDataGrouping;
import com.integration.zoy.entity.ZoyPgCancellationDetails;
import com.integration.zoy.entity.ZoyPgOtherCharges;
import com.integration.zoy.entity.ZoyPgTokenDetails;
import com.integration.zoy.entity.ZoyShareMaster;
import com.integration.zoy.model.ZoyBeforeCheckInCancellation;
import com.integration.zoy.model.ZoyOtherCharges;
import com.integration.zoy.model.ZoyShareDetails;
import com.integration.zoy.model.ZoyToken;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.utils.BulkUploadData;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class ZoyConfigurationMasterController implements ZoyConfigurationMasterImpl{

	private static final Logger log = LoggerFactory.getLogger(ZoyConfigurationMasterController.class);
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
	private static final Gson gson2 = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

	@Autowired
	OwnerDBImpl ownerDBImpl;


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateToken(ZoyToken details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if(details.getTokenId()!=null && !details.getTokenId().isEmpty()) {
				ZoyPgTokenDetails tokenDetails=ownerDBImpl.findTokenDetails(details.getTokenId());
				if(tokenDetails==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get token details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				tokenDetails.setFixedToken(details.getFixedToken()!=0?new BigDecimal(details.getFixedToken()):BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken()!=0?new BigDecimal(details.getVariableToken()):BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Updated Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgTokenDetails tokenDetails=new ZoyPgTokenDetails();
				tokenDetails.setFixedToken(details.getFixedToken()!=0?new BigDecimal(details.getFixedToken()):BigDecimal.ZERO);
				tokenDetails.setVariableToken(details.getVariableToken()!=0?new BigDecimal(details.getVariableToken()):BigDecimal.ZERO);
				ownerDBImpl.saveToken(tokenDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetToken() {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgTokenDetails tokenDetails=ownerDBImpl.findAllToken().get(0);
			if(tokenDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get token details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyToken token=new ZoyToken();
			token.setTokenId(tokenDetails.getTokenId());
			token.setVariableToken(tokenDetails.getVariableToken()!=null?tokenDetails.getVariableToken().doubleValue():0.0);
			token.setFixedToken(tokenDetails.getFixedToken()!=null?tokenDetails.getFixedToken().doubleValue():0.0);
			return new ResponseEntity<>(gson.toJson(token), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
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
				cancelDetails.setCancellationFixedCharges(details.getCancellationFixedCharges()!=0?new BigDecimal(details.getCancellationFixedCharges()):BigDecimal.ZERO);
				cancelDetails.setCancellationVariableCharges(details.getCancellationVariableCharges()!=0?new BigDecimal(details.getCancellationVariableCharges()):BigDecimal.ZERO);
				cancelDetails.setCancellationDays(null);
				ownerDBImpl.saveBeforeCancellation(cancelDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Updated Before CheckIn details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgCancellationDetails cancelDetails=new ZoyPgCancellationDetails();
				cancelDetails.setCancellationFixedCharges(details.getCancellationFixedCharges()!=0?new BigDecimal(details.getCancellationFixedCharges()):BigDecimal.ZERO);
				cancelDetails.setCancellationVariableCharges(details.getCancellationVariableCharges()!=0?new BigDecimal(details.getCancellationVariableCharges()):BigDecimal.ZERO);
				cancelDetails.setCancellationDays(null);
				ownerDBImpl.saveBeforeCancellation(cancelDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Before CheckIn details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error uploading property details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetBeforeCheckIn() {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgCancellationDetails cancellationDetails=ownerDBImpl.findAllBeforeCancellation().get(0);
			if(cancellationDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyBeforeCheckInCancellation cancellation=new ZoyBeforeCheckInCancellation();
			cancellation.setCancellationId(cancellationDetails.getCancellationId());
			cancellation.setCancellationVariableCharges(cancellationDetails.getCancellationVariableCharges()!=null?cancellationDetails.getCancellationVariableCharges().doubleValue():0.0);
			cancellation.setCancellationFixedCharges(cancellationDetails.getCancellationFixedCharges()!=null?cancellationDetails.getCancellationFixedCharges().doubleValue():0.0);
			cancellation.setCancellationDays(cancellationDetails.getCancellationDays());
			return new ResponseEntity<>(gson.toJson(cancellation), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting Before CheckIn details: " + e.getMessage(),e);
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
				response.setError("Updated Zoy Share details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyShareMaster shareDetails=new ZoyShareMaster();
				shareDetails.setPropertyShare(new BigDecimal(details.getPropertyShare()));
				shareDetails.setZoyShare(new BigDecimal(details.getZoyShare()));
				ownerDBImpl.saveZoyShare(shareDetails);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Zoy Share details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating zoy share details: " + e.getMessage(),e);
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
			log.error("Error Getting zoy share details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}


	@Override
	public ResponseEntity<String> zoyAdminConfigCreateUpdateOtherCharges(ZoyOtherCharges details) {
		ResponseBody response=new ResponseBody();
		try {
			if(details==null) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setError("Required cancellation details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			if(details.getOtherChargesId()!=null && !details.getOtherChargesId().isEmpty()) {
				ZoyPgOtherCharges other=ownerDBImpl.findZoyOtherCharges(details.getOtherChargesId());
				if(other==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get Other Charges details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				other.setDocumentCharges(new BigDecimal(details.getDocumentCharges()));
				other.setOtherGst(new BigDecimal(details.getOtherGst()));
				ownerDBImpl.saveOtherCharges(other);
				response.setStatus(HttpStatus.OK.value());
				response.setError("Updated Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyPgOtherCharges other=new ZoyPgOtherCharges();
				other.setDocumentCharges(new BigDecimal(details.getDocumentCharges()));
				other.setOtherGst(new BigDecimal(details.getOtherGst()));
				ownerDBImpl.saveOtherCharges(other);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/creating Other Charges details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}



	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetOtherCharges() {

		ResponseBody response=new ResponseBody();
		try {
			ZoyPgOtherCharges otherDetails=ownerDBImpl.findAllOtherCharges().get(0);
			if(otherDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get Other Charges details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyOtherCharges other=new ZoyOtherCharges();
			other.setOtherChargesId(otherDetails.getOtherChargesId());
			other.setDocumentCharges(otherDetails.getDocumentCharges().doubleValue());
			other.setOtherGst(otherDetails.getOtherGst().doubleValue());
			return new ResponseEntity<>(gson.toJson(other), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting Other Charges details: " + e.getMessage(),e);
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
			if(details.getDataGroupingId()!=null && !details.getDataGroupingId().isEmpty()) {
				ZoyDataGrouping group=ownerDBImpl.findZoyDataGroup(details.getDataGroupingId());
				if(group==null) {
					response.setStatus(HttpStatus.CONFLICT.value());
					response.setError("Unable to get Data Grouping details");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
				}
				group.setDataGroupingName(details.getDataGroupingName());
				ownerDBImpl.saveDataGroup(group);
				response.setStatus(HttpStatus.OK.value());
				response.setError("Updated Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				ZoyDataGrouping group=new ZoyDataGrouping();
				group.setDataGroupingName(details.getDataGroupingName());
				ownerDBImpl.saveDataGroup(group);

				response.setStatus(HttpStatus.OK.value());
				response.setError("Saved Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error saving/updating Data Grouping details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@Override
	public ResponseEntity<String> zoyAdminConfigGetDataGrouping() {
		ResponseBody response=new ResponseBody();
		try {
			ZoyDataGrouping groupDetails=ownerDBImpl.findAllDataGroup().get(0);
			if(groupDetails==null) {
				response.setStatus(HttpStatus.CONFLICT.value());
				response.setError("Unable to get Data Grouping details");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.CONFLICT);
			}
			ZoyDataGrouping group=new ZoyDataGrouping();
			group.setDataGroupingId(groupDetails.getDataGroupingId());
			group.setDataGroupingName(groupDetails.getDataGroupingName());
			return new ResponseEntity<>(gson.toJson(group), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting Data Grouping details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
