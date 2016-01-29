package com.star.mobile.video.view;

import java.lang.reflect.Field;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

public class InterceptTouchViewPager extends ViewPager {

	private FixedSpeedScroller mScroller;

	public InterceptTouchViewPager(Context context) {
		super(context);
	}

	public InterceptTouchViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(context,	new AccelerateInterpolator());
			mField.set(this, mScroller);
			mScroller.setmDuration(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		getParent().requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(arg0);
	}

}
