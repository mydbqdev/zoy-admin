package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_ekyc_type_master", schema = "pgusers")
public class UserEkycTypeMaster {

    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "user_ekyc_type_id", updatable = false, nullable = false, unique = true, length = 36)
    private String userEkycTypeId;

    @Column(name = "user_ekyc_type_name")
    private String userEkycTypeName;

  
    public String getUserEkycTypeId() {
        return userEkycTypeId;
    }

    public void setUserEkycTypeId(String userEkycTypeId) {
        this.userEkycTypeId = userEkycTypeId;
    }

    public String getUserEkycTypeName() {
        return userEkycTypeName;
    }

    public void setUserEkycTypeName(String userEkycTypeName) {
        this.userEkycTypeName = userEkycTypeName;
    }
}
