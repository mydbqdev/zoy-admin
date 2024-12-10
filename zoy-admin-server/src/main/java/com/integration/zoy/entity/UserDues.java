package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_dues", schema = "pgusers")
public class UserDues {

    @Id
    @Column(name = "user_money_due_id")
    private String userMoneyDueId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_money_due_images")
    private String userMoneyDueImages;

    @Column(name = "user_money_due_bill_start_date")
    private Timestamp userMoneyDueBillStartDate;

    @Column(name = "user_money_due_timestamp")
    private Timestamp userMoneyDueTimestamp;

    @Column(name = "user_money_due_amount")
    private BigDecimal userMoneyDueAmount;

    @Column(name = "user_money_due_billing_type")
    private String userMoneyDueBillingType;

    @Column(name = "user_money_due_description")
    private String userMoneyDueDescription;

    @Column(name = "user_money_due_type")
    private String userMoneyDueType;

    @Column(name = "user_money_due_bill_end_date")
    private Timestamp userMoneyDueBillEndDate;

    // Getters and Setters
    public String getUserMoneyDueId() {
        return userMoneyDueId;
    }

    public void setUserMoneyDueId(String userMoneyDueId) {
        this.userMoneyDueId = userMoneyDueId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMoneyDueImages() {
        return userMoneyDueImages;
    }

    public void setUserMoneyDueImages(String userMoneyDueImages) {
        this.userMoneyDueImages = userMoneyDueImages;
    }

    public Timestamp getUserMoneyDueBillStartDate() {
        return userMoneyDueBillStartDate;
    }

    public void setUserMoneyDueBillStartDate(Timestamp userMoneyDueBillStartDate) {
        this.userMoneyDueBillStartDate = userMoneyDueBillStartDate;
    }

    public Timestamp getUserMoneyDueTimestamp() {
        return userMoneyDueTimestamp;
    }

    public void setUserMoneyDueTimestamp(Timestamp userMoneyDueTimestamp) {
        this.userMoneyDueTimestamp = userMoneyDueTimestamp;
    }

    public BigDecimal getUserMoneyDueAmount() {
        return userMoneyDueAmount;
    }

    public void setUserMoneyDueAmount(BigDecimal userMoneyDueAmount) {
        this.userMoneyDueAmount = userMoneyDueAmount;
    }

    public String getUserMoneyDueBillingType() {
        return userMoneyDueBillingType;
    }

    public void setUserMoneyDueBillingType(String userMoneyDueBillingType) {
        this.userMoneyDueBillingType = userMoneyDueBillingType;
    }

    public String getUserMoneyDueDescription() {
        return userMoneyDueDescription;
    }

    public void setUserMoneyDueDescription(String userMoneyDueDescription) {
        this.userMoneyDueDescription = userMoneyDueDescription;
    }

    public String getUserMoneyDueType() {
        return userMoneyDueType;
    }

    public void setUserMoneyDueType(String userMoneyDueType) {
        this.userMoneyDueType = userMoneyDueType;
    }

    public Timestamp getUserMoneyDueBillEndDate() {
        return userMoneyDueBillEndDate;
    }

    public void setUserMoneyDueBillEndDate(Timestamp userMoneyDueBillEndDate) {
        this.userMoneyDueBillEndDate = userMoneyDueBillEndDate;
    }
}
