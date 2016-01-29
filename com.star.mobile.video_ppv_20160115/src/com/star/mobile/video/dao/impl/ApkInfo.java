package com.star.mobile.video.dao.impl;

import android.graphics.drawable.Drawable;

/**
 * 获得apk的信息
 * 
 * @author Lee 2015.08.03 version 1.0
 */
public class ApkInfo {
	//apk名字
	private String appName = "";
	//包名
	private String packageName = "";
	//版本名称
	private String versionName = "";
	//版本号
	private int versionCode = 0;
	//app图片
	private Drawable icon;

	private boolean userApp;
	
	public boolean isUserApp() {
		return userApp;
	}

	public void setUserApp(boolean userApp) {
		this.userApp = userApp;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

}
