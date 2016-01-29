package com.star.mobile.video.view;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.star.mobile.video.R;
import com.star.mobile.video.account.ChooseAreaActivity;
import com.star.mobile.video.activity.WelcomeActivity;
import com.star.mobile.video.home.HomeActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;

public class PosterContainer extends RelativeLayout {

	private ViewPager vp_appPoster;
//	private LinearLayout ll_posterDot;
//	private ImageView[] dots;
	private View tv_appStart;
	private PosterChangeListener pcListener;
	private int[] resIds = new int[] {R.drawable.one,R.drawable.two,R.drawable.three,R.drawable.four};

	public PosterContainer(Context context) {
		this(context, null);
	}

	public PosterContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_poster_container,
				this);
		initView();
		initPosterData();
	}

	private void initView() {
		vp_appPoster = (ViewPager) findViewById(R.id.vp_appposter_group);
		pcListener = new PosterChangeListener();
		vp_appPoster.setOnPageChangeListener(pcListener);
//		ll_posterDot = (LinearLayout) findViewById(R.id.ll_posterdot_group);
		tv_appStart = findViewById(R.id.tv_app_start);
//		tv_appStart.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
////				setVisibility(View.GONE);
//				SharedPreferencesUtil.setAppGuideDone(getContext());
//				WelcomeActivity wa = (WelcomeActivity) getContext();
//				CommonUtil.startActivity(wa, ChooseAreaActivity.class);
//				CommonUtil.finishActivity(wa);
//			}
//		});
		setGoHomeListener();
	}
	
	public void setGoHomeListener(){
		tv_appStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(SharedPreferencesUtil.getAreaCode((Activity)getContext())==null){
					CommonUtil.startActivity((Activity)getContext(), ChooseAreaActivity.class);
				}else{
					CommonUtil.startActivity((Activity)getContext(), HomeActivity.class);
				}
				SharedPreferencesUtil.setAppGuideDone(getContext());
				CommonUtil.finishActivity((WelcomeActivity) getContext());
			}
		});
	}

	private void initPosterData() {
		ArrayList<View> posters = new ArrayList<View>();
//		dots = new ImageView[resIds.length];
		ImageView dot;
		ImageView poster;
//		ll_posterDot.removeAllViews();
		for (int i = 0; i < resIds.length; i++) {
			poster = new ImageView(getContext());
			poster.setScaleType(ScaleType.FIT_XY);
			poster.setImageResource(resIds[i]);
			posters.add(poster);

			dot = new ImageView(getContext());
			dot.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,	LayoutParams.WRAP_CONTENT));
//			dots[i] = dot;
//			if (i == 0) {
//				dots[i].setBackgroundResource(R.drawable.guide_dot_focus);
//			} else {
//				dots[i].setBackgroundResource(R.drawable.guide_dot_line);
//			}
//			ll_posterDot.addView(dots[i]);
		}
		ViewPagerAdapter adapter = new ViewPagerAdapter(posters);
		vp_appPoster.setAdapter(adapter);
		pcListener.onPageSelected(0);
	}
	
	class PosterChangeListener implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			if(arg0 == resIds.length-1){
				tv_appStart.setVisibility(View.VISIBLE);
			}else{
				tv_appStart.setVisibility(View.GONE);
			}
//			for (int i = 0; i < resIds.length; i++) {
//				if(i == arg0) {
//					dots[i].setBackgroundResource(R.drawable.guide_dot_focus);
//				}else{
//					dots[i].setBackgroundResource(R.drawable.guide_dot_line);
//				}
//			}
		}
		
	}

	class ViewPagerAdapter extends PagerAdapter {

		private ArrayList<View> views;

		public ViewPagerAdapter(ArrayList<View> views) {
			this.views = views;
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(views.get(position), 0);
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View view, Object arg1) {
			return (view == arg1);
		}
		
		@Override
		public void destroyItem(View view, int position, Object arg2) {
			((ViewPager) view).removeView(views.get(position));
		}
	}
	
	
}
