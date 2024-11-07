package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.sql.Timestamp;

@Entity
@Table(name = "user_pg_details", schema = "pgusers")
public class UserPgDetails {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "user_pg_details_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userPgDetailsId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_pg_tenant_id")
    private String userPgTenantId;

    @Column(name = "user_pg_booking_id")
    private String userPgBookingId;

    @Column(name = "user_pg_rental_need_details_timestamp")
    private Timestamp userPgRentalNeedDetailsTimestamp;

    @Column(name = "user_pg_rental_need_details_isagreed")
    private Boolean userPgRentalNeedDetailsIsAgreed;

    @Column(name = "user_pg_owner_id")
    private String userPgOwnerId;

    @Column(name = "user_pg_property_id")
    private String userPgPropertyId;

    @Column(name = "user_pg_checkout_date")
    private Timestamp userPgCheckoutDate;

    @Column(name = "user_pg_is_accepted")
    private Boolean userPgIsAccepted;
   
    @Column(name = "reason")
    private String reason;
    
    @Column(name = "user_pg_request_raised_date")
    private Timestamp reqRaisedDate;
    
    @Column(name = "message")
    private String message;
    
    @Column(name = "user_pg_move_out_request_out_date")
    private Timestamp userPgMoveOutRequestOutDate;
   
    @Column(name = "user_pg_move_out_request_raised_date")
    private Timestamp userPgMoveOutRequestRaisedDate;
    
    public String getUserPgDetailsId() {
        return userPgDetailsId;
    }

    public void setUserPgDetailsId(String userPgDetailsId) {
        this.userPgDetailsId = userPgDetailsId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPgTenantId() {
        return userPgTenantId;
    }

    public void setUserPgTenantId(String userPgTenantId) {
        this.userPgTenantId = userPgTenantId;
    }

    public String getUserPgBookingId() {
        return userPgBookingId;
    }

    public void setUserPgBookingId(String userPgBookingId) {
        this.userPgBookingId = userPgBookingId;
    }

    public Timestamp getUserPgRentalNeedDetailsTimestamp() {
        return userPgRentalNeedDetailsTimestamp;
    }

    public void setUserPgRentalNeedDetailsTimestamp(Timestamp userPgRentalNeedDetailsTimestamp) {
        this.userPgRentalNeedDetailsTimestamp = userPgRentalNeedDetailsTimestamp;
    }

    public Boolean getUserPgRentalNeedDetailsIsAgreed() {
        return userPgRentalNeedDetailsIsAgreed;
    }

    public void setUserPgRentalNeedDetailsIsAgreed(Boolean userPgRentalNeedDetailsIsAgreed) {
        this.userPgRentalNeedDetailsIsAgreed = userPgRentalNeedDetailsIsAgreed;
    }

    public String getUserPgOwnerId() {
        return userPgOwnerId;
    }

    public void setUserPgOwnerId(String userPgOwnerId) {
        this.userPgOwnerId = userPgOwnerId;
    }

    public String getUserPgPropertyId() {
        return userPgPropertyId;
    }

    public void setUserPgPropertyId(String userPgPropertyId) {
        this.userPgPropertyId = userPgPropertyId;
    }

    public Timestamp getUserPgCheckoutDate() {
        return userPgCheckoutDate;
    }

    public void setUserPgCheckoutDate(Timestamp userPgCheckoutDate) {
        this.userPgCheckoutDate = userPgCheckoutDate;
    }

    public Boolean getUserPgIsAccepted() {
        return userPgIsAccepted;
    }

    public void setUserPgIsAccepted(Boolean userPgIsAccepted) {
        this.userPgIsAccepted = userPgIsAccepted;
    }

	public String getPincode() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Timestamp getReqRaisedDate() {
		return reqRaisedDate;
	}

	public void setReqRaisedDate(Timestamp reqRaisedDate) {
		this.reqRaisedDate = reqRaisedDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getUserPgMoveOutRequestOutDate() {
		return userPgMoveOutRequestOutDate;
	}

	public void setUserPgMoveOutRequestOutDate(Timestamp userPgMoveOutRequestOutDate) {
		this.userPgMoveOutRequestOutDate = userPgMoveOutRequestOutDate;
	}

	public Timestamp getUserPgMoveOutRequestRaisedDate() {
		return userPgMoveOutRequestRaisedDate;
	}

	public void setUserPgMoveOutRequestRaisedDate(Timestamp userPgMoveOutRequestRaisedDate) {
		this.userPgMoveOutRequestRaisedDate = userPgMoveOutRequestRaisedDate;
	}
	
	
	
}
