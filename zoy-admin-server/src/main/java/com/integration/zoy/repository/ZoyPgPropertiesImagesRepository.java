package com.integration.zoy.repository;


import com.integration.zoy.entity.ZoyPgPropertiesImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoyPgPropertiesImagesRepository extends JpaRepository<ZoyPgPropertiesImages, String> {
   
}
