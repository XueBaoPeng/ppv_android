package com.star.mobile.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;

public class ComingSoonActivity extends BaseActivity implements OnClickListener{
	
	private TextView title;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_coming_soon);
		((ImageView) findViewById(R.id.iv_actionbar_back)).setOnClickListener(this);
		title = (TextView) findViewById(R.id.tv_actionbar_title);
		setTitle(getIntent());
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setTitle(intent);
	}
	
	private void setTitle(Intent intent) {
		title.setText(intent.getStringExtra("title"));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			onBackPressed();
			break;

		default:
			break;
		}
	}
}
