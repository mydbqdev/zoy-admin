package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.RegisteredPartner;
@Repository
public interface RegisteredPartnerDetailsRepository extends JpaRepository<RegisteredPartner,Long>{
	
	@Query(value = "select (select count(*) from pgowners.zoy_pg_registered_owner_details) as registeredOwners", nativeQuery = true)
	List<Object[]> getOwnerCardsDetails();
	
	@Query(value = "SELECT\r\n"
			+ "(SELECT COUNT(*)\r\n"
			+ " FROM pgowners.zoy_pg_registered_owner_details zrd\r\n"
			+ " WHERE zrd.register_id NOT IN (\r\n"
			+ "     SELECT szrd.register_id\r\n"
			+ "     FROM pgowners.zoy_pg_registered_owner_details szrd\r\n"
			+ "     JOIN pgadmin.pg_owner_master pom\r\n"
			+ "     ON szrd.register_id = pom.register_id\r\n"
			+ " )) AS registeredOwnerCount,      \r\n"
			+ "(SELECT COUNT(*)\r\n"
			+ " FROM pgadmin.pg_owner_master pzm\r\n"
			+ " JOIN pgcommon.user_profile up ON pzm.email_id = up.email_id\r\n"
			+ " WHERE up.enabled = false) AS disabledUsersCount,\r\n"
			+ "(SELECT COUNT(*)\r\n"
			+ " FROM pgowners.zoy_pg_owner_details o) AS totalOwnersCount,\r\n"
			+ "(SELECT COUNT(*)\r\n"
			+ " FROM (\r\n"
			+ "     SELECT o.pg_owner_id\r\n"
			+ "     FROM pgowners.zoy_pg_owner_details o\r\n"
			+ "     LEFT JOIN pgowners.zoy_pg_property_details p\r\n"
			+ "     ON o.pg_owner_id = p.pg_owner_id\r\n"
			+ "     GROUP BY o.pg_owner_id\r\n"
			+ "     HAVING COUNT(p.property_id) = 0\r\n"
			+ " ) AS subquery) AS ownersWithNoPropertiesCount",
	        nativeQuery = true)
	List<Object[]> getOwnerStatistics();

	@Query(value=" SELECT *\r\n"
			+ " FROM pgowners.zoy_pg_registered_owner_details zrd\r\n"
			+ " WHERE zrd.register_id NOT IN (\r\n"
			+ "     SELECT szrd.register_id\r\n"
			+ "     FROM pgowners.zoy_pg_registered_owner_details szrd\r\n"
			+ "     JOIN pgadmin.pg_owner_master pom\r\n"
			+ "     ON szrd.register_id = pom.register_id\r\n"
			+ " )" ,nativeQuery=true)
	List<RegisteredPartner> getAllRegisteredUsers();
	
	
}
