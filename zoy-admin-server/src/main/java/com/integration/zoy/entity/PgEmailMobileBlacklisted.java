package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Data;

@Data
@Entity
@Table(name = "pg_email_mobile_blacklisted", schema = "pgcommon")
public class PgEmailMobileBlacklisted {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "pg_blacklisted_id", length = 36, nullable = false)
    private String id;

    @Column(name = "pg_blocklisted_email", length = 255, nullable = false)
    private String email;

    @Column(name = "pg_blacklisted_mobile", length = 20, nullable = false)
    private String mobile;
}
