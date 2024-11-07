package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_terms_master", schema = "pgowners")
public class ZoyPgTermsMaster {

	@Id
	@GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "term_id", updatable = false, nullable = false, unique = true, length = 36)
	private String termId;

	@Column(name = "grace_period", length = 10)
	private String gracePeriod;

	@Column(name = "late_payment_fee", precision = 10, scale = 2)
	private BigDecimal latePaymentFee;

	@Column(name = "is_late_payment_fee")
	private Boolean isLatePaymentFee;

	@Column(name = "notice_period", length = 20)
	private String noticePeriod;

	@Column(name = "security_deposit", length = 20)
	private String securityDeposit;

	@Column(name = "agreement_duration", length = 20)
	private String agreementDuration;


	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(String gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	public BigDecimal getLatePaymentFee() {
		return latePaymentFee;
	}

	public void setLatePaymentFee(BigDecimal latePaymentFee) {
		this.latePaymentFee = latePaymentFee;
	}

	public Boolean getIsLatePaymentFee() {
		return isLatePaymentFee;
	}

	public void setIsLatePaymentFee(Boolean isLatePaymentFee) {
		this.isLatePaymentFee = isLatePaymentFee;
	}

	public String getNoticePeriod() {
		return noticePeriod;
	}

	public void setNoticePeriod(String noticePeriod) {
		this.noticePeriod = noticePeriod;
	}

	public String getSecurityDeposit() {
		return securityDeposit;
	}

	public void setSecurityDeposit(String securityDeposit) {
		this.securityDeposit = securityDeposit;
	}

	public String getAgreementDuration() {
		return agreementDuration;
	}

	public void setAgreementDuration(String agreementDuration) {
		this.agreementDuration = agreementDuration;
	}

}
