package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "triggered_cond", schema = "pgadmin")
public class TriggeredCond {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "cond_name", nullable = false, length = 10)
	private String condName;

	// Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCondName() {
		return condName;
	}

	public void setCondName(String condName) {
		this.condName = condName;
	}
}

