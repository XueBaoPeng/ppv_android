package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.star.mobile.video.R;
import com.star.mobile.video.util.CustomAnimationDrawableNew;

/**
 * 动画 view
 * @author zhangkai
 *
 */
public class AnimView extends LinearLayout {
	
	private ImageView ivAnim;
	private Context context;
	private AnimationFinish finishListener;
	
	public AnimView(Context context) {
		this(context,null);
	}

	public AnimView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_anim, this);
		ivAnim = (ImageView) findViewById(R.id.iv_anim);
		this.context = context;
	}
	
	
	public void setAnimationFinishListener(AnimationFinish al) {
		this.finishListener = al;
	}
	
	public void setAnimResource(int resId) {
		AnimationDrawable ad = (AnimationDrawable) context.getResources().getDrawable(resId);
		setBackground(ivAnim, ad);
		ad.start();
	}
	
	/**
	 * 监听动画完毕
	 * @param resId
	 */
	public void setCustomAnimResource(int resId) {
		AnimationDrawable ad = (AnimationDrawable) context.getResources().getDrawable(resId);
		CustomAnimationDrawableNew cd = new CustomAnimationDrawableNew(ad) {
			
			@Override
			public void onAnimationFinish() {
				if(finishListener != null) {
					finishListener.finish();
				}
			}
		};
		setBackground(ivAnim, cd);
		cd.start();
	} 

	public interface AnimationFinish {
		void finish();
	}

	public void setBackground(ImageView imageView, Drawable drawable){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			imageView.setBackground(drawable);
		} else {
			imageView.setBackgroundDrawable(drawable);
		}
	}
}
