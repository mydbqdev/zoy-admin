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
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.integration.zoy.entity.AdminUserMaster;
import com.integration.zoy.entity.FollowUps;
import com.integration.zoy.entity.RegisteredPartner;
import com.integration.zoy.entity.UserHelpRequest;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.model.FollowUp;
import com.integration.zoy.model.SupportTicketDTO;
import com.integration.zoy.model.TicketAssign;
import com.integration.zoy.model.UpdateStatus;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.FollowUpRepository;
import com.integration.zoy.repository.RegisteredPartnerDetailsRepository;
import com.integration.zoy.repository.UserHelpRequestRepository;
import com.integration.zoy.service.AdminReportImpl;
import com.integration.zoy.service.NotificationsAndAlertsService;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.SupportDBImpl;
import com.integration.zoy.service.TimestampFormatterUtilService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PaginationRequest;
import com.integration.zoy.utils.RegisterLeadDetails;
import com.integration.zoy.utils.ResponseBody;
import com.integration.zoy.utils.SupportUsres;
import com.integration.zoy.utils.UserPaymentFilterRequest;

@RestController
@RequestMapping("")
public class ZoyAdminSupportController implements ZoyAdminSupportImpl{
	private static final Logger log = LoggerFactory.getLogger(ZoyAdminUserController.class);
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
	AdminReportImpl adminReportImpl;

	@Autowired
	OwnerDBImpl ownerDBImpl;

	@Autowired
	RegisteredPartnerDetailsRepository registeredPartnerDetailsRepository;
	
	@Autowired
	UserHelpRequestRepository userHelpRequestRepository;

	@Autowired
	AuditHistoryUtilities auditHistoryUtilities;

	@Autowired
	private NotificationsAndAlertsService notificationsAndAlertsService;

	@Autowired
	AdminUserMasterRepository userMasterRepository;

	@Autowired
	FollowUpRepository followUpRepository;
	
	@Autowired
	AdminUserMasterRepository adminUserMasterRepo;
	
	@Autowired
	TimestampFormatterUtilService tuService;
	
	@Autowired
	SupportDBImpl supportDBImpl;
	

