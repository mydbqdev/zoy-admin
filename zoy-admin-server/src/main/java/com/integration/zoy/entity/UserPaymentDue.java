package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "user_payment_due", schema = "pgusers")
@IdClass(UserPaymentDueId.class)
public class UserPaymentDue {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "user_money_due_id")
    private String userMoneyDueId;

    @Id
    @Column(name = "user_payment_id")
    private String userPaymentId;

    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMoneyDueId() {
        return userMoneyDueId;
    }

    public void setUserMoneyDueId(String userMoneyDueId) {
        this.userMoneyDueId = userMoneyDueId;
    }

    public String getUserPaymentId() {
        return userPaymentId;
    }

    public void setUserPaymentId(String userPaymentId) {
        this.userPaymentId = userPaymentId;
    }
}
