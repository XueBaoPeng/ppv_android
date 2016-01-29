package com.star.mobile.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.star.mobile.video.R;

public class LoadingProgressBar extends LinearLayout {

	public LoadingProgressBar(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.loading_progress, this);
	}

	public LoadingProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.loading_progress, this);
	}
}
