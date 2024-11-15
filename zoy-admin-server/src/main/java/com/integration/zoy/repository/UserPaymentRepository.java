package com.integration.zoy.repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.integration.zoy.entity.UserPayment;

@Repository
public interface UserPaymentRepository extends JpaRepository<UserPayment, String> {
	@Query(value = "SELECT up.user_id, " +
            "up.user_payment_timestamp, " +
            "up.user_payment_bank_transaction_id, " +
            "up.user_payment_result_status, " +
            "up.user_payment_payable_amount, " +
            "up.user_payment_gst, " +
            "ud.user_personal_name, " +
            "pgd.user_pg_propertyname, " +
            "pgd.user_pg_property_id, " +
            "bd.bed_name, " +
            "up.user_payment_zoy_payment_type, " +
            "up.user_payment_result_method " +
     "FROM pgusers.user_payments up " +
     "JOIN (SELECT DISTINCT pgd.user_id, " +
           "pgd.user_pg_propertyname, " +
           "pgd.user_pg_property_id " +
           "FROM pgusers.user_pg_details pgd) pgd " +
     "ON up.user_id = pgd.user_id " +
     "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
     "JOIN pgowners.zoy_pg_owner_booking_details bkd " +
     "ON up.user_id = bkd.tenant_id " +
     "AND up.user_payment_booking_id = bkd.booking_id " +
     "AND pgd.user_pg_property_id = bkd.property_id " +
     "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
     "WHERE up.user_payment_timestamp BETWEEN :fromDate AND :toDate " +
     "ORDER BY up.user_payment_timestamp DESC", 
     nativeQuery = true)
List<Object[]> findUserPaymentDetailsByUserIdAndDateRange(
  @Param("fromDate") Timestamp fromDate, 
  @Param("toDate") Timestamp toDate);
	
	@Query(value = "SELECT up.user_payment_timestamp AS transaction_date, " +
	        "up.user_payment_bank_transaction_id AS transaction_number, " +
	        "up.user_id, " +
	        "ud.user_personal_name, " +
	        "up.user_payment_payable_amount, " +
	        "up.user_payment_gst " +
	    "FROM pgusers.user_payments up " +
	    "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
	    "WHERE up.user_payment_timestamp BETWEEN :fromDate AND :toDate", nativeQuery = true)
	List<Object[]> findConsolidatedFinanceReportByDateRange(
	    @Param("fromDate") Timestamp fromDate,
	    @Param("toDate") Timestamp toDate);
	
	@Query(value = "SELECT up.user_payment_payable_amount " +
            "FROM pgusers.user_payments up " +
            "WHERE up.user_payment_id = :userPaymentId", nativeQuery = true)
BigDecimal findUserPaymentPayableAmountByUserPaymentId(@Param("userPaymentId") String userPaymentId);

	@Query(value = "SELECT o.pg_owner_id AS ownerId, " +
            "o.pg_owner_name AS ownerName, " +
            "pd.property_id AS pgId, " +
            "pd.property_name AS pgName, " +
            "SUM(up.user_payment_payable_amount) AS totalAmountFromTenants, " +
            "up.user_payment_timestamp AS transactionDate, " +
            "up.user_payment_bank_transaction_id AS transactionNumber, " +
            "up.user_payment_payment_status AS paymentStatus " +
     "FROM pgusers.user_payments up " +
     "JOIN pgusers.user_pg_details pgd ON up.user_id = pgd.user_id " +
     "JOIN pgusers.user_details ud ON up.user_id = ud.user_id " +
     "JOIN pgowners.zoy_pg_owner_booking_details bkd ON up.user_id = bkd.tenant_id " +
     "AND up.user_payment_booking_id = bkd.booking_id " +
     "AND pgd.user_pg_property_id = bkd.property_id " +
     "JOIN pgowners.zoy_pg_bed_details bd ON bkd.selected_bed = bd.bed_id " +
     "JOIN pgowners.zoy_pg_property_details pd ON bkd.property_id = pd.property_id " +
     "JOIN pgowners.zoy_pg_owner_details o ON pd.pg_owner_id = o.pg_owner_id " +
     "WHERE up.user_payment_timestamp BETWEEN :fromDate AND :toDate " +
     "GROUP BY o.pg_owner_id, o.pg_owner_name, pd.property_id, pd.property_name, up.user_payment_timestamp, up.user_payment_bank_transaction_id, up.user_payment_payment_status", nativeQuery = true)
List<Object[]> findVendorPaymentDetailsDateRange(
     @Param("fromDate") Timestamp fromDate, 
     @Param("toDate") Timestamp toDate);
	
}
