package com.integration.zoy.entity;

import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "zoy_vendor_master", schema = "pgvendor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZoyVendorMaster {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "vendor_id", updatable = false, nullable = false, unique = true, length = 36)
    private String vendorId;

    @Column(name = "vendor_company_name", length = 255, nullable = false)
    private String vendorCompanyName;

    @Column(name = "vendor_first_name", length = 50, nullable = false)
    private String vendorFirstName;

    @Column(name = "vendor_last_name", length = 50)
    private String vendorLastName;

    @Column(name = "vendor_address", length = 500, nullable = false)
    private String vendorAddress;

    @Column(name = "vendor_mobile_no", nullable = false)
    private Long vendorMobileNo;

    @Column(name = "vendor_alternative_no")
    private Long vendorAlternativeNo;

    @Column(name = "vendor_email", length = 255, nullable = false)
    private String vendorEmail;

    @Column(name = "vendor_gst_registrastion_no", length = 100, nullable = false)
    private String vendorGstRegistrationNo;

    @Column(name = "vendor_password", columnDefinition = "text")
    @JsonIgnore
    private String vendorPassword;

    @Column(name = "vendor_mpin")
    @JsonIgnore
    private Long vendorMpin;

    @Column(name = "vendor_bio_metric")
    private Boolean vendorBioMetric = false;

    @Column(name = "vendor_mpin_status")
    private Boolean vendorMpinStatus = false;

    @Column(name = "vendor_created_at", nullable = false)
    @CreationTimestamp
    private Timestamp vendorCreatedAt;

    @Column(name = "vendor_updated_at")
    @UpdateTimestamp
    private Timestamp vendorUpdatedAt;

    @Column(name = "vendor_status", length = 1, nullable = false)
    @JsonIgnore
    private String vendorStatus = "P";

    @Column(name = "vendor_image", length = 500)
    private String vendorImage;

    @Column(name = "vendor_lead", nullable = false)
    private Boolean vendorLead = false;
    
    @Column(name = "vendor_password_change", nullable = false)
    private Boolean vendorPasswordChange = false;
    
    @Column(name = "vendor_rejected_reason")
    private String vendorRejectedReason;
    
}
