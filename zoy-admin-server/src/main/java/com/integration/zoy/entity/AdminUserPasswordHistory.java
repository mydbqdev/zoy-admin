package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "user_password_history", schema = "pgadmin")
public class AdminUserPasswordHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "user_email", nullable = false)
	private String userEmail;

	@Column(name = "password", nullable = false)
	private String password;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

