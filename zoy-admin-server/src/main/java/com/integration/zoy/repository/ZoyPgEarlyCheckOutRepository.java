package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgEarlyCheckOut;

@Repository
public interface ZoyPgEarlyCheckOutRepository extends JpaRepository<ZoyPgEarlyCheckOut, String> {
}