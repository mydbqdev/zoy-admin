package com.integration.zoy.model;

public class TicketCategory {

	private String totalIssues;
	private String resolved;
	private String Opened;
	private String Pending;
	private String cancelled;

	public String getTotalIssues() {
		return totalIssues;
	}

	public void setTotalIssues(String totalIssues) {
		this.totalIssues = totalIssues;
	}

	public String getResolved() {
		return resolved;
	}

	public void setResolved(String resolved) {
		this.resolved = resolved;
	}

	public String getOpened() {
		return Opened;
	}

	public void setOpened(String opened) {
		Opened = opened;
	}

	public String getPending() {
		return Pending;
	}

	public void setPending(String pending) {
		Pending = pending;
	}

	public String getCancelled() {
		return cancelled;
	}

	public void setCancelled(String cancelled) {
		this.cancelled = cancelled;
	}

}
