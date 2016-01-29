package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.star.cms.model.Program;
import com.star.mobile.video.R;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.RingtoneUtil;

public class AlertDialogActivity extends Activity implements OnClickListener{


	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_dialog);
        TextView title = (TextView) findViewById(R.id.tv_alert_title);
        TextView startime = (TextView) findViewById(R.id.tv_epg_startime);
		findViewById(R.id.task_dialog_btn).setOnClickListener(this);
		
		SharedPreferences mSharePre = getSharedPreferences("alert_info", Context.MODE_PRIVATE);
		int pos = mSharePre.getInt(Constant.RINGTONE_POS, 0);
		RingtoneUtil.getInstance(this).playRingtone(pos);
		Program program = (Program) getIntent().getSerializableExtra("program");
		int timePos = getIntent().getIntExtra("pos", 0);
		if(program != null){
			title.setText(program.getName());
		}
		String[] times = getResources().getStringArray(R.array.Alert_Remind);
		startime.setText("The program will start in "+times[timePos]);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				RingtoneUtil.getInstance(AlertDialogActivity.this).stopRingtone();
			}
		}, 4000);
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN && isOutOfBounds(this, event)) {
			RingtoneUtil.getInstance(this).stopRingtone();
			finish();
		}
		return super.onTouchEvent(event);
	}

	private boolean isOutOfBounds(Activity context, MotionEvent event) {
		final int x = (int) event.getX();
		final int y = (int) event.getY();
		final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
		final View decorView = context.getWindow().getDecorView();
		return (x < -slop) || (y < -slop)|| (x > (decorView.getWidth() + slop))|| (y > (decorView.getHeight() + slop));
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.task_dialog_btn){
			RingtoneUtil.getInstance(this).stopRingtone();
			finish();
		}
	}
	
	@Override
	public void onBackPressed() {
		RingtoneUtil.getInstance(this).stopRingtone();
		super.onBackPressed();
	}
}
