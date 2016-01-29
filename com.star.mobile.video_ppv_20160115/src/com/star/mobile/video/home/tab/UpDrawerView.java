package com.star.mobile.video.home.tab;

import android.content.Context;
import android.util.AttributeSet;

public class UpDrawerView extends DrawerView {
	
	public UpDrawerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public UpDrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	
	public void open(){
		startMoveAnim(this.getScrollY(), -this.getScrollY(), animDuration);
		setOpen(true);
	}
	
	public void close(){
		startMoveAnim(this.getScrollY(),(getHeight() - this.getScrollY()), animDuration);
		setOpen(false);
	}
	
	public void init(){
		mState = STATE_ONSCREEN;
		minRawY = 0;
		offsetY = 0;
		transY = 0;
		scrollY = 0;
	}
	
	public void scrollTop() {
		int rawY = scrollY;
//		Log.e("AAA", "State="+mState+"....."+"rawY="+rawY+"; minRawY="+minRawY);
		switch (mState) {
		case STATE_OFFSCREEN:
			if (rawY >= minRawY) {
				minRawY = rawY;
			} else {
				mState = STATE_RETURNING;
			}
			transY = rawY;
			break;
		case STATE_ONSCREEN:
			if (rawY > getHeight()) {
				mState = STATE_OFFSCREEN;
				minRawY = rawY;
			}
			transY = rawY;
			break;

		case STATE_RETURNING:
			transY = (rawY - minRawY) + getHeight();
			if (transY < 0) {
				transY = 0;
				minRawY = rawY + getHeight();
			}
			if (rawY == 0) {
				mState = STATE_ONSCREEN;
				transY = 0;
			}
			if (transY > getHeight()) {
				mState = STATE_OFFSCREEN;
				minRawY = rawY;
			}
			break;
		}
		if(transY<0){
			transY = 0;
		}
		scrollTo(0, transY);
		if(Math.abs(transY)>=getHeight()){
			setOpen(false);
		}else{
			setOpen(true);
		}
	}
	
	public void move(int visPos){
		if(getScrollY()>0){
			if (Math.abs(getTransY())<getHeight()/2) {
				open();
				offsetY -= Math.abs(getTransY());
			} else if(getScrollY()>getHeight()/2&&getScrollY()<getHeight()){
				if(visPos==0){
					open();
					offsetY -= Math.abs(getTransY());
				}else{
					close();
					offsetY -= -getHeight()+getScrollY();
				}
			}
		}
	}
}
