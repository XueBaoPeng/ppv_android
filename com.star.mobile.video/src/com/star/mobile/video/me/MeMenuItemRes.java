package com.star.mobile.video.me;

import com.star.mobile.video.model.MenuItemRes;

public class MeMenuItemRes extends MenuItemRes {

	private Class<?> clazz;

	public MeMenuItemRes() {
	}

	public MeMenuItemRes(String itemTitle) {
		this(itemTitle, null);
	}

	public MeMenuItemRes(String itemTitle, Class<?> clazz) {
		this.clazz = clazz;
		setItemTitle(itemTitle);
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}
}
