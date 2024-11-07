package com.integration.zoy.entity;


import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "review_ratings_master", schema = "pgcommon")
public class ReviewRatingsMaster {
    @Id
    @GeneratedValue(generator = "UUID")
   	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
   	@Column(name = "review_type_id", updatable = false, nullable = false, unique = true, length = 36)
    private String reviewTypeId;

    @Column(name = "review_type", nullable = false)
    private String reviewType;

    // Getters and Setters

    public String getReviewTypeId() {
        return reviewTypeId;
    }

    public void setReviewTypeId(String reviewTypeId) {
        this.reviewTypeId = reviewTypeId;
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }
}
