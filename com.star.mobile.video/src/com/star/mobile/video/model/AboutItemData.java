package com.star.mobile.video.model;

public class AboutItemData extends BaseItemData {

	private int imageResouse;
	private String lItemName;
	private String rItemName;
	private Integer rightIcon;
	
	public AboutItemData() {}
	public AboutItemData(String itemName){
		this.itemName = itemName;
	}
	
	public AboutItemData(Class<?> target){
		this.target = target;
	}
	public AboutItemData(String itemName,Class<?> target){
		this.itemName = itemName;
		this.target = target;
	}
	public AboutItemData(int imageResouse,String itemName,Class<?> target){
		this.imageResouse = imageResouse;
		this.itemName = itemName;
		this.target = target;
	}
	public String getlItemName() {
		return lItemName;
	}
	public void setlItemName(String lItemName) {
		this.lItemName = lItemName;
	}
	public String getrItemName() {
		return rItemName;
	}
	public void setrItemName(String rItemName) {
		this.rItemName = rItemName;
	}
	public Integer getRightIcon() {
		return rightIcon;
	}
	public void setRightIcon(Integer rightIcon) {
		this.rightIcon = rightIcon;
	}
	public int getImageResouse() {
		return imageResouse;
	}
	public void setImageResouse(int imageResouse) {
		this.imageResouse = imageResouse;
	}
	
}
