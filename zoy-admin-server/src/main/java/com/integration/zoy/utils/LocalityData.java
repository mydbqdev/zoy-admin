package com.integration.zoy.utils;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class LocalityData {

   @JsonProperty("locality")
   String locality;

   @JsonProperty("browseImageUri")
   String browseImageUri;

   @JsonProperty("sub_level")
   String subLevel;

   @JsonProperty("location")
   Location location;


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
    
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }
	@Override
	public String toString() {
		return "LocalityData [locality=" + locality + ", browseImageUri=" + browseImageUri + ", subLevel=" + subLevel
				+ ", location=" + location + "]";
	}
    
}