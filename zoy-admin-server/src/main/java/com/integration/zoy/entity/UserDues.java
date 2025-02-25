package com.integration.zoy.entity;

import javax.persistence.*;


import java.math.BigDecimal;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_dues", schema = "pgusers")
public class UserDues {

    @Id
    @Column(name = "user_money_due_id")
    private String userMoneyDueId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_money_due_images")
    private String userMoneyDueImages;

    @Column(name = "user_money_due_bill_start_date")
    private Timestamp userMoneyDueBillStartDate;

    @Column(name = "user_money_due_timestamp")
    private Timestamp userMoneyDueTimestamp;

    @Column(name = "user_money_due_amount")
    private BigDecimal userMoneyDueAmount;

    @Column(name = "user_money_due_billing_type")
    private String userMoneyDueBillingType;

    @Column(name = "user_money_due_description")
    private String userMoneyDueDescription;

    @Column(name = "user_money_due_type")
    private String userMoneyDueType;

    @Column(name = "user_money_due_bill_end_date")
    private Timestamp userMoneyDueBillEndDate;
    
    @Column(name = "user_booking_id")
    private String userBookingId;
    
    @Column(name = "user_due_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal userDueAmount = BigDecimal.ZERO;

    @Column(name = "user_sgst_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal userSgstAmount = BigDecimal.ZERO;

    @Column(name = "user_igst_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal userIgstAmount = BigDecimal.ZERO;

    @Column(name = "user_cgst_amount", nullable = false, precision = 20, scale = 2)
    private BigDecimal userCgstAmount = BigDecimal.ZERO;

    @Column(name = "user_sgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userSgstPercentage = BigDecimal.ZERO;

    @Column(name = "user_cgst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userCgstPercentage = BigDecimal.ZERO;

    @Column(name = "user_igst_percentage", nullable = false, precision = 10, scale = 2)
    private BigDecimal userIgstPercentage = BigDecimal.ZERO;

    // Getters and Setters
    public String getUserMoneyDueId() {
        return userMoneyDueId;
    }

    public void setUserMoneyDueId(String userMoneyDueId) {
        this.userMoneyDueId = userMoneyDueId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserMoneyDueImages() {
        return userMoneyDueImages;
    }

    public void setUserMoneyDueImages(String userMoneyDueImages) {
        this.userMoneyDueImages = userMoneyDueImages;
    }

    public Timestamp getUserMoneyDueBillStartDate() {
        return userMoneyDueBillStartDate;
    }

    public void setUserMoneyDueBillStartDate(Timestamp userMoneyDueBillStartDate) {
        this.userMoneyDueBillStartDate = userMoneyDueBillStartDate;
    }

    public Timestamp getUserMoneyDueTimestamp() {
        return userMoneyDueTimestamp;
    }

    public void setUserMoneyDueTimestamp(Timestamp userMoneyDueTimestamp) {
        this.userMoneyDueTimestamp = userMoneyDueTimestamp;
    }

    public BigDecimal getUserMoneyDueAmount() {
        return userMoneyDueAmount;
    }

    public void setUserMoneyDueAmount(BigDecimal userMoneyDueAmount) {
        this.userMoneyDueAmount = userMoneyDueAmount;
    }

    public String getUserMoneyDueBillingType() {
        return userMoneyDueBillingType;
    }

    public void setUserMoneyDueBillingType(String userMoneyDueBillingType) {
        this.userMoneyDueBillingType = userMoneyDueBillingType;
    }

    public String getUserMoneyDueDescription() {
        return userMoneyDueDescription;
    }

    public void setUserMoneyDueDescription(String userMoneyDueDescription) {
        this.userMoneyDueDescription = userMoneyDueDescription;
    }

    public String getUserMoneyDueType() {
        return userMoneyDueType;
    }

    public void setUserMoneyDueType(String userMoneyDueType) {
        this.userMoneyDueType = userMoneyDueType;
    }

    public Timestamp getUserMoneyDueBillEndDate() {
        return userMoneyDueBillEndDate;
    }

    public void setUserMoneyDueBillEndDate(Timestamp userMoneyDueBillEndDate) {
        this.userMoneyDueBillEndDate = userMoneyDueBillEndDate;
    }

	public String getUserBookingId() {
		return userBookingId;
	}

	public void setUserBookingId(String userBookingId) {
		this.userBookingId = userBookingId;
	}
	
	public BigDecimal getUserDueAmount() {
		return userDueAmount;
	}

	public void setUserDueAmount(BigDecimal userDueAmount) {
		this.userDueAmount = userDueAmount;
	}

	public BigDecimal getUserSgstAmount() {
		return userSgstAmount;
	}

	public void setUserSgstAmount(BigDecimal userSgstAmount) {
		this.userSgstAmount = userSgstAmount;
	}

	public BigDecimal getUserIgstAmount() {
		return userIgstAmount;
	}

	public void setUserIgstAmount(BigDecimal userIgstAmount) {
		this.userIgstAmount = userIgstAmount;
	}

	public BigDecimal getUserCgstAmount() {
		return userCgstAmount;
	}

	public void setUserCgstAmount(BigDecimal userCgstAmount) {
		this.userCgstAmount = userCgstAmount;
	}

	public BigDecimal getUserSgstPercentage() {
		return userSgstPercentage;
	}

	public void setUserSgstPercentage(BigDecimal userSgstPercentage) {
		this.userSgstPercentage = userSgstPercentage;
	}

	public BigDecimal getUserCgstPercentage() {
		return userCgstPercentage;
	}

	public void setUserCgstPercentage(BigDecimal userCgstPercentage) {
		this.userCgstPercentage = userCgstPercentage;
	}

	public BigDecimal getUserIgstPercentage() {
		return userIgstPercentage;
	}

	public void setUserIgstPercentage(BigDecimal userIgstPercentage) {
		this.userIgstPercentage = userIgstPercentage;
	}

	@PrePersist
    private void generateDueId() {
        if (this.userMoneyDueId == null || this.userMoneyDueId.isEmpty()) {
            String prefix = "DUE_ZOY";
            String uniquePart = generateUniquePartSafe();
            String formattedDate = String.format("%1$tY%1$tm%1$td", new Timestamp(System.currentTimeMillis())); 
            this.userMoneyDueId = prefix + formattedDate + uniquePart.toUpperCase();
        }
    }
	private String generateUniquePartSafe() {
        try {
            String nanoTimestamp = String.valueOf(Instant.now().toEpochMilli()) + System.nanoTime();
            String uuidPart = UUID.randomUUID().toString();
            String rawId = nanoTimestamp + uuidPart;
            return hashWithSHA256Safe(rawId).substring(0, 8);
        } catch (Exception e) {
            return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
    }

    private String hashWithSHA256Safe(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return input.substring(0, Math.min(8, input.length()));
        }
    }
    
}
