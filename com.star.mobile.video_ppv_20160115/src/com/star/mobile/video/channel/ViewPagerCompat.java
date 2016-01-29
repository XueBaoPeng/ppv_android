/*
 * @author http://blog.csdn.net/singwhatiwanna
 */

package com.star.mobile.video.channel;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ViewPagerCompat extends ViewPager {
	
	/**
	 * 控制是否滑动  默认是不可以滑动
	 */
	private boolean isScrollable = false;  
    

	public ViewPagerCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

  
    public void setScrollable(boolean isScrollable) {
		this.isScrollable = isScrollable;
	}
    /**
     * 在mViewTouchMode为true的时候，ViewPager不拦截点击事件，点击事件将由子View处理
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	getParent().requestDisallowInterceptTouchEvent(true);
	    if (isScrollable == false) {
	    	return false;
	    } else {
	    	return super.onTouchEvent(ev);
	    }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
	    if (isScrollable == false) {
	    	return false;
	    } else {
	    	return super.onInterceptTouchEvent(ev);
	    }
    }

   
}
