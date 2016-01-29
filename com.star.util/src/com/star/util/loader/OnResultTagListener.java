package com.star.util.loader;

public abstract class OnResultTagListener<T> implements OnResultListener<T>{

	private String tag;

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
