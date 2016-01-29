package com.star.util.animator;

import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.star.util.MainUIHandler;

public class UIAnimator {
	private long startime;
	private long duration = 250;
	private Runnable runnable;
	private OnComplete onComplete;
	private Interpolator interpolator;
	private long delay = 0;
	
	private UIAnimator(){}
	
	public static interface OnChange<T>{
		void onChange(T value);
	}

	public static interface OnComplete{
		void onComplete();
	}
	
	
	public static UIAnimator ofInt(final int start,final int end,final OnChange<Integer> onChange){
		final UIAnimator animator = new UIAnimator();
		animator.runnable = new Runnable() {
			@Override
			public void run() {
				long now = AnimationUtils.currentAnimationTimeMillis();
				float process = (now - animator.startime) / (float)animator.duration;
				if(process < 1){
					int value = (int)(start + (end - start) * process);
					onChange.onChange(value);
					
					MainUIHandler.handler().post(this);
				}else{
					onChange.onChange(end);
					if(animator.onComplete != null){
						animator.onComplete.onComplete();
					}
				}
			}
		};
		return animator;
	}
	
	public static UIAnimator ofFloat(final float start,final float end,final OnChange<Float> onChange){
		final UIAnimator animator = new UIAnimator();
		animator.runnable = new Runnable() {
			@Override
			public void run() {
				long now = AnimationUtils.currentAnimationTimeMillis();
				float process = (now - animator.startime) / (float)animator.duration;
				if(process < 1){
					float value = start + (end - start) * process;
					if(animator.interpolator != null){
						value = animator.interpolator.getInterpolation(value);
					}
					onChange.onChange(value);
					MainUIHandler.handler().post(this);
				}else{
					onChange.onChange(end);
					if(animator.onComplete != null){
						animator.onComplete.onComplete();
					}
				}
			}
		};
		return animator;		
	}
	
	public UIAnimator setDuration(long duration){
		this.duration = duration;
		return this;
	}
	public UIAnimator setOnComplete(OnComplete onComplete){
		this.onComplete = onComplete;
		return this;
	}
	
	public UIAnimator setInterpolator(Interpolator interpolator){
		this.interpolator = interpolator;
		return this;
	}
	
	public UIAnimator setDelay(long delay){
		this.delay = delay;
		return this;
	}
	
	public void start(){
		startime = AnimationUtils.currentAnimationTimeMillis() + delay;
		MainUIHandler.handler().postDelayed(runnable, delay);
	};
	public void stop(){
		MainUIHandler.handler().removeCallbacks(runnable);
	};

	
}
