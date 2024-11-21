package com.integration.zoy.utils;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPaymentFilterRequest {

	@JsonProperty("fromDate")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp fromDate;
	
	@JsonProperty("toDate")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp toDate;
	
	@JsonProperty("page")
	private int page = 0;
	
	@JsonProperty("size")
	private int size = 10; 
	
	@JsonProperty("filterData")
	private String filterData;
	
	@JsonProperty("templateName")
	private String templateName;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("sort")
	private String sort;

	public Timestamp getFromDate() {
		return fromDate;
	}

	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}

	public Timestamp getToDate() {
		return toDate;
	}

	public void setToDate(Timestamp toDate) {
		this.toDate = toDate;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getFilterData() {
		return filterData;
	}

	public void setFilterData(String filterData) {
		this.filterData = filterData;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	
}