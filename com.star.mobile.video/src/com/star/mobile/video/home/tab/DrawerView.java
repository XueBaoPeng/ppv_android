package com.star.mobile.video.home.tab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class DrawerView extends LinearLayout {
	
	/** Scroller 拖动类 */
	private Scroller mScroller;
	/** 是否 打开*/
	private boolean isOpen = true;
	/** 是否在动画 */
	private boolean isMove = false;
	
	protected int animDuration = 300;

	
	public DrawerView(Context context) {
		super(context);
		init(context);
	}

	public DrawerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		//Interpolator 设置为有反弹效果的  （Bounce：反弹）
//		Interpolator interpolator = new BounceInterpolator();
		mScroller = new Scroller(context, new AccelerateDecelerateInterpolator());
	}
	
	/**
	 * 拖动动画
	 * @param startY  
	 * @param dy  垂直距离, 滚动的y距离
	 * @param duration 时间
	 */
	public void startMoveAnim(int startY, int dy, int duration) {
		setMove(true);
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();//通知UI线程的更新
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	public void computeScroll() {
		//判断是否还在滚动，还在滚动为true
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			//更新界面
			postInvalidate();
			setMove(true);
		} else {
			setMove(false);
		}
		super.computeScroll();
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isMove() {
		return isMove;
	}

	public void setMove(boolean isMove) {
		this.isMove = isMove;
	}

	public int getAnimDuration() {
		return animDuration;
	}

	public void setAnimDuration(int animDuration) {
		this.animDuration = animDuration;
	}
	
	public int getTransY() {
		return transY;
	}
	
	public void setTransY(int transY){
		this.transY = transY;
	}

	protected int offsetY = 0;
	protected int scrollY = 0;
	protected int minRawY = 0;
	protected static final int STATE_ONSCREEN = 0;
	protected static final int STATE_OFFSCREEN = 1;
	protected static final int STATE_RETURNING = 2;
	protected static final int STATE_EXPANDED = 3;
	protected int mState = STATE_ONSCREEN;
	protected int transY = 0;

}
