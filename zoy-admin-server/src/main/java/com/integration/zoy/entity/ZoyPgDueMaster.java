package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_due_master", schema = "pgowners")
public class ZoyPgDueMaster {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "due_type_id", nullable = false, length = 36)
    private String dueTypeId;

    @Column(name = "due_name", nullable = false)
    private String dueName;

    @Column(name = "due_image")
    private String dueImage;
    
    @Column(name = "due_status")
    private Boolean dueStatus;
    
    
    public String getDueTypeId() {
        return dueTypeId;
    }

    public void setDueTypeId(String dueTypeId) {
        this.dueTypeId = dueTypeId;
    }

    public String getDueName() {
        return dueName;
    }

    public void setDueName(String dueName) {
        this.dueName = dueName;
    }

	public String getDueImage() {
		return dueImage;
	}

	public void setDueImage(String dueImage) {
		this.dueImage = dueImage;
	}

	public Boolean getDueStatus() {
		return dueStatus;
	}

	public void setDueStatus(Boolean dueStatus) {
		this.dueStatus = dueStatus;
	}

	@Override
	public String toString() {
		return "ZoyPgDueMaster [dueTypeId=" + dueTypeId + ", dueName=" + dueName + ", dueImage=" + dueImage
				+ ", dueStatus=" + dueStatus + "]";
	}

	

    
}
