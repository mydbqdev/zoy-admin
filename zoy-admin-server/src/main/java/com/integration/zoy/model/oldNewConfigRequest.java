package com.integration.zoy.model;

import com.integration.zoy.utils.ZoyShortTermDetails;

public class oldNewConfigRequest {
	private ZoyBeforeCheckInCancellationModel newBCCRule;
	private ZoyBeforeCheckInCancellationModel oldBCCRule;
	private ZoyShortTermDetails newSTDRule;
	private ZoyShortTermDetails oldSTDRule;

	public ZoyBeforeCheckInCancellationModel getNewBCCRule() {
		return newBCCRule;
	}

	public void setNewBCCRule(ZoyBeforeCheckInCancellationModel newBCCRule) {
		this.newBCCRule = newBCCRule;
	}

	public ZoyBeforeCheckInCancellationModel getOldBCCRule() {
		return oldBCCRule;
	}

	public void setOldBCCRule(ZoyBeforeCheckInCancellationModel oldBCCRule) {
		this.oldBCCRule = oldBCCRule;
	}

	public ZoyShortTermDetails getNewSTDRule() {
		return newSTDRule;
	}

	public void setNewSTDRule(ZoyShortTermDetails newSTDRule) {
		this.newSTDRule = newSTDRule;
	}

	public ZoyShortTermDetails getOldSTDRule() {
		return oldSTDRule;
	}

	public void setOldSTDRule(ZoyShortTermDetails oldSTDRule) {
		this.oldSTDRule = oldSTDRule;
	}

}
