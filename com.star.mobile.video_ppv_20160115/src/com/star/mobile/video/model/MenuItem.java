package com.star.mobile.video.model;

import java.util.List;

public class MenuItem<T extends MenuItemRes> {

	private List<T> itemRes;
	private T res;

	
	public MenuItem(T res) {
		this(res,null);
	}
	
	public MenuItem(T res, List<T> itemRes) {
		this.itemRes = itemRes;
		this.setRes(res);
	}

	public List<T> getItemRes() {
		return itemRes;
	}

	public T getRes() {
		return res;
	}

	public void setRes(T res) {
		this.res = res;
	}
}