	@Override
	public ResponseEntity<String> getRegisteredLeadDetailsByDateRange(UserPaymentFilterRequest filterRequest) {
		ResponseBody response = new ResponseBody();
		try {
			FilterData filterData = gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			boolean isSupportUser = false;
			CommonResponseDTO<RegisterLeadDetails> registerLeads = adminReportImpl.getRegisterLeadDetails(filterRequest, filterData, applyPagination,isSupportUser);
			return new ResponseEntity<>(gson.toJson(registerLeads), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error in API /zoy_admin/registered_lead_details.getRegisteredLeadDetailsByDateRange", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> getSupportUserDetails() {
		ResponseBody response = new ResponseBody();
		try {
			List<SupportUsres> supportUsrs = ownerDBImpl.getAllSupportUserNames();
			return new ResponseEntity<>(gson2.toJson(supportUsrs), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting support user details API:/zoy_admin/support_user_details.getSupportUserDetails", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> assignTicketsToSupportTeam(TicketAssign assignTicket) {
		ResponseBody response=new ResponseBody();
		try {
			Optional<RegisteredPartner> partner = registeredPartnerDetailsRepository.findByRegisterId(assignTicket.getInquiryNumber());
			if (partner.isPresent()) {
				String currentDate=tuService.currentDate();
				RegisteredPartner existingPartner = partner.get();
				String historyContentForTicketAssign;
				String userName="";
				Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
				if(user.isPresent()) {
					userName=user.get().getFirstName()+" "+user.get().getLastName();
				}
				if (existingPartner.getAssignedToEmail() != null && !existingPartner.getAssignedToEmail().isEmpty()) {
					historyContentForTicketAssign = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " has been reassigned to " + assignTicket.getName() + " on " + currentDate + "." + " Reassigned by " + userName + ".";
				} else {
					historyContentForTicketAssign = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " has been assigned to " + assignTicket.getName() + " on " + currentDate + "." + " Assigned by " + userName + ".";
				}
				existingPartner.setAssignedToEmail(assignTicket.getEmail());
				existingPartner.setAssignedToName(assignTicket.getName());
				existingPartner.setStatus(ZoyConstant.INPROGRESS);
				registeredPartnerDetailsRepository.save(existingPartner);
				response.setMessage("Ticket assigned successfully.");
				response.setStatus(HttpStatus.OK.value());

				auditHistoryUtilities.leadHistory(historyContentForTicketAssign,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus());
				String historyContentForChangeTicketStatus = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " Status has been Changed To " + existingPartner.getStatus() + "."+ " On " + currentDate;
				auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus());
				AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
				String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
				notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
				return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
			} else {
				response.setMessage("Inquiry number does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
			}
		} catch (Exception e) {
			response.setMessage("An error occurred while assigning the ticket.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getMessage());
		}
	}

	@Override
	public ResponseEntity<String> getLeadFollowUpHostory(String inquiryNumber) {
		ResponseBody response=new ResponseBody();
		try {
			List<String> leadFollowUpHistory = ownerDBImpl.getLeadFollowUpHistory(inquiryNumber);
			if (leadFollowUpHistory.isEmpty()) {
				response.setStatus(HttpStatus.OK.value());
				response.setError("No data available for the given inquiry number.");
				return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
			}
			return new ResponseEntity<>(gson2.toJson(leadFollowUpHistory), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error getting Lead follow up history API:/zoy_admin/lead_followup_history.getLeadFollowUpHostory", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> getAssignedLeadTickets(UserPaymentFilterRequest filterRequest) {
		ResponseBody response = new ResponseBody();
		try {
			FilterData filterData = gson.fromJson(filterRequest.getFilterData(), FilterData.class);
			boolean applyPagination = true;
			boolean isSupportUser = true;
			CommonResponseDTO<RegisterLeadDetails> registerLeads = adminReportImpl.getRegisterLeadDetails(filterRequest, filterData, applyPagination,isSupportUser);
			return new ResponseEntity<>(gson.toJson(registerLeads), HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error in API /zoy_admin/registered_lead_details.getRegisteredLeadDetailsByDateRange", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public ResponseEntity<String> updateInquiryStatus(UpdateStatus updateStatus) {
		ResponseBody response=new ResponseBody();
		try {
			Optional<RegisteredPartner> partner = registeredPartnerDetailsRepository.findByRegisterId(updateStatus.getInquiryNumber());
			if (partner.isPresent()) {
				
				String currentDate=tuService.currentDate();
				RegisteredPartner existingPartner = partner.get();
				String previousStatus=existingPartner.getStatus();
				existingPartner.setStatus(updateStatus.getStatus());
				registeredPartnerDetailsRepository.save(existingPartner);
				response.setMessage("Status Updated successfully.");
				response.setStatus(HttpStatus.OK.value());
				String historyContentForChangeTicketStatus = "Lead Ticket Number " + existingPartner.getRegisterId() + " Status has been Changed From " + previousStatus + " To " + updateStatus.getStatus() + " On " + currentDate + ".";
				auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,SecurityContextHolder.getContext().getAuthentication().getName(),updateStatus.getInquiryNumber(),existingPartner.getStatus());
				return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
			} else {
				response.setMessage("Inquiry number does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
			}
		} catch (Exception e) {
			response.setMessage("An error occurred while Changing the Lead Status.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getMessage());
		}
	}

	@Override
	public ResponseEntity<String> createFollowUp(FollowUp followUp) {
		ResponseBody response=new ResponseBody();
		try {
			FollowUps followUpsEntity = new FollowUps();
			followUpsEntity.setInquiryId(followUp.getInquiryId());
			followUpsEntity.setFollowUpDate(followUp.getFollowUpDate());
			followUpsEntity.setRemarks(followUp.getRemarks());
			followUpsEntity.setReminderSet(followUp.getReminderSet());
			followUpsEntity.setReminderDate(followUp.getReminderDate());
			followUpsEntity.setStatus(followUp.getStatus());
			followUpsEntity.setUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
			followUpRepository.save(followUpsEntity);
			response.setStatus(HttpStatus.OK.value());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Error  while fetching follow-up details API:/zoy_admin/follow_up_details.FetchAllFollowUpDetails " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}

	}

	@Override
	public ResponseEntity<String> FetchAllFollowUpDetails() {
		ResponseBody response=new ResponseBody();
		try {


			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			List<FollowUps> followUpsList = followUpRepository.findByUserEmail(email);
			if (followUpsList.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follow-up details found for the given email address");
			}
			List<FollowUp> followUpModelList = followUpsList.stream()
					.map(this::convertToModel)
					.collect(Collectors.toList());
			response.setStatus(HttpStatus.OK.value());
			response.setMessage("Follow-up details saved successfully");
			return new ResponseEntity<>(gson.toJson(followUpModelList), HttpStatus.OK);
		}catch (Exception e) {
			log.error("Error getting while saving follow-up details API:/zoy_admin/follow_up_details.createFollowUp " + e.getMessage(),e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}

	private FollowUp convertToModel(FollowUps followUpsEntity) {
	    FollowUp followUpModel = new FollowUp();
	    followUpModel.setInquiryId(followUpsEntity.getInquiryId());
	    followUpModel.setFollowUpDate(followUpsEntity.getFollowUpDate());
	    followUpModel.setRemarks(followUpsEntity.getRemarks());
	    followUpModel.setReminderSet(followUpsEntity.getReminderSet());
	    followUpModel.setReminderDate(followUpsEntity.getReminderDate());
	    followUpModel.setStatus(followUpsEntity.getStatus());
	    return followUpModel;
	}
	
	
	@Override
	public ResponseEntity<String> zoyOpenSupportTicketList(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			CommonResponseDTO<SupportTicketDTO> ticketList = supportDBImpl.zoySupportTicketList(paginationRequest,false);
			return new ResponseEntity<>(gson2.toJson(ticketList), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching ticket list: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch ticket list");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Unexpected error occurredAPI:/zoy_admin/open-support-ticket.zoyOpenSupportTicketList", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	@Override
	public ResponseEntity<String> zoyCloseSupportTicketList(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			CommonResponseDTO<SupportTicketDTO> ticketList = supportDBImpl.zoySupportTicketList(paginationRequest,true);
			return new ResponseEntity<>(gson2.toJson(ticketList), HttpStatus.OK);
		}catch (DataAccessException dae) {
			log.error("Database error occurred while fetching ticket list: " + dae.getMessage(), dae);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError("Database error: Unable to fetch ticket list");
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);

		}catch (Exception e) {
			log.error("Unexpected error occurredAPI:/zoy_admin/close-support-ticket.zoyCloseSupportTicketList", e);
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			response.setError(e.getMessage());
			return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@Override
	public ResponseEntity<String> assignTicketsToTeam(TicketAssign assignTicket) {
		ResponseBody response=new ResponseBody();
		try {
			if(assignTicket.getInquiryType().equals(ZoyConstant.LEAD_GEN)) {
				Optional<RegisteredPartner> partner = registeredPartnerDetailsRepository.findByRegisterId(assignTicket.getInquiryNumber());
				if (partner.isPresent()) {
					String currentDate=tuService.currentDate();
					RegisteredPartner existingPartner = partner.get();
					String historyContentForTicketAssign;
					String userName="";
					Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
					if(user.isPresent()) {
						userName=user.get().getFirstName()+" "+user.get().getLastName();
					}
					if (existingPartner.getAssignedToEmail() != null && !existingPartner.getAssignedToEmail().isEmpty()) {
						historyContentForTicketAssign = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " has been reassigned to " + assignTicket.getName() + " on " + currentDate + "." + " Reassigned by " + userName + ".";
						if(assignTicket.getSelf()) {
							return ResponseEntity.status(HttpStatus.CONFLICT).body("The lead ticket has already been assigned to another team member.");
							}
					} else {
						historyContentForTicketAssign = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " has been assigned to " + assignTicket.getName() + " on " + currentDate + "." + " Assigned by " + userName + ".";
					}
					existingPartner.setAssignedToEmail(assignTicket.getEmail());
					existingPartner.setAssignedToName(assignTicket.getName());
					existingPartner.setStatus(ZoyConstant.OPEN);
					registeredPartnerDetailsRepository.save(existingPartner);
					response.setMessage("Lead Ticket has been assigned successfully.");
					response.setStatus(HttpStatus.OK.value());
	
					auditHistoryUtilities.leadHistory(historyContentForTicketAssign,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus());
					
					String historyContentForChangeTicketStatus = "Lead Ticket Number " + assignTicket.getInquiryNumber() + " Status has been Changed To " + existingPartner.getStatus() + "."+ " On " + currentDate;
					auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus());
					
					if(!assignTicket.getSelf()) {
					AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
					String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
					notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
					}
					
					return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
				} else {
					response.setMessage("Lead Inquiry number does not exist.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
				}
			}else if(assignTicket.getInquiryType().equals(ZoyConstant.SUPPORT_TICKET)) {
				Optional<UserHelpRequest> partner = userHelpRequestRepository.findById(assignTicket.getInquiryNumber());
				if (partner.isPresent()) {
					String currentDate=tuService.currentDate();
					UserHelpRequest existingPartner = partner.get();
					String historyContentForTicketAssign="";
					String userName="";
					Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
					if(user.isPresent()) {
						userName=user.get().getFirstName()+" "+user.get().getLastName();
					}
					if (existingPartner.getAssignedToEmail() != null && !existingPartner.getAssignedToEmail().isEmpty()) {
						historyContentForTicketAssign = "Support Ticket Number " + assignTicket.getInquiryNumber() + " has been reassigned to " + assignTicket.getName() + " on " + currentDate + "." + " Reassigned by " + userName + ".";
						if(assignTicket.getSelf()) {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("The ticket has already been assigned to another team member.");
						}
					} else {
						historyContentForTicketAssign = "Support Ticket Number " + assignTicket.getInquiryNumber() + " has been assigned to " + assignTicket.getName() + " on " + currentDate + "." + " Assigned by " + userName + ".";
					}
					existingPartner.setAssignedToEmail(assignTicket.getEmail());
					existingPartner.setAssignedToName(assignTicket.getName());
					existingPartner.setRequestStatus(ZoyConstant.OPEN);
					userHelpRequestRepository.save(existingPartner);
					response.setMessage("Support Ticket has been assigned successfully.");
					response.setStatus(HttpStatus.OK.value());
	
					auditHistoryUtilities.userHelpRequestHistory(historyContentForTicketAssign,existingPartner.getRequestStatus(),assignTicket.getInquiryNumber(),assignTicket.getEmail());
					
					String historyContentForChangeTicketStatus = "Support Ticket Number " + assignTicket.getInquiryNumber() + " Status has been Changed To " + existingPartner.getRequestStatus() + "."+ " On " + currentDate;
					auditHistoryUtilities.userHelpRequestHistory(historyContentForChangeTicketStatus,existingPartner.getRequestStatus(),assignTicket.getInquiryNumber(),assignTicket.getEmail());
					
					if(!assignTicket.getSelf()) {
					AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
					String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
					notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
					}
					
					return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
				} else {
					response.setMessage("Lead Inquiry number does not exist.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
				}
			}else {
				// nothing matching
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
			}
		} catch (Exception e) {
			log.error("Error in assign to team {}",response.getMessage());
			response.setMessage("An error occurred while assigning the ticket.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getMessage());
		}
	}

}