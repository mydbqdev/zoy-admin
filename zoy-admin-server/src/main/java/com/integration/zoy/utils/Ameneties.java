package com.integration.zoy.utils;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Ameneties {
	@SerializedName("amenties")
	List<Amenetie> amenties;


	public void setAmenties(List<Amenetie> amenties) {
		this.amenties = amenties;
	}
	public List<Amenetie> getAmenties() {
		return amenties;
	}
	@Override
	public String toString() {
		return "Ameneties [amenties=" + amenties + "]";
	}

	public static class Amenetie{
		@SerializedName("ameneties_id")
		String amenetiesId;

		@SerializedName("ameneties_name")
		String amenetiesName;

		public Amenetie(String amenetiesId, String amenetiesName) {
			this.amenetiesId=amenetiesId;
			this.amenetiesName=amenetiesName;
		}

		public Amenetie() {
		}

		public String getAmenetiesId() {
			return amenetiesId;
		}

		public void setAmenetiesId(String amenetiesId) {
			this.amenetiesId = amenetiesId;
		}

		public String getAmenetiesName() {
			return amenetiesName;
		}

		public void setAmenetiesName(String amenetiesName) {
			this.amenetiesName = amenetiesName;
		}


	}

}
