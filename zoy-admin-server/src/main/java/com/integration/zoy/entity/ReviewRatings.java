package com.integration.zoy.entity;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "review_ratings", schema = "pgcommon")
public class ReviewRatings {
    @Id
    @GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "rating_id", updatable = false, nullable = false, unique = true, length = 36)
    private String ratingId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "overall_rating")
    private BigDecimal overallRating;

    @Column(name = "partner_id", nullable = false)
    private String partnerId;

    @Column(name = "property_id", nullable = false)
    private String propertyId;

    @Column(name = "rating_review_id")
    private String ratingReviewId;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "written_review", columnDefinition = "text")
    private String writtenReview;

    // Getters and Setters

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getOverallRating() {
        return overallRating;
    }

    public void setOverallRating(BigDecimal overallRating) {
        this.overallRating = overallRating;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getRatingReviewId() {
        return ratingReviewId;
    }

    public void setRatingReviewId(String ratingReviewId) {
        this.ratingReviewId = ratingReviewId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getWrittenReview() {
        return writtenReview;
    }

    public void setWrittenReview(String writtenReview) {
        this.writtenReview = writtenReview;
    }
}
