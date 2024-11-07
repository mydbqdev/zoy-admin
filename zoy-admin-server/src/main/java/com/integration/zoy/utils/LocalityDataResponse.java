package com.integration.zoy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

   
public class LocalityDataResponse {

   @SerializedName("locality")
   String locality;

   @SerializedName("browseImageUri")
   String browseImageUri;

   @SerializedName("sub_level")
   String subLevel;

   @SerializedName("location")
   LocationResponse location;


    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getLocality() {
        return locality;
    }
    
    public void setBrowseImageUri(String browseImageUri) {
        this.browseImageUri = browseImageUri;
    }
    public String getBrowseImageUri() {
        return browseImageUri;
    }
    
    public void setSubLevel(String subLevel) {
        this.subLevel = subLevel;
    }
    public String getSubLevel() {
        return subLevel;
    }
    
    public void setLocation(LocationResponse location) {
        this.location = location;
    }
    public LocationResponse getLocation() {
        return location;
    }
	@Override
	public String toString() {
		return "LocalityData [locality=" + locality + ", browseImageUri=" + browseImageUri + ", subLevel=" + subLevel
				+ ", location=" + location + "]";
	}
    
}