package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "zoy_pg_share_image", schema = "pgowners")
public class ZoyPgShareImage {

	@Id
	@Column(name = "image_id", length = 255)
	private String imageId;

	@Column(name = "share_id", length = 255)
    private String shareId;


    // Getters and Setters
    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}

