package com.integration.zoy.utils;

public class OwnerLeadPaginationRequest {
	private int pageIndex=0;
    private int pageSize=10;
    private String sortActive;
    private String sortDirection;
    private OwnerLeadFilter filter;
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
	public OwnerLeadFilter getFilter() {
		return filter;
	}
	public void setFilter(OwnerLeadFilter filter) {
		this.filter = filter;
	}
    
    
}
