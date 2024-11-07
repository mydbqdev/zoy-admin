package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class MoveOuts {
	@SerializedName("moveOuts")
	private List<String> moveOuts;

	public List<String> getMoveOuts() {
		return moveOuts;
	}

	public void setMoveOuts(List<String> moveOuts) {
		this.moveOuts = moveOuts;
	}

}
