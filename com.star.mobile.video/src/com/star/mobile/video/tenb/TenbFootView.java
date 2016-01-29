package com.star.mobile.video.tenb;

import com.star.mobile.video.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class TenbFootView extends LinearLayout {
	
	
	public TenbFootView(Context context) {
		this(context, null);
	}

	public TenbFootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.tenb_footview, this);
	}
	
	

}
