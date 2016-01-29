package com.star.mobile.video.model;

import android.graphics.drawable.Drawable;

public class AlertSettingItem {

	private String itemContent;
	private String[] childItems;
	private Drawable drawable;
	private int selectPos;
	private int itemId;

	public String getItemContent() {
		return itemContent;
	}

	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}

	public String[] getChildItems() {
		return childItems;
	}

	public void setChildItems(String[] childItems) {
		this.childItems = childItems;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	public int getSelectPos() {
		return selectPos;
	}

	public void setSelectPos(int selectPos) {
		this.selectPos = selectPos;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
}
