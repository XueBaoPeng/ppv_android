package com.star.mobile.video.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.star.mobile.video.R;

public class LoadingView extends LinearLayout{

	private ImageView ivLoadin;
	
	
	public LoadingView(Context context) {
		this(context, null);
	}
	
	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_loading, this);
		ivLoadin = (ImageView) findViewById(R.id.iv_loading);
		AnimationDrawable ad = (AnimationDrawable)context.getResources().getDrawable(R.drawable.loading_animation_list);
		ivLoadin.setBackgroundDrawable(ad);
		ad.start();
	}
}
