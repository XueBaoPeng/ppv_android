package com.star.mobile.video.guide;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.star.mobile.video.R;
import com.star.mobile.video.util.DensityUtil;

public class GuideHomeLeft extends LinearLayout {
	private Context mContext;
	private ImageView iv;
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;
	private AnimationSet animation;
	private boolean isAnim = true;
	private GuideCustomizeCallback guideCustomizeCallback;
	private int dp2px;
	public GuideHomeLeft(Context context) {
		super(context);
		mContext = context;
		initView();
		
	}
	public GuideHomeLeft(Context context, AttributeSet attrs) {
		super(context, attrs);
		animation = new AnimationSet(context, attrs);  
		mContext = context;
		initView();
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN) {
		      //当手指按下的时候
		      x1 = event.getX();
		      y1 = event.getY();
		      return true;	
	    }
		if(event.getAction() == MotionEvent.ACTION_MOVE) {
		}
	    if(event.getAction() == MotionEvent.ACTION_UP) {
		      //当手指离开的时候
		      x2 = event.getX();
		      y2 = event.getY();
		      if(x1 - x2 > dp2px ) {
		    	  isAnim = false;
		    	  guideCustomizeCallback.onGuide(1);
		    	  return false;
		      } else if(x2 - x1 > dp2px ) {
		    	  isAnim = false;
		    	  guideCustomizeCallback.onGuide(0);
		    	  return false;
		      }
	    }
	    return false;
	}
	private void initView() {
		dp2px = DensityUtil.dip2px(mContext, 100);
		int px = DensityUtil.dip2px(mContext, 5);
		LayoutInflater.from(mContext).inflate(R.layout.guide_left, this);
		iv =(ImageView) findViewById(R.id.iv_guide_left);
		int dip2px = DensityUtil.dip2px(mContext, 60);
		LinearLayout.LayoutParams params_iv = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params_iv.setMargins(0, px, dip2px, 0);
		params_iv.gravity = Gravity.RIGHT;
		iv.setLayoutParams(params_iv);
		anim();
	}
	private void anim(){
		
//		Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_guide_left);
		
		TranslateAnimation translate_one = new TranslateAnimation(0, -dp2px, 0, 0);
		translate_one.setFillAfter(true);
		translate_one.setDuration(1000);
		TranslateAnimation translate_two = new TranslateAnimation(0, 0, 0, 0);
		translate_two.setDuration(2000);
		translate_two.setFillAfter(true);
		AlphaAnimation alpha = new AlphaAnimation((float) 0.6,1);
		alpha.setDuration(1000);
		alpha.setFillAfter(true);
		animation.addAnimation(translate_one);
		animation.addAnimation(alpha);
		animation.addAnimation(translate_two);
		iv.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				
			}
			public void onAnimationRepeat(Animation animation) {
				
			}
			public void onAnimationEnd(Animation animation) {
				if(isAnim){
					iv.startAnimation(animation);
				}
				
			}
		} );
	}
	public void setGuideCustomizeCallback(GuideCustomizeCallback guideCustomizeCallback) {
		this.guideCustomizeCallback = guideCustomizeCallback;
	}
}
