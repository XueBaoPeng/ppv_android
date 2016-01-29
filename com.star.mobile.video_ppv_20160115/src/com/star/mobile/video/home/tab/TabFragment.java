package com.star.mobile.video.home.tab;

import android.widget.AbsListView;

import com.star.mobile.video.base.BaseFragment;

public abstract class TabFragment extends BaseFragment {

	private DownDrawerView homeBottomTab;
	private DrawerScrollListener drawerListener;

	public DownDrawerView getHomeBottomTab() {
		return homeBottomTab;
	}

	public void setHomeBottomTab(DownDrawerView homeBottomTab) {
		this.homeBottomTab = homeBottomTab;
	}

	public DrawerScrollListener getDrawerListener() {
		return drawerListener;
	}

	public void setDrawerListener(DrawerScrollListener drawerListener) {
		this.drawerListener = drawerListener;
	}
	
	public void initScrollListener(AbsListView scrollview){
		if(getDrawerListener() == null){
			return;
		}
		getDrawerListener().setDrawerView(getHomeBottomTab(), null, null);
		scrollview.setOnScrollListener(drawerListener);
	}
	
	public void resetDrawerView(){
		if(getDrawerListener() == null){
			return;
		}
		getDrawerListener().initView();
	}
	
}
