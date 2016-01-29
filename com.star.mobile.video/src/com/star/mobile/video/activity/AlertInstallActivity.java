package com.star.mobile.video.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.service.DownloadService;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.DownloadUtil;

public class AlertInstallActivity extends Activity{


	private TextView leftText;
	private TextView rightText;
	private TextView title;
	private TextView content;
	private NetworkInfo networkInfo;
	private String what;
	private boolean forceUp;
	private String updateInfo;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt_dialog);
        title = (TextView) findViewById(R.id.prompt_title);
        title.setVisibility(View.VISIBLE);
        content = (TextView) findViewById(R.id.prompt_content);
        content.setVisibility(View.VISIBLE);
        
        leftText = (TextView) findViewById(R.id.prompt_later);
        rightText = (TextView) findViewById(R.id.prompt_ok);
        
        what = getIntent().getStringExtra("what");
        updateInfo = getIntent().getStringExtra("updateInfo");
        forceUp = getIntent().getBooleanExtra("forceUp", false);
        
        if("install".equals(what) || what==null){
        	setInstall();
        }else if("update".equals(what)){
        	setUpdate();
        }
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		networkInfo = connectivityManager.getActiveNetworkInfo();
    }

	private void setUpdate() {
		rightText.setText(getString(R.string.update));
        leftText.setText(forceUp?getString(R.string.exit):getString(R.string.later));
		title.setText(getString(R.string.upgrade_information));
		if(updateInfo == null){
			content.setText(getString(R.string.please_update_the_app_and_run_it_again));
		}else {
			String [] data = updateInfo.split("\n");
			StringBuffer sb = new StringBuffer();
			for(int i=0;i<data.length;i++){
				if(i>1)
					break;
				if(i==1)
					sb.append("\n");
				sb.append(data[i]);
			}
			content.setText(sb.toString());
		}
		leftText.setOnClickListener(updateListener);
		rightText.setOnClickListener(updateListener);
	}

	private void setInstall() {
		rightText.setText(getString(R.string.install));
        leftText.setText(forceUp?getString(R.string.exit):getString(R.string.later));
		title.setText(getString(R.string.install_information));
		content.setText(getString(R.string.have_already_downloaded_latest_version));
		leftText.setOnClickListener(installListener);
		rightText.setOnClickListener(installListener);
	}
	
	private void set3G(){
		rightText.setText(getString(R.string.ok));
        leftText.setText(forceUp?getString(R.string.exit):getString(R.string.later));
		title.setText(getString(R.string.upgrade_information));
		content.setText(getString(R.string.current_network_threen_g));
		leftText.setOnClickListener(g3Listener);
		rightText.setOnClickListener(g3Listener);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if("update".equals(what)&&(event.getAction()==MotionEvent.ACTION_DOWN)&&isOutOfBounds(this, event)) {
			callback(false);
			finish();
		}else{
			return true;
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
	
	OnClickListener installListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean ok = false;
			switch (v.getId()) {
			case R.id.prompt_ok:
				ok = true;
				DownloadUtil.setUp(getApplicationContext(), SharedPreferencesUtil.getNewVersion(AlertInstallActivity.this));
				break;
			case R.id.prompt_content:
				ok = false;
				break;
			}
			if(what!=null){
				callback(ok);
			}
			finish();
		}
	};
	
	OnClickListener g3Listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean ok = false;
			switch (v.getId()) {
			case R.id.prompt_ok:
				Intent intent = new Intent(AlertInstallActivity.this,DownloadService.class);
				startService(intent);
				ok = true;
				break;
			case R.id.prompt_content:
				ok = false;
				break;
			}
			if(what!=null){
				callback(ok);
			}
			finish();
		}
	};
	
	OnClickListener updateListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			boolean ok = false;
			switch (v.getId()) {
			case R.id.prompt_ok:
				ok = false;
				if(networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					set3G();
					return;
				} else {
					Intent intent = new Intent(AlertInstallActivity.this,DownloadService.class);
					startService(intent);
					ok = true;
				}
				break;
			case R.id.prompt_content:
				ok = false;
				break;
			}
			if(what!=null){
				callback(ok);
			}
			finish();
		}
	};
	
	@Override
	public void onBackPressed() {
		if(what!=null){
			callback(false);
			finish();
		}else{
			super.onBackPressed();
		}
	}
	
	private void callback(boolean isOK){
		Intent data = new Intent();
		data.putExtra("ok", isOK);
        setResult(120, data);  
	}
}
