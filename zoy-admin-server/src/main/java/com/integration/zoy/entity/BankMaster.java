package com.integration.zoy.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity 
@Table(name = "bank_master", schema = "pgcommon")
public class BankMaster {

	@EmbeddedId
	private BankMasterId id;

	public BankMaster() {
	}

	public BankMaster(String userId, String userBankId) {
		this.id = new BankMasterId(userId, userBankId);
	}

	public BankMasterId getId() {
		return id;
	}

	public void setId(BankMasterId id) {
		this.id = id;
	}
}
