package com.star.mobile.video.activity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.FullScreenVideoView;
import com.star.mobile.video.view.VideoSeekBar;

public class LandscapePalyActivity extends BaseActivity {
	
	private FullScreenVideoView mVideoView;
	private RelativeLayout rlTopView;
	private RelativeLayout rlButtomView;
	private RelativeLayout rlCenterView;
	private ImageView ivPlayBg;
	private ImageView ivBreak;
	private ImageView ivLineTwo;
	private TextView tvVideoEndTime;
	private TextView tvVideoStartTime;
	private VideoSeekBar sbPlayProgress;
	private TextView tvEpgName;
	private ImageView ivSwitchScreen;
	private ImageView ivPlay;
	private String cachePath;
	private boolean playStatus = true;
	private boolean upViewStatus = true;
	private boolean isLive = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_landscape_paly);
		initView();
		
		String uri = getIntent().getStringExtra("uri");
		String epgName = getIntent().getStringExtra("epgname");
		String epgStartTime = getIntent().getStringExtra("startTime");
		String epgEndTime = getIntent().getStringExtra("endTime");
		
		isLive = getIntent().getBooleanExtra("islive", false);
		
		if(isLive) {
			tvVideoStartTime.setVisibility(View.VISIBLE);
			tvVideoStartTime.setText(epgStartTime);
			tvVideoEndTime.setText(epgEndTime);
			ivPlayBg.setVisibility(View.GONE);
			ivLineTwo.setVisibility(View.GONE);
		} else {
			tvVideoStartTime.setVisibility(View.GONE);
			ivPlayBg.setVisibility(View.VISIBLE);
			ivLineTwo.setVisibility(View.VISIBLE);
		}
		
		if(epgName != null) {
			setEpgName(epgName);
		}
		if(uri != null) {
			if(!uri.isEmpty()) {
				setVieoPath(dir()+uri);
			} else {
				ToastUtil.buttomShowToast(LandscapePalyActivity.this, getString(R.string.video_resource_does_not_exist));
			}
		} else {
			ToastUtil.buttomShowToast(LandscapePalyActivity.this, getString(R.string.video_resource_does_not_exist));
		}
		setSeekBarProgress();
	}
	
	private void initView() {
		mVideoView = (FullScreenVideoView) findViewById(R.id.vv_play);
		rlTopView = (RelativeLayout) findViewById(R.id.rl_top);
		rlCenterView = (RelativeLayout) findViewById(R.id.rl_center);
		rlButtomView = (RelativeLayout) findViewById(R.id.rl_buttom);
		ivPlayBg = (ImageView) findViewById(R.id.iv_play_bg);
		ivBreak = (ImageView) findViewById(R.id.iv_break);
		ivLineTwo = (ImageView) findViewById(R.id.iv_line2);
		tvVideoStartTime = (TextView) findViewById(R.id.tv_video_start_time);
		tvVideoEndTime = (TextView) findViewById(R.id.tv_video_end_time);
		sbPlayProgress = (VideoSeekBar) findViewById(R.id.pb_play_progress);
		tvEpgName = (TextView) findViewById(R.id.tv_epg_name);
		ivSwitchScreen = (ImageView) findViewById(R.id.iv_switch_screen);
		ivPlay = (ImageView) findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(click);
		rlCenterView.setOnClickListener(click);
		ivBreak.setOnClickListener(click);
		ivSwitchScreen.setOnClickListener(click);
		sbPlayProgress.setOnSeekBarChangeListener(change);
		mVideoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				play();
			}
		});
	}
	

	private String dir(){
		if(cachePath!=null){
			return cachePath;
		}
		File cacheDir;
		
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"starTempImages");
        else
            cacheDir=getCacheDir();
		
		cachePath = cacheDir.getAbsolutePath();
		
		return cachePath;
	
	}
	

	private void setVieoPath(String path) {
		if(path==null)
			return;
		mVideoView.setVideoURI(Uri.parse(path));
	}
	private void setVideoTime(Long time) {
		tvVideoEndTime.setText(DateFormat.formatSeconds(time));
	}
	
	private void setEpgName(String name) {
		tvEpgName.setText(name);
	}
	
	private void setSeekBarProgress () {
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				sbPlayProgress.setMax(mVideoView.getDuration());
				sbPlayProgress.setProgress(mVideoView.getCurrentPosition());
			}
		};
		timer.schedule(timerTask, 0,1000);
	}
	
	
	private void play() {
		if(playStatus) {
			mVideoView.start();
			ivPlay.setImageResource(R.drawable.play);
			playStatus = false;
		} else {
			mVideoView.pause();
			ivPlay.setImageResource(R.drawable.pause);
			playStatus = true;
		}
		if(!isLive) {
			setVideoTime((long) mVideoView.getDuration());
			ivPlay.setVisibility(View.VISIBLE);
		} else {
			ivPlay.setVisibility(View.GONE);
		}
		automaticHideUpView();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVideoView.stopPlayback();
	}
	
	
	private OnSeekBarChangeListener change = new OnSeekBarChangeListener() {
		private boolean isDown = false;
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			isDown = false;
			sbPlayProgress.hideSeekDialog();
			int progress = seekBar.getProgress();
			if(mVideoView != null) {
				if(mVideoView.isPlaying() || playStatus) {
					mVideoView.seekTo(progress);
				} 
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			isDown = true;
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if(isDown) {
				sbPlayProgress.showSeekDialog(DateFormat.formatSeconds((long)progress));
			} else {
				sbPlayProgress.hideSeekDialog();
			}
		}
	};
	
	private Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0) {
				hideUpView();
				sbPlayProgress.hideSeekDialog();
			}
		};
	};
	
	
	private void automaticHideUpView() {
		Timer time = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			@Override
			public void run() {
				if(upViewStatus) {
					Message msg = new Message();
					msg.what = 0;
					handler.sendMessage(msg);
					upViewStatus = false;
				}
			}
		};
		time.schedule(timerTask, 10000);
	}
	
	
	private void showUpView() {
		rlTopView.setVisibility(View.VISIBLE);
		rlButtomView.setVisibility(View.VISIBLE);
		ivPlayBg.setVisibility(View.VISIBLE);
		
	}
	
	private void hideUpView() {
		rlTopView.setVisibility(View.GONE);
		rlButtomView.setVisibility(View.GONE);
		ivPlayBg.setVisibility(View.GONE);
	}
	
	private void showHideUpView() {
		if(upViewStatus) {
			hideUpView();
			upViewStatus = false;
		} else {
			showUpView();
			upViewStatus = true;
			automaticHideUpView();
		}
	}
	
	private OnClickListener click = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.iv_play:
				play();
				break;
			case R.id.rl_center:
				showHideUpView();
				break;
			case R.id.iv_break:
				finish();
				break;
			case R.id.iv_switch_screen:
				finish();
				break;
			default:
				break;
			}
		}
	};
}
