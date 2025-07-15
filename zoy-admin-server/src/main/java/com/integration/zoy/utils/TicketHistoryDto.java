package com.integration.zoy.utils;

import lombok.Data;

@Data
public class TicketHistoryDto {
	private String ticketId;
	private String description;
	private String newStatus;
	private String newAssignee;
	private String changedBy;
}
