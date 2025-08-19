package com.integration.zoy.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.integration.zoy.entity.PgEmailMobileBlacklisted;
import com.integration.zoy.exception.ZoyAdminApplicationException;
import com.integration.zoy.model.BlacklistDeleteRequest;
import com.integration.zoy.model.PgBlacklistedModel;
import com.integration.zoy.model.PgOwnerFilter;
import com.integration.zoy.repository.PgEmailMobileBlacklistedRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PgBlacklistedResponse;
import com.integration.zoy.utils.ResponseBody;

@RestController
@RequestMapping("")
public class PgBlacklistedController implements PgBlacklistedImpl {
	private static final Logger log=LoggerFactory.getLogger(PgBlacklistedController.class);
	@Autowired
	private PgEmailMobileBlacklistedRepository blacklistedRepository;

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

	@Override
	public ResponseEntity<String> saveBlacklistedDetails(PgBlacklistedModel model) {
		ResponseBody response = new ResponseBody();
		try {
			if(model.getId()==null) {
				PgEmailMobileBlacklisted blacklisted = new PgEmailMobileBlacklisted();
				blacklisted.setEmail(model.getEmail());
				blacklisted.setMobile(model.getMobile());
				PgEmailMobileBlacklisted saved=blacklistedRepository.save(blacklisted);
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Blacklisted value saved successfully");
				response.setData(saved);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			} else {
				Optional<PgEmailMobileBlacklisted> blacklist = blacklistedRepository.findById(model.getId());
				if(!blacklist.isPresent()) {
					response.setStatus(HttpStatus.NOT_FOUND.value());
					response.setMessage("Blacklisted id not found");
					return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
				}
				PgEmailMobileBlacklisted blacklisted = blacklist.get();
				blacklisted.setEmail(model.getEmail());
				blacklisted.setMobile(model.getMobile());
				PgEmailMobileBlacklisted saved=blacklistedRepository.save(blacklisted);
				response.setStatus(HttpStatus.OK.value());
				response.setMessage("Blacklisted value updated successfully");
				response.setData(saved);
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
		} catch (Exception e) {
			log.error("Error occurred while saving blacklisted details API:/zoy_admin/saveBlacklistedDetails.saveBlacklistedDetails ", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
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
	public ResponseEntity<String> deleteBlacklistedDetails(BlacklistDeleteRequest model) {
		ResponseBody response = new ResponseBody();
		try {
			if(model.getIds()==null && model.getIds().isEmpty()) {
				response.setStatus(HttpStatus.BAD_REQUEST.value());
				response.setMessage("Missing ids");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
			}
			blacklistedRepository.deleteByIdIn(model.getIds());
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Blacklisted value deleted successfully");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error occurred while saving blacklisted details API:/zoy_admin/deleteBlacklistedDetails.deleteBlacklistedDetails ", e);
			try {
				new ZoyAdminApplicationException(e, "");
			}catch(Exception ex){
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
	public ResponseEntity<String> getBlacklisted(PgOwnerFilter pgOwnerFilter) {
		ResponseBody response=new ResponseBody();
		try {
			Pageable pageable = PageRequest.of(pgOwnerFilter.getPageIndex(), pgOwnerFilter.getPageSize(), Sort.by("email").ascending());
			Page<PgEmailMobileBlacklisted> result =  blacklistedRepository.search(pgOwnerFilter.getSearchContent(),pageable);
			CommonResponseDTO<PgEmailMobileBlacklisted> data =new CommonResponseDTO<>(result.getContent(), (int) result.getTotalElements());
			return new ResponseEntity<>(gson.toJson(data), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getUserPaymentsByDateRange API:/zoy_admin/getBlacklisted.getBlacklisted ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getAllBlacklisted() {
		ResponseBody response=new ResponseBody();
		try {
			List<PgEmailMobileBlacklisted> result =  blacklistedRepository.findAll();
			List<String> emailIds = result.stream()
			        .map(PgEmailMobileBlacklisted::getEmail)
			        .filter(e -> e != null && !e.trim().isEmpty())  
			        .collect(Collectors.toList());
			List<String> mobileNos = result.stream()
			        .map(PgEmailMobileBlacklisted::getMobile)
			        .filter(m -> m != null && !m.trim().isEmpty())
			        .collect(Collectors.toList());
	        PgBlacklistedResponse blacklistedResponse = new PgBlacklistedResponse();
	        blacklistedResponse.setEmailIds(emailIds);
	        blacklistedResponse.setMobileNos(mobileNos);
	        return new ResponseEntity<>(gson.toJson(blacklistedResponse), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting getUserPaymentsByDateRange API:/zoy_admin/getBlacklisted.getBlacklisted ",e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}


}