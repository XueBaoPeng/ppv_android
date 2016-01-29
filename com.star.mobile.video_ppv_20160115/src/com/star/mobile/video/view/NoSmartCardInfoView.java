package com.star.mobile.video.view;

import com.star.mobile.video.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoSmartCardInfoView extends LinearLayout{
	
	private TextView tvMsg;
	
	public NoSmartCardInfoView(Context context) {
		this(context, null);
	}

	public NoSmartCardInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_smart_card_no_info, this);
		tvMsg = (TextView) findViewById(R.id.tv_msg);
	}
	
	
	public void setMsg(String text) {
		tvMsg.setText(text);
	}

}
