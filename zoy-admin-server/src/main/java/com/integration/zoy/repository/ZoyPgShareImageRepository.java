package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgShareImage;

@Repository
public interface ZoyPgShareImageRepository extends JpaRepository<ZoyPgShareImage, String> {
 
}
