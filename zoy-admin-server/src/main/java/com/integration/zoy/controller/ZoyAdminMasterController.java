package com.integration.zoy.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.integration.zoy.entity.NotificationModeMaster;
import com.integration.zoy.entity.RentalAgreementDoc;
import com.integration.zoy.entity.UserBillingMaster;
import com.integration.zoy.entity.UserCurrencyMaster;
import com.integration.zoy.entity.UserEkycTypeMaster;
import com.integration.zoy.entity.ZoyPgAmenetiesMaster;
import com.integration.zoy.entity.ZoyPgDueFactorMaster;
import com.integration.zoy.entity.ZoyPgDueMaster;
import com.integration.zoy.entity.ZoyPgDueTypeMaster;
import com.integration.zoy.entity.ZoyPgFloorNameMaster;
import com.integration.zoy.entity.ZoyPgRentCycleMaster;
import com.integration.zoy.entity.ZoyPgRoomTypeMaster;
import com.integration.zoy.entity.ZoyPgShareMaster;
import com.integration.zoy.entity.ZoyPgShortTermMaster;
import com.integration.zoy.entity.ZoyPgTypeMaster;
import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.exception.ZoyAdminApplicationException;
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
import com.integration.zoy.model.FloorName;
import com.integration.zoy.model.FloorNameId;
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
import com.integration.zoy.model.ShortTerm;
import com.integration.zoy.model.TotalBookingsDetails;
import com.integration.zoy.model.UserNameDTO;
import com.integration.zoy.repository.RentalAgreementDocRepository;
import com.integration.zoy.repository.UserBookingsRepository;
import com.integration.zoy.service.CommonDBImpl;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.UserDBImpl;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.service.ZoyS3Service;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.DueMaster;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.RentalAgreementDocDto;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.UserPaymentFilterRequest;

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

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;
	
	@Autowired
	private UserBookingsRepository userBookings;

	@Autowired
	ZoyS3Service zoyS3Service;
	
	@Autowired
	ZoyAdminService zoyAdminService;


	@Value("${app.minio.Amenities.photos.bucket.name}")
	private String amenitiesPhotoBucketName;
	
	@Autowired
	RentalAgreementDocRepository rentalAgreementDocRepository;
	

	@Value("${app.minio.zoypg.upload.rental.agreement.bucket.name}")
	private String zoyPgRentalDocsUploadBucketName;

	@Override
	public ResponseEntity<String> zoyAdminAmenities() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgAmenetiesMaster> amenetiesMaster =  ownerDBImpl.getAllAmeneties();
			for (ZoyPgAmenetiesMaster amenity : amenetiesMaster) {
			    if (amenity.getAmenetiesImage() != null && !amenity.getAmenetiesImage().trim().isEmpty()) {
			        amenity.setAmenetiesImage(zoyAdminService.generatePreSignedUrl(amenitiesPhotoBucketName, amenity.getAmenetiesImage()));
			    }
			}
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

			if (amenetie.getAmenetiesImage() == null || amenetie.getAmenetiesImage().isEmpty()) { 
				response.setMessage("No image is provided");
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST); 
			}

			String imageUrl = zoyS3Service.uploadFile(amenitiesPhotoBucketName,zoyPgAmenetiesMaster.getAmenetiesId(), amenetie.getAmenetiesImage());
			zoyPgAmenetiesMaster.setAmenetiesName(amenetie.getAmeneties());
			zoyPgAmenetiesMaster.setAmenetiesImage(imageUrl);		
			ZoyPgAmenetiesMaster saved=ownerDBImpl.createAmeneties(zoyPgAmenetiesMaster);

			//audit history here
			String historyContent=" has created the Amenities for,"+amenetie.getAmeneties();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldAmenities=zoyPgAmenetiesMaster.getAmenetiesName();
				zoyPgAmenetiesMaster.setAmenetiesName(amenetie.getAmeneties());
				ZoyPgAmenetiesMaster updated=ownerDBImpl.createAmeneties(zoyPgAmenetiesMaster);
				//audit history here
				String historyContent=" has updated the Amenities for, from "+oldAmenities+" to "+amenetie.getAmeneties();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
	public ResponseEntity<String> zoyAdminAmenitiesUpdate(AmenetiesId amenetie) {
		ResponseBody response = new ResponseBody();
		try {
			ZoyPgAmenetiesMaster zoyPgAmenetiesMaster = ownerDBImpl.findAmeneties(amenetie.getId());

			if (zoyPgAmenetiesMaster != null) {
				final String oldAmenities = zoyPgAmenetiesMaster.getAmenetiesName();
				final String oldImage = zoyPgAmenetiesMaster.getAmenetiesImage();

				zoyPgAmenetiesMaster.setAmenetiesName(amenetie.getAmeneties());

				if (amenetie.getAmenetiesImage() != null && !amenetie.getAmenetiesImage().isEmpty()) {
					String imageUrl = zoyS3Service.uploadFile(amenitiesPhotoBucketName, zoyPgAmenetiesMaster.getAmenetiesId(), amenetie.getAmenetiesImage());
					zoyPgAmenetiesMaster.setAmenetiesImage(imageUrl);
				}

				ZoyPgAmenetiesMaster updated = ownerDBImpl.createAmeneties(zoyPgAmenetiesMaster);

				String historyContent = " has updated the Amenities from " + oldAmenities + " to " + amenetie.getAmeneties();
				if (amenetie.getAmenetiesImage() != null && !amenetie.getAmenetiesImage().isEmpty()) {
					historyContent += ", Image updated from " + oldImage + " to " + amenetie.getAmenetiesImage();
				}
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Ameneties found for the given Id " + amenetie.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating ameneties details API:/zoy_admin/amenetiesUpdate.zoyAdminAmenitiesUpdate ", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

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

			//audit history here
			String historyContent=" has created the Factor for, "+dueFactor.getFactorName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldAmenities=zoyPgDueFactorMaster.getFactorName();
				zoyPgDueFactorMaster.setFactorName(dueFactorId.getFactorName());
				ZoyPgDueFactorMaster updated=ownerDBImpl.updateDueFactor(zoyPgDueFactorMaster);
				//audit history here
				String historyContent=" has updated the Factor for, from "+oldAmenities+" to "+dueFactorId.getFactorName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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

			//audit history here
			String historyContent=" has created the Rent Cycle for, "+rentCycle.getRentCycleName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldAmenities=zoyPgRentCycleMasters.getCycleName();
				zoyPgRentCycleMasters.setCycleName(rentCycleId.getRentCycleName());
				ZoyPgRentCycleMaster updated=ownerDBImpl.saveRentCycle(zoyPgRentCycleMasters);
				//audit history here
				String historyContent=" has updated the Rent Cycle for, from "+oldAmenities+" to "+rentCycleId.getRentCycleName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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

			//audit history here
			String historyContent=" has created the Room Type for, "+roomType.getRoomTypeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldAmenities=zoyPgRoomTypeMasters.getRoomTypeName();
				zoyPgRoomTypeMasters.setRoomTypeName(roomTypeId.getRoomTypeName());
				ZoyPgRoomTypeMaster updated=ownerDBImpl.updateRoomType(zoyPgRoomTypeMasters);
				//audit history here
				String historyContent=" has updated the Room Type for, from "+oldAmenities+" to "+roomTypeId.getRoomTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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

			//audit history here
			String historyContent=" has created the Share Type for, Type= "+shareType.getShareType()+" , Occupancy Count "+shareType.getShareOccupancyCount();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldType=zoyPgShareMasters.getShareType();
				final int oldCount=zoyPgShareMasters.getShareOccupancyCount();
				zoyPgShareMasters.setShareType(shareTypeId.getShareType());
				zoyPgShareMasters.setShareOccupancyCount(shareTypeId.getShareOccupancyCount());
				ZoyPgShareMaster updated=ownerDBImpl.updateShare(zoyPgShareMasters);

				//audit history here
				StringBuffer historyContent=new StringBuffer(" has updated the Share Type for ");
				if(!oldType.equals(shareTypeId.getShareType())) {
					historyContent.append(", Type from "+oldType+" to "+shareTypeId.getShareType());
				}
				if(oldCount!=shareTypeId.getShareOccupancyCount()) {
					historyContent.append(" , Occupancy Count from "+oldCount+" to "+shareTypeId.getShareOccupancyCount());
				}				
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent.toString(), ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
			//audit history here
			String historyContent=" has created the PG Type for, "+pgType.getPgTypeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=zoyPgTypeMasters.getPgTypeName();
				zoyPgTypeMasters.setPgTypeName(pgTypeId.getPgTypeName());
				ZoyPgTypeMaster updated=ownerDBImpl.updatePgType(zoyPgTypeMasters);

				//audit history here
				String historyContent=" has updated the PG Type for, from "+oldCount+" to "+pgTypeId.getPgTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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

			//audit history here
			String historyContent=" has created the Notification for, "+notificationMode.getNotificationModeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=notificationModeMasters.getNotificationModName();
				notificationModeMasters.setNotificationModName(notificationModeId.getNotificationModeName());
				NotificationModeMaster updated=userDBImpl.updateNotificationMode(notificationModeMasters);

				//audit history here
				String historyContent=" has updated the Notification for, from "+oldCount+" to "+notificationModeId.getNotificationModeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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

			//audit history here
			String historyContent=" has created the Billing Type for, "+billingType.getBillingTypeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=userBillingMasters.getBillingTypeName();
				userBillingMasters.setBillingTypeName(billingType.getBillingTypeName());
				UserBillingMaster updated=userDBImpl.updateUserBillingMaster(userBillingMasters);
				//audit history here
				String historyContent=" has updated the Billing Type for, from "+oldCount+" to "+billingType.getBillingTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
			//audit history here
			String historyContent=" has updated the Currency Type for, "+currencyType.getCurrencyName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=userCurrencyMasters.getCurrencyName();
				userCurrencyMasters.setCurrencyName(currencyTypeId.getCurrencyName());
				UserCurrencyMaster updated=userDBImpl.updateUserCurrency(userCurrencyMasters);
				//audit history here
				String historyContent=" has updated the Currency Type for, from "+oldCount+" to "+currencyTypeId.getCurrencyName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
			for (ZoyPgDueMaster due : userDueMasters) {
			    if (due.getDueImage() != null && !due.getDueImage().trim().isEmpty()) {
			    	due.setDueImage(zoyAdminService.generatePreSignedUrl(amenitiesPhotoBucketName, due.getDueImage()));
			    }
			}
			return new ResponseEntity<>(gson2.toJson(userDueMasters), HttpStatus.OK);
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
			String imageUrl = zoyS3Service.uploadFile(amenitiesPhotoBucketName,dueType.getDueTypeName(),dueType.getDueTypeImage());
			userDueMasters.setDueImage(imageUrl);
			userDueMasters.setDueStatus(true);
			ZoyPgDueMaster saved=ownerDBImpl.saveUserDueMaster(userDueMasters);
			//audit history here
			String historyContent=" has created the Due Type for, "+dueType.getDueTypeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=userDueMasters.getDueName();
				userDueMasters.setDueName(dueTypeId.getDueTypeName());
				ZoyPgDueMaster updated=ownerDBImpl.updateDueMaster(userDueMasters);

				//audit history here
				String historyContent=" has updated the Due Type for, from "+oldCount+" to "+dueTypeId.getDueTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
	
	@Override
	public ResponseEntity<String> zoyAdminDueTypeUpdate(DueTypeId dueTypeId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgDueMaster userDueMasters =  ownerDBImpl.findUserDueMaster(dueTypeId.getId());
			if(userDueMasters!=null) {
				final String oldCount=userDueMasters.getDueName();
				userDueMasters.setDueName(dueTypeId.getDueTypeName());
				String imageUrl = zoyS3Service.uploadFile(amenitiesPhotoBucketName,dueTypeId.getDueTypeName(),dueTypeId.getDueTypeImage());
				userDueMasters.setDueImage(imageUrl);
				ZoyPgDueMaster updated=ownerDBImpl.updateDueMaster(userDueMasters);

				//audit history here
				String historyContent=" has updated the Due Type for, from "+oldCount+" to "+dueTypeId.getDueTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
			//audit history here
			String historyContent=" has created the EKYC for, "+ekycType.getUserEkycTypeName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

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
				final String oldCount=userEkycTypeMasters.getUserEkycTypeName();
				userEkycTypeMasters.setUserEkycTypeName(ekycTypeId.getUserEkycTypeName());
				UserEkycTypeMaster updated=userDBImpl.updateEkycType(userEkycTypeMasters);
				//audit history here
				String historyContent=" has updated the EKYC for, from "+oldCount+" to "+ekycTypeId.getUserEkycTypeName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

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
	public ResponseEntity<String> zoyPgOwnerDetails(PaginationRequest paginationRequest) {
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
	public ResponseEntity<String> zoyAuditActivitiesLogDetails(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			CommonResponseDTO<AuditActivitiesLogDTO> auditActivitiesLogList = userDBImpl.getAuditActivitiesLogCount(paginationRequest);
			return new ResponseEntity<>(gson.toJson(auditActivitiesLogList), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Unexpected error occurredAPI:/zoy_admin/audit-activitieslog.auditactivitieslogdetails", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


	//User Name List Details
	@Override
	public ResponseEntity<String> zoyUserNameList() {
		ResponseBody response = new ResponseBody();
		try {
			List<UserNameDTO> userNameList = userDBImpl.getUserNameList();
			return new ResponseEntity<>(gson2.toJson(userNameList), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Error while occurredAPI:/zoy_admin/userName-List zoyUserNameList()", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	//User Audit reports Download
	@Override
	public ResponseEntity<byte[]> downloadUserAuditReport(PaginationRequest paginationRequest) {
		byte[] fileData=null;
		String fileName ="";
		MediaType contentType = null;

		try {
			fileData = userDBImpl.generateDynamicReport(paginationRequest);

			if (fileData.length == 0) {
				return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  
			}

			String fileExtension;

			switch (paginationRequest.getDownloadType().toLowerCase()) {
			case "excel":
				contentType = MediaType.APPLICATION_OCTET_STREAM; 
				fileExtension = ".xlsx";
				break;
			case "csv":
				contentType = MediaType.TEXT_PLAIN; 
				fileExtension = ".csv";
				break;

			default:
				contentType = MediaType.TEXT_PLAIN; 
				fileExtension = ".csv";
				break;
			}

			fileName = paginationRequest + fileExtension;

			String historyContent=" has downloaded the audit report";	
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_AUDIT_REPORT_DOWNLOAD);
		}catch(Exception ex) {
			log.error("Error getting download User Audit Report API:/zoy_admin/download_user_audit_report.downloadUserAuditReport",ex);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage().getBytes());
		}
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
				.contentType(contentType)
				.body(fileData);
	}
	
	@Override
	public ResponseEntity<String> zoyAdminManageShortTerm(List<ShortTerm> shortTermList) {
		ResponseBody response = new ResponseBody();
	    List<ZoyPgShortTermMaster> processedList = new ArrayList<>();
	    
	    try {
	        for (ShortTerm shortTerm : shortTermList) {
	            ZoyPgShortTermMaster zoyPgShortTermMaster;
	            if (shortTerm.getZoyPgShortTermMasterId() != null) {
	                // Update existing record
	                zoyPgShortTermMaster = ownerDBImpl.findShortTerm(shortTerm.getZoyPgShortTermMasterId());
	                if (zoyPgShortTermMaster != null) {
	                    zoyPgShortTermMaster.setStartDay(shortTerm.getStartDay());
	                    zoyPgShortTermMaster.setEndDay(shortTerm.getEndDay());
	                    zoyPgShortTermMaster.setPercentage(BigDecimal.ZERO);
	                    ZoyPgShortTermMaster updated = ownerDBImpl.createShortTerm(zoyPgShortTermMaster);
	                    processedList.add(updated);
	                    
	                    // Audit history
	                    String historyContent = " has updated the Short Term for, start day from " + zoyPgShortTermMaster.getStartDay() + " to " + shortTerm.getStartDay() + " ,and End day from " + zoyPgShortTermMaster.getEndDay() + " to " + shortTerm.getEndDay();
	                    auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);
	                } else {
	                    response.setStatus(HttpStatus.NOT_FOUND.value());
	                    response.setMessage("No Short term for the given Id " + shortTerm.getZoyPgShortTermMasterId());
	                    return new ResponseEntity<>(gson2.toJson(response), HttpStatus.NOT_FOUND);
	                }
	            } else {
	                // Create new record
	                zoyPgShortTermMaster = new ZoyPgShortTermMaster();
	                zoyPgShortTermMaster.setStartDay(shortTerm.getStartDay());
	                zoyPgShortTermMaster.setEndDay(shortTerm.getEndDay());
	                zoyPgShortTermMaster.setPercentage(BigDecimal.ZERO);
	                ZoyPgShortTermMaster saved = ownerDBImpl.createShortTerm(zoyPgShortTermMaster);
	                processedList.add(saved);
	                
	                // Audit history
	                String historyContent = " has created the Short Term for, start day " + shortTerm.getStartDay() + " ,End day " + shortTerm.getEndDay();
	                auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);
	            }
	        }
	        
	        return new ResponseEntity<>(gson2.toJson(processedList), HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Error managing short term details API:/zoy_admin/shortTerm/manage ", e);
	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }

	}


	@Override
	public ResponseEntity<String> zoyAdminShortTerm() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgShortTermMaster> zoyPgShortTermMasters =  ownerDBImpl.findAllShortTerm();
			return new ResponseEntity<>(gson2.toJson(zoyPgShortTermMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting ekyc type details API:/zoy_admin/shortTerm.zoyAdminEkycType ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	
	}

	@Override
	public ResponseEntity<String> getBookingDetails(UserPaymentFilterRequest filterdata) {
		ResponseBody response = new ResponseBody();

	    try {
	        Timestamp fromDate = filterdata.getFromDate();
	        Timestamp endDate = filterdata.getToDate();

	        if (fromDate == null || endDate == null) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("From Date and End Date are required.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        //Timestamp fromDate = Timestamp.valueOf(fromDateString);
	        //Timestamp fromDate = Timestamp.valueOf(endDateString);

	        long checkedInCount = userBookings.getBookedCountByDates(fromDate, endDate); 
	        long bookedCount = userBookings.getCheckInCountByDates(fromDate, endDate);
	        long vacancyCount = userBookings.getVacancyCount(fromDate, endDate); 

	        TotalBookingsDetails bookingDetails = new TotalBookingsDetails();
	        bookingDetails.setCheckedIn(checkedInCount);
	        bookingDetails.setBooked(bookedCount);
	        bookingDetails.setVacancy(vacancyCount);

	        return new ResponseEntity<>(gson.toJson(bookingDetails), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error("Error getting booking details API:/zoy_admin/getBookingDetails.getBookingDetails", e);

	        try {
	            new ZoyAdminApplicationException(e, "");
	        } catch (Exception ex) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError(ex.getMessage());
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        response.setStatus(HttpStatus.BAD_REQUEST.value());
	        response.setError(e.getMessage());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	    }
	}

	@Override
	public ResponseEntity<String> zoyAdminFloorNamePost(FloorName floorName) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgFloorNameMaster floorNameMasters =  new ZoyPgFloorNameMaster();
			floorNameMasters.setFloorName(floorName.getFloorName());
			ZoyPgFloorNameMaster saved=ownerDBImpl.createFloorName(floorNameMasters);

			//audit history here
			String historyContent=" has created the Floor Name for, "+floorName.getFloorName();
			auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_CREATE);

			return new ResponseEntity<>(gson2.toJson(saved), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error posting floor name details API:/zoy_admin/floorName.zoyAdminFloorNamePost ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> zoyAdminFloorName() {
		ResponseBody response=new ResponseBody();
		try {
			List<ZoyPgFloorNameMaster> floorNameMasters =  ownerDBImpl.getAllFloorNames();
			return new ResponseEntity<>(gson2.toJson(floorNameMasters), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting room type details API:/zoy_admin/floorName.zoyAdminFloorName",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> zoyAdminFloorNamePut(FloorNameId floorNameId) {
		ResponseBody response=new ResponseBody();
		try {
			ZoyPgFloorNameMaster floorNameMasters =  ownerDBImpl.findFloorName(floorNameId.getId());
			if(floorNameMasters!=null) {
				final String oldAmenities=floorNameMasters.getFloorName();
				floorNameMasters.setFloorName(floorNameId.getFloorName());
				ZoyPgFloorNameMaster updated=ownerDBImpl.updateFloorName(floorNameMasters);
				//audit history here
				String historyContent=" has updated the Floor Name for, from "+oldAmenities+" to "+floorNameId.getFloorName();
				auditHistoryUtilities.auditForCommon(SecurityContextHolder.getContext().getAuthentication().getName(), historyContent, ZoyConstant.ZOY_ADMIN_DB_CONFIG_UPDATE);

				return new ResponseEntity<>(gson2.toJson(updated), HttpStatus.OK);
			} else {
				response.setStatus(HttpStatus.NOT_FOUND.value());
				response.setMessage("No Floor Name for the given Id " + floorNameId.getId());
				return new ResponseEntity<>(gson2.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error updating room type details API:/zoy_admin/roomType.zoyAdminRoomTypePut ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> zoyAdminConfigUpdateRentalAgreementdocument(String rentalAgreementDocId ,MultipartFile file) {
		ResponseBody response = new ResponseBody();
		String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			if (rentalAgreementDocId != null && !rentalAgreementDocId.isEmpty()) {

				Optional<RentalAgreementDoc> RentalAgreementDetails = rentalAgreementDocRepository.findById(rentalAgreementDocId);
				if (RentalAgreementDetails.isEmpty()) {
					response.setStatus(HttpStatus.BAD_REQUEST.value());
					response.setError("Required Rental Agreement Document details not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
					
				} else {
					RentalAgreementDoc oldDetails = RentalAgreementDetails.get();

					String oldFixed = oldDetails.getRentalAgreementDoc();
					
					String uploadedFileName = "Rental Agreement";
					String fileUrl = zoyS3Service.uploadFile(zoyPgRentalDocsUploadBucketName,uploadedFileName,file);
					
					oldDetails.setRentalAgreementDoc(fileUrl);


					rentalAgreementDocRepository.save(oldDetails);
//
//					// Audit the update action
//					String historyContent = " has updated the Rental Agreement Document  from "
//							+ oldFixed + " to " + fileUrl;
//					auditHistoryUtilities.auditForCommon(currentUser, historyContent,
//							ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_UPDATE);
				}
			} else {
				RentalAgreementDoc newRentalAgreementDoc = new RentalAgreementDoc();
				
				String uploadedFileName = "Rental Agreement";
				String fileUrl = zoyS3Service.uploadFile(zoyPgRentalDocsUploadBucketName,uploadedFileName,file);
				
				newRentalAgreementDoc.setRentalAgreementDoc(fileUrl);
				rentalAgreementDocRepository.save(newRentalAgreementDoc);

//				// Audit the creation action
//				String historyContent = " has updated the Rental Agreement Document "
//						+ fileUrl;
//				auditHistoryUtilities.auditForCommon(currentUser, historyContent,
//						ZoyConstant.ZOY_ADMIN_MASTER_CONFIG_CREATE);
			}

			List<RentalAgreementDoc> allDetails = rentalAgreementDocRepository.findAll();
			List<RentalAgreementDocDto> dto = allDetails.stream().map(this::convertToDTO)
					.collect(Collectors.toList());

			response.setStatus(HttpStatus.OK.value());
			response.setData(dto);
			response.setMessage("Rental Agreement details successfully saved/updated");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

		} catch (Exception e) {
			log.error(
					"Error saving/updating Rental Agreement Document details API:/zoy_admin/config/force-checkout.zoyAdminConfigUpdateForceCheckOut ",
					e);
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			response.setError("An internal error occurred while processing the request");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	private RentalAgreementDocDto convertToDTO(RentalAgreementDoc entity) {
		RentalAgreementDocDto dto = new RentalAgreementDocDto();
		dto.setRentalAgreementDocId(entity.getRentalAgreementDocId());
		dto.setRentalAgreementDoc(entity.getRentalAgreementDoc());
		return dto;
	}

	
}
