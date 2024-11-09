package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_role", schema = "pgadmin")
public class AdminUserRole {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_email", referencedColumnName = "user_email", nullable = false)
	private UserMaster userMaster;

	@ManyToOne
	@JoinColumn(name = "role_id", referencedColumnName = "id", nullable = false)
	private AppRole appRole;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UserMaster getUserMaster() {
		return userMaster;
	}

	public void setUserMaster(UserMaster userMaster) {
		this.userMaster = userMaster;
	}

	public AppRole getAppRole() {
		return appRole;
	}

	public void setAppRole(AppRole appRole) {
		this.appRole = appRole;
	}
}
