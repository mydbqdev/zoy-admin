package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UserBookingPaymentId implements Serializable {
	  private static final long serialVersionUID = 1L;
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_bookings_id")
    private String userBookingsId;

    @Column(name = "user_payment_id")
    private String userPaymentId;

    public UserBookingPaymentId() {
      
    }

    public UserBookingPaymentId(String userId, String userBookingsId, String userPaymentId) {
        this.userId = userId;
        this.userBookingsId = userBookingsId;
        this.userPaymentId = userPaymentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserBookingsId() {
        return userBookingsId;
    }

    public void setUserBookingsId(String userBookingsId) {
        this.userBookingsId = userBookingsId;
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
        if (!(o instanceof UserBookingPaymentId)) return false;
        UserBookingPaymentId that = (UserBookingPaymentId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(userBookingsId, that.userBookingsId) &&
               Objects.equals(userPaymentId, that.userPaymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userBookingsId, userPaymentId);
    }
}
