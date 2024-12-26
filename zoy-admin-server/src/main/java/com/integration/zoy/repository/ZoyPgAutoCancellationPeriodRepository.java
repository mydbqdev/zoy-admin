package com.integration.zoy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.integration.zoy.entity.ZoyPgAutoCancellationPeriod;

public interface ZoyPgAutoCancellationPeriodRepository extends  JpaRepository<ZoyPgAutoCancellationPeriod, String> {

}
