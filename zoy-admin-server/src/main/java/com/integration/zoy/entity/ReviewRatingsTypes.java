package com.integration.zoy.entity;

import javax.persistence.*;

@Entity
@Table(name = "review_ratings_types", schema = "pgcommon")
public class ReviewRatingsTypes {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;


	@Column(name = "rating_id")
	private String ratingId;

	@Column(name = "review_type_id")
	private String reviewTypeId;

	@Column(name = "rating")
	private Long rating;

	// Getters and Setters
	
	public String getRatingId() {
		return ratingId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRatingId(String ratingId) {
		this.ratingId = ratingId;
	}

	public String getReviewTypeId() {
		return reviewTypeId;
	}

	public void setReviewTypeId(String reviewTypeId) {
		this.reviewTypeId = reviewTypeId;
	}

	public Long getRating() {
		return rating;
	}

	public void setRating(Long rating) {
		this.rating = rating;
	}
}
