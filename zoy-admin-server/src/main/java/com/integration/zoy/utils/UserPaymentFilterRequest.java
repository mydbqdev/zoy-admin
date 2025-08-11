package com.integration.zoy.utils;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPaymentFilterRequest {
	
	@JsonProperty("pageIndex")
	private int pageIndex;
	
	@JsonProperty("pageSize")
	private int pageSize; 
	
	@JsonProperty("sortActive")
	private String sortActive;
	
	@JsonProperty("sortDirection")
	private String sortDirection;
	
	@JsonProperty("cityLocation")
	private String cityLocation;
	
	
	@JsonProperty("fromDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp fromDate;
	
	@JsonProperty("toDate")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp toDate;
	
	
	@JsonProperty("reportType")
	private String reportType;
	
	
	@JsonProperty("downloadType")
	private String downloadType;
	
	@JsonProperty("filterData")
	private String filterData;
	
	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("isAlert")
	private boolean isAlert;
	
	@JsonProperty("searchText")
	private String searchText;
	
	@JsonProperty("lowRating")
	private String lowRating;

	public String getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getSortActive() {
		return sortActive;
	}

	public void setSortActive(String sortActive) {
		this.sortActive = sortActive;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	public String getCityLocation() {
		return cityLocation;
	}

	public void setCityLocation(String cityLocation) {
		this.cityLocation = cityLocation;
	}

	
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

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}

	public String getFilterData() {
		return filterData;
	}

	public void setFilterData(String filterData) {
		this.filterData = filterData;
	}

	public boolean getIsAlert() {
		return isAlert;
	}

	public void setISAlert(boolean isAlert) {
		this.isAlert = isAlert;
	}
	
	public String getSearchText() {
	    return searchText;
	}
 
	public void setSearchText(String searchText) {
	    this.searchText = searchText;
	}

	public String getLowRating() {
		return lowRating;
	}

	public void setLowRating(String lowRating) {
		this.lowRating = lowRating;
	}
	
}