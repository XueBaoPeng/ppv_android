package com.star.mobile.video.model;

import java.io.Serializable;

public class LinkPkg implements Serializable {

	/**
	 * 
	 */ 
	private static final long serialVersionUID = 1L;
	
	private String url;
	private String title;
	private String imgurl;
	private String description;


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		String result = null;
		result = url;
		if (title != null)
			result = result + "###title=" + title;
		if (imgurl != null)
			result = result + "###imgurl=" + imgurl;
		if (description != null)
			result = result + "###description=" + description;

		return result;
	}
}
