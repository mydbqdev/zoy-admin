package com.integration.zoy.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgBedDetails;

@Repository
public interface ZoyPgBedDetailsRepository extends JpaRepository<ZoyPgBedDetails, String> {

	
	@Query(value = "select zpbd.* from pgowners.zoy_pg_property_details zppd "
			+ "join pgowners.zoy_pg_property_floors zppf on zppf.property_id =zppd.property_id "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zpfr.floor_id = zppf.floor_id "
			+ "join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zpfr.room_id "
			+ "join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id "
			+ "where zppd.property_id =:propertyId and zpbd.bed_name =:bedName",nativeQuery = true)
	List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName);
	
}
