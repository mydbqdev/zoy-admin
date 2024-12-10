package com.integration.zoy.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertiesImages;

@Repository
public interface ZoyPgPropertiesImagesRepository extends JpaRepository<ZoyPgPropertiesImages, String> {
   
}
