package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgTimeMaster;

@Repository
public interface ZoyPgTimeMasterRepository extends JpaRepository<ZoyPgTimeMaster, String> {


}
