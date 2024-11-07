package com.integration.zoy.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "zoy_pg_share_master", schema = "pgowners")
public class ZoyPgShareMaster {

    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "share_id", updatable = false, nullable = false, unique = true, length = 36)
    private String shareId;

    @Column(name = "share_type", length = 50)
    private String shareType;

    @Column(name = "share_occupancy_count")
    private Integer shareOccupancyCount;

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public Integer getShareOccupancyCount() {
        return shareOccupancyCount;
    }

    public void setShareOccupancyCount(Integer shareOccupancyCount) {
        this.shareOccupancyCount = shareOccupancyCount;
    }
}
