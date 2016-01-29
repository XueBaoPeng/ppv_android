package com.star.mobile.video.view;

import com.star.mobile.video.R;
import com.star.mobile.video.service.FeedbackService;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AppDetailFootView extends LinearLayout{
	
	private TextView btnFastUser;
	
	public AppDetailFootView(Context context) {
		this(context, null);
	}

	public AppDetailFootView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_appinfo_foot, this);
		btnFastUser = (TextView) findViewById(R.id.btn_fast_user);
		btnFastUser.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		FeedbackService fs = FeedbackService.getInstance(context);
		if(fs.isDoFourLayer()) {
			btnFastUser.setVisibility(View.VISIBLE);
		} else {
			btnFastUser.setVisibility(View.GONE);
		}
	}
	
	public void setFastUserButtonOnClick(OnClickListener l) {
		btnFastUser.setOnClickListener(l);
	}

}
