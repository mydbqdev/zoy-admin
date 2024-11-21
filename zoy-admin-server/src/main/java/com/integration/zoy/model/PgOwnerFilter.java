package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

public class PgOwnerFilter {

	    @JsonProperty("searchContent")
	    @JsonInclude(JsonInclude.Include.NON_ABSENT)
	    @JsonSetter(nulls = Nulls.FAIL)
	    private String searchContent = null;
	    
	    @JsonProperty("pageIndex")
	    @JsonInclude(JsonInclude.Include.NON_ABSENT)
	    @JsonSetter(nulls = Nulls.FAIL)
	    private Integer pageIndex = null;

	    @JsonProperty("pageSize")
	    @JsonInclude(JsonInclude.Include.NON_ABSENT)
	    @JsonSetter(nulls = Nulls.FAIL)
	    private Integer pageSize = null;

		public String getSearchContent() {
			return searchContent;
		}

		public void setSearchContent(String searchContent) {
			this.searchContent = searchContent;
		}

		public Integer getPageIndex() {
			return pageIndex;
		}

		public void setPageIndex(Integer pageIndex) {
			this.pageIndex = pageIndex;
		}

		public Integer getPageSize() {
			return pageSize;
		}

		public void setPageSize(Integer pageSize) {
			this.pageSize = pageSize;
		}

		
}
