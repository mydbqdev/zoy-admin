package com.integration.zoy.utils;
import java.util.List;

import com.google.gson.annotations.SerializedName;

   
public class Images {

   @SerializedName("images")
   List<Images> images;

   @SerializedName("type")
   String type;


    public void setImages(List<Images> images) {
        this.images = images;
    }
    public List<Images> getImages() {
        return images;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }
    
}