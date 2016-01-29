package com.star.mobile.video.model;

public abstract class BaseItemData {
	
	protected String itemName;
	protected Class<?> target;
	protected int icon;
	
	public String getItemName() {
		return itemName;
	}
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
	public Class<?> getTarget() {
		return target;
	}
	public void setTarget(Class<?> target) {
		this.target = target;
	}
	public int getIcon() {
		return icon;
	}
	public void setIcon(int icon) {
		this.icon = icon;
	}
}
