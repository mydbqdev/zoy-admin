package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "user_master", schema = "pgusers")
public class UserMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "user_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "user_mobile")
    private String userMobile;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_pin")
    private Integer userPin;

    @Column(name = "user_id_enc")
    private String userIdEnc;

    @Column(name = "user_first_name")
    private String userFirstName;

    @Column(name = "user_last_name")
    private String userLastName;

    @Column(name = "user_ekyc_isekycverified")
    private Boolean userEkycIsEkycVerified;
    
    @Column(name = "user_ekyc_isvideo_verified")
    private Boolean userEkycIsVideoVerified;

    @Column(name = "user_created_at")
    @CreationTimestamp
    private Timestamp userCreatedAt;

    @Column(name = "user_modified_at")
    @UpdateTimestamp
    private Timestamp userModifiedAt;
    
    @Column(name = "user_gender")
    private String userGender;

    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getUserPin() {
        return userPin;
    }

    public void setUserPin(Integer userPin) {
        this.userPin = userPin;
    }

    public String getUserIdEnc() {
        return userIdEnc;
    }

    public void setUserIdEnc(String userIdEnc) {
        this.userIdEnc = userIdEnc;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

	public Boolean getUserEkycIsEkycVerified() {
		return userEkycIsEkycVerified;
	}

	public void setUserEkycIsEkycVerified(Boolean userEkycIsEkycVerified) {
		this.userEkycIsEkycVerified = userEkycIsEkycVerified;
	}

	public Timestamp getUserCreatedAt() {
        return userCreatedAt;
    }

    public void setUserCreatedAt(Timestamp userCreatedAt) {
        this.userCreatedAt = userCreatedAt;
    }

    public Timestamp getUserModifiedAt() {
        return userModifiedAt;
    }

    public void setUserModifiedAt(Timestamp userModifiedAt) {
        this.userModifiedAt = userModifiedAt;
    }

	public String getUserGender() {
		return userGender;
	}

	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}

	public Boolean getUserEkycIsVideoVerified() {
		return userEkycIsVideoVerified;
	}

	public void setUserEkycIsVideoVerified(Boolean userEkycIsVideoVerified) {
		this.userEkycIsVideoVerified = userEkycIsVideoVerified;
	}

	@Override
	public String toString() {
		return "UserMaster [userId=" + userId + ", userMobile=" + userMobile + ", userEmail=" + userEmail + ", userPin="
				+ userPin + ", userIdEnc=" + userIdEnc + ", userFirstName=" + userFirstName + ", userLastName="
				+ userLastName + ", userEkycIsEkycVerified=" + userEkycIsEkycVerified + ", userEkycIsVideoVerified="
				+ userEkycIsVideoVerified + ", userCreatedAt=" + userCreatedAt + ", userModifiedAt=" + userModifiedAt
				+ ", userGender=" + userGender + "]";
	}

	

	

}
