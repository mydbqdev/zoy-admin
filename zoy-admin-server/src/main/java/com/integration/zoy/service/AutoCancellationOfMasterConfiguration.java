package com.integration.zoy.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.integration.zoy.repository.PgOwnerMaterRepository;

@Service
public class AutoCancellationOfMasterConfiguration {

    @Value("${spring.jackson.time-zone}")
    private String currentTimeZone;

    @Autowired
    private NotificationsAndAlertsService notificationsAndAlertsService;
    
    @Autowired
    private ZoyEmailService zoyEmailService;
    
    @Autowired
    private PgOwnerMaterRepository pgOwnerMaterRepository;

    @PersistenceContext
    private EntityManager entityManager;
    
	private static final Logger log=LoggerFactory.getLogger(ScheduledService.class);

    @Transactional
    public int autoCancellationOfMaterConfiguration() {
        // Set timezone and current timestamp
        TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
        Calendar calendar = Calendar.getInstance(timeZone);
        Timestamp currentTimestamp = new Timestamp(calendar.getTimeInMillis());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(timeZone);
        String timestampString = sdf.format(currentTimestamp);

        Map<String, String> tableRuleMap = new LinkedHashMap<>();
        tableRuleMap.put("pgowners.zoy_pg_short_term_renting_duration", "Short term duration period:");
        tableRuleMap.put("pgowners.zoy_pg_force_check_out", "Force Checkout");
        tableRuleMap.put("pgowners.zoy_pg_short_term_master", "Short term master");
        tableRuleMap.put("pgowners.zoy_pg_rent_gst", "GST Charges");
        tableRuleMap.put("pgowners.zoy_pg_other_charges", "Other Charges");
        tableRuleMap.put("pgcommon.zoy_data_grouping", "Data Grouping");
        tableRuleMap.put("pgowners.zoy_pg_auto_cancellation_master", "Cancellation And Refund Policy (Before check in)");
        tableRuleMap.put("pgowners.zoy_pg_auto_cancellation_after_check_in", "After check-in Date");
        tableRuleMap.put("pgowners.zoy_pg_early_check_out", "Early Check-out Rules");
        tableRuleMap.put("pgowners.zoy_pg_cancellation_details", "Cancellation Details");
        tableRuleMap.put("pgowners.zoy_pg_security_deposit_details", "Security Deposit Limits");
        tableRuleMap.put("pgowners.zoy_pg_token_details", "Token Advance");
        tableRuleMap.put("pgowners.zoy_pg_no_rental_agreement", "No Rental Agreement");

        int totalUpdated = 0;

        for (Map.Entry<String, String> entry : tableRuleMap.entrySet()) {
            String table = entry.getKey();
            String rule = entry.getValue();

            String selectSql = "SELECT effective_date FROM " + table + 
                               " WHERE approved_by IS NULL " +
                               " AND comments IS NULL " +
                               " AND DATE(effective_date) <= (DATE('" + timestampString + "') + INTERVAL '15 days') " +
                               " LIMIT 1";
            
            javax.persistence.Query selectQuery = entityManager.createNativeQuery(selectSql);

            try {
                Object effectiveDateResult = selectQuery.getSingleResult();

                if (effectiveDateResult != null) {
                    String effectiveDateStr = effectiveDateResult.toString();

                    String updateSql = "UPDATE " + table + 
                                       " SET approved_by = 'Auto cancelled', " +
                                       " comments = 'Auto-cancelled because approval should be done at least 15 days before the effective date' " +
                                       " WHERE approved_by IS NULL " +
                                       " AND comments IS NULL " +
                                       " AND DATE(effective_date) <= (DATE('" + timestampString + "') + INTERVAL '15 days')";

                    javax.persistence.Query updateQuery = entityManager.createNativeQuery(updateSql);

                    int updated = updateQuery.executeUpdate();

                    if (updated > 0) {
                        totalUpdated += updated;
                        
                        notificationsAndAlertsService.masterConfigurationRuleRejection(rule);
                        zoyEmailService.sendAutoRejectionEmail(rule, effectiveDateStr);
                    }
                }
            } catch (javax.persistence.NoResultException noResult) {
                continue;
            } catch (Exception e) {
                System.err.println("Failed to update table: " + table + ". Error: " + e.getMessage());
            }
        }

        return totalUpdated;
    }
    
    
    
    
    public void sendForceCheckoutRuleEffective() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            String currentDateStr = sdf.format(calendar.getTime());

