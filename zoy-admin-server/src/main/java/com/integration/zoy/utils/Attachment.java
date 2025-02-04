package com.integration.zoy.utils;

public class Attachment {
	private String fileName;
	private byte[] content;
	private String mimeType;

	public Attachment(String fileName, byte[] content, String mimeType) {
		this.fileName = fileName;
		this.content = content;
		this.mimeType = mimeType;
	}

	// Getters
	public String getFileName() {
		return fileName;
	}

	public byte[] getContent() {
		return content;
	}

	public String getMimeType() {
		return mimeType;
	}
}
