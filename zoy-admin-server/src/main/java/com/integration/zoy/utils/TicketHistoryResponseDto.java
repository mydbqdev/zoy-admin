package com.integration.zoy.utils;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class TicketHistoryResponseDto {
	private String ticketId;
	private String description;
	private String newStatus;
	private String newAssignee;
	private String changedBy;
	private Timestamp createdAt;
	private List<String> historyImage;
}
