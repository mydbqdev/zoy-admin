package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.entity.UserHelpRequest;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface UserHelpRequestRepository extends JpaRepository<UserHelpRequest,String>{
	
	@Query(value = "SELECT \r\n"
//			+ "    uhr.user_help_request_id,\r\n"
			+ "    um.user_first_name || ' ' || um.user_last_name AS username,\r\n"
			+ "    zppd.property_name,\r\n"
			+ "    puhsc.categories_name,\r\n"
			+ "    uhr.description,\r\n"
			+ "    uhr.urgency,\r\n"
			+ "    uhr.request_status,\r\n"
			+ "    uhr.created_at,\r\n"
			+ "    uhr.update_at,\r\n"
			+ "    uhr.assign_to_email,\r\n"
			+ "    uhr.assign_to_name,\r\n"
			+ "    STRING_AGG(uhri.user_help_request_image_url, ',') AS images,\r\n"
			+ "    um.user_mobile\r\n,"
			+ "    zppd.property_house_area\r\n"
			+ "FROM pgusers.user_help_request uhr\r\n"
			+ "JOIN pgcommon.pg_user_help_desk_categories puhsc \r\n"
			+ "    ON uhr.categories_id = puhsc.categories_id\r\n"
			+ "LEFT JOIN pgusers.user_request_images uri \r\n"
			+ "    ON uri.user_help_request_id = uhr.user_help_request_id\r\n"
			+ "LEFT JOIN pgusers.user_help_request_images uhri \r\n"
			+ "    ON uhri.user_help_request_image_id = uri.user_help_request_image_id\r\n"
			+ "LEFT JOIN pgusers.user_master um \r\n"
			+ "    ON uhr.user_id = um.user_id\r\n"
			+ "LEFT JOIN pgowners.zoy_pg_property_details zppd \r\n"
			+ "    ON uhr.property_id = zppd.property_id\r\n"
			+ "WHERE uhr.user_help_request_id = :registerId \r\n"
			+ "  AND uhr.request_status = :status\r\n"
			+ "GROUP BY \r\n"
			+ "    uhr.user_help_request_id,um.user_first_name,um.user_last_name,zppd.property_name,puhsc.categories_name,\r\n"
			+ "    uhr.description,uhr.urgency,uhr.request_status,uhr.created_at,uhr.update_at,uhr.assign_to_email,uhr.assign_to_name,zppd.property_house_area,um.user_mobile", nativeQuery = true)
	List<Object[]> getComplaintTicketDetails(@Param("registerId")String registerId,@Param("status")String status);
	
	@Query(value = "select \r\n"
			+ "user_help_request_id,\r\n"
			+ "created_at,\r\n"
			+ "user_email,\r\n"
			+ "description,\r\n"
			+ "request_status \r\n"
			+ "from pgusers.user_help_request_history where user_help_request_id =:registerId order by created_at desc", nativeQuery = true)
	List<Object[]> getComplaintTicketHistory(@Param("registerId")String registerId);
	
	@Query(value = "select ud.user_email ,ud.user_first_name || ' ' || ud.user_last_name AS username FROM pgusers.user_help_request uhr\n"
			+ "left join pgusers.user_master ud on ud.user_id =uhr.user_id \n"
			+ "where uhr.user_help_request_id=:registerId", nativeQuery = true)
	List<Object[]> getCompainUserEmail(@Param("registerId")String registerId);
	

	@Query(value="SELECT user_help_request_image_url FROM pgusers.user_request_images uri \r\n"
	        + "LEFT JOIN pgusers.user_help_request_images uhri ON uri.user_help_request_image_id = uhri.user_help_request_image_id \r\n"
	        + "WHERE uri.user_help_request_id = :registerId", nativeQuery=true)
	List<String> getImages(@Param("registerId") String registerId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE pgowners.zoy_pg_registered_owner_details " +
	               "SET status = 'Closed' " +
	               "WHERE status = 'Resolved' " +
	               "AND ts <= (CAST(:timeZone AS date) - interval '48 hours')", nativeQuery = true)
	void updateResolvedRequestsToClosedAfter48Hours(String timeZone);
	
	@Query(value = "  SELECT  \r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgowners.zoy_pg_registered_owner_details \r\n"
			+ "        WHERE status IN ('New', 'Open') \r\n"
			+ "          AND assign_to_name IS NULL \r\n"
			+ "          AND assign_to_email IS NULL\r\n"
			+ "    ) +\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgusers.user_help_request \r\n"
			+ "        WHERE request_status IN ('New', 'Open') \r\n"
			+ "          AND assign_to_email IS NULL\r\n"
			+ "          AND assign_to_name IS NULL\r\n"
			+ "    ) AS total_pending_count,\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgowners.zoy_pg_registered_owner_details \r\n"
			+ "        WHERE status IN ('Closed', 'Resolved')\r\n"
			+ "    ) +\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgusers.user_help_request \r\n"
			+ "        WHERE request_status IN ('Closed', 'Resolved')\r\n"
			+ "    ) AS total_closed_count,\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgusers.user_help_request\r\n"
			+ "        WHERE request_status = 'Cancelled'\r\n"
			+ "    ) +\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgowners.zoy_pg_registered_owner_details\r\n"
			+ "        WHERE status = 'Cancelled'\r\n"
			+ "    ) AS total_cancelled_count,\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgusers.user_help_request\r\n"
			+ "        WHERE request_status IN ('Open', 'In Progress', 'Reopen', 'On Hold','New')\r\n"
			+ "          AND assign_to_name IS NOT NULL\r\n"
			+ "          or assign_to_email IS NOT NULL\r\n"
			+ "    ) +\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgowners.zoy_pg_registered_owner_details\r\n"
			+ "        WHERE status IN ('Open', 'In Progress', 'Reopen', 'On Hold','New')\r\n"
			+ "          AND assign_to_name IS NOT NULL\r\n"
			+ "          AND assign_to_email IS NOT NULL\r\n"
			+ "    ) AS total_open_count,\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgusers.user_help_request\r\n"
			+ "    ) +\r\n"
			+ "    (\r\n"
			+ "        SELECT COUNT(*) \r\n"
			+ "        FROM pgowners.zoy_pg_registered_owner_details\r\n"
			+ "    ) AS total_issues", nativeQuery = true)
	List<Object[]> getTicketInfo();

	
}
