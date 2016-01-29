package com.star.mobile.video.view;

import com.star.mobile.video.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class DiscoberyFootView extends LinearLayout {
	
	public DiscoberyFootView(Context context) {
		this(context, null);
	}

	public DiscoberyFootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.discovery_footview, this);
	}

}
