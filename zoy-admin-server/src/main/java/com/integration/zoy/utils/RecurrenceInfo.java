package com.integration.zoy.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecurrenceInfo   {
	@JsonProperty("startDate")
	private Long startDate = null;

	@JsonProperty("endDate")
	private Long endDate = null;

	@JsonProperty("repeatType")
	private String repeatType = null;

	@JsonProperty("repeatEvery")
	private Integer repeatEvery = null;

	@JsonProperty("days")
	private List<String> days = null;

	@JsonProperty("onDay")
	private Integer onDay = null;

	@JsonProperty("onWeek")
	private String onWeek = null;

	@JsonProperty("onMonth")
	private String onMonth = null;

	public RecurrenceInfo startDate(Long startDate) {
		this.startDate = startDate;
		return this;
	}


	public Long getStartDate() {
		return startDate;
	}

	public void setStartDate(Long startDate) {
		this.startDate = startDate;
	}

	public RecurrenceInfo endDate(Long endDate) {
		this.endDate = endDate;
		return this;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public RecurrenceInfo repeatType(String repeatType) {
		this.repeatType = repeatType;
		return this;
	}


	public String getRepeatType() {
		return repeatType;
	}

	public void setRepeatType(String repeatType) {
		this.repeatType = repeatType;
	}

	public RecurrenceInfo repeatEvery(Integer repeatEvery) {
		this.repeatEvery = repeatEvery;
		return this;
	}


	public Integer getRepeatEvery() {
		return repeatEvery;
	}

	public void setRepeatEvery(Integer repeatEvery) {
		this.repeatEvery = repeatEvery;
	}

	public RecurrenceInfo days(List<String> days) {
		this.days = days;
		return this;
	}

	public RecurrenceInfo addDaysItem(String daysItem) {
		if (this.days == null) {
			this.days = new ArrayList<String>();
		}
		this.days.add(daysItem);
		return this;
	}


	public List<String> getDays() {
		return days;
	}

	public void setDays(List<String> days) {
		this.days = days;
	}

	public RecurrenceInfo onDay(Integer onDay) {
		this.onDay = onDay;
		return this;
	}


	public Integer getOnDay() {
		return onDay;
	}

	public void setOnDay(Integer onDay) {
		this.onDay = onDay;
	}

	public RecurrenceInfo onWeek(String onWeek) {
		this.onWeek = onWeek;
		return this;
	}


	public String getOnWeek() {
		return onWeek;
	}

	public void setOnWeek(String onWeek) {
		this.onWeek = onWeek;
	}

	public RecurrenceInfo onMonth(String onMonth) {
		this.onMonth = onMonth;
		return this;
	}


	public String getOnMonth() {
		return onMonth;
	}

	public void setOnMonth(String onMonth) {
		this.onMonth = onMonth;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		RecurrenceInfo recurrenceInfo = (RecurrenceInfo) o;
		return Objects.equals(this.startDate, recurrenceInfo.startDate) &&
				Objects.equals(this.endDate, recurrenceInfo.endDate) &&
				Objects.equals(this.repeatType, recurrenceInfo.repeatType) &&
				Objects.equals(this.repeatEvery, recurrenceInfo.repeatEvery) &&
				Objects.equals(this.days, recurrenceInfo.days) &&
				Objects.equals(this.onDay, recurrenceInfo.onDay) &&
				Objects.equals(this.onWeek, recurrenceInfo.onWeek) &&
				Objects.equals(this.onMonth, recurrenceInfo.onMonth);
	}

	@Override
	public int hashCode() {
		return Objects.hash(startDate, endDate, repeatType, repeatEvery, days, onDay, onWeek, onMonth);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class RecurrenceInfo {\n");

		sb.append("    startDate: ").append(toIndentedString(startDate)).append("\n");
		sb.append("    endDate: ").append(toIndentedString(endDate)).append("\n");
		sb.append("    repeatType: ").append(toIndentedString(repeatType)).append("\n");
		sb.append("    repeatEvery: ").append(toIndentedString(repeatEvery)).append("\n");
		sb.append("    days: ").append(toIndentedString(days)).append("\n");
		sb.append("    onDay: ").append(toIndentedString(onDay)).append("\n");
		sb.append("    onWeek: ").append(toIndentedString(onWeek)).append("\n");
		sb.append("    onMonth: ").append(toIndentedString(onMonth)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
