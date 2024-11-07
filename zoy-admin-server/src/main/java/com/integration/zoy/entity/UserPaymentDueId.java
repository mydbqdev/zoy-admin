package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserPaymentDueId implements Serializable {
	 private static final long serialVersionUID = 1L;
    private String userId;
    private String userMoneyDueId;
    private String userPaymentId;

   
    public UserPaymentDueId() {}

    public UserPaymentDueId(String userId, String userMoneyDueId, String userPaymentId) {
        this.userId = userId;
        this.userMoneyDueId = userMoneyDueId;
        this.userPaymentId = userPaymentId;
    }

   
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPaymentDueId)) return false;
        UserPaymentDueId that = (UserPaymentDueId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(userMoneyDueId, that.userMoneyDueId) &&
                Objects.equals(userPaymentId, that.userPaymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userMoneyDueId, userPaymentId);
    }
}
