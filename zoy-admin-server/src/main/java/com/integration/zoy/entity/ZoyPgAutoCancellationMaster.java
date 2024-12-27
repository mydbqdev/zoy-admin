package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_auto_cancellation_master", schema = "pgowners")
public class ZoyPgAutoCancellationMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "auto_cancellation_id", updatable = false, nullable = false, unique = true, length = 36)
    private String autoCancellationId;

    @Column(name = "auto_cancellation_day")
    private int autoCancellationDay = 0;

    // Getters and Setters
    public String getAutoCancellationId() {
        return autoCancellationId;
    }

    public void setAutoCancellationId(String autoCancellationId) {
        this.autoCancellationId = autoCancellationId;
    }

    public int getAutoCancellationDay() {
        return autoCancellationDay;
    }

    public void setAutoCancellationDay(int autoCancellationDay) {
        this.autoCancellationDay = autoCancellationDay;
    }

    @Override
    public String toString() {
        return "ZoyPgAutoCancellationMaster{" +
                "autoCancellationId='" + autoCancellationId + '\'' +
                ", autoCancellationDay=" + autoCancellationDay +
                '}';
    }
}