            List<String[]> data = pgOwnerMaterRepository.findCheckOutDaysByDate(currentDateStr);

            if (data != null && data.size() >= 2) {
                String pastDate = data.get(0)[0];
                int oldValue  = Integer.parseInt(data.get(0)[1]);
                String futureDate  = data.get(1)[0];
                int newValue = Integer.parseInt(data.get(1)[1]);

                zoyEmailService.sendForceCheckoutChangeEmail(futureDate, oldValue, newValue);
            } else {
                log.warn("Not enough data returned for processing force checkout change.");
            }
        } catch (Exception e) {
            log.error("Error in sendRuleEffective(): ", e);
        }
    }

    public void sendNoRentalAgreementRuleEffective() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            String currentDateStr = sdf.format(calendar.getTime());

            List<String[]> data = pgOwnerMaterRepository.findNoRentalAgreementDaysByDate(currentDateStr);

            if (data != null && data.size() >= 2) {
                String pastDate = data.get(0)[0];
                int oldValue  = Integer.parseInt(data.get(0)[1]);
                String futureDate  = data.get(1)[0];
                int newValue = Integer.parseInt(data.get(1)[1]);

                zoyEmailService.sendNoRentalAgreementChangeEmail(futureDate, oldValue, newValue);
            } else {
                log.warn("Not enough data returned for processing force checkout change.");
            }
        } catch (Exception e) {
            log.error("Error in sendRuleEffective(): ", e);
        }
    }
    
    public void sendTokenAdvanceRuleEffective() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            String currentDateStr = sdf.format(calendar.getTime());

            List<String[]> data = pgOwnerMaterRepository.findTokenAdvanceDetailsByDate(currentDateStr);

            if (data != null && data.size() >= 2) {
                try {
                    String pastDate = data.get(0)[0];
                    BigDecimal oldFixed = new BigDecimal(data.get(0)[1]); 
                    BigDecimal oldVariable = new BigDecimal(data.get(0)[2]); 

                    String futureDate = data.get(1)[0];
                    BigDecimal newFixed = new BigDecimal(data.get(1)[1]); 
                    BigDecimal newVariable = new BigDecimal(data.get(1)[2]); 

                    zoyEmailService.sendTokenAdvanceRuleChangeEmail(
                        futureDate, oldFixed, oldVariable, newFixed, newVariable
                    );
                } catch (NumberFormatException e) {
                    log.error("Error parsing numeric values from data: ", e);
                }
            } else {
                log.warn("Not enough data returned for processing force checkout change.");
            }
        } catch (Exception e) {
            log.error("Error in sendRentalAgreementRuleEffective(): ", e);
        }
    }
    
    
    public void sendSecurityDepositRuleEffective() {
        try {
            TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
            Calendar calendar = Calendar.getInstance(timeZone);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(timeZone);
            String currentDateStr = sdf.format(calendar.getTime());

            List<String[]> data = pgOwnerMaterRepository.findTokenAdvanceDetailsByDate(currentDateStr);

            if (data != null && data.size() >= 2) {
                try {
                    String pastDate = data.get(0)[0];
                    BigDecimal oldFixed = new BigDecimal(data.get(0)[1]); 
                    BigDecimal oldVariable = new BigDecimal(data.get(0)[2]); 

                    String futureDate = data.get(1)[0];
                    BigDecimal newFixed = new BigDecimal(data.get(1)[1]); 
                    BigDecimal newVariable = new BigDecimal(data.get(1)[2]); 

                    zoyEmailService.sendSecurityDepositLimitsChangeEmail(futureDate, oldFixed, oldVariable, newFixed, newVariable);
                } catch (NumberFormatException e) {
                    log.error("Error parsing numeric values from data: ", e);
                }
            } else {
                log.warn("Not enough data returned for processing force checkout change.");
            }
        } catch (Exception e) {
            log.error("Error in sendRentalAgreementRuleEffective(): ", e);
        }
    }


}
