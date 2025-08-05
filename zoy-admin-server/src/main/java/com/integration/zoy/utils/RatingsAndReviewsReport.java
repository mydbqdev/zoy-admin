package com.integration.zoy.utils;

import java.sql.Timestamp;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class RatingsAndReviewsReport {

	@SerializedName("writtenReview")
	public String writtenReview;

	@SerializedName("overallRating")
	public String overallRating;

	@SerializedName("customerName")
	public String customerName;

	@SerializedName("customerImage")
	public String customerImage;

	@SerializedName("pgName")
	public String propertyName;

	@SerializedName("reviewDate")
	public Timestamp reviewDate;

	@SerializedName("customerMobileNo")
	public String customerMobileNo;

	@SerializedName("cleanliness")
	public String cleanliness;

	@SerializedName("amenities")
	public String amenities;

	@SerializedName("maintenance")
	public String maintenance;

	@SerializedName("valueForMoney")
	public String valueForMoney;

	@SerializedName("accommodation")
	public String accommodation;

	@SerializedName("reviews")
	public List<ReviewReplies> threads;
	
	@SerializedName("ownerContactNum")
	public String ownerContactNum;
	
	@SerializedName("ownerName")
	public String ownerName;
	
	public String getOwnerContactNum() {
	    return ownerContactNum;
	}
 
	public void setOwnerContactNum(String ownerContactNum) {
	    this.ownerContactNum = ownerContactNum;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName ;
	}
	public String getCleanliness() {
		return cleanliness;
	}

	public void setCleanliness(String cleanliness) {
		this.cleanliness = cleanliness;
	}

	public String getAmenities() {
		return amenities;
	}

	public void setAmenities(String amenities) {
		this.amenities = amenities;
	}

	public String getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(String maintenance) {
		this.maintenance = maintenance;
	}

	public String getValueForMoney() {
		return valueForMoney;
	}

	public void setValueForMoney(String valueForMoney) {
		this.valueForMoney = valueForMoney;
	}

	public String getAccommodation() {
		return accommodation;
	}

	public void setAccommodation(String accommodation) {
		this.accommodation = accommodation;
	}

	public String getCustomerMobileNo() {
		return customerMobileNo;
	}

	public void setCustomerMobileNo(String customerMobileNo) {
		this.customerMobileNo = customerMobileNo;
	}

	public String getWrittenReview() {
		return writtenReview;
	}

	public void setWrittenReview(String writtenReview) {
		this.writtenReview = writtenReview;
	}

	public String getOverallRating() {
		return overallRating;
	}

	public void setOverallRating(String overallRating) {
		this.overallRating = overallRating;
	}

	public Timestamp getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Timestamp reviewDate) {
		this.reviewDate = reviewDate;
	}

	public List<ReviewReplies> getThreads() {
		return threads;
	}

	public void setThreads(List<ReviewReplies> threads) {
		this.threads = threads;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerImage() {
		return customerImage;
	}

	public void setCustomerImage(String customerImage) {
		this.customerImage = customerImage;
	}

	public static class ReviewReplies {

		@SerializedName("reviewRepliesId")
		String reviewRepliesId;

		@SerializedName("ratingReviewId")
		String ratingReviewId;

		@SerializedName("customerId")
		String customerId;

		@SerializedName("customerName")
		String customerName;

		@SerializedName("customerImage")
		String customerImage;

		@SerializedName("partnerId")
		String partnerId;

		@SerializedName("partnerName")
		String partnerName;

		@SerializedName("partnerImage")
		String partnerImage;

		@SerializedName("review")
		String review;

		@SerializedName("timeStamp")
		Timestamp timeStamp;

		@SerializedName("isEdited")
		Boolean edited;

		@SerializedName("isDeleted")
		Boolean deleted;

		public ReviewReplies(String reviewRepliesId, String ratingReviewId, String customerId, String customerName,
				String partnerId, String partnerName, String review, Timestamp timeStamp, Boolean edited,
				Boolean deleted, String customerImage, String partnerImage) {
			this.reviewRepliesId = reviewRepliesId;
			this.ratingReviewId = ratingReviewId;
			this.customerId = customerId;
			this.customerName = customerName;
			this.partnerId = partnerId;
			this.partnerName = partnerName;
			this.review = review;
			this.timeStamp = timeStamp;
			this.edited = edited;
			this.deleted = deleted;
			this.customerImage = customerImage;
			this.partnerImage = partnerImage;
		}

		public String getReviewRepliesId() {
			return reviewRepliesId;
		}

		public void setReviewRepliesId(String reviewRepliesId) {
			this.reviewRepliesId = reviewRepliesId;
		}

		public String getRatingReviewId() {
			return ratingReviewId;
		}

		public void setRatingReviewId(String ratingReviewId) {
			this.ratingReviewId = ratingReviewId;
		}

		public String getCustomerId() {
			return customerId;
		}

		public void setCustomerId(String customerId) {
			this.customerId = customerId;
		}

		public String getCustomerName() {
			return customerName;
		}

		public void setCustomerName(String customerName) {
			this.customerName = customerName;
		}

		public String getPartnerId() {
			return partnerId;
		}

		public void setPartnerId(String partnerId) {
			this.partnerId = partnerId;
		}

		public String getPartnerName() {
			return partnerName;
		}

		public void setPartnerName(String partnerName) {
			this.partnerName = partnerName;
		}

		public Timestamp getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(Timestamp timeStamp) {
			this.timeStamp = timeStamp;
		}

		public String getReview() {
			return review;
		}

		public void setReview(String review) {
			this.review = review;
		}

		public Boolean getEdited() {
			return edited;
		}

		public void setEdited(Boolean edited) {
			this.edited = edited;
		}

		public Boolean getDeleted() {
			return deleted;
		}

		public void setDeleted(Boolean deleted) {
			this.deleted = deleted;
		}

		public String getCustomerImage() {
			return customerImage;
		}

		public void setCustomerImage(String customerImage) {
			this.customerImage = customerImage;
		}

		public String getPartnerImage() {
			return partnerImage;
		}

		public void setPartnerImage(String partnerImage) {
			this.partnerImage = partnerImage;
		}

	}
}
