package com.integration.zoy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOwnerSettlementSplitUp;
import com.integration.zoy.entity.ZoyPgOwnerSettlementStatus;

@Repository
public interface ZoyPgOwnerSettlementSplitUpRepository extends JpaRepository<ZoyPgOwnerSettlementSplitUp, String> {

	

}
