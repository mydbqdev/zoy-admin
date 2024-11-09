package com.integration.zoy.entity;

import javax.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "user_temprory", schema = "pgadmin")
public class AdminUserTemporary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "user_email", nullable = false)
    private AdminUserMaster userMaster;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private AppRole appRole;

    @Column(name = "is_approve", nullable = true)
    private Boolean isApprove = false;

    @Column(name = "created_on")
    private Timestamp createdOn;

    @Column(name = "approve_on")
    private Timestamp approveOn;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AdminUserMaster getUserMaster() {
        return userMaster;
    }

    public void setUserMaster(AdminUserMaster userMaster) {
        this.userMaster = userMaster;
    }

    public AppRole getAppRole() {
        return appRole;
    }

    public void setAppRole(AppRole appRole) {
        this.appRole = appRole;
    }

    public Boolean getIsApprove() {
        return isApprove;
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
