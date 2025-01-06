package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDetails;

@Repository
public interface ZoyPgPropertyDetailsRepository extends JpaRepository<ZoyPgPropertyDetails, String> {
	
	@Query(value ="SELECT DISTINCT zppd.property_city FROM pgowners.zoy_pg_property_details zppd", nativeQuery = true)
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

}