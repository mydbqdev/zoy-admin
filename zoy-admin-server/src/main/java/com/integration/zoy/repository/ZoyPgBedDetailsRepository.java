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

	
	@Query(value = "select zpbd.* from pgowners.zoy_pg_property_details zppd  "
			+ "join pgowners.zoy_pg_property_floors zppf on zppf.property_id =zppd.property_id  "
			+ "join pgowners.zoy_pg_property_floor_details zppfd on zppf.floor_id =zppfd.floor_id  "
			+ "join pgowners.zoy_pg_floor_rooms zpfr on zpfr.floor_id =zppfd.floor_id  "
			+ "join pgowners.zoy_pg_room_details zprd on zprd.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_room_beds zprb on zprb.room_id =zpfr.room_id  "
			+ "join pgowners.zoy_pg_bed_details zpbd on zpbd.bed_id =zprb.bed_id  "
			+ "where zppd.property_id =:propertyId and zpbd.bed_name =:bedName "
			+ "and zprd.room_name =:roomName and zppfd.floor_name =:floorName",nativeQuery = true)
	List<ZoyPgBedDetails> findBedDetails(String propertyId, String bedName,String roomName,String floorName);
	
	
	@Query(value = "SELECT bd.bed_id " +
            "FROM pgowners.zoy_pg_bed_details bd " +
            "JOIN pgowners.zoy_pg_room_beds rb ON bd.bed_id = rb.bed_id " +
            "JOIN pgowners.zoy_pg_room_details rd ON rb.room_id = rd.room_id " +
            "JOIN pgowners.zoy_pg_floor_rooms fr ON rd.room_id = fr.room_id " +
            "JOIN pgowners.zoy_pg_property_floors pf ON fr.floor_id = pf.floor_id " +
            "WHERE pf.property_id = :propertyId", nativeQuery = true)
List<String> findAllBedIdsByPropertyId(@Param("propertyId") String propertyId);
	
	 @Modifying
	    @Transactional
	    @Query(value = "DELETE FROM pgowners.zoy_pg_bed_details WHERE bed_id IN :bedIds", nativeQuery = true)
	    void deleteByBedIds(@Param("bedIds") List<String> bedIds);
}
