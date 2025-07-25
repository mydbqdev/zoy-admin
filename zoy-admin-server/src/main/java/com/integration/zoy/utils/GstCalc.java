package com.integration.zoy.utils;

import com.google.gson.annotations.SerializedName;

public class GstCalc {
	@SerializedName("gst")
	private Double gst = 0.0;
	
	@SerializedName("sgst")
	private Double sgst = 0.0;
	
	@SerializedName("igst")
	private Double igst = 0.0;
	
	@SerializedName("cgst")
	private Double cgst = 0.0;
	
	@SerializedName("gstPecenatge")
	private Double gstPecenatge = 0.0;
	
	@SerializedName("sgstPecenatge")
	private Double sgstPecenatge = 0.0;
	
	@SerializedName("igstPecenatge")
	private Double igstPecenatge = 0.0;
	
	@SerializedName("cgstPecenatge")
	private Double cgstPecenatge = 0.0;

	public Double getSgst() {
		return sgst;
	}

	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}

	public Double getIgst() {
		return igst;
	}

	public void setIgst(Double igst) {
		this.igst = igst;
	}

	public Double getCgst() {
		return cgst;
	}

	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}

	public Double getSgstPecenatge() {
		return sgstPecenatge;
	}

	public void setSgstPecenatge(Double sgstPecenatge) {
		this.sgstPecenatge = sgstPecenatge;
	}

	public Double getIgstPecenatge() {
		return igstPecenatge;
	}

	public void setIgstPecenatge(Double igstPecenatge) {
		this.igstPecenatge = igstPecenatge;
	}

	public Double getCgstPecenatge() {
		return cgstPecenatge;
	}

	public void setCgstPecenatge(Double cgstPecenatge) {
		this.cgstPecenatge = cgstPecenatge;
	}

	public Double getGst() {
		return gst;
	}

	public void setGst(Double gst) {
		this.gst = gst;
	}

	public Double getGstPecenatge() {
		return gstPecenatge;
	}

	public void setGstPecenatge(Double gstPecenatge) {
		this.gstPecenatge = gstPecenatge;
	}
	
	
	
}
