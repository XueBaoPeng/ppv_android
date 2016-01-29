package com.star.mobile.video.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.activity.BrowserActivity;
import com.star.mobile.video.util.CommonUtil;

public class ClipTextView extends LinearLayout {

	public ClipTextView(final Context context, final String name, final String url) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_clip_item, this);
		TextView clip_name = (TextView) findViewById(R.id.tv_clip_name);
		clip_name.setText(name);
		clip_name.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		clip_name.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, BrowserActivity.class);
				intent.putExtra("loadUrl", url);
				intent.putExtra("pageName", name);
				CommonUtil.startActivity((Activity)context, intent);
			}
		});
	}
}
