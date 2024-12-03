package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOtherCharges;

@Repository
public interface ZoyPgOtherChargesRepository extends JpaRepository<ZoyPgOtherCharges, String> {
}