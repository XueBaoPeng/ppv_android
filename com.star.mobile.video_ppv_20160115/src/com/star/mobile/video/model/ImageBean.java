package com.star.mobile.video.model;

import java.io.Serializable;

public class ImageBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2182598887160458922L;
	private String path;
	private boolean selected;

	public ImageBean(String path, boolean selected) {
		super();
		this.path = path;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
