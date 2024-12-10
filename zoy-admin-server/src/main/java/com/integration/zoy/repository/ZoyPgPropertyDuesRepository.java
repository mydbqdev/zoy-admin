package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgPropertyDues;
import com.integration.zoy.entity.ZoyPgPropertyDuesId;

@Repository
public interface ZoyPgPropertyDuesRepository extends JpaRepository<ZoyPgPropertyDues, ZoyPgPropertyDuesId> {
}
