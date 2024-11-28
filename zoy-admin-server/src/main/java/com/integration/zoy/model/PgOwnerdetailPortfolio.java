package com.integration.zoy.model;

import java.util.List;

public class PgOwnerdetailPortfolio {

	private PgOwnerProfile profile;
	private PgOwnerbasicInformation pgOwnerbasicInformation;
	private List<PgOwnerPropertyInformation> pgOwnerPropertyInformation;
	private List<PgOwnerBusinessInfo> pgOwnerBusinessInfo;

	public List<PgOwnerBusinessInfo> getPgOwnerBusinessInfo() {
		return pgOwnerBusinessInfo;
	}

	public void setPgOwnerBusinessInfo(List<PgOwnerBusinessInfo> pgOwnerBusinessInfo) {
		this.pgOwnerBusinessInfo = pgOwnerBusinessInfo;
	}

	public PgOwnerProfile getProfile() {
		return profile;
	}

	public void setProfile(PgOwnerProfile profile) {
		this.profile = profile;
	}

	public PgOwnerbasicInformation getPgOwnerbasicInformation() {
		return pgOwnerbasicInformation;
	}

	public void setPgOwnerbasicInformation(PgOwnerbasicInformation pgOwnerbasicInformation) {
		this.pgOwnerbasicInformation = pgOwnerbasicInformation;
	}

	public List<PgOwnerPropertyInformation> getPgOwnerPropertyInformation() {
		return pgOwnerPropertyInformation;
	}

	public void setPgOwnerPropertyInformation(List<PgOwnerPropertyInformation> pgOwnerPropertyInformation) {
		this.pgOwnerPropertyInformation = pgOwnerPropertyInformation;
	}

}
