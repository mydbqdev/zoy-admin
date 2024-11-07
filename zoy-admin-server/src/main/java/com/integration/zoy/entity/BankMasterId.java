package com.integration.zoy.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class BankMasterId implements Serializable {
	  private static final long serialVersionUID = 1L;
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_bank_id")
    private String userBanksId;


    public BankMasterId(String userId, String userBanksId) {
        this.userId = userId;
        this.userBanksId = userBanksId;
    }


	public BankMasterId() {
		// TODO Auto-generated constructor stub
	}


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}


	public String getUserBanksId() {
		return userBanksId;
	}


	public void setUserBanksId(String userBanksId) {
		this.userBanksId = userBanksId;
	}

    
}
