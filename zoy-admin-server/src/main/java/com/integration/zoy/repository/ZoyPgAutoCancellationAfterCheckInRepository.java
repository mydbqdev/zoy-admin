package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgAutoCancellationAfterCheckIn;

@Repository
public interface ZoyPgAutoCancellationAfterCheckInRepository extends JpaRepository<ZoyPgAutoCancellationAfterCheckIn, String> {
}