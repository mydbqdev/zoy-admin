package com.integration.zoy.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOwnerDetails;

@Repository
public interface ZoyPgOwnerDetailsRepository extends JpaRepository<ZoyPgOwnerDetails, String> {
	
}
