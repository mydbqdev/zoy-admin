package com.integration.zoy.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.ZoyPgOwnerSettlementStatus;

@Repository
public interface ZoyPgOwnerSettlementStatusRepository extends JpaRepository<ZoyPgOwnerSettlementStatus, String> {

	
	@Query(value="select * from pgowners.zoy_pg_owner_settlement_status where owner_id =:ownerId "
			+ "and property_id=:propertyId and is_approved =false and is_rejected =false" , nativeQuery = true)
	List<ZoyPgOwnerSettlementStatus> findPgOwnerSettlement(String ownerId, String propertyId);

	@Query(value = "SELECT " + "  CASE "
			+ "    WHEN s.payment_process_date >= to_date(split_part(:financialYear, '-', 1) || '-04-01', 'YYYY-MM-DD') "
			+ "     AND s.payment_process_date < to_date(split_part(:financialYear, '-', 1) || '-07-01', 'YYYY-MM-DD') THEN 'Q1' "
			+ "    WHEN s.payment_process_date >= to_date(split_part(:financialYear, '-', 1) || '-07-01', 'YYYY-MM-DD') "
			+ "     AND s.payment_process_date < to_date(split_part(:financialYear, '-', 1) || '-10-01', 'YYYY-MM-DD') THEN 'Q2' "
			+ "    WHEN s.payment_process_date >= to_date(split_part(:financialYear, '-', 1) || '-10-01', 'YYYY-MM-DD') "
			+ "     AND s.payment_process_date < to_date(split_part(:financialYear, '-', 2) || '-01-01', 'YYYY-MM-DD') THEN 'Q3' "
			+ "    WHEN s.payment_process_date >= to_date(split_part(:financialYear, '-', 2) || '-01-01', 'YYYY-MM-DD') "
			+ "     AND s.payment_process_date <= to_date(split_part(:financialYear, '-', 2) || '-03-31', 'YYYY-MM-DD') THEN 'Q4' "
			+ "  END AS quarter, " + "  SUM(s.zoy_share_amount) AS total_revenue "
			+ "FROM pgowners.zoy_pg_owner_settlement_status s " + "WHERE s.payment_process_date BETWEEN "
			+ "  to_date(split_part(:financialYear, '-', 1) || '-04-01', 'YYYY-MM-DD') AND "
			+ "  to_date(split_part(:financialYear, '-', 2) || '-03-31', 'YYYY-MM-DD') " + "GROUP BY quarter "
			+ "ORDER BY quarter", nativeQuery = true)
	List<Object[]> getQuarterlyRevenueByFinancialYear(@Param("financialYear") String financialYear);

	@Query(value = 
		    "SELECT " +
		    " to_char(payment_process_date, 'YYYY-MM-DD') AS date, " +
		    " COALESCE(SUM(zoy_share_amount) / 1000, 0) AS revenue_in_thousands " +
		    "FROM pgowners.zoy_pg_owner_settlement_status " +
		    "WHERE payment_process_date >= CURRENT_DATE - INTERVAL '6 days' " +
		    "  AND payment_process_date <= CURRENT_DATE " +
		    "GROUP BY date " +
		    "ORDER BY date"
		, nativeQuery = true)
		List<Object[]> getLast7DaysRevenue();

}
