package com.integration.zoy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.integration.zoy.exception.WebServiceException;
import com.integration.zoy.model.SupportTicketDTO;
import com.integration.zoy.repository.RegisteredPartnerDetailsRepository;
import com.integration.zoy.utils.CommonResponseDTO;
import com.integration.zoy.utils.PaginationRequest;

@Service
public class SupportDBService implements SupportDBImpl{
	private static final Logger log = LoggerFactory.getLogger(SupportDBService.class);
	
	@Autowired
	RegisteredPartnerDetailsRepository registeredPartnerDetailsRepository;
	
	@Autowired
	EntityManager entityManager;

	
	@Override
	public CommonResponseDTO<SupportTicketDTO> zoySupportTicketList(PaginationRequest paginationRequest,boolean isClose,boolean isFinanceUser)  throws WebServiceException {
		try {
			StringBuilder queryBuilder = new StringBuilder();
			if (isClose) {
				// we will add extra column as closedOn for closed/resolved/cancelled status date
				if(isFinanceUser) {
					queryBuilder.append(
							"select * from (\n"
							+ "select zprod.register_id as ticket_id,zprod.ts as created_date,true as priority,zprod.inquired_for as support_type,zprod.assign_to_email as assign_email,zprod.assign_to_name as assign_name, zprod.status as status, 'LEAD_GEN' as type\n"
							+ "from pgowners.zoy_pg_registered_owner_details zprod)tkt WHERE 1=1   \r\n"
							);
				}else {
			queryBuilder.append(
					"select * from (\n"
					+ "select zprod.register_id as ticket_id,zprod.ts as created_date,true as priority,zprod.inquired_for as support_type,zprod.assign_to_email as assign_email,zprod.assign_to_name as assign_name, zprod.status as status, 'LEAD_GEN' as type\n"
					+ "from pgowners.zoy_pg_registered_owner_details zprod \n"
					+ "union all\n"
					+ "select helpreq.user_help_request_id as ticket_id,helpreq.created_at as created_date,helpreq.urgency as priority,CONCAT('Support for, ',cat.categories_name) as support_type,helpreq.assign_to_email as assign_email,helpreq.assign_to_name as assign_name,helpreq.request_status as status,'SUPPORT_TICKET' as type \n"
					+ "from pgusers.user_help_request helpreq\n"
					+ "left join pgcommon.pg_user_help_desk_categories cat on cat.categories_id =helpreq.categories_id\n"
					+ ")tkt WHERE 1=1   \r\n"
					);
				}
			}else {
				if(isFinanceUser) {
					queryBuilder.append(
							"select * from (\n"
							+ "select zprod.register_id as ticket_id,zprod.ts as created_date,true as priority,zprod.inquired_for as support_type,zprod.assign_to_email as assign_email,zprod.assign_to_name as assign_name, zprod.status as status, 'LEAD_GEN' as type\n"
							+ "from pgowners.zoy_pg_registered_owner_details zprod )tkt WHERE 1=1  \r\n"
							);
				}else {
				queryBuilder.append(
						"select * from (\n"
						+ "select zprod.register_id as ticket_id,zprod.ts as created_date,true as priority,zprod.inquired_for as support_type,zprod.assign_to_email as assign_email,zprod.assign_to_name as assign_name, zprod.status as status, 'LEAD_GEN' as type\n"
						+ "from pgowners.zoy_pg_registered_owner_details zprod \n"
						+ "union all\n"
						+ "select helpreq.user_help_request_id as ticket_id,helpreq.created_at as created_date,helpreq.urgency as priority,CONCAT('Support for, ',cat.categories_name) as support_type,helpreq.assign_to_email as assign_email,helpreq.assign_to_name as assign_name,helpreq.request_status as status,'SUPPORT_TICKET' as type \n"
						+ "from pgusers.user_help_request helpreq\n"
						+ "left join pgcommon.pg_user_help_desk_categories cat on cat.categories_id =helpreq.categories_id\n"
						+ ")tkt WHERE 1=1  \r\n"
						);
				}
			}
			Map<String, Object> parameters = new HashMap<>();


			if (paginationRequest.getFilter().getSearchText() != null && !paginationRequest.getFilter().getSearchText().isEmpty()) {
				queryBuilder.append("AND ( \r\n");
				queryBuilder.append(" LOWER(tkt.status) LIKE LOWER('%' || :status || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.support_type) LIKE LOWER('%' || :typesupport || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.ticket_id) LIKE LOWER('%' || :tktid || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.assign_name) LIKE LOWER('%' || :assignname || '%') \r\n");
				queryBuilder.append(" ) \r\n");
				parameters.put("status", paginationRequest.getFilter().getSearchText());
				parameters.put("typesupport", paginationRequest.getFilter().getSearchText());
				parameters.put("tktid", paginationRequest.getFilter().getSearchText());
				parameters.put("assignname", paginationRequest.getFilter().getSearchText());
			}
			
			if (paginationRequest.getFilter().getStartDate() != null && paginationRequest.getFilter().getEndDate() != null) {
				queryBuilder.append(" AND (tkt.created_date between CAST(:fromDate AS TIMESTAMP)  AND CAST(:toDate AS TIMESTAMP)) \r\n");
				parameters.put("fromDate", paginationRequest.getFilter().getStartDate());
				parameters.put("toDate", paginationRequest.getFilter().getEndDate());
			}
			
			if(null !=paginationRequest.getFilter().getEmail() && paginationRequest.getFilter().getEmail().length()>0) {
				queryBuilder.append(" AND LOWER(tkt.assign_email) = LOWER('"+paginationRequest.getFilter().getEmail()+"') \r\n");
			}


			if (isClose) {
				if(null !=paginationRequest.getFilter().getStatus() && paginationRequest.getFilter().getStatus().length()>0) {
					queryBuilder.append("AND LOWER(status) in "+paginationRequest.getFilter().getStatus()+" \r\n");
				}else {
					queryBuilder.append("AND LOWER(status) in ('close','closed','cancelled','cancel','resolved','resolve') \r\n");
				}
				//parameters.put("status", "close");
			}else {
				if(null !=paginationRequest.getFilter().getStatus() && paginationRequest.getFilter().getStatus().length()>0) {
					queryBuilder.append("AND LOWER(status) in "+paginationRequest.getFilter().getStatus()+" \r\n");
				}else {
					queryBuilder.append("AND LOWER(status) not in ('close','closed','cancelled','cancel','resolved','resolve') \r\n");
				}
				//parameters.put("status", "close");
			}
			
			if (paginationRequest.getIsUserActivity()) {
	            String assignedEmail = SecurityContextHolder.getContext().getAuthentication().getName();
	            if (isClose) {
	            	queryBuilder.append("AND (LOWER(tkt.assign_email) = LOWER(:assignedEmail))\r\n");
	            }else {
	            	 queryBuilder.append("AND (LOWER(tkt.assign_email) = LOWER(:assignedEmail) OR LOWER(tkt.assign_email) ='' OR tkt.assign_email IS NULL ) AND tkt.type='SUPPORT_TICKET' \r\n");
	            }
	            parameters.put("assignedEmail", assignedEmail);
	        }
			
			if (paginationRequest.getSortDirection() != null && !paginationRequest.getSortDirection().isEmpty()
					&& paginationRequest.getSortActive() != null) {
				String sort = "";
				switch (paginationRequest.getSortActive()) {
				case "ticket_id":
					sort = "ticket_id";
					break;
				case "created_date":
					sort = "created_date";
					break;
				case "ticket_type":
					sort = "support_type";
					break;
				case "priority":
					sort = "priority";
					break;
				case "assign_name":
					sort = "assign_name";
					break;
				case "status":
					sort = "status";
					break;
				default:
					sort = "ticket_id";
				}
				String sortDirection = paginationRequest.getSortDirection().equalsIgnoreCase("ASC") ? "ASC" : "DESC";
				queryBuilder.append(" ORDER BY ").append(sort).append(" ").append(sortDirection);
			} else {
				queryBuilder.append(" order by tkt.created_date desc ");
			}
			Query query = entityManager.createNativeQuery(queryBuilder.toString());
			parameters.forEach(query::setParameter);

			    int filterCount = query.getResultList().size();
				query.setFirstResult(paginationRequest.getPageIndex() * paginationRequest.getPageSize());
				query.setMaxResults(paginationRequest.getPageSize());

			List<Object[]> results = query.getResultList();
			List<SupportTicketDTO> registerLeadDetails = results.stream().map(row -> {
				SupportTicketDTO dto = new SupportTicketDTO();
			    dto.setTicket_id(row[0] != null ? (String) row[0] : "");
			    dto.setCreated_date(row[1] != null ? String.valueOf(row[1]) : "");
			    dto.setPriority(row[2] != null ? (boolean) row[2] : false);
			    dto.setTicket_type(row[3] != null ? (String) row[3] : "");
			   
			    dto.setAssign_email(row[4] != null ? (String) row[4] : "");
			    dto.setAssign_name(row[5] != null ? (String) row[5] : "");
			    dto.setStatus(row[6] != null && row[5] != null && row[4] != null ? (String) row[6] : (row[6] != null ? (String) row[6] :"New"));
			    dto.setType(row[7] != null ? (String) row[7] : "");
			    return dto;
			}).collect(Collectors.toList());
			
			return new CommonResponseDTO<>(registerLeadDetails, filterCount);
		} catch (Exception e) {
			log.error("Error :{}",e.getMessage());
			throw new WebServiceException("Error retrieving support ticket list: " + e.getMessage());
		}
	}
}
