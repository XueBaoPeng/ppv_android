package com.star.mobile.video.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.analytics.HitBuilders;
import com.star.cms.model.Recommend;
import com.star.mobile.video.StarApplication;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private List<Recommend> recommends;
	private Context context;
	public ViewPagerAdapter(Context context, List<Recommend> recommends, List<View> views) {
		this.views = views;
		this.context = context;
		this.recommends = recommends;
	}
	
	public ViewPagerAdapter(Context context, List<View> views) {
		this.context = context;
		this.views = views;
	}
	
	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		if(views.size()==0){
			((ViewPager) container).removeAllViews();
			return;
		}
		try{
			View view = views.get(position);
			((ViewPager) container).removeView(view);
		}catch (Exception e) {
			Log.e("ViewPagerAdapter", "new data's size < older...", e);
		}
	}

	@Override
	public Object instantiateItem(View container, final int position) {
		View view = views.get(position);
		try{
			((ViewPager) container).addView(view);
		}catch(Exception e){
			Log.e("", "view has parent", e);
		}
		if(view instanceof com.star.ui.ImageView){
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Recommend rec = recommends.get(position);
					int type = rec.getType().getNum();
					CommonUtil.goActivityOrFargment(context, type, rec.getCode(), rec.getOwnID(), rec.getUrl(), rec.getName());
					StarApplication.mTracker.send(new HitBuilders.EventBuilder().setCategory(Constant.GA_EVENT_BUSINESS)
							.setAction(Constant.GA_EVENT_POSTER_CLICK).setLabel("POSTER_CODE:"+rec.getCode()+", ownID:"+rec.getOwnID()).setValue(1).build());
				}
			});
		}
		return view;
	}

	public View getItemAtPosition(int position) {
		return views.get(position);
	}
	
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
	
	public void updateDataAndRefreshUI(ArrayList<View> views){
		this.views = views;
		notifyDataSetChanged();
	}
	
	public void updateDataAndRefreshUI(List<Recommend> recommends, List<View> views){
		this.recommends = recommends;
		this.views = views;
		notifyDataSetChanged();
	}
}
