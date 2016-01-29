package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.me.feedback.UserReportActivity;

public class FeedbackAlertDialogActivity extends Activity implements OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm_dialog);
		Intent intent = getIntent();
        TextView title = (TextView) findViewById(R.id.tv_title);
        TextView startime = (TextView) findViewById(R.id.tv_content);
        ((TextView) findViewById(R.id.cancel_btn)).setText("Feedback");
        ((TextView) findViewById(R.id.ok_btn)).setText("Dismiss");
        ImageView line = (ImageView) findViewById(R.id.iv_line);
        if(intent.getBooleanExtra("isShowTitle", false)) {
        	title.setVisibility(View.GONE);
        	line.setVisibility(View.GONE);
        }
        findViewById(R.id.rl_cancel).setOnClickListener(this);
        findViewById(R.id.rl_ok).setOnClickListener(this);
        startime.setVisibility(View.VISIBLE);
		startime.setText(getString(R.string.four_layer_dialog_content));
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			finish();
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_cancel:
			Intent intent = new Intent(this,UserReportActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.rl_ok:
			finish();
		default:
			break;
		}
	}

}
