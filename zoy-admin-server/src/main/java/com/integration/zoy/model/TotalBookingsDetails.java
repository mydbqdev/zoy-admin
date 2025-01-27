package com.integration.zoy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TotalBookingsDetails {

    @JsonProperty("checkedIn")
    private long checkedIn;

    @JsonProperty("booked")
    private long booked;

    @JsonProperty("vacancy")
    private long vacancy;

    @JsonProperty("fromDate")
    private String fromDate;

    @JsonProperty("endDate")
    private String endDate;

    public long getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(long checkedIn) {
        this.checkedIn = checkedIn;
    }

    public long getBooked() {
        return booked;
    }

    public void setBooked(long booked) {
        this.booked = booked;
    }

    public long getVacancy() {
        return vacancy;
    }

    public void setVacancy(long vacancy) {
        this.vacancy = vacancy;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
