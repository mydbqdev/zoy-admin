package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_due_type_master", schema = "pgowners")
public class ZoyPgDueTypeMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "due_id", updatable = false, nullable = false, unique = true, length = 36)
    private String dueId;

    @Column(name = "due_type", length = 50)
    private String dueType;
    
    @Column(name = "amount")
    private BigDecimal amount;
    
    @Column(name = "isautogenerate")
    private Boolean isautogenerate;

    // Constructors
    public ZoyPgDueTypeMaster() {
    }

    public ZoyPgDueTypeMaster(String dueType) {
        this.dueType = dueType;
    }

    // Getters and Setters
    public String getDueId() {
        return dueId;
    }

    public void setDueId(String dueId) {
        this.dueId = dueId;
    }

    public String getDueType() {
        return dueType;
    }

    public void setDueType(String dueType) {
        this.dueType = dueType;
    }

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Boolean getIsautogenerate() {
		return isautogenerate;
	}

	public void setIsautogenerate(Boolean isautogenerate) {
		this.isautogenerate = isautogenerate;
	}
    
    


}
