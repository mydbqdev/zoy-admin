package com.integration.zoy.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.integration.zoy.model.oldNewConfigRequest;
import com.integration.zoy.repository.PgOwnerMaterRepository;
import com.integration.zoy.utils.ZoyShortTermDetails;
import com.integration.zoy.utils.ZoyShortTermDto;

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

	private static final Logger log = LoggerFactory.getLogger(ScheduledService.class);

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
		tableRuleMap.put("pgowners.zoy_pg_auto_cancellation_master",
				"Cancellation And Refund Policy (Before check in)");
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

			String selectSql = "SELECT effective_date FROM " + table + " WHERE approved_by IS NULL "
					+ " AND comments IS NULL " + " AND DATE(effective_date) <= (DATE('" + timestampString
					+ "') + INTERVAL '15 days') " + " LIMIT 1";

			javax.persistence.Query selectQuery = entityManager.createNativeQuery(selectSql);

			try {
				Object effectiveDateResult = selectQuery.getSingleResult();

				if (effectiveDateResult != null) {
					String effectiveDateStr = effectiveDateResult.toString();

					String updateSql = "UPDATE " + table + " SET approved_by = 'Auto cancelled', "
							+ " comments = 'Auto-cancelled because approval should be done at least 15 days before the effective date' "
							+ " WHERE approved_by IS NULL " + " AND comments IS NULL "
							+ " AND DATE(effective_date) <= (DATE('" + timestampString + "') + INTERVAL '15 days')";

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
				int oldValue = Integer.parseInt(data.get(0)[1]);
				String futureDate = data.get(1)[0];
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
				int oldValue = Integer.parseInt(data.get(0)[1]);
				String futureDate = data.get(1)[0];
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

					zoyEmailService.sendTokenAdvanceRuleChangeEmail(futureDate, oldFixed, oldVariable, newFixed,
							newVariable);
				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing token advance change.");
			}
		} catch (Exception e) {
			log.error("Error in sendTokenAdvanceRuleEffective(): ", e);
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

					zoyEmailService.sendSecurityDepositLimitsChangeEmail(futureDate, oldFixed, oldVariable, newFixed,
							newVariable);
				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing security deposit change.");
			}
		} catch (Exception e) {
			log.error("Error in sendSecurityDepositRuleEffective(): ", e);
		}
	}

	public void sendGSTRuleEffective() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(timeZone);
			String currentDateStr = sdf.format(calendar.getTime());

			List<String[]> data = pgOwnerMaterRepository.findGSTDetailsByDate(currentDateStr);

			if (data != null && data.size() >= 2) {
				try {
					String pastDate = data.get(0)[0];
					BigDecimal oldCGstPercentage = new BigDecimal(data.get(0)[1]);
					BigDecimal oldMonthlyRent = new BigDecimal(data.get(0)[2]);

					String futureDate = data.get(1)[0];
					BigDecimal cgstPercentage = new BigDecimal(data.get(1)[1]);
					BigDecimal monthlyRent = new BigDecimal(data.get(1)[2]);

					zoyEmailService.sendGstChargesChangeEmail(futureDate, oldCGstPercentage, oldMonthlyRent,
							cgstPercentage, monthlyRent);

				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing GST change.");
			}
		} catch (Exception e) {
			log.error("Error in sendGSTRuleEffective(): ", e);
		}
	}

	public void sendOtherChargersRuleEffective() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(timeZone);
			String currentDateStr = sdf.format(calendar.getTime());

			List<String[]> data = pgOwnerMaterRepository.findOtherChargesDetailsByDate(currentDateStr);

			if (data != null && data.size() >= 2) {
				try {
					String pastDate = data.get(0)[0];
					BigDecimal oldOwnerCharges = new BigDecimal(data.get(0)[1]);
					BigDecimal oldOwnerEkycCharges = new BigDecimal(data.get(0)[2]);
					BigDecimal oldTenantCharges = new BigDecimal(data.get(0)[3]);
					BigDecimal oldTenantEkycCharges = new BigDecimal(data.get(0)[4]);

					String futureDate = data.get(1)[0];
					BigDecimal ownerCharges = new BigDecimal(data.get(1)[1]);
					BigDecimal ownerEkycCharges = new BigDecimal(data.get(1)[2]);
					BigDecimal tenantCharges = new BigDecimal(data.get(1)[3]);
					BigDecimal tenantEkycCharges = new BigDecimal(data.get(1)[4]);

					zoyEmailService.sendOtherChargesChangeEmailForOwners(futureDate, oldOwnerCharges,
							oldOwnerEkycCharges, ownerCharges, ownerEkycCharges);
					zoyEmailService.sendOtherChargesChangeEmailForUsers(futureDate, oldTenantCharges,
							oldTenantEkycCharges, tenantCharges, tenantEkycCharges);
				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing GST change.");
			}
		} catch (Exception e) {
			log.error("Error in sendGSTRuleEffective(): ", e);
		}
	}

	public void sendAfterCheckInDateRuleEffective() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(timeZone);
			String currentDateStr = sdf.format(calendar.getTime());

			List<String[]> data = pgOwnerMaterRepository.findAftercheckInDateDetailsByDate(currentDateStr);

			if (data != null && data.size() >= 2) {
				try {
					String pastDate = data.get(0)[0];
					String oldTriggerCondition = data.get(0)[1];
					Long oldAutoCancellationDay = Long.parseLong(data.get(0)[2]);

					String futureDate = data.get(1)[0];
					String newTriggerCondition = data.get(1)[1];
					Long newAutoCancellationDay = Long.parseLong(data.get(1)[2]);

					zoyEmailService.sendAutoCancellationAfterCheckinChangeEmail(futureDate, oldAutoCancellationDay,
							newAutoCancellationDay, oldTriggerCondition, newTriggerCondition);

				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing Auto Cancellation After Check-In change.");
			}
		} catch (Exception e) {
			log.error("Error in sendAfterCheckInDateRuleEffective(): ", e);
		}
	}

	public void sendEarlyCheckOutRuleEffective() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(timeZone);
			String currentDateStr = sdf.format(calendar.getTime());

			List<String[]> data = pgOwnerMaterRepository.findEarlyCheckOutDetailsByDate(currentDateStr);

			if (data != null && data.size() >= 2) {
				try {
					String pastDate = data.get(0)[0];
					String oldTriggerCondition = data.get(0)[1];
					Long oldDay = Long.parseLong(data.get(0)[2]);

					String futureDate = data.get(1)[0];
					String newTriggerCondition = data.get(1)[1];
					Long newDay = Long.parseLong(data.get(1)[2]);

					zoyEmailService.sendEarlyCheckoutChangeEmail(futureDate, oldDay, newDay, oldTriggerCondition,
							newTriggerCondition);

				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing Early Check Out change.");
			}
		} catch (Exception e) {
			log.error("Error in sendEarlyCheckOutRuleEffective(): ", e);
		}
	}

	public void sendSecurityDepositDeadlineRuleEffective() {
		try {
			TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
			Calendar calendar = Calendar.getInstance(timeZone);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sdf.setTimeZone(timeZone);
			String currentDateStr = sdf.format(calendar.getTime());

			List<String[]> data = pgOwnerMaterRepository.findSecurityDepositDeadlineDetailsByDate(currentDateStr);

			if (data != null && data.size() >= 2) {
				try {
					String pastDate = data.get(0)[0];
					String oldTriggerCondition = data.get(0)[1];
					Long oldDay = Long.parseLong(data.get(0)[2]);
					BigDecimal oldDeductionPercentage = new BigDecimal(data.get(0)[3]);

					String futureDate = data.get(1)[0];
					String newTriggerCondition = data.get(1)[1];
					Long newDay = Long.parseLong(data.get(1)[2]);
					BigDecimal newDeductionPercentage = new BigDecimal(data.get(1)[3]);

					zoyEmailService.sendSecurityDepositDeadlineChangeEmail(futureDate, oldDay, oldDeductionPercentage,
							newDay, newDeductionPercentage, oldTriggerCondition, newTriggerCondition);

				} catch (NumberFormatException e) {
					log.error("Error parsing numeric values from data: ", e);
				}
			} else {
				log.warn("Not enough data returned for processing Early Check Out change.");
			}
		} catch (Exception e) {
			log.error("Error in sendEarlyCheckOutRuleEffective(): ", e);
		}
	}
	
	
	public void sendShortTermRuleEffective() {
	    try {
	        TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
	        Calendar calendar = Calendar.getInstance(timeZone);

	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        sdf.setTimeZone(timeZone);
	        String currentDateStr = sdf.format(calendar.getTime());
	        
	        List<Object[]> newData = pgOwnerMaterRepository.findShortTermNewDetailsByDate(currentDateStr);
	        List<Object[]> oldData = pgOwnerMaterRepository.findShortTermOldDetailsByDate(currentDateStr);

	        oldNewConfigRequest configRequest = new oldNewConfigRequest();

	        ZoyShortTermDetails newRule = mapToZoyShortTermDetails(newData);
	        ZoyShortTermDetails oldRule = mapToZoyShortTermDetails(oldData);

	        configRequest.setNewSTDRule(newRule);
	        configRequest.setOldSTDRule(oldRule);

	        if (!newData.isEmpty()) {
	            zoyEmailService.sendEmailNotificationsForShortTerm(oldRule, newRule);
	        } else {
	            log.warn("No new short-term rule found for current or upcoming effective dates.");
	        }

	    } catch (Exception e) {
	        log.error("Error in sendShortTermRuleEffective(): ", e);
	    }
	}

	
	    public static ZoyShortTermDetails mapToZoyShortTermDetails(List<Object[]> data) {
	        ZoyShortTermDetails details = new ZoyShortTermDetails();
	        if (data == null || data.isEmpty()) 
	        	return details;

	        List<ZoyShortTermDto> dtoList = new ArrayList<>();

	        for (Object[] row : data) {
	            ZoyShortTermDto dto = new ZoyShortTermDto();

	            dto.setShortTermId(row[0] != null ? row[0].toString() : null);
	            dto.setStartDay(row[1] != null ? ((Number) row[1]).intValue() : 0);
	            dto.setEndDay(row[2] != null ? ((Number) row[2]).intValue() : 0);
	            dto.setPercentage(row[3] instanceof BigDecimal ? (BigDecimal) row[3] : BigDecimal.ZERO);
	            dtoList.add(dto);
	        }

	        Object[] first = data.get(0);
	        details.setEffectiveDate(first[5] != null ? first[5].toString() : null);
	        details.setApproved(first[6] != null && (Boolean) first[6]);
	        details.setApprovedBy(first[7] != null ? first[7].toString() : null);
	        details.setCreatedBy(first[8] != null ? first[8].toString() : null);
	        details.setPgType(first[4] != null ? first[4].toString() : null);
	        details.setComments(first[9] != null ? first[9].toString() : null);

	        details.setZoyShortTermDtoInfo(dtoList);
	        return details;
	    }
	

	    
	    public void sendCancellationAndRefundPolicyBeforeCheckInRuleEffective() {
		    try {
		        TimeZone timeZone = TimeZone.getTimeZone(currentTimeZone);
		        Calendar calendar = Calendar.getInstance(timeZone);

		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        sdf.setTimeZone(timeZone);
		        String currentDateStr = sdf.format(calendar.getTime());
		        
		        List<String> pgtype = pgOwnerMaterRepository.findAllPgTypeIds();
		        
		        for (String type : pgtype) {
		        
		        
		        List<Object[]> newData = pgOwnerMaterRepository.findShortTermNewDetailsByDate(currentDateStr);
		        List<Object[]> oldData = pgOwnerMaterRepository.findShortTermOldDetailsByDate(currentDateStr);
		        
		        

		        oldNewConfigRequest configRequest = new oldNewConfigRequest();

		        ZoyShortTermDetails newRule = mapToZoyShortTermDetails(newData);
		        ZoyShortTermDetails oldRule = mapToZoyShortTermDetails(oldData);

		        configRequest.setNewSTDRule(newRule);
		        configRequest.setOldSTDRule(oldRule);

		        if (!newData.isEmpty()) {
		            zoyEmailService.sendEmailNotificationsForShortTerm(oldRule, newRule);
		        } else {
		            log.warn("No new short-term rule found for current or upcoming effective dates.");
		        }

		    } 
		    }  catch (Exception e) {
		        log.error("Error in sendShortTermRuleEffective(): ", e);
		    }
		}

}
