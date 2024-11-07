package com.integration.zoy.utils;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

   
public class BrowserLocality {

   @JsonProperty("locality")
   String locality;

   @JsonProperty("sub_level")
   String subLevel;

   @JsonProperty("localityData")
   List<LocalityData> localityData;


    public void setLocality(String locality) {
        this.locality = locality;
    }
    public String getLocality() {
        return locality;
    }
    
    public void setSubLevel(String subLevel) {
        this.subLevel = subLevel;
    }
    public String getSubLevel() {
        return subLevel;
    }
    
    public void setLocalityData(List<LocalityData> localityData) {
        this.localityData = localityData;
    }
    public List<LocalityData> getLocalityData() {
        return localityData;
    }
	@Override
	public String toString() {
		return "BrowserLocality [locality=" + locality + ", subLevel=" + subLevel + ", localityData=" + localityData
				+ "]";
	}
    
    
    
}