package com.integration.zoy.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user_temprory", schema = "pgadmin")
public class AdminUserTemporary implements Serializable{
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	private AdminUserTemporaryPK adminUserTemporaryPK;

    @Column(name = "is_approve", nullable = true)
    private Boolean isApprove = false;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "approve_on")
    private Timestamp approveOn;

    // Getters and Setters

    

    public Boolean getIsApprove() {
        return isApprove;
    }

    public AdminUserTemporaryPK getAdminUserTemporaryPK() {
		return adminUserTemporaryPK;
	}

	public void setAdminUserTemporaryPK(AdminUserTemporaryPK adminUserTemporaryPK) {
		this.adminUserTemporaryPK = adminUserTemporaryPK;
	}

	public void setIsApprove(Boolean isApprove) {
        this.isApprove = isApprove;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public Timestamp getApproveOn() {
        return approveOn;
    }

    public void setApproveOn(Timestamp approveOn) {
        this.approveOn = approveOn;
    }
}
