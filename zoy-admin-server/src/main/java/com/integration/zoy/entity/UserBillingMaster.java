package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity 
@Table(name = "user_billing_master", schema = "pgusers")
public class UserBillingMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "billing_type_id", updatable = false, nullable = false, unique = true, length = 36)
    private String billingTypeId;

    @Column(name = "billing_type_name", length = 50)
    private String billingTypeName;

 
    public String getBillingTypeId() {
        return billingTypeId;
    }

    public void setBillingTypeId(String billingTypeId) {
        this.billingTypeId = billingTypeId;
    }

    public String getBillingTypeName() {
        return billingTypeName;
    }

    public void setBillingTypeName(String billingTypeName) {
        this.billingTypeName = billingTypeName;
    }
}
