package com.integration.zoy.entity;


import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "pg_owners", schema = "pgsales")
@Data
public class PgOwner {
    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "owner_id")
    private String ownerId;
    
    @Column(name = "firstname")
    private String firstname;
    
    @Column(name = "lastname")
    private String lastname;
    
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    
    @Column(name = "mobile_number")
    private String mobileNumber;
    
    @Column(name = "alternative_number1")
    private String alternativeNumber1;
    
    @Column(name = "alternative_number2")
    private String alternativeNumber2;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Timestamp createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "is_owner")
    private Boolean isOwner;
    
}
