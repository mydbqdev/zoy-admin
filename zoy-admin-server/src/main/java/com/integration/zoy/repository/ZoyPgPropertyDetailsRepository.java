package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface ZoyPgPropertyDetailsRepository extends JpaRepository<ZoyPgPropertyDetails, String> {

	@Query(value ="SELECT DISTINCT zppd.property_city \r\n"
			+ "FROM pgowners.zoy_pg_property_details zppd \r\n"
			+ "ORDER BY zppd.property_city ASC", nativeQuery = true)
	String[] findDistinctCities();


	@Query(value = "select zpod.pg_owner_id ||','||zpod.pg_owner_name||','||zpod.pg_owner_mobile , "
			+ "STRING_AGG(DISTINCT zppd.property_id ||'|'|| zppd.property_name , ',') AS propertyDetails  "
			+ "	from pgowners.zoy_pg_owner_details zpod join pgowners.zoy_pg_property_details zppd "
			+ "on zppd.pg_owner_id =zpod.pg_owner_id group by zpod.pg_owner_id ",nativeQuery = true)
	List<String[]> getOwnerPropertyDetails();


	@Query(value="SELECT ud.user_personal_name From pgusers.user_details ud Where ud.user_id=:userId",nativeQuery=true)
	String fetchingUserNameForRatings(@Param("userId") String userId);

	@Query(value = "select rr.review_replies_id,rr.rating_id ,rr.customer_id ,concat(um.user_first_name,' ',um.user_last_name),rr.partner_id ,zpod.pg_owner_name,"
			+ "rr.review,rr.created_at,rr.is_edited,rr.is_deleted,ud.user_profile_image,zpod.pg_owner_profile_image from pgcommon.review_replies rr  "
			+ "left join pgusers.user_master um on um.user_id =rr.customer_id  "
			+ "left join pgowners.zoy_pg_owner_details zpod on zpod.pg_owner_id =rr.partner_id  "
			+ "left join pgusers.user_details ud on ud.user_id =rr.customer_id "
			+ "where rating_id = :ratingId order by rr.created_at asc", 
			nativeQuery = true)	    
	List<String[]> findAllReviewsReplies(String ratingId);


	@Query(value = "SELECT " +
			"(SELECT COUNT(*) AS active_properties_count " +
			"FROM (" +
			"    SELECT " +
			"        zppd.property_id, " +
			"        zppd.property_name, " +
			"        zppd.property_contact_number, " +
			"        zppd.property_pg_email, " +
			"        zppd.property_house_area, " +
			"        zpb.fixed_rent, " +
			"        zpb.in_date, " +
			"        zpb.out_date " +
			"    FROM pgowners.zoy_pg_property_details zppd " +
			"    JOIN pgowners.zoy_pg_owner_booking_details zpb ON zppd.property_id = zpb.property_id " +
			"    JOIN pgowners.zoy_pg_owner_details zpod ON zppd.pg_owner_id = zpod.pg_owner_id " +
			"    JOIN pgusers.user_bookings ub ON zpb.booking_id = ub.user_bookings_id " +
			"    WHERE ub.user_bookings_web_check_in = true " +
			"      AND ub.user_bookings_web_check_out = false " +
			"      AND ub.user_bookings_is_cancelled = false " +
			"    GROUP BY " +
			"        zppd.property_id, " +
			"        zppd.property_name, " +
			"        zppd.property_contact_number, " +
			"        zppd.property_pg_email, " +
			"        zppd.property_house_area, " +
			"        zpb.fixed_rent, " +
			"        zpb.in_date, " +
			"        zpb.out_date " +
			"    HAVING COUNT(zpb.booking_id) > 0 " +
			") AS subquery) AS potentialPropertiesCount, " +

	        "(SELECT COUNT(*) " +
	        "FROM (" +
	        "    SELECT " +
	        "        zpod.pg_owner_name, " +
	        "        zpd.property_name, " +
	        "        zpd.property_contact_number, " +
	        "        zpd.property_pg_email, " +
	        "        zpd.property_house_area, " +
	        "        MAX(zpobd.out_date) AS last_out_date, " +
	        "        MAX(zpobd.in_date) AS last_in_date " +
	        "    FROM pgowners.zoy_pg_property_details AS zpd " +
	        "    JOIN pgowners.zoy_pg_owner_details AS zpod " +
	        "        ON zpd.pg_owner_id = zpod.pg_owner_id " +
	        "    LEFT JOIN pgowners.zoy_pg_owner_booking_details AS zpobd " +
	        "        ON zpd.property_id = zpobd.property_id " +
	        "    LEFT JOIN pgusers.user_bookings AS ub " +
	        "        ON zpobd.booking_id = ub.user_bookings_id " +
	        "    WHERE zpd.property_id NOT IN ( " +
	        "        SELECT DISTINCT zpobd.property_id " +
	        "        FROM pgowners.zoy_pg_owner_booking_details AS zpobd " +
	        "        JOIN pgusers.user_bookings AS ub " +
	        "            ON zpobd.booking_id = ub.user_bookings_id " +
	        "        AND ( " +
	        "            ub.user_bookings_is_cancelled = true " +
	        "            OR ub.user_bookings_web_check_out = true " +
	        "        ) " +
	        "    ) " +
	        "    GROUP BY " +
	        "        zpod.pg_owner_name, " +
	        "        zpd.property_name, " +
	        "        zpd.property_contact_number, " +
	        "        zpd.property_pg_email, " +
	        "        zpd.property_house_area " +
	        ") AS subquery) AS nonPotentialPropertiesCount, " +

	        "(SELECT COUNT(*) " +
	        "FROM (" +
	        "    SELECT DISTINCT " +
	        "        zpd.property_contact_number, " +
	        "        (SELECT COUNT(*) " +
	        "         FROM pgowners.zoy_pg_owner_booking_details AS zpobd2 " +
	        "         JOIN pgusers.user_bookings AS ub2 " +
	        "             ON zpobd2.booking_id = ub2.user_bookings_id " +
	        "         WHERE zpobd2.property_id = zpobd.property_id " +
	        "           AND zpobd2.in_date > CURRENT_DATE " +
	        "           AND ub2.user_bookings_is_cancelled = false " +
	        "           AND ub2.user_bookings_web_check_out = false " +
	        "           AND ub2.user_bookings_web_check_in = false) AS future_bookings " +
	        "    FROM pgowners.zoy_pg_owner_booking_details AS zpobd " +
	        "    JOIN pgusers.user_bookings AS ub " +
	        "        ON zpobd.booking_id = ub.user_bookings_id " +
	        "    JOIN pgowners.zoy_pg_property_details AS zpd " +
	        "        ON zpobd.property_id = zpd.property_id " +
	        "    JOIN pgowners.zoy_pg_owner_details AS zpod " +
	        "        ON zpd.pg_owner_id = zpod.pg_owner_id " +
	        "    WHERE zpobd.in_date > CURRENT_DATE " +
	        "      AND ub.user_bookings_is_cancelled = false " +
	        "      AND ub.user_bookings_web_check_out = false " +
	        "      AND ub.user_bookings_web_check_in = false " +
	        "      AND zpobd.property_id NOT IN ( " +
	        "          SELECT DISTINCT zpobd2.property_id " +
	        "          FROM pgowners.zoy_pg_owner_booking_details AS zpobd2 " +
	        "          JOIN pgusers.user_bookings AS ub2 " +
	        "              ON zpobd2.booking_id = ub2.user_bookings_id " +
	        "          WHERE ub2.user_bookings_is_cancelled = false " +
	        "            AND ub2.user_bookings_web_check_out = false " +
	        "            AND ub2.user_bookings_web_check_in = true " +
	        "      ) " +
	        ") AS subquery) AS upcomingPotentialPropertyCount " ,
	        nativeQuery = true)
	List<Object[]> findPropertiesCardDetails();

	@Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(zoy_code, 7) AS INTEGER)), 0) " +
			"FROM pgadmin.pg_owner_master WHERE zoy_code LIKE CONCAT(:zoyCode, '%')", nativeQuery = true)
	Integer findZoyCodeCounter(@Param("zoyCode") String zoyCode);


}