package com.star.mobile.video.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.adapter.ViewPagerAdapter;
import com.star.mobile.video.base.BaseFragment;
import com.star.mobile.video.service.EggAppearService;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.view.EpgOnAirListView;

public class OnAirFragment extends BaseFragment implements OnPageChangeListener, OnClickListener {
	private ViewPager vp_epg_group;
	private Activity homeActivity;
	private ArrayList<View> views = new ArrayList<View>();
	private View mView;
	private TextView tv_now;
	private TextView tv_next;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(mView != null){
			ViewGroup parent = (ViewGroup) mView.getParent();  
			if(parent != null) {  
				parent.removeView(mView);  
			}   
			return mView;
		}
		homeActivity = getActivity();
		mView = inflater.inflate(R.layout.fragment_onair, null);
		initView();
		EggAppearService.appearEgg(getActivity(), EggAppearService.OnAir);
		return mView;
	}
	
	private void initView() {
		tv_now = (TextView) mView.findViewById(R.id.tv_onair_now);
		tv_next = (TextView) mView.findViewById(R.id.tv_onair_next);
		vp_epg_group = (ViewPager) mView.findViewById(R.id.vp_epg_group);

		views.add(new EpgOnAirListView(homeActivity, Constant.PAGE_ONAIR_NOW));
		views.add(new EpgOnAirListView(homeActivity, Constant.PAGE_ONAIR_NEXT));
		vp_epg_group.setAdapter(new ViewPagerAdapter(homeActivity, views));
		vp_epg_group.setOnPageChangeListener(this);
		
		tv_now.setOnClickListener(this);
		tv_next.setOnClickListener(this);
		tv_now.requestFocus();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		switch (arg0) {
		case 0:
			onClick(tv_now);
			break;
		case 1:
			onClick(tv_next);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_onair_now:
			tv_now.setTextColor(homeActivity.getResources().getColor(R.color.onair_btn_focus));
			tv_next.setTextColor(homeActivity.getResources().getColor(R.color.onair_btn_unfocus));
			tv_now.setBackgroundResource(R.drawable.now_focus);
			tv_next.setBackgroundDrawable(null);
			vp_epg_group.setCurrentItem(0);
			break;
		case R.id.tv_onair_next:
			tv_now.setTextColor(homeActivity.getResources().getColor(R.color.onair_btn_unfocus));
			tv_next.setTextColor(homeActivity.getResources().getColor(R.color.onair_btn_focus));
			tv_now.setBackgroundDrawable(null);
			tv_next.setBackgroundResource(R.drawable.next_focus);
			vp_epg_group.setCurrentItem(1);
			break;
		}
	}
}
