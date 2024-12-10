package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_due_master", schema = "pgusers")
public class UserDueMaster {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "due_type_id", updatable = false, nullable = false, unique = true, length = 36)
	private String dueTypeId;

	@Column(name = "due_type_name")
	private String dueTypeName;


	public String getDueTypeId() {
		return dueTypeId;
	}

	public void setDueTypeId(String dueTypeId) {
		this.dueTypeId = dueTypeId;
	}

	public String getDueTypeName() {
		return dueTypeName;
	}

	public void setDueTypeName(String dueTypeName) {
		this.dueTypeName = dueTypeName;
	}
}
