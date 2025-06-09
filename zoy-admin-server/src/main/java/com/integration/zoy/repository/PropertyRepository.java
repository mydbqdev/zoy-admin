package com.integration.zoy.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.Property;

@Repository
public interface PropertyRepository extends JpaRepository<Property, String> {
    boolean existsByName(String name);
    @Query("SELECT p.whoCanStay as category, COUNT(p) as count FROM Property p WHERE p.salesId = :createdBy  GROUP BY p.whoCanStay")
    List<Map<String, Object>> countPropertiesByCategory(String createdBy);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.salesId = :createdBy")
    Long countAllProperties(String createdBy);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.whoCanStay = :category")
    Long countByCategory(String category);
    
    @Query(value="select distinct p.property_id ,p.name,concat(p.city,',',p.state),  "
    		+ "p.total_occupancy ,p.category,pi2.image_url,zprod.register_id,pom.zoy_code,pom.email_id,pom.initial_zoy_code FROM pgsales.properties p   "
    		+ "left join pgsales.property_image pi2 on pi2.property_id =p.property_id and pi2.is_thumbnail =true  "
    		+ "left join pgsales.pg_owners po on po.owner_id =p.owner_id   "
    		+ "left join pgowners.zoy_pg_registered_owner_details zprod on zprod.property_id =p.property_id "
    		+ "left join pgadmin.pg_owner_master pom on pom.register_id =zprod.register_id  "
    		+ "where p.is_active =true and sales_id =:createdBy order by p.name asc",nativeQuery = true)
    List<String[]> findAllActiveProperties(String createdBy);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.owner.ownerId = :ownerId")
    long countByOwnerId(String ownerId);
}