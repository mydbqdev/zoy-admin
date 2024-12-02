package com.integration.zoy.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgSecurityDepositDetails;

@Repository
public interface ZoyPgSecurityDepositDetailsRepository extends JpaRepository<ZoyPgSecurityDepositDetails, String> {
}