package com.integration.zoy.entity;



import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_bookings", schema = "pgusers")
public class UserBookings {

    @Id
    @Column(name = "user_bookings_id")
    private String userBookingsId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_bookings_tenant_id")
    private String userBookingsTenantId;

    @Column(name = "user_bookings_date")
    private Timestamp userBookingsDate;

    @Column(name = "user_bookings_is_cancelled")
    private Boolean userBookingsIsCancelled;

    @Column(name = "user_bookings_pg_owner_id")
    private String userBookingsPgOwnerId;

    @Column(name = "user_bookings_property_id")
    private String userBookingsPropertyId;

    @Column(name = "user_bookings_web_check_in")
    private Boolean userBookingsWebCheckIn;

    @Column(name = "user_bookings_web_check_out")
    private Boolean userBookingsWebCheckOut;

   
    public String getUserBookingsId() {
        return userBookingsId;
    }

    public void setUserBookingsId(String userBookingsId) {
        this.userBookingsId = userBookingsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserBookingsTenantId() {
        return userBookingsTenantId;
    }

    public void setUserBookingsTenantId(String userBookingsTenantId) {
        this.userBookingsTenantId = userBookingsTenantId;
    }

    public Timestamp getUserBookingsDate() {
        return userBookingsDate;
    }

    public void setUserBookingsDate(Timestamp userBookingsDate) {
        this.userBookingsDate = userBookingsDate;
    }

    public Boolean getUserBookingsIsCancelled() {
        return userBookingsIsCancelled;
    }

    public void setUserBookingsIsCancelled(Boolean userBookingsIsCancelled) {
        this.userBookingsIsCancelled = userBookingsIsCancelled;
    }

    public String getUserBookingsPgOwnerId() {
        return userBookingsPgOwnerId;
    }

    public void setUserBookingsPgOwnerId(String userBookingsPgOwnerId) {
        this.userBookingsPgOwnerId = userBookingsPgOwnerId;
    }

    public String getUserBookingsPropertyId() {
        return userBookingsPropertyId;
    }

    public void setUserBookingsPropertyId(String userBookingsPropertyId) {
        this.userBookingsPropertyId = userBookingsPropertyId;
    }

    public Boolean getUserBookingsWebCheckIn() {
        return userBookingsWebCheckIn;
    }

    public void setUserBookingsWebCheckIn(Boolean userBookingsWebCheckIn) {
        this.userBookingsWebCheckIn = userBookingsWebCheckIn;
    }

    public Boolean getUserBookingsWebCheckOut() {
        return userBookingsWebCheckOut;
    }

    public void setUserBookingsWebCheckOut(Boolean userBookingsWebCheckOut) {
        this.userBookingsWebCheckOut = userBookingsWebCheckOut;
    }
}
