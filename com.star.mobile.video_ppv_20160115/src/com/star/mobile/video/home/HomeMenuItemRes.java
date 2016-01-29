package com.star.mobile.video.home;

import android.support.v4.app.Fragment;

import com.star.mobile.video.model.MenuItemRes;

public class HomeMenuItemRes extends MenuItemRes {

	private Fragment fragment;

	public HomeMenuItemRes(String itemTitle, int focusRes, int unfocusRes,
			Fragment fragment) {
		super(itemTitle, focusRes, unfocusRes);
		this.setFragment(fragment);
	}

	public Fragment getFragment() {
		return fragment;
	}

	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}

}
