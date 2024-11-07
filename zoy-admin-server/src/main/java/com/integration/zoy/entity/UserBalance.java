package com.integration.zoy.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_balance", schema = "pgusers")
public class UserBalance {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "amount", precision = 20, scale = 2, columnDefinition = "numeric(20,2) default 0")
    private BigDecimal amount = BigDecimal.ZERO;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
