package com.integration.zoy.model;

import java.util.List;

public class PgOwnerPropertyInformation {
	private String propertyName;
	private String propertyId;
	private String status;
	private String zoyFixedShare;
	private String zoyVariableShare;
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

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public String getZoyFixedShare() {
		return zoyFixedShare;
	}

	public void setZoyFixedShare(String zoyFixedShare) {
		this.zoyFixedShare = zoyFixedShare;
	}

	public String getZoyVariableShare() {
		return zoyVariableShare;
	}

	public void setZoyVariableShare(String zoyVariableShare) {
		this.zoyVariableShare = zoyVariableShare;
	}

	
	
	

}