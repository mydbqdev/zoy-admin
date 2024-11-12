package com.integration.zoy.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserDues;

@Repository
public interface UserDuesRepository extends JpaRepository<UserDues, String> {
	
	@Query(value = "SELECT u.user_id, " +
            "u.user_money_due_amount, " +
            "u.user_money_due_bill_start_date, " +
            "ud.user_personal_name, " +
            "pgd.user_pg_propertyname, " +
            "pgd.user_pg_property_id, " +
            "bd.bed_name, " +
            "u.user_money_due_id "+
    "FROM pgusers.user_dues u " +
    "JOIN pgusers.user_details ud ON u.user_id = ud.user_id " +
    "JOIN pgusers.user_pg_details pgd ON u.user_id = pgd.user_id " +
    "JOIN pgowners.zoy_pg_owner_booking_details bkd ON u.user_id = bkd.tenant_id " +
    "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
    "WHERE u.user_money_due_timestamp BETWEEN :fromDate AND :toDate", nativeQuery = true)
List<Object[]> findUserDuesDetailsByDateRange(
  @Param("fromDate") Timestamp fromDate, 
  @Param("toDate") Timestamp toDate);




}
