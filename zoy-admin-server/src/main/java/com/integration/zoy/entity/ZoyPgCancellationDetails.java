package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;

@Entity
@Table(name = "zoy_pg_cancellation_details", schema = "pgowners")
public class ZoyPgCancellationDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "cancellation_id", nullable = false, length = 36)
    private String cancellationId;

    @Column(name = "cancellation_fixed_charges", precision = 10, scale = 2)
    private BigDecimal cancellationFixedCharges;

    @Column(name = "cancellation_variable_charges", precision = 10, scale = 2)
    private BigDecimal cancellationVariableCharges;

    @Column(name = "cancellation_days")
    private Integer cancellationDays;

    // Getters and Setters
    public String getCancellationId() {
        return cancellationId;
    }

    public void setCancellationId(String cancellationId) {
        this.cancellationId = cancellationId;
    }

    public BigDecimal getCancellationFixedCharges() {
        return cancellationFixedCharges;
    }

    public void setCancellationFixedCharges(BigDecimal cancellationFixedCharges) {
        this.cancellationFixedCharges = cancellationFixedCharges;
    }

    public BigDecimal getCancellationVariableCharges() {
        return cancellationVariableCharges;
    }

    public void setCancellationVariableCharges(BigDecimal cancellationVariableCharges) {
        this.cancellationVariableCharges = cancellationVariableCharges;
    }

    public Integer getCancellationDays() {
        return cancellationDays;
    }

    public void setCancellationDays(Integer cancellationDays) {
        this.cancellationDays = cancellationDays;
    }

    @Override
    public String toString() {
        return "CancellationDetails{" +
                "cancellationId='" + cancellationId + '\'' +
                ", cancellationFixedCharges=" + cancellationFixedCharges +
                ", cancellationVariableCharges=" + cancellationVariableCharges +
                ", cancellationDays=" + cancellationDays +
                '}';
    }
}