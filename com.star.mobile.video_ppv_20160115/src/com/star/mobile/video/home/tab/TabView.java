package com.star.mobile.video.home.tab;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.star.mobile.video.channel.ChannelControlView;
import com.star.mobile.video.channel.ChatView;
import com.star.mobile.video.util.DensityUtil;

public abstract class TabView<T extends AbsListView> extends RelativeLayout {

	public TabView(Context context) {
		super(context);
	}
	
	public TabView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private DownDrawerView homeBottomTab;
	private com.star.ui.DragTopLayout dragLayout;
	private ChannelControlView controlTab;
	private DrawerScrollListener drawerListener;

	public DownDrawerView getHomeBottomTab() {
		return homeBottomTab;
	}

	public void setHomeBottomTab(DownDrawerView homeBottomTab) {
		this.homeBottomTab = homeBottomTab;
	}
	
	public ChannelControlView getControlTab() {
		return controlTab;
	}

	public void setControlTab(ChannelControlView controlTab) {
		this.controlTab = controlTab;
	}

	public com.star.ui.DragTopLayout getDragLayout() {
		return dragLayout;
	}

	public void setDragLayout(com.star.ui.DragTopLayout dragLayout) {
		this.dragLayout = dragLayout;
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
		if(this instanceof ChatView){
			getDrawerListener().setDrawerView(null, getControlTab(), getDragLayout());
		}else{
			getDrawerListener().setDrawerView(getHomeBottomTab(), getControlTab(), getDragLayout());
		}
		getDrawerListener().setscrollYIsComputed(false);
		getDrawerListener().initView();
		List<Integer> hheights = new ArrayList<Integer>();
		hheights.add(DensityUtil.dip2px(getContext(), 48));
		drawerListener.setHeaderHeights(hheights);
		scrollview.setOnScrollListener(drawerListener);
	}
	
	protected void initTabsStatus() {
		if((getHomeBottomTab()!=null)){
			if(this instanceof ChatView){
				if(getHomeBottomTab().isOpen()){
					getHomeBottomTab().close();
				}
			}else{
				if(!getHomeBottomTab().isOpen()){
					getHomeBottomTab().open();
				}
			}
		}
		if(getControlTab()!=null&&!getControlTab().isOpen()){
			getControlTab().open();
		}
		if(getDragLayout()!=null){
			getDragLayout().closeTopView(true);
		}
	}
}
