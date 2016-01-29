package com.star.mobile.video.activity;

import org.libsdl.app.PlayerSurface;
import org.libsdl.app.PlayerUtil;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
/**
 * 视频直播
 * @author Lee
 * @version 1.0 2015/08/24
 *
 */
public class VideoLivePlayer extends BaseActivity implements OnClickListener{
	private RelativeLayout mVideoLivePlayerRL;
	private ImageView mVideoLiveBackIV;
	private TextView mVideoLiveNameTV;
	private ImageView mVideoLiveSearchIV;
	private com.star.ui.ImageView mVideoLiveVideoIV;
	private ImageView mVideoFullScreenIV;
	private ImageView mVideoLiveSettingIV;
	
	private PlayerUtil mSdlActivity;
	private PlayerSurface mVideoView;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertical_live_video_play);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initView();
		initData();
	}


	private void initView() {
		mVideoLivePlayerRL = (RelativeLayout) findViewById(R.id.video_live_player_rl);
		mVideoLiveVideoIV = (com.star.ui.ImageView) findViewById(R.id.video_live_video_pic);
		mVideoLiveBackIV = (ImageView) findViewById(R.id.video_live_back_iv);
		mVideoLiveNameTV = (TextView) findViewById(R.id.video_live_video_name);
		mVideoLiveSearchIV = (ImageView) findViewById(R.id.video_live_search_iv);
		mVideoFullScreenIV = (ImageView) findViewById(R.id.video_live_full_screen_iv);
		mVideoLiveSettingIV = (ImageView) findViewById(R.id.video_live_setting_iv);
	}
	
	private void initData() {
		mVideoLiveBackIV.setOnClickListener(this);
		mVideoLiveSearchIV.setOnClickListener(this);
		mVideoFullScreenIV.setOnClickListener(this);
		mVideoLiveSettingIV.setOnClickListener(this);
		
		mSdlActivity = new PlayerUtil(this, mHandler);
		mVideoView = mSdlActivity.getSDLSurface();
		mVideoLivePlayerRL.addView(mVideoView);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.video_live_back_iv:
			//TODO 返回按钮
			break;
		case R.id.video_live_search_iv:
			//TODO 跳转到搜索页面
			break;
		case R.id.video_live_full_screen_iv:
			//TODO 点击全屏
			break;
		case R.id.video_live_setting_iv:
			//TODO 设置视频质量
			break;
		default:
			break;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
