package com.integration.zoy.utils;

public class PaginationRequest {
	private int pageIndex=0;
    private int pageSize=10;
    private String sortActive;
    private String sortDirection;
    private String userEmail;
    private String searchText;
    private String activity;
    private Filter filter;
  	private String downloadType;
    private boolean isUserActivity;

    public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
		}
	public int getPageIndex() {
		return pageIndex;
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
	
	public Filter getFilter() {
		return filter;
	}
	public void setFilter(Filter filter) {
		this.filter = filter;
	}
	public void setUserActivity(boolean isUserActivity) {
		this.isUserActivity = isUserActivity;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getSearchText() {
		return searchText;
	}
	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getDownloadType() {
		return downloadType;
	}
	public void setDownloadType(String downloadType) {
		this.downloadType = downloadType;
	}
	public boolean getIsUserActivity() {
		return isUserActivity;
	}
	public void setIsUserActivity(boolean isUserActivity) {
		this.isUserActivity = isUserActivity;
	}

    
}
