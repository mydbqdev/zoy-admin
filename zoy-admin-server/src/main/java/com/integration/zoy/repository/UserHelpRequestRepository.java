package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.integration.zoy.entity.UserHelpRequest;


@Repository
public interface UserHelpRequestRepository extends JpaRepository<UserHelpRequest,String>{
	
	@Query(value = "SELECT  "
			+ "    case when um.user_first_name is null then zpod.pg_owner_name else um.user_first_name || ' ' || um.user_last_name end AS username, "
			+ "    zppd.property_name, "
			+ "    COALESCE(puhsc.categories_name, '') || CASE WHEN puhdsc.sub_categories_name IS NOT NULL  "
			+ "    THEN ' - ' || puhdsc.sub_categories_name ELSE '' END AS category_name, "
			+ "    uhr.description, "
			+ "    uhr.urgency, "
			+ "    uhr.request_status, "
			+ "    uhr.created_at, "
			+ "    uhr.update_at, "
			+ "    uhr.assign_to_email, "
			+ "    uhr.assign_to_name, "
			+ "    STRING_AGG(uhri.user_help_request_image_url, ',') AS images, "
			+ "    case when um.user_mobile is null then zpod.pg_owner_mobile else um.user_mobile end, "
			+ "    zppd.property_house_area "
			+ "FROM pgusers.user_help_request uhr "
			+ "JOIN pgcommon.pg_user_help_desk_categories puhsc  "
			+ "    ON uhr.categories_id = puhsc.categories_id "
			+ "left join pgcommon.pg_user_help_desk_sub_categories puhdsc  "
			+ "	on puhdsc.sub_categories_id =uhr.sub_categories_id  "
			+ "LEFT JOIN pgusers.user_request_images uri  "
			+ "    ON uri.user_help_request_id = uhr.user_help_request_id "
			+ "LEFT JOIN pgusers.user_help_request_images uhri  "
			+ "    ON uhri.user_help_request_image_id = uri.user_help_request_image_id "
			+ "LEFT JOIN pgusers.user_master um  "
			+ "    ON uhr.user_id = um.user_id "
			+ "left join pgowners.zoy_pg_owner_details zpod on zpod.pg_owner_id =uhr.partner_id  "
			+ "LEFT JOIN pgowners.zoy_pg_property_details zppd  "
			+ "    ON uhr.property_id = zppd.property_id "
			+ "WHERE uhr.user_help_request_id = :registerId "
			+ "GROUP BY  "
			+ "    uhr.user_help_request_id,um.user_first_name,um.user_last_name,zppd.property_name,puhsc.categories_name, "
			+ "    uhr.description,uhr.urgency,uhr.request_status,uhr.created_at,uhr.update_at,uhr.assign_to_email, "
			+ "    uhr.assign_to_name,zppd.property_house_area,um.user_mobile,zpod.pg_owner_name,zpod.pg_owner_mobile,puhdsc.sub_categories_name", nativeQuery = true)
	List<Object[]> getComplaintTicketDetails(@Param("registerId")String registerId);
	
	@Query(value = "select  "
			+ "user_help_request_id, "
			+ "created_at, "
			+ "user_email, "
			+ "description, "
			+ "request_status  "
			+ "from pgusers.user_help_request_history where user_help_request_id =:registerId order by created_at desc", nativeQuery = true)
	List<Object[]> getComplaintTicketHistory(@Param("registerId")String registerId);
	
	@Query(value = "select ud.user_email ,ud.user_first_name || ' ' || ud.user_last_name AS username FROM pgusers.user_help_request uhr\n"
			+ "left join pgusers.user_master ud on ud.user_id =uhr.user_id \n"
			+ "where uhr.user_help_request_id=:registerId", nativeQuery = true)
	List<Object[]> getCompainUserEmail(@Param("registerId")String registerId);
	

	@Query(value="SELECT user_help_request_image_url FROM pgusers.user_request_images uri  "
	        + "LEFT JOIN pgusers.user_help_request_images uhri ON uri.user_help_request_image_id = uhri.user_help_request_image_id  "
	        + "WHERE uri.user_help_request_id = :registerId", nativeQuery=true)
	List<String> getImages(@Param("registerId") String registerId);
	
	@Modifying
	@Transactional
	@Query(value = "UPDATE pgusers.user_help_request\r\n"
			+ "SET request_status = 'Closed'\r\n"
			+ "WHERE request_status = 'Resolved'\r\n"
			+ "AND update_at <= (CAST(:timeZone AS timestamp) - INTERVAL '48 hours')", nativeQuery = true)
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
