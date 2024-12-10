package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "role_screen", schema = "pgadmin")
public class RoleScreen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "screen_name", nullable = false)
    private String screenName;

    @Column(name = "read_prv", nullable = true)
    private Boolean readPrv = false;

    @Column(name = "write_prv", nullable = true)
    private Boolean writePrv = false;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
    private AppRole appRole;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public Boolean getReadPrv() {
        return readPrv;
    }

    public void setReadPrv(Boolean readPrv) {
        this.readPrv = readPrv;
    }

    public Boolean getWritePrv() {
        return writePrv;
    }

    public void setWritePrv(Boolean writePrv) {
        this.writePrv = writePrv;
    }

    public AppRole getAppRole() {
        return appRole;
    }

    public void setAppRole(AppRole appRole) {
        this.appRole = appRole;
    }
}

