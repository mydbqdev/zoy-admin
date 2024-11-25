package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserDueMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.model.Amenetie;
import com.integration.zoy.model.AmenetiesId;
import com.integration.zoy.model.BillingType;
import com.integration.zoy.model.BillingTypeId;
import com.integration.zoy.model.CurrencyType;
import com.integration.zoy.model.CurrencyTypeId;
import com.integration.zoy.model.DueFactor;
import com.integration.zoy.model.DueFactorId;
import com.integration.zoy.model.DueType;
import com.integration.zoy.model.DueTypeId;
import com.integration.zoy.model.EkycType;
import com.integration.zoy.model.EkycTypeId;
import com.integration.zoy.model.NotificationMode;
import com.integration.zoy.model.NotificationModeId;
import com.integration.zoy.model.OwnerPropertyDTO;
import com.integration.zoy.model.PgType;
import com.integration.zoy.model.PgTypeId;
import com.integration.zoy.model.RentCycle;
import com.integration.zoy.model.RentCycleId;
import com.integration.zoy.model.RoomType;
import com.integration.zoy.model.RoomTypeId;
import com.integration.zoy.model.ShareType;
import com.integration.zoy.model.ShareTypeId;
import com.integration.zoy.service.CommonDBImpl;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.UserDBImpl;
import com.integration.zoy.utils.OwnerLeadPaginationRequest;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class ZoyAdminMasterController implements ZoyAdminMasterImpl {

	private static final Logger log = LoggerFactory.getLogger(ZoyAdminMasterController.class);
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
	CommonDBImpl commonDBImpl;

	@Autowired
	UserDBImpl userDBImpl;

	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Override
	public ResponseEntity<String> zoyAdminAmenities() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgAmenetiesMaster> amenetiesMaster =  ownerDBImpl.getAllAmeneties();
			return new ResponseEntity<>(gson2.toJson(amenetiesMaster), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ameneties details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminAmenitiesPost(Amenetie amenetie) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgAmenetiesMaster zoyPgAmenetiesMaster=new ZoyPgAmenetiesMaster();
			zoyPgAmenetiesMaster.setAmenetiesName(amenetie.getAmeneties());
			ZoyPgAmenetiesMaster saved=ownerDBImpl.createAmeneties(zoyPgAmenetiesMaster);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting ameneties details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminAmenitiesPut(AmenetiesId amenetie) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgAmenetiesMaster zoyPgAmenetiesMaster=ownerDBImpl.findAmeneties(amenetie.getId());
			if(zoyPgAmenetiesMaster!=null) {
				zoyPgAmenetiesMaster.setAmenetiesName(amenetie.getAmeneties());
				ZoyPgAmenetiesMaster updated=ownerDBImpl.createAmeneties(zoyPgAmenetiesMaster);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Ameneties for the given Id " + amenetie.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating ameneties details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Due Factor
	@Override
	public ResponseEntity<String> zoyAdminFactor() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgDueFactorMaster> zoyPgDueFactorMasters =  ownerDBImpl.getAllDueFactors();
			return new ResponseEntity<>(gson2.toJson(zoyPgDueFactorMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting factor details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminFactorPost(DueFactor dueFactor) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgDueFactorMaster zoyPgDueFactorMasters =  new ZoyPgDueFactorMaster();
			zoyPgDueFactorMasters.setFactorName(dueFactor.getFactorName());
			ZoyPgDueFactorMaster saved= ownerDBImpl.createDueFactor(zoyPgDueFactorMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting factor details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminFactorPut(DueFactorId dueFactorId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgDueFactorMaster zoyPgDueFactorMaster=ownerDBImpl.findDueFactor(dueFactorId.getId());
			if(zoyPgDueFactorMaster!=null) {
				zoyPgDueFactorMaster.setFactorName(dueFactorId.getFactorName());
				ZoyPgDueFactorMaster updated=ownerDBImpl.updateDueFactor(zoyPgDueFactorMaster);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Due Factor for the given Id " + dueFactorId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating factor details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Rent Cycle
	@Override
	public ResponseEntity<String> zoyAdminRentCycle() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgRentCycleMaster> zoyPgRentCycleMasters =  ownerDBImpl.getAllRentCycle();
			return new ResponseEntity<>(gson2.toJson(zoyPgRentCycleMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting rent cycle details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminRentCyclePost(RentCycle rentCycle) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgRentCycleMaster zoyPgRentCycleMasters =  new ZoyPgRentCycleMaster();
			zoyPgRentCycleMasters.setCycleName(rentCycle.getRentCycleName());
			ZoyPgRentCycleMaster saved=ownerDBImpl.saveRentCycle(zoyPgRentCycleMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting rent cycle details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminRentCyclePut(RentCycleId rentCycleId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgRentCycleMaster zoyPgRentCycleMasters =  ownerDBImpl.findRentCycle(rentCycleId.getId());
			if(zoyPgRentCycleMasters!=null) {
				zoyPgRentCycleMasters.setCycleName(rentCycleId.getRentCycleName());
				ZoyPgRentCycleMaster updated=ownerDBImpl.updateRentCycle(zoyPgRentCycleMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Rent Cycle for the given Id " + rentCycleId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating rent cycle details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Room Type
	@Override
	public ResponseEntity<String> zoyAdminRoomType() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgRoomTypeMaster> zoyPgRoomTypeMasters =  ownerDBImpl.getAllRoomTypes();
			return new ResponseEntity<>(gson2.toJson(zoyPgRoomTypeMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting room type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminRoomTypePost(RoomType roomType) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgRoomTypeMaster zoyPgRoomTypeMasters =  new ZoyPgRoomTypeMaster();
			zoyPgRoomTypeMasters.setRoomTypeName(roomType.getRoomTypeName());
			ZoyPgRoomTypeMaster saved=ownerDBImpl.createRoomType(zoyPgRoomTypeMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting room type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@Override
	public ResponseEntity<String> zoyAdminRoomTypePut(RoomTypeId roomTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgRoomTypeMaster zoyPgRoomTypeMasters =  ownerDBImpl.findRoomType(roomTypeId.getId());
			if(zoyPgRoomTypeMasters!=null) {
				zoyPgRoomTypeMasters.setRoomTypeName(roomTypeId.getRoomTypeName());
				ZoyPgRoomTypeMaster updated=ownerDBImpl.updateRoomType(zoyPgRoomTypeMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Rent Cycle for the given Id " + roomTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating room type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Share Type
	@Override
	public ResponseEntity<String> zoyAdminShareType() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgShareMaster> zoyPgShareMasters =  ownerDBImpl.getAllShares();
			return new ResponseEntity<>(gson2.toJson(zoyPgShareMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting share type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminShareTypePost(ShareType shareType) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgShareMaster zoyPgShareMasters = new ZoyPgShareMaster();
			zoyPgShareMasters.setShareType(shareType.getShareType());
			zoyPgShareMasters.setShareOccupancyCount(shareType.getShareOccupancyCount());
			ZoyPgShareMaster saved=ownerDBImpl.createShare(zoyPgShareMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting share type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminShareTypePut(ShareTypeId shareTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgShareMaster zoyPgShareMasters =  ownerDBImpl.getShareById(shareTypeId.getId());
			if(zoyPgShareMasters!=null) {
				zoyPgShareMasters.setShareType(shareTypeId.getShareType());
				zoyPgShareMasters.setShareOccupancyCount(shareTypeId.getShareOccupancyCount());
				ZoyPgShareMaster updated=ownerDBImpl.updateShare(zoyPgShareMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Rent Cycle for the given Id " + shareTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating share type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Pg Type
	@Override
	public ResponseEntity<String> zoyAdminPgType() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgTypeMaster> zoyPgTypeMasters =  ownerDBImpl.getAllPgTypes();
			return new ResponseEntity<>(gson2.toJson(zoyPgTypeMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting pg type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminPgTypePost(PgType pgType) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgTypeMaster zoyPgTypeMasters =  new ZoyPgTypeMaster();
			zoyPgTypeMasters.setPgTypeName(pgType.getPgTypeName());
			ZoyPgTypeMaster saved=ownerDBImpl.createPgType(zoyPgTypeMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting pg type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminPgTypePut(PgTypeId pgTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgTypeMaster zoyPgTypeMasters =  ownerDBImpl.getPgTypeById(pgTypeId.getId());
			if(zoyPgTypeMasters!=null) {
				zoyPgTypeMasters.setPgTypeName(pgTypeId.getPgTypeName());
				ZoyPgTypeMaster updated=ownerDBImpl.updatePgType(zoyPgTypeMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Rent Cycle for the given Id " + pgTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating pg type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Notification Mode
	@Override
	public ResponseEntity<String> zoyAdminNotificationModeName() {
		ResponseBody response=new ResponseBody();
		try {
			List<NotificationModeMaster> notificationModeMasters =  userDBImpl.findAllNotificationMode();
			return new ResponseEntity<>(gson2.toJson(notificationModeMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting notification mode details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminNotificationModeNamePost(NotificationMode notificationMode) {
		ResponseBody response=new ResponseBody();
		try {
			NotificationModeMaster notificationModeMasters = new NotificationModeMaster();
			notificationModeMasters.setNotificationModName(notificationMode.getNotificationModeName());
			NotificationModeMaster saved=userDBImpl.saveNotificationMode(notificationModeMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting notification mode details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminNotificationModeNamePut(NotificationModeId notificationModeId) {
		ResponseBody response=new ResponseBody();
		try {
			NotificationModeMaster notificationModeMasters = userDBImpl.findNotificationMode(notificationModeId.getId());
			if(notificationModeMasters!=null) {
				notificationModeMasters.setNotificationModName(notificationModeId.getNotificationModeName());
				NotificationModeMaster updated=userDBImpl.updateNotificationMode(notificationModeMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Notification mode for the given Id " + notificationModeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating notification mode details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Billing type
	@Override
	public ResponseEntity<String> zoyAdminBillingType() {
		ResponseBody response=new ResponseBody();
		try {
			List<UserBillingMaster> userBillingMasters =  userDBImpl.findAllUserBillingMaster();
			return new ResponseEntity<>(gson2.toJson(userBillingMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting billing type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminBillingTypePost(BillingType billingType) {
		ResponseBody response=new ResponseBody();
		try {
			UserBillingMaster userBillingMasters =  new UserBillingMaster();
			userBillingMasters.setBillingTypeName(billingType.getBillingTypeName());
			UserBillingMaster saved=userDBImpl.saveUserBillingMaster(userBillingMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting billing type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminBillingTypePut(BillingTypeId billingType) {
		ResponseBody response=new ResponseBody();
		try {
			UserBillingMaster userBillingMasters =  userDBImpl.findUserBillingMaster(billingType.getId());
			if(userBillingMasters!=null) {
				userBillingMasters.setBillingTypeName(billingType.getBillingTypeName());
				UserBillingMaster updated=userDBImpl.updateUserBillingMaster(userBillingMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Billing type for the given Id " + billingType.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating billing type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Currency Type
	@Override
	public ResponseEntity<String> zoyAdminCurrencyType() {
		ResponseBody response=new ResponseBody();
		try {
			List<UserCurrencyMaster> userCurrencyMasters =  userDBImpl.findAllUserCurrency();
			return new ResponseEntity<>(gson2.toJson(userCurrencyMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting currency type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminCurrencyTypePost(CurrencyType currencyType) {
		ResponseBody response=new ResponseBody();
		try {
			UserCurrencyMaster userCurrencyMasters = new UserCurrencyMaster();
			userCurrencyMasters.setCurrencyName(currencyType.getCurrencyName());
			UserCurrencyMaster saved=userDBImpl.saveUserCurrency(userCurrencyMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting currency type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminCurrencyTypePut(CurrencyTypeId currencyTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			UserCurrencyMaster userCurrencyMasters =  userDBImpl.findCurrency(currencyTypeId.getId());
			if(userCurrencyMasters!=null) {
				userCurrencyMasters.setCurrencyName(currencyTypeId.getCurrencyName());
				UserCurrencyMaster updated=userDBImpl.updateUserCurrency(userCurrencyMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Currency type for the given Id " + currencyTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating currency type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Due Type
	@Override
	public ResponseEntity<String> zoyAdminDueType() {
		ResponseBody response=new ResponseBody();
		try {
			List<UserDueMaster> userDueMasters =  userDBImpl.findAllUserDueMaster();
			return new ResponseEntity<>(gson2.toJson(userDueMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting due type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminDueTypePost(DueType dueType) {
		ResponseBody response=new ResponseBody();
		try {
			UserDueMaster userDueMasters =  new UserDueMaster();
			userDueMasters.setDueTypeName(dueType.getDueTypeName());
			UserDueMaster saved=userDBImpl.saveUserDueMaster(userDueMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting due type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminDueTypePut(DueTypeId dueTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			UserDueMaster userDueMasters =  userDBImpl.findUserDueMaster(dueTypeId.getId());
			if(userDueMasters!=null) {
				userDueMasters.setDueTypeName(dueTypeId.getDueTypeName());
				UserDueMaster updated=userDBImpl.updateUserDueMaster(userDueMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Due type for the given Id " + dueTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating due type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	//Ekyc Type
	@Override
	public ResponseEntity<String> zoyAdminEkycType() {
		ResponseBody response=new ResponseBody();
		try {
			List<UserEkycTypeMaster> userEkycTypeMasters =  userDBImpl.findAllEkycTypes();
			return new ResponseEntity<>(gson2.toJson(userEkycTypeMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ekyc type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminEkycTypePost(EkycType ekycType) {
		ResponseBody response=new ResponseBody();
		try {
			UserEkycTypeMaster userEkycTypeMasters =  new UserEkycTypeMaster();
			userEkycTypeMasters.setUserEkycTypeName(ekycType.getUserEkycTypeName());
			UserEkycTypeMaster saved=userDBImpl.createEkycType(userEkycTypeMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting ekyc type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminEkycTypePut(EkycTypeId ekycTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			UserEkycTypeMaster userEkycTypeMasters =  userDBImpl.findEkycTypes(ekycTypeId.getId());
			if(userEkycTypeMasters!=null) {
				userEkycTypeMasters.setUserEkycTypeName(ekycTypeId.getUserEkycTypeName());
				UserEkycTypeMaster updated=userDBImpl.updateEkycType(userEkycTypeMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Ekyc type for the given Id " + ekycTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating ekyc type details: " + e.getMessage(),e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("Internal server error");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public ResponseEntity<String> zoyPgOwnerDetails(OwnerLeadPaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			Page<OwnerPropertyDTO> ownerPropertyList = userDBImpl.findAllOwnerWithPropertyCount( paginationRequest);
	            return new ResponseEntity<>(gson2.toJson(ownerPropertyList), HttpStatus.OK);
		}catch (DataAccessException dae) {
	        log.error("Database error occurred while fetching owner details: " + dae.getMessage(), dae);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError("Database error: Unable to fetch owner details");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	        
	    }catch (Exception e) {
	        log.error("Unexpected error occurred: " + e.getMessage(), e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError("An unexpected error occurred while fetching owner details");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}

}
