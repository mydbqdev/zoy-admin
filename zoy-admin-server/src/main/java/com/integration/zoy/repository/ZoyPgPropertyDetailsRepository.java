package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface ZoyPgPropertyDetailsRepository extends JpaRepository<ZoyPgPropertyDetails, String> {

	@Query(value ="SELECT DISTINCT zppd.property_city  "
			+ "FROM pgowners.zoy_pg_property_details zppd  "
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


	@Query(value = "select "
			+ "	( "
			+ "	select "
			+ "		COUNT(*) as active_properties_count "
			+ "	from "
			+ "		( "
			+ "		select "
			+ "			distinct zppd.property_id "
			+ "		from "
			+ "			pgowners.zoy_pg_property_details zppd "
			+ "		join pgowners.zoy_pg_owner_booking_details zpb on "
			+ "			zppd.property_id = zpb.property_id "
			+ "		join pgowners.zoy_pg_owner_details zpod on "
			+ "			zppd.pg_owner_id = zpod.pg_owner_id "
			+ "		join pgusers.user_bookings ub on "
			+ "			zpb.booking_id = ub.user_bookings_id "
			+ "		where "
			+ "			ub.user_bookings_web_check_in = true "
			+ "			and ub.user_bookings_web_check_out = false "
			+ "			and ub.user_bookings_is_cancelled = false ) as subquery) as potentialPropertiesCount, "
			+ "	( "
			+ "	select "
			+ "		COUNT(*) "
			+ "	from "
			+ "		( "
			+ "		select "
			+ "			distinct zpd.property_id "
			+ "		from "
			+ "			pgowners.zoy_pg_property_details as zpd "
			+ "		join pgowners.zoy_pg_owner_details as zpod on "
			+ "			zpd.pg_owner_id = zpod.pg_owner_id "
			+ "		left join pgowners.zoy_pg_owner_booking_details as zpobd on "
			+ "			zpd.property_id = zpobd.property_id "
			+ "		left join pgusers.user_bookings as ub on "
			+ "			zpobd.booking_id = ub.user_bookings_id "
			+ "		where "
			+ "			zpd.property_id not in ( "
			+ "			select "
			+ "				distinct zpobd.property_id "
			+ "			from "
			+ "				pgowners.zoy_pg_owner_booking_details as zpobd "
			+ "			join pgusers.user_bookings as ub on "
			+ "				zpobd.booking_id = ub.user_bookings_id "
			+ "				and ( ub.user_bookings_is_cancelled = true "
			+ "					or ub.user_bookings_web_check_out = true ) ) "
			+ "		 ) as subquery) as nonPotentialPropertiesCount, "
			+ "	( "
			+ "	select "
			+ "		count(pom.*) "
			+ "	from "
			+ "		pgadmin.pg_owner_master pom "
			+ "	left join pgowners.zoy_pg_property_details zppd on "
			+ "		pom.zoy_code = zppd.zoy_code "
			+ "	where "
			+ "		zppd.zoy_code is null ) as upcomingPotentialPropertyCount " ,
			nativeQuery = true)
	List<Object[]> findPropertiesCardDetails();

	@Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(zoy_code, 7) AS INTEGER)), 0) " +
			"FROM pgadmin.pg_owner_master WHERE zoy_code LIKE CONCAT(:zoyCode, '%')", nativeQuery = true)
	Integer findZoyCodeCounter(@Param("zoyCode") String zoyCode);


}