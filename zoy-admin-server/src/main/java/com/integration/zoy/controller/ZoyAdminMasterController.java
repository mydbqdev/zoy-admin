package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
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
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgDueMaster;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.model.Amenetie;
import com.integration.zoy.model.AmenetiesId;
import com.integration.zoy.model.AuditActivitiesLogDTO;
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
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.DueMaster;
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
			log.error("Error getting ameneties details API:/zoy_admin/ameneties.zoyAdminAmenities ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting ameneties details API:/zoy_admin/ameneties.zoyAdminAmenitiesPost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating ameneties details API:/zoy_admin/ameneties.zoyAdminAmenitiesPut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting factor details API:/zoy_admin/factor.zoyAdminFactor ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting factor details API:/zoy_admin/factor.zoyAdminFactorPost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating factor details API:/zoy_admin/factor.zoyAdminFactorPut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting rent cycle details API:/zoy_admin/rentCycle.zoyAdminRentCycle ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting rent cycle details API:/zoy_admin/rentCycle.zoyAdminRentCyclePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating rent cycle details API:/zoy_admin/rentCycle.zoyAdminRentCyclePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting room type details API:/zoy_admin/roomType.zoyAdminRoomType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting room type details API:/zoy_admin/roomType.zoyAdminRoomTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating room type details API:/zoy_admin/roomType.zoyAdminRoomTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting share type details API:/zoy_admin/shareType.zoyAdminShareType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting share type details API:/zoy_admin/shareType.zoyAdminShareTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating share type details API:/zoy_admin/shareType.zoyAdminShareTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting pg type details API:/zoy_admin/pgType.zoyAdminPgType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting pg type details API:/zoy_admin/pgType.zoyAdminPgTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating pg type details API:/zoy_admin/pgType.zoyAdminPgTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting notification mode details API:/zoy_admin/notification_mode.zoyAdminNotificationModeName ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting notification mode details API:/zoy_admin/notification_mode.zoyAdminNotificationModeNamePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating notification mode details API:/zoy_admin/notification_mode.zoyAdminNotificationModeNamePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting billing type details API:/zoy_admin/billingType.zoyAdminBillingType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting billing type details API:/zoy_admin/billingType.zoyAdminBillingTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating billing type details API:/zoy_admin/billingType.zoyAdminBillingTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting currency type details API:/zoy_admin/currencyType.zoyAdminCurrencyType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting currency type details API:/zoy_admin/currencyType.zoyAdminCurrencyTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating currency type details API:/zoy_admin/currencyType.zoyAdminCurrencyTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	//Due Type
	@Override
	public ResponseEntity<String> zoyAdminDueType() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgDueMaster> userDueMasters =  ownerDBImpl.findAllDueMaster();
			List<DueMaster> dueMasters = userDueMasters.stream()
			        .map(zoyPgDueMaster -> {
			            DueMaster dueMaster = new DueMaster();
			            dueMaster.setDueTypeId(zoyPgDueMaster.getDueTypeId());
			            dueMaster.setDueTypeName(zoyPgDueMaster.getDueName());
			            return dueMaster;
			        })
			        .collect(Collectors.toList());
			return new ResponseEntity<>(gson2.toJson(dueMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting due type details API:/zoy_admin/dueType.zoyAdminDueType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminDueTypePost(DueType dueType) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgDueMaster userDueMasters =  new ZoyPgDueMaster();
			userDueMasters.setDueName(dueType.getDueTypeName());
			ZoyPgDueMaster saved=ownerDBImpl.saveUserDueMaster(userDueMasters);
			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting due type details API:/zoy_admin/dueType.zoyAdminDueTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> zoyAdminDueTypePut(DueTypeId dueTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgDueMaster userDueMasters =  ownerDBImpl.findUserDueMaster(dueTypeId.getId());
			if(userDueMasters!=null) {
				userDueMasters.setDueName(dueTypeId.getDueTypeName());
				ZoyPgDueMaster updated=ownerDBImpl.updateDueMaster(userDueMasters);
				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Due type for the given Id " + dueTypeId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating due type details API:/zoy_admin/dueType.zoyAdminDueTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error getting ekyc type details API:/zoy_admin/ekycType.zoyAdminEkycType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error posting ekyc type details API:/zoy_admin/ekycType.zoyAdminEkycTypePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
			log.error("Error updating ekyc type details API:/zoy_admin/ekycType.zoyAdminEkycTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
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
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError("Database error: Unable to fetch owner details");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        
	    }catch (Exception e) {
	        log.error("Unexpected error occurredAPI:/zoy_admin/manage-owners.zoyPgOwnerDetails", e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}
	
	   //my Audit Activities log Details
		@Override
		public ResponseEntity<String> zoyAuditActivitiesLogDetails(OwnerLeadPaginationRequest paginationRequest) {
			ResponseBody response = new ResponseBody();
			try {
				CommonResponseDTO<AuditActivitiesLogDTO> auditActivitiesLogList = userDBImpl.getAuditActivitiesLogCount(paginationRequest);
		        return new ResponseEntity<>(gson2.toJson(auditActivitiesLogList), HttpStatus.OK);
	 		}catch (Exception e) {
		        log.error("Unexpected error occurredAPI:/zoy_admin/audit-activitieslog.auditactivitieslogdetails", e);
		        response.setStatus(HttpStatus.BAD_REQUEST.value());
		        response.setError(e.getMessage());
		        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		    }
		}
}
