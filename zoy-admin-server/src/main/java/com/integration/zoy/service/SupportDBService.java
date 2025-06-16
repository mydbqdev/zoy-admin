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
							  "SELECT * FROM (\n" +
				                        "SELECT zprod.register_id AS ticket_id, zprod.ts AS created_date, true AS priority, zprod.inquired_for AS support_type, " +
				                        "zprod.assign_to_email AS assign_email, zprod.assign_to_name AS assign_name, zprod.status AS status, " +
				                        "'LEAD_GEN' AS type, 'SUPPORT_TICKET' AS type, zprod.firstname || ' ' || zprod.lastname AS full_name \n" +
				                        "FROM pgowners.zoy_pg_registered_owner_details zprod) tkt WHERE 1=1 \n"
				                );
				}else {
			queryBuilder.append(
					"SELECT * FROM (\r\n"
					+ "    SELECT \r\n"
					+ "        zprod.register_id AS ticket_id,\r\n"
					+ "        zprod.ts AS created_date,\r\n"
					+ "        true AS priority,\r\n"
					+ "        zprod.inquired_for AS support_type,\r\n"
					+ "        zprod.assign_to_email AS assign_email,\r\n"
					+ "        zprod.assign_to_name AS assign_name,\r\n"
					+ "        zprod.status AS status,\r\n"
					+ "        'LEAD_GEN' AS type,\r\n"
					+ "        zprod.firstname || ' ' || zprod.lastname AS full_name\r\n"
					+ "    FROM pgowners.zoy_pg_registered_owner_details zprod\r\n"
					+ "    UNION ALL\r\n"
					+ "    SELECT \r\n"
					+ "        helpreq.user_help_request_id AS ticket_id,\r\n"
					+ "        helpreq.created_at AS created_date,\r\n"
					+ "        helpreq.urgency AS priority,\r\n"
					+ "        CONCAT('Support for, ', cat.categories_name) AS support_type,\r\n"
					+ "        helpreq.assign_to_email AS assign_email,\r\n"
					+ "        helpreq.assign_to_name AS assign_name,\r\n"
					+ "        helpreq.request_status AS status,\r\n"
					+ "        'SUPPORT_TICKET' AS type,\r\n"
					+ "        um.user_first_name || ' ' || um.user_last_name AS full_name\r\n"
					+ "    FROM pgusers.user_help_request helpreq\r\n"
					+ "    JOIN pgusers.user_master um ON um.user_id = helpreq.user_id\r\n"
					+ "    LEFT JOIN pgcommon.pg_user_help_desk_categories cat ON cat.categories_id = helpreq.categories_id\r\n"
					+ ") tkt \r\n"
					+ "WHERE 1=1 "
					);
				}
			}else {
				if(isFinanceUser) {
					queryBuilder.append(
							 "SELECT * FROM (\n" +
				                        "SELECT zprod.register_id AS ticket_id, zprod.ts AS created_date, true AS priority, zprod.inquired_for AS support_type, " +
				                        "zprod.assign_to_email AS assign_email, zprod.assign_to_name AS assign_name, zprod.status AS status, " +
				                        "'LEAD_GEN' AS type, 'SUPPORT_TICKET' AS type, zprod.firstname || ' ' || zprod.lastname AS full_name \n" +
				                        "FROM pgowners.zoy_pg_registered_owner_details zprod) tkt WHERE 1=1 \n"
				                );
				}else {
				queryBuilder.append(
						"SELECT * \r\n"
						+ "FROM (\r\n"
						+ "    SELECT \r\n"
						+ "        zprod.register_id AS ticket_id,\r\n"
						+ "        zprod.ts AS created_date,\r\n"
						+ "        true AS priority,\r\n"
						+ "        zprod.inquired_for AS support_type,\r\n"
						+ "        zprod.assign_to_email AS assign_email,\r\n"
						+ "        zprod.assign_to_name AS assign_name,\r\n"
						+ "        zprod.status AS status,\r\n"
						+ "        'LEAD_GEN' AS type,\r\n"
						+ "        zprod.firstname || ' ' || zprod.lastname AS full_name \r\n"
						+ "    FROM pgowners.zoy_pg_registered_owner_details zprod\r\n"
						+ "    UNION ALL\r\n"
						+ "    SELECT \r\n"
						+ "        helpreq.user_help_request_id AS ticket_id,\r\n"
						+ "        helpreq.created_at AS created_date,\r\n"
						+ "        helpreq.urgency AS priority,\r\n"
						+ "        CONCAT('Support for, ', cat.categories_name) AS support_type,\r\n"
						+ "        helpreq.assign_to_email AS assign_email,\r\n"
						+ "        helpreq.assign_to_name AS assign_name,\r\n"
						+ "        helpreq.request_status AS status,\r\n"
						+ "        'SUPPORT_TICKET' AS type,\r\n"
						+ "        um.user_first_name || ' ' || um.user_last_name AS full_name \r\n"
						+ "    FROM pgusers.user_help_request helpreq\r\n"
						+ "    JOIN pgusers.user_master um ON um.user_id = helpreq.user_id\r\n"
						+ "    LEFT JOIN pgcommon.pg_user_help_desk_categories cat ON cat.categories_id = helpreq.categories_id\r\n"
						+ ") tkt \r\n"
						+ "WHERE 1=1 "
						);
				}
			}
			Map<String, Object> parameters = new HashMap<>();


			if (paginationRequest.getFilter().getSearchText() != null && !paginationRequest.getFilter().getSearchText().isEmpty()) {
				queryBuilder.append(" AND ( \r\n");
				queryBuilder.append(" LOWER(tkt.status) LIKE LOWER('%' || :status || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.support_type) LIKE LOWER('%' || :typesupport || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.ticket_id) LIKE LOWER('%' || :tktid || '%') \r\n");
				queryBuilder.append( "OR LOWER(tkt.assign_name) LIKE LOWER('%' || :assignname || '%') \r\n");
			    queryBuilder.append(" OR LOWER(tkt.full_name) LIKE LOWER('%' || :fullname || '%') \r\n");
				queryBuilder.append(" ) \r\n");
				parameters.put("status", paginationRequest.getFilter().getSearchText());
				parameters.put("typesupport", paginationRequest.getFilter().getSearchText());
				parameters.put("tktid", paginationRequest.getFilter().getSearchText());
				parameters.put("assignname", paginationRequest.getFilter().getSearchText());
			    parameters.put("fullname", paginationRequest.getFilter().getSearchText());

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
				case "full_name":
				    sort = "full_name";
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
			    dto.setStatus(row[6] != null && row[5] != null && row[4] != null ? (String) row[6] : "New");
			    dto.setType(row[7] != null ? (String) row[7] : "");
			    dto.setTicketRaisedby(row[8] != null ? (String) row[8] : "");
			    return dto;
			}).collect(Collectors.toList());
			
			return new CommonResponseDTO<>(registerLeadDetails, filterCount);
		} catch (Exception e) {
			log.error("Error :{}",e.getMessage());
			throw new WebServiceException("Error retrieving support ticket list: " + e.getMessage());
		}
	}
}