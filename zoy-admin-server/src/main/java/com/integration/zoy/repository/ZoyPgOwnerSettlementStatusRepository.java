package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOwnerSettlementStatus;

@Repository
public interface ZoyPgOwnerSettlementStatusRepository extends JpaRepository<ZoyPgOwnerSettlementStatus, String> {

	
	@Query(value="select * from pgowners.zoy_pg_owner_settlement_status where owner_id =:ownerId "
			+ "and property_id=:propertyId and is_approved =false and is_rejected =false" , nativeQuery = true)
	List<ZoyPgOwnerSettlementStatus> findPgOwnerSettlement(String ownerId, String propertyId);

}
