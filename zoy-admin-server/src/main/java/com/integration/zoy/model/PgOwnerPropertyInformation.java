package com.integration.zoy.model;

import java.util.List;

public class PgOwnerPropertyInformation {
	private String propertyName;
	private String status;
	private BasicPropertyInformation basicPropertyInformation;
	private PgOwnerAdditionalInfo pgOwnerAdditionalInfo;
	private List<FloorInformation> floorInformation;

	public PgOwnerAdditionalInfo getPgOwnerAdditionalInfo() {
		return pgOwnerAdditionalInfo;
	}

	public void setPgOwnerAdditionalInfo(PgOwnerAdditionalInfo pgOwnerAdditionalInfo) {
		this.pgOwnerAdditionalInfo = pgOwnerAdditionalInfo;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BasicPropertyInformation getBasicPropertyInformation() {
		return basicPropertyInformation;
	}

	public void setBasicPropertyInformation(BasicPropertyInformation basicPropertyInformation) {
		this.basicPropertyInformation = basicPropertyInformation;
	}

	public List<FloorInformation> getFloorInformation() {
		return floorInformation;
	}

	public void setFloorInformation(List<FloorInformation> floorInformation) {
		this.floorInformation = floorInformation;
	}

	public void setPgOwnerAdditionalInfo(List<PgOwnerAdditionalInfo> singletonList) {
		// TODO Auto-generated method stub

	}

}