package com.integration.zoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.utils.ApiResponse;
import com.integration.zoy.utils.Role;
import com.integration.zoy.utils.UserDetails;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZoyAdminTicketSmartService {

	@Autowired
	TicketSmartService ticketSmartService;

	public Boolean createTicketSmartUser(ZoyPgSalesMasterModel pgSalesMasterModel) {
		try {
			UserDetails dto=new UserDetails();
			dto.setId(null);
			dto.setContactNumber(pgSalesMasterModel.getMobileNo());
			dto.setDesignation(pgSalesMasterModel.getUserDesignation());
			dto.setUserEmail(pgSalesMasterModel.getEmailId());
			dto.setFirstName(pgSalesMasterModel.getFirstName());
			dto.setLastName(pgSalesMasterModel.getLastName());

			String response=ticketSmartService.postTicketSmartAPI("/user_create", dto);
			if(response==null)
				return false;
			return true;
		} catch (Exception e) {
			log.error("Unable to create ticket smart user " + e);
			return false;
		}

	}

	public Object getTicketSmartUserDesignation() {
		try {
			String userDesignation=ticketSmartService.getTicketSmartAPI("/getUserDesignation");
			if(userDesignation==null) {
				return null;
			}
			TypeReference<ApiResponse<List<Role>>> typeRef = new TypeReference<>() {};
			ApiResponse<List<Role>> data = JsonParserUtil.fromJson(userDesignation, typeRef);
			if(data.getData().isEmpty()) {
				return null;
			}
			return data.getData();
		} catch (Exception e) {
			log.error("Unable to get ticket smart user desgination " + e);
			return null;
		}

	}

}
