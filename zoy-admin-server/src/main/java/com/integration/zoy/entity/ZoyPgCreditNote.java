package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "zoy_pg_credit_note", schema = "pgcommon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoyPgCreditNote {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "credit_id", nullable = false, length = 36)
    private String creditId;

    @Column(name = "property_id", nullable = false, length = 36)
    private String propertyId;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "booking_id", length = 36)
    private String bookingId;

    @Column(name = "adjustment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal adjustmentAmount;

    @Column(name = "adjustment_status", nullable = false)
    private boolean adjustmentStatus = false;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private Timestamp updatedAt;
}
