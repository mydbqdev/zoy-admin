package com.integration.zoy.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user_search_data", schema = "pgusers")
public class UserSearchData {

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
   	@Column(name = "user_search_id")
    private Integer userSearchId;
	
    @Column(name = "user_id")
    private String userId;

    @Column(name = "search_text", length = 255)
    private String searchText;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(name = "timestamp")
    private Timestamp timestamp;
    
    @Column(name = "user_search_selected_city")
    private String userSearchSelectedCity;

    // Getters and Setters

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

	public Integer getUserSearchId() {
		return userSearchId;
	}

	public void setUserSearchId(Integer userSearchId) {
		this.userSearchId = userSearchId;
	}

	public String getUserSearchSelectedCity() {
		return userSearchSelectedCity;
	}

	public void setUserSearchSelectedCity(String userSearchSelectedCity) {
		this.userSearchSelectedCity = userSearchSelectedCity;
	}
    
    
}
