package com.integration.zoy.model;

public class Bed {
    private String bedId;
    private String bedName;
    private String availabilityStatus;

    public Bed() {}

    public Bed(String bedId, String bedName, String availabilityStatus) {
        this.bedId = bedId;
        this.bedName = bedName;
        this.availabilityStatus = availabilityStatus;
    }

    public String getBedId() {
        return bedId;
    }

    public void setBedId(String bedId) {
        this.bedId = bedId;
    }

    public String getBedName() {
        return bedName;
    }

    public void setBedName(String bedName) {
        this.bedName = bedName;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return "Bed{" +
                "bedId='" + bedId + '\'' +
                ", bedName='" + bedName + '\'' +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                '}';
    }
}
