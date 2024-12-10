package com.integration.zoy.utils;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class BrowserLocalityResponse {

   @SerializedName("locality")
   String locality;

   @SerializedName("sub_level")
   String subLevel;

   @SerializedName("localityData")
   List<LocalityDataResponse> localityData;


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
    
    public void setLocalityData(List<LocalityDataResponse> localityData) {
        this.localityData = localityData;
    }
    public List<LocalityDataResponse> getLocalityData() {
        return localityData;
    }
	@Override
	public String toString() {
		return "BrowserLocality [locality=" + locality + ", subLevel=" + subLevel + ", localityData=" + localityData
				+ "]";
	}
    
    
    
}