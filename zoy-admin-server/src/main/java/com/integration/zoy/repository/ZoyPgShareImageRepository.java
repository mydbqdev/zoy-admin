package com.integration.zoy.repository;

import com.integration.zoy.entity.ZoyPgShareImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZoyPgShareImageRepository extends JpaRepository<ZoyPgShareImage, String> {
 
}
