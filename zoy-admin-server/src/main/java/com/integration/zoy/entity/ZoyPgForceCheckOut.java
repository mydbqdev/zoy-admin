package com.integration.zoy.entity;

import javax.persistence.*;


@Entity
@Table(name = "zoy_pg_force_check_out", schema = "pgowners")
public class ZoyPgForceCheckOut {

    @Id
    @Column(name = "force_check_out_id", nullable = false, length = 36)
    private String forceCheckOutId;

    @Column(name = "force_check_out_days", nullable = false)
    private int forceCheckOutDays;

    // Getters and Setters
    public String getForceCheckOutId() {
        return forceCheckOutId;
    }

    public void setForceCheckOutId(String forceCheckOutId) {
        this.forceCheckOutId = forceCheckOutId;
    }

    public int getForceCheckOutDays() {
        return forceCheckOutDays;
    }

    public void setForceCheckOutDays(int forceCheckOutDays) {
        this.forceCheckOutDays = forceCheckOutDays;
    }

    @Override
    public String toString() {
        return "ZoyPgForceCheckOut{" +
                "forceCheckOutId='" + forceCheckOutId + '\'' +
                ", forceCheckOutDays=" + forceCheckOutDays +
                '}';
    }
}
