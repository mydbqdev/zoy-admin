package com.integration.zoy.utils;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashMap;
import java.util.Map;
import org.springframework.validation.annotation.Validated;


public class Whatsapp   {
	@JsonProperty("tonumber")
	private String tonumber = null;

	@JsonProperty("text")
	private String text = null;

	@JsonProperty("type")
	private String type = null;

	@JsonProperty("originalUrl")
	private String originalUrl = null;

	@JsonProperty("previewUrl")
	private String previewUrl = null;

	@JsonProperty("caption")
	private String caption = null;

	@JsonProperty("url")
	private String url = null;

	@JsonProperty("filename")
	private String filename = null;

	@JsonProperty("appname")
	private String appname = null;

	@JsonProperty("templateid")
	private String templateid = null;

	@JsonProperty("params")
	private Map<Integer, String> params = null;

	@JsonProperty("attachtype")
	private String attachtype = null;

	@JsonProperty("attachlink")
	private String attachlink = null;

	@JsonProperty("attachfilename")
	private String attachfilename = null;

	@JsonProperty("latitude")
	private String latitude = null;

	@JsonProperty("longitude")
	private String longitude = null;

	@JsonProperty("locname")
	private String locname = null;

	@JsonProperty("locaddress")
	private String locaddress = null;

	public Whatsapp tonumber(String tonumber) {
		this.tonumber = tonumber;
		return this;
	}


	public String getTonumber() {
		return tonumber;
	}

	public void setTonumber(String tonumber) {
		this.tonumber = tonumber;
	}

	public Whatsapp text(String text) {
		this.text = text;
		return this;
	}


	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Whatsapp type(String type) {
		this.type = type;
		return this;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Whatsapp originalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
		return this;
	}


	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public Whatsapp previewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
		return this;
	}


	public String getPreviewUrl() {
		return previewUrl;
	}

	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	public Whatsapp caption(String caption) {
		this.caption = caption;
		return this;
	}


	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public Whatsapp url(String url) {
		this.url = url;
		return this;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Whatsapp filename(String filename) {
		this.filename = filename;
		return this;
	}


	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Whatsapp appname(String appname) {
		this.appname = appname;
		return this;
	}


	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public Whatsapp templateid(String templateid) {
		this.templateid = templateid;
		return this;
	}


	public String getTemplateid() {
		return templateid;
	}

	public void setTemplateid(String templateid) {
		this.templateid = templateid;
	}

	public Whatsapp params(Map<Integer, String> params) {
		this.params = params;
		return this;
	}

	public Whatsapp putParamsItem(Integer key, String paramsItem) {
		if (this.params == null) {
			this.params = new HashMap<Integer, String>();
		}
		this.params.put(key, paramsItem);
		return this;
	}


	public Map<Integer, String> getParams() {
		return params;
	}

	public void setParams(Map<Integer, String> params) {
		this.params = params;
	}

	public Whatsapp attachtype(String attachtype) {
		this.attachtype = attachtype;
		return this;
	}


	public String getAttachtype() {
		return attachtype;
	}

	public void setAttachtype(String attachtype) {
		this.attachtype = attachtype;
	}

	public Whatsapp attachlink(String attachlink) {
		this.attachlink = attachlink;
		return this;
	}


	public String getAttachlink() {
		return attachlink;
	}

	public void setAttachlink(String attachlink) {
		this.attachlink = attachlink;
	}

	public Whatsapp attachfilename(String attachfilename) {
		this.attachfilename = attachfilename;
		return this;
	}


	public String getAttachfilename() {
		return attachfilename;
	}

	public void setAttachfilename(String attachfilename) {
		this.attachfilename = attachfilename;
	}

	public Whatsapp latitude(String latitude) {
		this.latitude = latitude;
		return this;
	}


	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public Whatsapp longitude(String longitude) {
		this.longitude = longitude;
		return this;
	}


	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public Whatsapp locname(String locname) {
		this.locname = locname;
		return this;
	}


	public String getLocname() {
		return locname;
	}

	public void setLocname(String locname) {
		this.locname = locname;
	}

	public Whatsapp locaddress(String locaddress) {
		this.locaddress = locaddress;
		return this;
	}


	public String getLocaddress() {
		return locaddress;
	}

	public void setLocaddress(String locaddress) {
		this.locaddress = locaddress;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Whatsapp whatsapp = (Whatsapp) o;
		return Objects.equals(this.tonumber, whatsapp.tonumber) &&
				Objects.equals(this.text, whatsapp.text) &&
				Objects.equals(this.type, whatsapp.type) &&
				Objects.equals(this.originalUrl, whatsapp.originalUrl) &&
				Objects.equals(this.previewUrl, whatsapp.previewUrl) &&
				Objects.equals(this.caption, whatsapp.caption) &&
				Objects.equals(this.url, whatsapp.url) &&
				Objects.equals(this.filename, whatsapp.filename) &&
				Objects.equals(this.appname, whatsapp.appname) &&
				Objects.equals(this.templateid, whatsapp.templateid) &&
				Objects.equals(this.params, whatsapp.params) &&
				Objects.equals(this.attachtype, whatsapp.attachtype) &&
				Objects.equals(this.attachlink, whatsapp.attachlink) &&
				Objects.equals(this.attachfilename, whatsapp.attachfilename) &&
				Objects.equals(this.latitude, whatsapp.latitude) &&
				Objects.equals(this.longitude, whatsapp.longitude) &&
				Objects.equals(this.locname, whatsapp.locname) &&
				Objects.equals(this.locaddress, whatsapp.locaddress);
	}

	@Override
	public int hashCode() {
		return Objects.hash(tonumber, text, type, originalUrl, previewUrl, caption, url, filename, appname, templateid, params, attachtype, attachlink, attachfilename, latitude, longitude, locname, locaddress);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Whatsapp {\n");

		sb.append("    tonumber: ").append(toIndentedString(tonumber)).append("\n");
		sb.append("    text: ").append(toIndentedString(text)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    originalUrl: ").append(toIndentedString(originalUrl)).append("\n");
		sb.append("    previewUrl: ").append(toIndentedString(previewUrl)).append("\n");
		sb.append("    caption: ").append(toIndentedString(caption)).append("\n");
		sb.append("    url: ").append(toIndentedString(url)).append("\n");
		sb.append("    filename: ").append(toIndentedString(filename)).append("\n");
		sb.append("    appname: ").append(toIndentedString(appname)).append("\n");
		sb.append("    templateid: ").append(toIndentedString(templateid)).append("\n");
		sb.append("    params: ").append(toIndentedString(params)).append("\n");
		sb.append("    attachtype: ").append(toIndentedString(attachtype)).append("\n");
		sb.append("    attachlink: ").append(toIndentedString(attachlink)).append("\n");
		sb.append("    attachfilename: ").append(toIndentedString(attachfilename)).append("\n");
		sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
		sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
		sb.append("    locname: ").append(toIndentedString(locname)).append("\n");
		sb.append("    locaddress: ").append(toIndentedString(locaddress)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
