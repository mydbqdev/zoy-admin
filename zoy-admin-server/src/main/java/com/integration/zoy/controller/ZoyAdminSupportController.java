package com.integration.zoy.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.lang3.StringUtils;

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
import com.integration.zoy.model.ComplaintTicketDTO;
import com.integration.zoy.model.FilterData;
import com.integration.zoy.model.FollowUp;
import com.integration.zoy.model.RegisteredOwnerDetailsDTO;
import com.integration.zoy.model.SupportTicketDTO;
import com.integration.zoy.model.TicketAssign;
import com.integration.zoy.model.UpdateStatus;
import com.integration.zoy.model.UserTicketHistoryDTO;
import com.integration.zoy.repository.AdminUserMasterRepository;
import com.integration.zoy.repository.FollowUpRepository;
import com.integration.zoy.repository.LeadHistoryRepository;
import com.integration.zoy.repository.RegisteredPartnerDetailsRepository;
import com.integration.zoy.repository.UserHelpRequestRepository;
import com.integration.zoy.service.AdminReportImpl;
import com.integration.zoy.service.EmailService;
import com.integration.zoy.service.NotificationsAndAlertsService;
import com.integration.zoy.service.OwnerDBImpl;
import com.integration.zoy.service.SupportDBImpl;
import com.integration.zoy.service.TimestampFormatterUtilService;
import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.utils.AuditHistoryUtilities;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.Email;
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
	LeadHistoryRepository leadHistoryRepo;
	
	@Autowired
	AdminUserMasterRepository adminUserMasterRepo;
	
	@Autowired
	TimestampFormatterUtilService tuService;
	
	@Autowired
	SupportDBImpl supportDBImpl;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	ZoyAdminService zoyAdminService;

    @Value("${app.minio.user.docs.bucket.name}")
    private String userDocBucketName;
    
    @Value("${zoy.admin.toMail}")
	private String fromEmailId;
	
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
					historyContentForTicketAssign = "Lead Ticket has been reassigned to " + assignTicket.getName() +"," + " Reassigned by " + userName + ".";
				} else {
					historyContentForTicketAssign = "Lead Ticket has been assigned to " + assignTicket.getName() +  "," + " Assigned by " + userName + ".";
				}
				existingPartner.setAssignedToEmail(assignTicket.getEmail());
				existingPartner.setAssignedToName(assignTicket.getName());
				existingPartner.setStatus(ZoyConstant.INPROGRESS);
				registeredPartnerDetailsRepository.save(existingPartner);
				response.setMessage("Ticket assigned successfully.");
				response.setStatus(HttpStatus.OK.value());

				auditHistoryUtilities.leadHistory(historyContentForTicketAssign,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus(),currentDate);
				String historyContentForChangeTicketStatus = "Lead Ticket Status has been Changed To " + existingPartner.getStatus() + ".";
				auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus(),currentDate);
				AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
				String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
				notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
				return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
			} else {
				response.setMessage("Inquiry number does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
			}
		} catch (Exception e) {
			log.error("Error in assignTicketsToSupportTeam", e);
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
	
	
	
	/*
	 * Start coding for admin support
	 */
	
	@Override
	public ResponseEntity<String> zoyOpenSupportTicketList(PaginationRequest paginationRequest) {
		ResponseBody response = new ResponseBody();
		try {
			
			boolean isFinanceUser=false;
			Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
			if(user.isPresent()) {
				if(user.get().getDesignation().equals("Finance Admin")) {
					isFinanceUser=true;
				}
			}
			CommonResponseDTO<SupportTicketDTO> ticketList = supportDBImpl.zoySupportTicketList(paginationRequest,false,isFinanceUser);
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
			boolean isFinanceUser=false;
			Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
			if(user.isPresent()) {
				if(user.get().getDesignation().equals("Finance Admin")) {
					isFinanceUser=true;
				}
			}
			CommonResponseDTO<SupportTicketDTO> ticketList = supportDBImpl.zoySupportTicketList(paginationRequest,true,isFinanceUser);
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
					final String oldStatus=existingPartner.getStatus();
					String historyContentForTicketAssign;
					String userName="";
					Optional<AdminUserMaster> user=userMasterRepository.findById(SecurityContextHolder.getContext().getAuthentication().getName());
					if(user.isPresent()) {
						userName=user.get().getFirstName()+" "+user.get().getLastName();
					}
					if (existingPartner.getAssignedToEmail() != null && !existingPartner.getAssignedToEmail().isEmpty()) {
						historyContentForTicketAssign = "Lead Ticket has been reassigned to " + assignTicket.getName() + "," + " Reassigned by " + userName + ".";
						if(assignTicket.getSelf()) {
							return ResponseEntity.status(HttpStatus.CONFLICT).body("The lead ticket has already been assigned to another team member.");
							}
					} else {
						historyContentForTicketAssign = "Lead Ticket has been assigned to " + assignTicket.getName() +"," + " Assigned by " + userName + ".";
					}
					existingPartner.setAssignedToEmail(assignTicket.getEmail());
					existingPartner.setAssignedToName(assignTicket.getName());
					if(!oldStatus.equals(ZoyConstant.OPEN)) {
					existingPartner.setStatus(ZoyConstant.OPEN);
					}
					registeredPartnerDetailsRepository.save(existingPartner);
					response.setMessage("Lead Ticket has been assigned successfully.");
					response.setStatus(HttpStatus.OK.value());
	
					auditHistoryUtilities.leadHistory(historyContentForTicketAssign,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus(),currentDate);
					
					String historyContentForChangeTicketStatus = "Lead Ticket Status has been Changed To " + existingPartner.getStatus() + ".";
					auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,assignTicket.getEmail(),assignTicket.getInquiryNumber(),existingPartner.getStatus(),currentDate);
					
					if(!assignTicket.getSelf()) {
					AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
					String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
					notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
					}
					
					List<String> to = new ArrayList<>();
		            to.add(assignTicket.getEmail().toLowerCase());
		            if(!to.contains(user.get().getUserEmail())) {
		            to.add(user.get().getUserEmail());
		            }
					final String emailMessage=prepareEmailContentAssign(assignTicket.getName(),existingPartner.getDescription(),"High",currentDate,assignTicket.getInquiryNumber());
					final String subject ="New Lead Ticket Assigned: "+assignTicket.getInquiryNumber();
					emailSend(subject,emailMessage,to);
					
					if(existingPartner.getStatus().equals(ZoyConstant.OPEN) && !oldStatus.equals(ZoyConstant.OPEN)) {
						List<String> toEmail = new ArrayList<>();
						toEmail.add(assignTicket.getEmail().toLowerCase());
						toEmail.add(existingPartner.getEmail().toLowerCase());

						final String emailMessageOpen=prepareEmailContentOpenLead(existingPartner.getFirstname()+" "+existingPartner.getLastname(),existingPartner.getDescription(),currentDate,assignTicket.getInquiryNumber());
						final String subjectOpen ="Support Ticket Opened – "+assignTicket.getInquiryNumber();
						emailSend(subjectOpen,emailMessageOpen,toEmail);
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
						historyContentForTicketAssign = "Support Ticket has been reassigned to " + assignTicket.getName() + "," + " Reassigned by " + userName + ".";
						if(assignTicket.getSelf()) {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("The ticket has already been assigned to another team member.");
						}
					} else {
						historyContentForTicketAssign = "Support Ticket has been assigned to " + assignTicket.getName() + "," + " Assigned by " + userName + ".";
					}
					existingPartner.setAssignedToEmail(assignTicket.getEmail());
					existingPartner.setAssignedToName(assignTicket.getName());
					existingPartner.setRequestStatus(ZoyConstant.OPEN);
					userHelpRequestRepository.save(existingPartner);
					response.setMessage("Support Ticket has been assigned successfully.");
					response.setStatus(HttpStatus.OK.value());
	
					auditHistoryUtilities.userHelpRequestHistory(historyContentForTicketAssign,existingPartner.getRequestStatus(),assignTicket.getInquiryNumber(),assignTicket.getEmail(),currentDate);
					
					String historyContentForChangeTicketStatus = "Support Ticket Status has been changed to " + existingPartner.getRequestStatus() + ".";
					auditHistoryUtilities.userHelpRequestHistory(historyContentForChangeTicketStatus,existingPartner.getRequestStatus(),assignTicket.getInquiryNumber(),assignTicket.getEmail(),currentDate);
					
					if(!assignTicket.getSelf()) {
					AdminUserMaster adminuserDetails=adminUserMasterRepo.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName());
					String notificationMessage = "Ticket ID: " + assignTicket.getInquiryNumber() + " assigned to you by "+adminuserDetails.getFirstName()+" "+adminuserDetails.getLastName()+ " received on "+currentDate+" Check the details in the Tickets section.";
					notificationsAndAlertsService.ticketAssign(new String[]{assignTicket.getEmail()},notificationMessage);
					}
					List<String> to = new ArrayList<>();
		            to.add(assignTicket.getEmail().toLowerCase());
		            to.add(user.get().getUserEmail());
		            final String emailMessage=prepareEmailContentAssign(assignTicket.getName(),existingPartner.getDescription(),existingPartner.isUrgency()?"High":"Low",currentDate,assignTicket.getInquiryNumber());
					final String subject ="New Support Ticket Assigned: "+assignTicket.getInquiryNumber();
					emailSend(subject,emailMessage,to);
					return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
				} else {
					response.setMessage("Support Ticket number does not exist.");
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
	public ResponseEntity<String> updateInquiryStatus(UpdateStatus updateStatus) {
		ResponseBody response=new ResponseBody();
		try {
			if(updateStatus.getInquiryType().equals(ZoyConstant.LEAD_GEN)) {
			Optional<RegisteredPartner> partner = registeredPartnerDetailsRepository.findByRegisterId(updateStatus.getInquiryNumber());
			if (partner.isPresent()) {
				String historyContentForChangeTicketStatus="";
				String currentDate=tuService.currentDate();
				RegisteredPartner existingPartner = partner.get();
				String previousStatus=existingPartner.getStatus();
				existingPartner.setStatus(updateStatus.getStatus());
				registeredPartnerDetailsRepository.save(existingPartner);
				String emailMessage=null;
				String emailSubject="";
				List<String> to = new ArrayList<>();
				if(updateStatus.getStatus()!=null && updateStatus.getStatus().equals(previousStatus)) {
					response.setMessage("Comment has been added successfully.");
					historyContentForChangeTicketStatus = "Lead Ticket Status has been added the comment, "+updateStatus.getComment() +".";
				}else {
					response.setMessage("Status and comment has been updated successfully.");
					historyContentForChangeTicketStatus = "Lead Ticket Status has been Changed From " + previousStatus + " To " + updateStatus.getStatus() + " with comment, "+updateStatus.getComment() +".";
					to.add(partner.get().getEmail().toLowerCase());
					if(updateStatus.getStatus().equals(ZoyConstant.REOPEN)) {
						emailSubject="Your Support Ticket Has Been Reopened – "+updateStatus.getInquiryNumber();
					emailMessage=prepareEmailContentReopened(partner.get().getFirstname()+" "+partner.get().getLastname(),updateStatus.getInquiryNumber());
					}else if(updateStatus.getStatus().equals(ZoyConstant.CLOSE)){
						emailSubject="Your Support Ticket [#"+updateStatus.getInquiryNumber()+"] Has Been Closed ";
					emailMessage=prepareEmailContentClose(partner.get().getFirstname()+" "+partner.get().getLastname(),updateStatus.getInquiryNumber());
					}else {
						emailMessage=null;
					}
				}
				if(StringUtils.isNotEmpty(emailMessage) && !CollectionUtils.isEmpty(to)) {
					emailSend(emailSubject,emailMessage,to);
				}
				response.setStatus(HttpStatus.OK.value());
				
				auditHistoryUtilities.leadHistory(historyContentForChangeTicketStatus,SecurityContextHolder.getContext().getAuthentication().getName(),updateStatus.getInquiryNumber(),existingPartner.getStatus(),currentDate);
				return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
			} else {
				response.setMessage("Inquiry number does not exist.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
			}
			}else if(updateStatus.getInquiryType().equals(ZoyConstant.SUPPORT_TICKET)) {
				Optional<UserHelpRequest> partner = userHelpRequestRepository.findById(updateStatus.getInquiryNumber());
				if (partner.isPresent()) {
					String historyContentForChangeTicketStatus="";
					String currentDate=tuService.currentDate();
					UserHelpRequest existingPartner = partner.get();
					String previousStatus=existingPartner.getRequestStatus();
					existingPartner.setRequestStatus(updateStatus.getStatus());
					userHelpRequestRepository.save(existingPartner);
					String emailMessage=null;
					String emailSubject="";
					List<String> to = new ArrayList<>();
					if(updateStatus.getStatus()!=null && updateStatus.getStatus().equals(previousStatus)) {
						response.setMessage("Comment has been added successfully.");
						historyContentForChangeTicketStatus = "Support Ticket Status has been added the comment, "+updateStatus.getComment() +".";
					}else {
						response.setMessage("Status and comment has been updated successfully.");
						historyContentForChangeTicketStatus = "Support Ticket Status has been Changed From " + previousStatus + " To " + updateStatus.getStatus() + " with comment, "+updateStatus.getComment() + ".";
						List<Object[]> userDet= userHelpRequestRepository.getCompainUserEmail(existingPartner.getUserHelpRequestId());
						String userName="";
						 for (Object[] complaintDetail : userDet) {
							    to.add(complaintDetail[0] != null ? complaintDetail[0].toString() : "");
			                	userName=complaintDetail[1] != null ? complaintDetail[1].toString() : "";
						 }
						
						if(updateStatus.getStatus().equals(ZoyConstant.REOPEN)) {
							emailSubject="Your Support Ticket Has Been Reopened – "+updateStatus.getInquiryNumber();
						emailMessage=prepareEmailContentReopened(userName,updateStatus.getInquiryNumber());
						}else if(updateStatus.getStatus().equals(ZoyConstant.CLOSE)){
							emailSubject="Your Support Ticket [#"+updateStatus.getInquiryNumber()+"] Has Been Closed ";
						emailMessage=prepareEmailContentClose(userName,updateStatus.getInquiryNumber());
						}else {
							emailMessage=null;
						}
					}
					if(StringUtils.isNotEmpty(emailMessage) && !CollectionUtils.isEmpty(to)) {
						emailSend(emailSubject,emailMessage,to);
					}
					response.setStatus(HttpStatus.OK.value());
					auditHistoryUtilities.userHelpRequestHistory(historyContentForChangeTicketStatus,existingPartner.getRequestStatus(),updateStatus.getInquiryNumber(),SecurityContextHolder.getContext().getAuthentication().getName(),currentDate);

					
					return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(response));
				} else {
					response.setMessage("Support Ticket number does not exist.");
					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getMessage());
				}
			}else {
				// nothing matching
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found");
			}	
			
			
		} catch (Exception e) {
			log.error("Error getting support user details API:/zoy_admin/support_user_details.getSupportUserDetails", e);
			response.setMessage("An error occurred while Changing the Lead Status.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getMessage());
		}
	}


	@Override
	public ResponseEntity<String> getDetailsForEachTickets(UpdateStatus updateStatus) {
	    ResponseBody response = new ResponseBody();
	    try {
	        if (updateStatus == null) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Required filter details");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        if (!StringUtils.isEmpty(updateStatus.getInquiryNumber())) {
	            if (updateStatus.getInquiryType().equals(ZoyConstant.LEAD_GEN)) {
	                List<Object[]> ticketDetails = registeredPartnerDetailsRepository.getOwnerTicketDetails(
	                    updateStatus.getInquiryNumber(), updateStatus.getStatus());
	                List<Object[]> ticketHistory = leadHistoryRepo.getOwnerTicketHistory(updateStatus.getInquiryNumber());

	                if (ticketDetails.isEmpty()) {
	                    response.setStatus(HttpStatus.NOT_FOUND.value());
	                    response.setError("No support ticket details found.");
	                    return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	                }

	                RegisteredOwnerDetailsDTO registeredOwnerDetails = new RegisteredOwnerDetailsDTO();
	                List<UserTicketHistoryDTO> userTicketHistoryList = new ArrayList<>();

	                for (Object[] ticketDetail : ticketDetails) {
	                	registeredOwnerDetails.setTickeNumber(updateStatus.getInquiryNumber());
	                    registeredOwnerDetails.setName(ticketDetail[0] != null ? ticketDetail[0].toString() : null);
	                    registeredOwnerDetails.setOwnerEmail(ticketDetail[1] != null ? ticketDetail[1].toString() : null);
	                    registeredOwnerDetails.setMobile(ticketDetail[2] != null ? ticketDetail[2].toString() : null);
	                    registeredOwnerDetails.setPropertyName(ticketDetail[3] != null ? ticketDetail[3].toString() : null);
	                    registeredOwnerDetails.setAddress(ticketDetail[4] != null ? ticketDetail[4].toString() : null);
	                    registeredOwnerDetails.setPincode(ticketDetail[5] != null ? ticketDetail[5].toString() : null);
	                    registeredOwnerDetails.setInquiredFor(ticketDetail[6] != null ? ticketDetail[6].toString() : null);
	                    registeredOwnerDetails.setCreatedAt(ticketDetail[7] != null ? ticketDetail[7].toString() : null);
	                    registeredOwnerDetails.setStatus(ticketDetail[8] != null ? ticketDetail[8].toString() : null);
	                    registeredOwnerDetails.setState(ticketDetail[9] != null ? ticketDetail[9].toString() : null);
	                    registeredOwnerDetails.setCity(ticketDetail[10] != null ? ticketDetail[10].toString() : null);
	                    registeredOwnerDetails.setAssignedTo(ticketDetail[11] != null ? ticketDetail[11].toString() : null);
	                    registeredOwnerDetails.setAssignedToName(ticketDetail[12] != null ? ticketDetail[12].toString() : null);
	                    registeredOwnerDetails.setDescription(ticketDetail[13] != null ? ticketDetail[13].toString() : null);
	                    registeredOwnerDetails.setType(ZoyConstant.LEAD_GEN);
	                }

	                for (Object[] historyDetail : ticketHistory) {
	                    UserTicketHistoryDTO historyDTO = new UserTicketHistoryDTO();
	                    historyDTO.setUserHelpRequestId(historyDetail[0] != null ? historyDetail[0].toString() : null);
	                    historyDTO.setCreatedAt(historyDetail[1] != null ? Timestamp.valueOf(historyDetail[1].toString()) : null);
	                    historyDTO.setUserEmail(historyDetail[2] != null ? historyDetail[2].toString() : null);
	                    historyDTO.setDescription(historyDetail[3] != null ? historyDetail[3].toString() : null);
	                    historyDTO.setRequestStatus(historyDetail[4] != null ? historyDetail[4].toString() : null);

	                    userTicketHistoryList.add(historyDTO);
	                }

	                registeredOwnerDetails.setUserTicketHistory(userTicketHistoryList);
	                response.setMessage("Support ticket details fetched successfully.");
	                response.setData(registeredOwnerDetails);
	            } else  if(updateStatus.getInquiryType().equals(ZoyConstant.SUPPORT_TICKET)){
	                List<Object[]> complaintDetails = userHelpRequestRepository.getComplaintTicketDetails(
	                    updateStatus.getInquiryNumber(), updateStatus.getStatus());
	                List<Object[]> complaintHistory = userHelpRequestRepository.getComplaintTicketHistory(updateStatus.getInquiryNumber());

	                if (complaintDetails.isEmpty()) {
	                    response.setStatus(HttpStatus.NOT_FOUND.value());
	                    response.setError("No complaint ticket details found.");
	                    return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	                }

	                ComplaintTicketDTO complaintTicket = new ComplaintTicketDTO();
	                List<UserTicketHistoryDTO> complaintHistoryList = new ArrayList<>();

	                for (Object[] complaintDetail : complaintDetails) {
	                	complaintTicket.setTickeNumber(updateStatus.getInquiryNumber());
	                    complaintTicket.setName(complaintDetail[0] != null ? complaintDetail[0].toString() : null);
	                    complaintTicket.setPropertyName(complaintDetail[1] != null ? complaintDetail[1].toString() : null);
	                    complaintTicket.setCategoriesName(complaintDetail[2] != null ? complaintDetail[2].toString() : null);
	                    complaintTicket.setDescription(complaintDetail[3] != null ? complaintDetail[3].toString() : null);
	                    complaintTicket.setUrgency(complaintDetail[4] != null ? complaintDetail[4].toString() : null);
	                    complaintTicket.setStatus(complaintDetail[5] != null ? complaintDetail[5].toString() : null);
	                    complaintTicket.setCreatedAt(complaintDetail[6] != null ? Timestamp.valueOf(complaintDetail[6].toString()) : null);
	                    complaintTicket.setUpdatedAt(complaintDetail[7] != null ? Timestamp.valueOf(complaintDetail[7].toString()) : null);
	                    complaintTicket.setAssignTo(complaintDetail[8] != null ? complaintDetail[8].toString() : null);
	                    complaintTicket.setAssignToName(complaintDetail[9] != null ? complaintDetail[9].toString() : null);
	                    complaintTicket.setMobile(complaintDetail[11] != null ? complaintDetail[11].toString() : null);
	                    complaintTicket.setAddress(complaintDetail[12] != null ? complaintDetail[12].toString() : null);
	                    String imageLinks = complaintDetail[10] != null ? complaintDetail[10].toString() : null;
	                    complaintTicket.setType(ZoyConstant.SUPPORT_TICKET);
	                    StringBuilder finalImageUrls = new StringBuilder();

	                    if (!StringUtils.isEmpty(imageLinks)) {
	                        String[] imageArray = imageLinks.split(",");

	                        for (int i = 0; i < imageArray.length; i++) {
	                            String imageUrl = imageArray[i].trim();
	                            String preSignedUrl = zoyAdminService.generatePreSignedUrl(userDocBucketName,imageUrl);

	                            finalImageUrls.append(preSignedUrl);

	                            if (i < imageArray.length - 1) {
	                                finalImageUrls.append(",");
	                            }
	                        }
	                    }
	                    String combinedPreSignedUrls = finalImageUrls.toString();	                    
	                    zoyAdminService.generatePreSignedUrl(imageLinks, imageLinks);
	                    complaintTicket.setImagesUrls(combinedPreSignedUrls != null ? combinedPreSignedUrls : null);
	                }

	                for (Object[] historyDetail : complaintHistory) {
	                    UserTicketHistoryDTO historyDTO = new UserTicketHistoryDTO();
	                    historyDTO.setUserHelpRequestId(historyDetail[0] != null ? historyDetail[0].toString() : null);
	                    historyDTO.setCreatedAt(historyDetail[1] != null ? Timestamp.valueOf(historyDetail[1].toString()) : null);
	                    historyDTO.setUserEmail(historyDetail[2] != null ? historyDetail[2].toString() : null);
	                    historyDTO.setDescription(historyDetail[3] != null ? historyDetail[3].toString() : null);
	                    historyDTO.setRequestStatus(historyDetail[4] != null ? historyDetail[4].toString() : null);

	                    complaintHistoryList.add(historyDTO);
	                }

	                complaintTicket.setUserTicketHistory(complaintHistoryList);
	                response.setMessage("Complaint ticket details fetched successfully.");
	                response.setData(complaintTicket);
	            }else {
	            	 response.setStatus(HttpStatus.NOT_FOUND.value());
	                 response.setError("No support ticket details found.");
	                 return new ResponseEntity<>(gson.toJson(response), HttpStatus.NOT_FOUND);
	            }
	        } else {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Ticket number is missing.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        response.setStatus(HttpStatus.OK.value());
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);
	    } catch (Exception e) {
	        log.error("Error getting ticket details: updateInquiryStatus", e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError("Internal server error");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	
	private void emailSend(final String subject,final String message,final List<String> to) {
		Email email = new Email();
        email.setFrom(fromEmailId);
        email.setTo(to);
        email.setSubject(subject);
        email.setBody(message);
        email.setContent("text/html");
        emailService.sendEmail(email, null);
	}
	
	private String prepareEmailContentAssign(String name, String description, final String urgency, String currentDate,
			String inquiryNumber) {
		 final String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + "<p>Hi " +name + ",</p>"
	                + "<p>A new support ticket has been assigned to you. Please find the details below: </p>"
	                + "<p>"
	                + "<strong>Ticket ID:</strong> " + inquiryNumber + "<br>"
	                + "<strong>Issue Summary:</strong>" + description + "<br>"
	                + "<strong>Priority:</strong>" + urgency + "<br>"
	                + "<strong>Submitted On:</strong>" + currentDate + "<br>"
	                + "</p>"
	                +"<p><strong>Action Required:</strong><br>\n"
	                + "Please review the ticket and begin working on a resolution and update the status.\n"
	                + "</p>"
	                + "<p class='footer' style='margin-top: 20px;'>"
	                + "Best regards,<br>"
	                + "ZOY Support."
	                + "</p>"
	                + "</body>"
	                + "</html>";
		return message;
	}
	
	
	private String prepareEmailContentOpenLead(String name, String description,String currentDate,
			String inquiryNumber) {
		 final String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + "<p>Dear " +name + ",</p>"
	                + "<p>Thank you for reaching out to us. We’ve received your request and have opened a support ticket to assist you. </p>"
	                + "<p>"
	                + "<strong>Ticket ID:</strong> " + inquiryNumber + "<br>"
	                + "<strong>Subject:</strong>" + description + "<br>"
	                + "<strong>Date Opened:</strong>" + currentDate + "<br>"
	                + "</p>"
	                +"<p>Our support team is currently reviewing your request and will resolve it as soon as possible.<br>Thank you for your patience. \n"
	                + "</p>"
	                + "<p class='footer' style='margin-top: 20px;'>"
	                + "Best regards,<br>"
	                + "ZOY Support."
	                + "</p>"
	                + "</body>"
	                + "</html>";
		return message;
	}
	
	private String prepareEmailContentClose(String name, String inquiryNumber) {
		 final String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + "<p>Dear " +name + ",</p>"
	                + "<p>We’re writing to let you know that your support ticket [#"+inquiryNumber+"] has been successfully resolved and is now closed. </p>"
	                + "<p>If you feel that your issue has not been fully resolved or you have any further questions, please don’t hesitate to reopen the ticket within 48 hours. </p>"
	                +" <p>Thank you for reaching out to our support team. We’re always here to help! </p>"
	                + "<p class='footer' style='margin-top: 20px;'>"
	                + "Best regards,<br>"
	                + "ZOY Support."
	                + "</p>"
	                + "</body>"
	                + "</html>";
		return message;
	}
	
	private String prepareEmailContentReopened(String name, String inquiryNumber) {
		 final String message = "<html>"
	                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
	                + "<p>Dear " +name + ",</p>"
	                + "<p>We wanted to inform you that your support ticket ["+inquiryNumber+"] has been reopened for further review. </p>"
	                + "<p>Our team is currently looking into the issue, and we’ll provide you with an update as soon as possible.</p>"
	                +" <p>Thank you for your patience and continued cooperation.</p>"
	                + "<p class='footer' style='margin-top: 20px;'>"
	                + "Best regards,<br>"
	                + "ZOY Support."
	                + "</p>"
	                + "</body>"
	                + "</html>";
		return message;
	}


	@Override
	public ResponseEntity<String> getImagesForEachTickets(String inquiryNumber) {
	    ResponseBody response = new ResponseBody();

	    try {
	        if (StringUtils.isBlank(inquiryNumber)) {
	            response.setStatus(HttpStatus.BAD_REQUEST.value());
	            response.setError("Inquiry number is required.");
	            return new ResponseEntity<>(gson.toJson(response), HttpStatus.BAD_REQUEST);
	        }

	        List<String> imageData = userHelpRequestRepository.getImages(inquiryNumber);

	        ComplaintTicketDTO complaintTicket = new ComplaintTicketDTO();

	        if (imageData != null && !imageData.isEmpty()) {
	            List<String> imageUrls = new ArrayList<>();

	            for (String image : imageData) {
	                if (StringUtils.isNotBlank(image)) {
	                    String preSignedUrl = zoyAdminService.generatePreSignedUrl(userDocBucketName, image.trim());
	                    imageUrls.add(preSignedUrl);
	                }
	            }

	            complaintTicket.setImagesUrls(String.join(",", imageUrls));
	        } else {
	            complaintTicket.setImagesUrls("");
	        }

	        response.setStatus(HttpStatus.OK.value());
	        response.setMessage("Ticket images fetched successfully.");
	        response.setData(complaintTicket);
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.OK);

	    } catch (Exception e) {
	        log.error("Error in GetImagesForEachTickets", e);
	        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setError("Internal server error occurred while fetching ticket images.");
	        return new ResponseEntity<>(gson.toJson(response), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	/*
	 * End coding for admin support
	 */

}