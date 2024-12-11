package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_security_deposit_refund_rules", schema = "pgcommon")
public class ZoyPgSecurityDepositRefundRule {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "rule_id", updatable = false, nullable = false, length = 36)
    private String ruleId;
    
    @Column(name = "max_days_for_refund", nullable = true)
    private Integer maxDaysForRefund;
    
    @Column(name = "platform_charges", nullable = true)
    private BigDecimal plotformCharges;

  
    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

	public Integer getMaxDaysForRefund() {
		return maxDaysForRefund;
	}

	public void setMaxDaysForRefund(Integer maxDaysForRefund) {
		this.maxDaysForRefund = maxDaysForRefund;
	}

	public BigDecimal getPlotformCharges() {
		return plotformCharges;
	}

	public void setPlotformCharges(BigDecimal plotformCharges) {
		this.plotformCharges = plotformCharges;
	}

	
 
}
