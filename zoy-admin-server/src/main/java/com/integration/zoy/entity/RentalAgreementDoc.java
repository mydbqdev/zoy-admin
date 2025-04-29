package com.integration.zoy.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "zoy_pg_rental_agreement_doc", schema = "pgcommon")
public class RentalAgreementDoc {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "rental_Agreement_doc_id", nullable = false, unique = true, length = 36)
	private String rentalAgreementDocId;

	@Column(name = "rental_Agreement_doc")
	private String rentalAgreementDoc;

	@Column(name = "created_at", nullable = false)
	@CreationTimestamp
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Timestamp uploadedAt;
	
	// Getters and Setters

	public String getRentalAgreementDocId() {
		return rentalAgreementDocId;
	}

	public void setRentalAgreementDocId(String rentalAgreementDocId) {
		this.rentalAgreementDocId = rentalAgreementDocId;
	}

	public String getRentalAgreementDoc() {
		return rentalAgreementDoc;
	}

	public void setRentalAgreementDoc(String rentalAgreementDoc) {
		this.rentalAgreementDoc = rentalAgreementDoc;
	}

	public Timestamp getUploadedAt() {
		return uploadedAt;
	}

	public void setUploadedAt(Timestamp uploadedAt) {
		this.uploadedAt = uploadedAt;
	}

}
