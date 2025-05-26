package com.integration.zoy.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.RegisteredPartner;

import io.lettuce.core.dynamic.annotation.Param;
@Repository
public interface RegisteredPartnerDetailsRepository extends JpaRepository<RegisteredPartner,Long>{
	
	@Query(value = "select (select count(*) from pgowners.zoy_pg_registered_owner_details) as registeredOwners", nativeQuery = true)
	List<Object[]> getOwnerCardsDetails();
	
	@Query(value = "SELECT "
			+ "(SELECT COUNT(*) "
			+ " FROM pgowners.zoy_pg_registered_owner_details zrd "
			+ " WHERE zrd.register_id NOT IN ( "
			+ "     SELECT szrd.register_id "
			+ "     FROM pgowners.zoy_pg_registered_owner_details szrd "
			+ "     JOIN pgadmin.pg_owner_master pom "
			+ "     ON szrd.register_id = pom.register_id "
			+ " )) AS registeredOwnerCount,       "
			+ "(SELECT COUNT(*) "
			+ " FROM pgadmin.pg_owner_master pzm "
			+ " JOIN pgcommon.user_profile up ON pzm.email_id = up.email_id "
			+ " WHERE up.enabled = false) AS disabledUsersCount, "
			+ "(SELECT COUNT(*) "
			+ " FROM pgowners.zoy_pg_owner_details o) AS totalOwnersCount, "
			+ "(SELECT COUNT(*) "
			+ " FROM ( "
			+ "     SELECT o.pg_owner_id "
			+ "     FROM pgowners.zoy_pg_owner_details o "
			+ "     LEFT JOIN pgowners.zoy_pg_property_details p "
			+ "     ON o.pg_owner_id = p.pg_owner_id "
			+ "     GROUP BY o.pg_owner_id "
			+ "     HAVING COUNT(p.property_id) = 0 "
			+ " ) AS subquery) AS ownersWithNoPropertiesCount",
	        nativeQuery = true)
	List<Object[]> getOwnerStatistics();

	@Query(value=" SELECT * "
			+ " FROM pgowners.zoy_pg_registered_owner_details zrd "
			+ " WHERE zrd.register_id NOT IN ( "
			+ "     SELECT szrd.register_id "
			+ "     FROM pgowners.zoy_pg_registered_owner_details szrd "
			+ "     JOIN pgadmin.pg_owner_master pom "
			+ "     ON szrd.register_id = pom.register_id "
			+ " )" ,nativeQuery=true)
	List<RegisteredPartner> getAllRegisteredUsers();
	
	Optional<RegisteredPartner> findByRegisterId(String registerId);
	
	@Query(value = "SELECT  "
			+ "concat(zprod.firstname,'',zprod.lastname) as name, "
			+ "email AS OwnerEmail, "
			+ "mobile AS Mobile, "
			+ "property_name AS PropertyName, "
			+ "address as Address, "
			+ "pincode as pincode, "
			+ "inquired_for AS InquiredFor, "
			+ "ts AS Date, "
			+ "status AS Status, "
			+ "state AS State, "
			+ "city AS City, "
			+ "assign_to_name AS AssignedTo, "
			+ "assign_to_name as AssignedToName, "
			+ "description AS Description "
			+ "FROM "
			+ "pgowners.zoy_pg_registered_owner_details zprod where register_id=:registerId and status=:status", nativeQuery = true)
	List<Object[]> getOwnerTicketDetails(@Param("registerId")String registerId,@Param("status")String status);
	

}
