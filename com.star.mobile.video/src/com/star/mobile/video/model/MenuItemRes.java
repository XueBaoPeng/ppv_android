package com.star.mobile.video.model;

public class MenuItemRes {

	private String itemTitle;
	private String itemContent;
	private int focusRes;
	private int unfocusRes;
	private boolean foucs;

	public MenuItemRes(){}
	
	public MenuItemRes(String itemTitle){
		this.itemTitle = itemTitle;
	}
	
	public MenuItemRes(String itemTitle, int focusRes, int unfocusRes) {
		this.setItemTitle(itemTitle);
		this.focusRes = focusRes;
		this.setUnfocusRes(unfocusRes);
	}
	
	public int getFocusRes() {
		return focusRes;
	}

	public int getUnfocusRes() {
		return unfocusRes;
	}

	public String getItemContent() {
		return itemContent;
	}

	public void setItemContent(String itemContent) {
		this.itemContent = itemContent;
	}

	public String getItemTitle() {
		return itemTitle;
	}

	public void setItemTitle(String itemTitle) {
		this.itemTitle = itemTitle;
	}

	public void setUnfocusRes(int unfocusRes) {
		this.unfocusRes = unfocusRes;
	}

	public boolean isFoucs() {
		return foucs;
	}

	public void setFoucs(boolean foucs) {
		this.foucs = foucs;
	}
}
