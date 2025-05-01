package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SupportTicketDTO {
	
	@JsonProperty("ticket_id")
	private String ticket_id;
	
	@JsonProperty("created_date")
    private String created_date;
	
	@JsonProperty("ticket_type")
    private String ticket_type;
	
	@JsonProperty("priority")
    private boolean priority;
	
	@JsonProperty("assign_name")
    private String assign_name;
	
	@JsonProperty("assign_email")
    private String assign_email;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("type")
    private String type;

	public String getTicket_id() {
		return ticket_id;
	}

	public void setTicket_id(String ticket_id) {
		this.ticket_id = ticket_id;
	}

	public String getCreated_date() {
		return created_date;
	}

	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}

	public String getTicket_type() {
		return ticket_type;
	}

	public void setTicket_type(String ticket_type) {
		this.ticket_type = ticket_type;
	}

	public boolean isPriority() {
		return priority;
	}

	public void setPriority(boolean priority) {
		this.priority = priority;
	}

	public String getAssign_name() {
		return assign_name;
	}

	public void setAssign_name(String assign_name) {
		this.assign_name = assign_name;
	}

	public String getAssign_email() {
		return assign_email;
	}

	public void setAssign_email(String assign_email) {
		this.assign_email = assign_email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
