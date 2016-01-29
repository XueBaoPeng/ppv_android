package com.star.mobile.video.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.ui.ImageView;
/**
 * 头像放大
 * @author Administrator
 *
 */
public class HeadMagnifyActivity extends BaseActivity implements OnClickListener {
	
	private ImageView ivHead;
	private TextView tvNickname;
	private String nickname;
	private String headUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_head_magnify);
		findViewById(R.id.iv_actionbar_back).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_actionbar_title)).setText(getString(R.string.fragment_tag_Me));
		ivHead = (ImageView) findViewById(R.id.iv_head);
		tvNickname = (TextView) findViewById(R.id.tv_nickname);
		currentIntent(getIntent());
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		currentIntent(intent);
	}
	
	private void currentIntent(Intent intent) {
		nickname = intent.getStringExtra("nickname");
		headUrl = intent.getStringExtra("headurl");
		ivHead.setUrl(headUrl);
		tvNickname.setText(nickname);
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
