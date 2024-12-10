package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_owner_details", schema = "pgowners")
public class ZoyPgOwnerDetails {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "pg_owner_id", updatable = false, nullable = false, unique = true, length = 36)
    private String pgOwnerId;

    @Column(name = "zoy_code", length = 50)
    private String zoyCode;

    @Column(name = "pg_owner_name", length = 100)
    private String pgOwnerName;

    @Column(name = "pg_owner_email", length = 100)
    private String pgOwnerEmail;

    @Column(name = "pg_owner_mobile", length = 15)
    private String pgOwnerMobile;
    
    @Column(name = "pg_owner_encrypted_aadhar")
    private String pgOwnerEncryptedAadhar;
    
    @Column(name = "create_at", nullable = false)
	@CreationTimestamp
	private Timestamp ts;


    public String getPgOwnerId() {
        return pgOwnerId;
    }

    public void setPgOwnerId(String pgOwnerId) {
        this.pgOwnerId = pgOwnerId;
    }

    public String getZoyCode() {
        return zoyCode;
    }

    public void setZoyCode(String zoyCode) {
        this.zoyCode = zoyCode;
    }

    public String getPgOwnerName() {
        return pgOwnerName;
    }

    public void setPgOwnerName(String pgOwnerName) {
        this.pgOwnerName = pgOwnerName;
    }

    public String getPgOwnerEmail() {
        return pgOwnerEmail;
    }

    public void setPgOwnerEmail(String pgOwnerEmail) {
        this.pgOwnerEmail = pgOwnerEmail;
    }

    public String getPgOwnerMobile() {
        return pgOwnerMobile;
    }

    public void setPgOwnerMobile(String pgOwnerMobile) {
        this.pgOwnerMobile = pgOwnerMobile;
    }

	public String getPgOwnerEncryptedAadhar() {
		return pgOwnerEncryptedAadhar;
	}

	public void setPgOwnerEncryptedAadhar(String pgOwnerEncryptedAadhar) {
		this.pgOwnerEncryptedAadhar = pgOwnerEncryptedAadhar;
	}

	public Timestamp getTs() {
		return ts;
	}

	public void setTs(Timestamp ts) {
		this.ts = ts;
	}
    
}

