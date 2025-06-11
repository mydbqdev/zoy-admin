package com.integration.zoy.model;

public class PgTypeDTO {

    private String pgTypeId;
    private String pgTypeName;
    private String genderNames; // Comma-separated list of gender names

    // Constructors
    public PgTypeDTO() {}

    public PgTypeDTO(String pgTypeId, String pgTypeName, String genderNames) {
        this.pgTypeId = pgTypeId;
        this.pgTypeName = pgTypeName;
        this.genderNames = genderNames;
    }

    // Getters and Setters
    public String getPgTypeId() {
        return pgTypeId;
    }

    public void setPgTypeId(String pgTypeId) {
        this.pgTypeId = pgTypeId;
    }

    public String getPgTypeName() {
        return pgTypeName;
    }

    public void setPgTypeName(String pgTypeName) {
        this.pgTypeName = pgTypeName;
    }

    public String getGenderNames() {
        return genderNames;
    }

    public void setGenderNames(String genderNames) {
        this.genderNames = genderNames;
    }

    @Override
    public String toString() {
        return "PgTypeDTO [pgTypeId=" + pgTypeId + ", pgTypeName=" + pgTypeName + ", genderNames=" + genderNames + "]";
    }
}

