package com.integration.zoy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.integration.zoy.model.ZoyPgSalesMasterModel;
import com.integration.zoy.utils.ApiResponse;
import com.integration.zoy.utils.Role;
import com.integration.zoy.utils.SingleUserGroupResponseDto;
import com.integration.zoy.utils.TicketHistoryDto;
import com.integration.zoy.utils.TicketHistoryResponseDto;
import com.integration.zoy.utils.UserDetails;
import com.integration.zoy.utils.UserGroupDto;
import com.integration.zoy.utils.UserGroupResponseDto;
import com.integration.zoy.utils.UserMaster;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZoyAdminTicketSmartService {

	@Autowired
	TicketSmartService ticketSmartService;

	private  ObjectMapper objectMapper = new ObjectMapper();

	public UserMaster createTicketSmartUser(ZoyPgSalesMasterModel pgSalesMasterModel) {
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
				return null;
			TypeReference<ApiResponse<UserMaster>> typeRef = new TypeReference<>() {};
			ApiResponse<UserMaster> data = JsonParserUtil.fromJson(response, typeRef);
			if(data.getData()==null) {
				return null;
			}
			return data.getData();
		} catch (Exception e) {
			log.error("Unable to create ticket smart user " + e);
			return null;
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

	public List<UserGroupResponseDto> getTicketSmartUserGroup() {
		try {
			String userGroup=ticketSmartService.getTicketSmartAPI("/getUserGroup");
			if(userGroup==null) {
				return null;
			}
			TypeReference<ApiResponse<List<UserGroupResponseDto>>> typeRef = new TypeReference<>() {};
			ApiResponse<List<UserGroupResponseDto>> data = JsonParserUtil.fromJson(userGroup, typeRef);
			if(data.getData().isEmpty()) {
				return null;
			}
			return data.getData();
		} catch (Exception e) {
			log.error("Unable to get ticket smart user desgination " + e);
			return null;
		}
	}

	public SingleUserGroupResponseDto getTicketSmartUserGroup(String groupId) {
		try {
			String userGroup=ticketSmartService.getTicketSmartAPI("/getSingleUserGroup?groupId="+groupId);
			if(userGroup==null) {
				return null;
			}
			TypeReference<ApiResponse<SingleUserGroupResponseDto>> typeRef = new TypeReference<>() {};
			ApiResponse<SingleUserGroupResponseDto> data = JsonParserUtil.fromJson(userGroup, typeRef);
			if(data.getData()==null) {
				return null;
			}
			return data.getData();
		} catch (Exception e) {
			log.error("Unable to get ticket smart user group " + e);
			return null;
		}
	}

	public boolean assignTicketToGroup(UserMaster userCreated, ZoyPgSalesMasterModel pgSalesMasterModel) {
		try {
			SingleUserGroupResponseDto groupDto=getTicketSmartUserGroup(pgSalesMasterModel.getUserGroupId());
			if(groupDto==null) 
				return false;
			UserGroupDto dto=new UserGroupDto();
			dto.setId(groupDto.getId());
			List<String> userIds=groupDto.getUserIds();
			userIds.add(userCreated.getId());
			dto.setUserId(userIds);
			String response=ticketSmartService.postTicketSmartAPI("/saveUserGroup", dto);
			if(response==null)
				return false;
			return true;
		} catch (Exception e) {
			log.error("Unable to assign user to ticket group " + e);
			return false;
		}
	}

	public TicketHistoryResponseDto updateUserTicket(String ticketId,String description,String status) {
		try {
			TicketHistoryDto ticketHistoryDto=new TicketHistoryDto();
			ticketHistoryDto.setTicketId(ticketId);
			ticketHistoryDto.setDescription(description);
			ticketHistoryDto.setNewStatus(status);
			ticketHistoryDto.setNewAssignee(null);
			String ticketJson = objectMapper.writeValueAsString(ticketHistoryDto);
			String response=ticketSmartService.postTicketSmartAPI("/updateTicket", ticketJson,null);
			if(response==null) 
				return null;
			TypeReference<ApiResponse<TicketHistoryResponseDto>> typeRef = new TypeReference<>() {};
			ApiResponse<TicketHistoryResponseDto> data = JsonParserUtil.fromJson(response, typeRef);
			if(data.getData()==null) {
				return null;
			}
			return data.getData();
		} catch (Exception e) {
			log.error("Unable to update ticket", e);
			return null;
		}
	}

}
