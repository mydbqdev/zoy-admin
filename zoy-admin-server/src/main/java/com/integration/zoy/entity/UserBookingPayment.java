package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_booking_payment", schema = "pgusers")
public class UserBookingPayment {

    @EmbeddedId
    private UserBookingPaymentId id;

    public UserBookingPayment() {
        // Default constructor
    }

    public UserBookingPayment(String userId, String userBookingsId, String userPaymentId) {
        this.id = new UserBookingPaymentId(userId, userBookingsId, userPaymentId);
    }

    public UserBookingPaymentId getId() {
        return id;
    }

    public void setId(UserBookingPaymentId id) {
        this.id = id;
    }

}
