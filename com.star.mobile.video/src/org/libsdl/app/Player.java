package org.libsdl.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.enm.VideoType;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.model.MyTimer;
import com.star.mobile.video.search.SearchActivity;
import com.star.mobile.video.shared.SharedPreferencesUtil;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.CommonUtil.PromptDialogClickListener;
import com.star.mobile.video.util.ToastUtil;
import com.star.mobile.video.view.HorizontalVideoPlayInfoView;
import com.star.mobile.video.view.VideoLiveViewControl;
import com.star.mobile.video.view.VideoQualitySettingView;
import com.star.mobile.video.view.VideoQualitySettingView.Quality;
import com.star.mobile.video.view.VideoSeekBar;

public class Player extends BaseActivity implements OnClickListener{
	private String TAG = "Player";
	private PlayerUtil mSdlActivity;

	private VideoSeekBar mSeekBar;
	private TextView currentTime, tTime;
	private RelativeLayout mVideoPlayRL;
	private ImageView imgPlay;
	RelativeLayout bottom;
	View mHideContainer;
	private TextView videoTitle;
	private FrameLayout frameContainer;
	private FrameLayout wholeFrameContainer;
	RelativeLayout videoPlayerRL;
	private PlayerSurface mVideoView;

	ImageView mVideoCenterPlayImageView;
	com.star.ui.ImageView mVideoImageView;

	private ImageView ivBreak;
	private ImageView mVideoSearchIV;
	
	private RelativeLayout mVideoSwitchScreenRL;
	private ImageView ivSwitchScreen;
	private RelativeLayout mVideoSettingRL;

	private GestureDetector gestureDetector = null;
	// handler
	public final int MSG_FINISH = 7;
	public static final int MSG_LOAD_FINISHED = 10;
	public static final int MSG_LOAD_UNFINISHED = 11;
	public final int MSG_OPEN_ERROR = 12;
	public final int MSG_OPEN_OK = 13;
	public final int MSG_SEEK_UPDATE = 30;
	public final int MSG_HIDE_MASKVIEW = 33;
	public final int MSG_UPDATE_CURRENT_TIME = 34;
	public static final int MSG_VIDEO_QUALITY = 35;
	public static final int MSG_LOADING_VIDEO_DELAY = 36;
	public final int MSG_UPDATE_ANALYTICS = 37;
	public final int MSG_POST_ANALYTICS = 38;
	public final int MSG_DELAY_ANALYTICS = 39;
	public static final int MSG_URL_ERROR = 8;


	boolean isPortraitOrientation = true;
	MyTimer seekbarTimer;
	private HomeWatcher mHomeWatcher;
	private String filename;
	private String title;

	long contentId;

	ChannelVO mChannel;
	Boolean isLive = false;

	private LayoutParams videoLP;
	private RelativeLayout.LayoutParams wholeFrameContainerLP;
	private LinearLayout.LayoutParams videoPlayerRLLP;

//	int RECOMMENT_VIDEO = 0;// 挂载视频
	int mPosition;
	HorizontalVideoPlayInfoView mHorizontalVideoPlayInfoView;
	com.star.mobile.video.view.ListView mVideoMoreInfoListview;
	TextView mNoneMoreVideoTextView;
	private ProgressBar playerPB;
	private TextView progressMsg;
	private TextView replayMsg;
	private LinearLayout mVideoDemandContolLL;
	private com.star.mobile.video.view.VideoLiveViewControl mVideoLiveViewControl;
	String isDejia;
	private boolean isStarted = false;
	private boolean isSchedule;//是不是时间列表里的视频
	int prePosition;
	int repeatCnt = 0;
	
	private PlayerGesture mPlayerGesture;
	View mVolumeBrightnessLayout;
	ImageView mOperationBg;
	ImageView mOperationPercent;
	
	private PlayerPauseView mPlayerPauseView;
	private FrameLayout popupWindowContainer;
	
	PlayerEndView mPlayerEndView;
	private long mWatchRecommendCount;
	public VideoQualitySettingView mVideoQualitySettingView;
	public VideoQualitySettingView mPortraitVideoQualitySettingView;
	private Quality QUALITY;
	private RelativeLayout mVideoQualityGuideRL;
	private ImageView mVideoQualityGuideOkIV;
	private int videoTransPosition;
	
	
	BufferedReader bufferedReader;
	HashSet<Integer> idSet;
	ArrayList<HashMap<String, Object>> toSend;
	PlayerAnalytic PA;
	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				case MSG_FINISH:
					Log.d(TAG, "receive MSG_FINISH");
					
//					playerPB.setVisibility(View.GONE);
					break;
				case MSG_URL_ERROR:
					Log.d(TAG, "url error!!!");
					alertCannotPlay();
					break;
				case MSG_HIDE_MASKVIEW:
					hideUpView();
					break;
				case MSG_LOADING_VIDEO_DELAY:
					int videoTotalTime = mSdlActivity.getDuration();
					if (videoTotalTime == 0) {
						Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_propomt), Toast.LENGTH_LONG).show();
					}
					break;
				case MSG_VIDEO_QUALITY:
					//控制视频质量
					QUALITY = (Quality) msg.obj;
					if (mSdlActivity != null) {
						videoTransPosition = mSdlActivity.getCurrentPosition();
					}
					if (QUALITY == Quality.NORMAL) {
						//获得视频的url
						getNormalUrl();
					}else if (QUALITY == Quality.LOW) {
						getLowUrl();
					}
					break;
					
				case MSG_UPDATE_CURRENT_TIME:
					int currentPos = mSdlActivity.getCurrentPosition();
					int totalTime = mSdlActivity.getDuration();
					mSeekBar.setMax(totalTime);
					mSeekBar.setProgress(currentPos);
					if (!isLive && currentPos > totalTime)
						currentPos = totalTime;
					if (PlayerUtil.isPlayStart() == 1 && playerPB.getVisibility() == View.VISIBLE
							&& isStarted == false && PlayerUtil.isPlayFinish() == 0) {
						playerPB.setVisibility(View.GONE);
						progressMsg.setVisibility(View.GONE);
						if(isLive) hideBottomBar();
						else showBottomBar();
						imgPlay.setBackgroundResource(R.drawable.vp_pause);
						mVideoSwitchScreenRL.setVisibility(View.VISIBLE);
						if (videoTransPosition > 0) {
							mSdlActivity.seekTo(videoTransPosition);
							videoTransPosition = 0;
						}
						
						bottom.setVisibility(View.VISIBLE);
						isStarted = true;
						//视频加载完以后显示listview
						mVideoMoreInfoListview.setVisibility(View.VISIBLE);
					}
					if (PlayerUtil.isPlayFinish() == 1 && isStarted) {
						showWhenFinished();
					}
					if(isLive && currentPos > 0){
//						Log.d(TAG, "Cnt: " + repeatCnt + " currentPos: " + currentPos + " Pre: " + prePosition + "  Total: " + totalTime);
						if( prePosition == currentPos) {
//							playerPB.setVisibility(View.VISIBLE);
							repeatCnt ++;
							if(repeatCnt >= 6  && PlayerUtil.PlayerIsPlay() == 1 ) restartLive();
						}
						else {
//							playerPB.setVisibility(View.GONE);
							repeatCnt = 0;
						}
					}
					if (!isLive && currentPos > 0) {
						if (prePosition == currentPos) {
							repeatCnt ++;
							if (repeatCnt >= 10 && QUALITY == Quality.NORMAL) {
								repeatCnt = 0;
								//网络状态提示
								ToastUtil.centerShowToast(Player.this,getResources().getString(R.string.network_propomt));
							}
						}
					}
					currentTime.setText(mSdlActivity.formatTime(currentPos));
					tTime.setText(mSdlActivity.formatTime(totalTime));
					prePosition = currentPos;

					//					Object[] objs = {SharedPreferencesUtil.getToken(getApplicationContext()), contentId};
					//					new LogTask().execute(objs);
					break;
				case MSG_UPDATE_ANALYTICS:
					String line = "";
					try {
						Process process = null;
						try {
							process = Runtime.getRuntime().exec("logcat -d -s SDL/APP");
						} catch (IOException e) {
							e.printStackTrace();
						}
						bufferedReader = new BufferedReader(
								new InputStreamReader(process.getInputStream()));
						while ( (line = bufferedReader.readLine()) != null) {
							int index = line.indexOf("http_speed_test");
							if(index > 0) {
								PA.processLog(line.substring(index + 16));
							}
							//zy:delay time analytic
							int index2 = line.indexOf("zy analytic start time = ");
							if(index2 > 0) {
								PA.processPlayerDelay(line.substring(index2 + 25), 0, PlayerUtil.isPlayStart()==1?true:false);
							}
							int index3 = line.indexOf("zy analytic ka time = ");
							if(index3 > 0) {
								PA.processPlayerDelay(line.substring(index3 + 22), 1, PlayerUtil.isPlayStart()==1?true:false);
							}
							int index4 = line.indexOf("zy analytic seek time = ");
							if(index4 > 0) {
								PA.processPlayerDelay(line.substring(index4 + 24), 2, PlayerUtil.isPlayStart()==1?true:false);
							}
							int index5 = line.indexOf("zy analytic finish = ");
							if(index5 > 0) {
								PA.finishPlayerDelay(line.substring(index5 + 21));
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//Log.w(TAG, "zy send screen");
					//GA.sendScreen("zy2");
					break;
				case MSG_POST_ANALYTICS:
					PA.postLog();
					break;
				//case MSG_DELAY_ANALYTICS:
					//PA.processPlayerDelay();
					//break;
				}
			}
			super.handleMessage(msg);
		}

	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		mPlayerGesture = new PlayerGesture(this);
		mPlayerPauseView = new PlayerPauseView(this);
		mPlayerEndView = new PlayerEndView(this);
		
		try {
			isDejia =  getIntent().getStringExtra("dejia");
			String isLiveStr = getIntent().getStringExtra("isLive");
			if(isDejia != null) {
				parseWebParams(isLiveStr);
			}else {
				isLive = getIntent().getBooleanExtra("isLive", false);
				mPosition = getIntent().getIntExtra("position", 0);
				mChannel = (ChannelVO) getIntent().getSerializableExtra("channel");
				mPlayerEndView.mVideoContent = (List<VOD>) getIntent().getSerializableExtra("videocontent");
				mPlayerEndView.mVod = mPlayerEndView.mVideoContent.get(mPosition);	
				contentId = mPlayerEndView.mVod.getVideo().getId();
				filename =  mPlayerEndView.mVod.getVideo().getResources().get(0).getUrl();
				title = mPlayerEndView.mVod.getName();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			exitActivity();
			return;
		}
		setContentView(R.layout.activity_vertical_video_play);

		findViews();
//		isLive = true;

				
		if (isDejia == null) {
			hideVideoLiveView();
			mPlayerEndView.initData();
		} else {
			// 德甲、意甲的直播
			if (isLive) {
				//直播的时候隐藏时间、进度条、暂停按钮、设置按钮
				hideNoLiveView();
				setVideoLiveData();
			} else {
				hideVideoLiveView();
				mPlayerEndView.setIsSchedule(isSchedule);
				mPlayerEndView.initDejiaData();
			}
		}
		
		setPortrait();
		playerPB.setVisibility(View.INVISIBLE);
		progressMsg.setVisibility(View.GONE);

		if (filename == null || filename.isEmpty()) {
			exitActivity();
			return;
		}
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setVideoName(title);

		gestureDetector = new GestureDetector(this, mPlayerGesture.getGestureListener());
		// Press Home Button
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
			@Override
			public void onHomePressed() {
				if(mSdlActivity != null && mSdlActivity.isPlaying()) {
					imgPlay.setBackgroundResource(R.drawable.vp_play);
					mSdlActivity.stop();
				}
			}

			@Override
			public void onHomeLongPressed() {
			}
		});
		mHomeWatcher.startWatch();

		if (SharedPreferencesUtil.getVideoFirstTime(this)) {
			ToastUtil.centerShowToast(this, getResources().getString(R.string.player_first_time_msg));
			SharedPreferencesUtil.setVideoFirstTime(this, false);
		}
		mPlayerPauseView.initPauseView();
		popupWindowContainer.addView(mPlayerPauseView.popupView);
		showVideoSetting();
//		idSet = new HashSet<Integer>();
//		toSend = new ArrayList<HashMap<String, Object>>(10);

//		if(isLive) {
//			PA = new PlayerAnalytic(Player.this);
//			PA.postBasicInfo();
//			setUpdateAnalytics();
//		}
	}
	public String getFilename() {
		return filename;
	}
	/**
	 * 设置直播的数据
	 */
	private void setVideoLiveData() {
		mVideoDemandContolLL.setVisibility(View.GONE);
		mVideoLiveViewControl.setVisibility(View.VISIBLE);
		mVideoLiveViewControl.setPlayer(this);
		mVideoLiveViewControl.setChannelID(mPlayerEndView.mCurrentChannelId);
		mVideoLiveViewControl.setPosition(mPosition);
		mVideoLiveViewControl.refreshVideoData();
	}

	private void hideNoLiveView() {
		mSeekBar.setVisibility(View.GONE);
		currentTime.setVisibility(View.GONE);
		tTime.setVisibility(View.GONE);
		mVideoPlayRL.setVisibility(View.GONE);
		mVideoSettingRL.setVisibility(View.GONE);
	}
	/**
	 * 是否显示视频设置
	 */
	private void showVideoSetting() {
		if (mPlayerEndView.mVod != null) {
			Content video = mPlayerEndView.mVod.getVideo();
			if (video != null) {
				List<Resource> resources = video.getResources();
				if (resources.size() > 1) {
					showVideoQualityGuide();
					mVideoSettingRL.setVisibility(View.VISIBLE);
				}else {
					mVideoSettingRL.setVisibility(View.GONE);
				}
			}
		}else {
			mVideoSettingRL.setVisibility(View.GONE);
		}
	}
	/**
	 * 设置电影的名字
	 * @param name
	 */
	public void setVideoName(String name) {
		videoTitle.setText(name);
	}
	

	/**
	 * 隐藏直播页面
	 */
	private void hideVideoLiveView() {
		mVideoLiveViewControl.setVisibility(View.GONE);
		mVideoDemandContolLL.setVisibility(View.VISIBLE);
	}
	
	private void parseWebParams(String isLiveStr) {
		if(isLiveStr!= null  && isLiveStr.equals("true")) isLive = true;
		else isLive = false;
		filename = getIntent().getStringExtra("filename");
		title = getIntent().getStringExtra("epgname");
		String schedule = getIntent().getStringExtra("isSchedule");
		if ("true".equals(schedule)) {
			isSchedule = true;
		}else {
			isSchedule = false;
		}
		String contentStr = getIntent().getStringExtra("vodId");
		if(contentStr != null) {
			contentId = Integer.parseInt(contentStr);
		}
		else contentId = -1;

		String positionStr = getIntent().getStringExtra("locationId");
		if(positionStr != null) 
			mPosition = Integer.parseInt(positionStr);
		else mPosition = 0;
		
		String channelIdStr = getIntent().getStringExtra("channelID");
		if(channelIdStr != null) 
			mPlayerEndView.mCurrentChannelId = Integer.parseInt(channelIdStr);
		else mPlayerEndView.mCurrentChannelId = 0;
	}

	protected void restartLive() {
		clear();
		initVideoView();
		mVideoLiveViewControl.refreshVideoData();

		if(!isPortraitOrientation) {
			setLandscape();
		}
	}
	/**
	 * 获得Low的url
	 */
	private void getLowUrl() {
		if (mPlayerEndView.mVod != null) {
			Content video = mPlayerEndView.mVod.getVideo();
			if (video != null) {
				List<Resource> resources = video.getResources();
				for (Resource resource : resources) {
					VideoType type = resource.getType();
					if (type == VideoType.LD) {
						if (!filename.equals(resource.getUrl())) {
							filename = resource.getUrl();
							clear();
							initVideoView();
						}
					}
				}
			}
		}
	}

	/**
	 * 获得normal的url
	 */
	private void getNormalUrl() {
		if (mPlayerEndView.mVod != null) {
			Content video = mPlayerEndView.mVod.getVideo();
			if (video != null) {
				List<Resource> resources = video.getResources();
				for (Resource resource : resources) {
					VideoType type = resource.getType();
					if (type == VideoType.SD) {
						if (!filename.equals(resource.getUrl())) {
							filename = resource.getUrl();
							clear();
							initVideoView();
							handler.sendEmptyMessageDelayed(MSG_LOADING_VIDEO_DELAY, 10*1000);
						}
					}
				}
			}
		}
	}
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		isDejia =  intent.getStringExtra("dejia");
		String isLiveStr =  intent.getStringExtra("isLive");
		if(isDejia != null) {
			parseWebParams(isLiveStr);
			setPortrait();
		}
		
		PlayerUtil mPlayerUtil = mSdlActivity;
		if (mPlayerUtil != null && !mPlayerUtil.isPlaying()) {
			imgPlay.setBackgroundResource(R.drawable.vp_pause);
			mPlayerUtil.start();
		}
		mPlayerPauseView.dismissPauseView();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			isPortraitOrientation = false;
			//获得屏幕分辨率
			DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
			int width = displayMetrics.widthPixels;
			int height = displayMetrics.heightPixels;
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);

			wholeFrameContainer.setLayoutParams(new RelativeLayout.LayoutParams(width, height));
			videoPlayerRL.setLayoutParams(new LinearLayout.LayoutParams(width, height));

			Log.d(TAG, "width and height: " + width + " x " + height);
			mVideoView.setLayoutParams(new LayoutParams(width, height));

		} else {
			isPortraitOrientation = true;
			mPlayerPauseView.dismissPauseView();
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

			wholeFrameContainer.setLayoutParams(wholeFrameContainerLP);
			videoPlayerRL.setLayoutParams(videoPlayerRLLP);
			mVideoView.setLayoutParams(videoLP);

			if (PlayerUtil.isPlayFinish() == 1 && isStarted) {
				showWhenFinished();
			}
		}
	}

	@Override
	public void onBackPressed() {
		if(!isPortraitOrientation) {
			if(mPlayerEndView.mVideoEndView != null)mPlayerEndView.mVideoEndView.hideVideoEndView();
			setPortrait();
			mPortraitVideoQualitySettingView.setVisibility(View.GONE);
		} 
		else {
			if(mVideoLiveViewControl.hidefaceLayout())
				return;
			super.onBackPressed();
		}
	}

	/**
	 * 竖屏显示
	 */
	private void setPortrait() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		ivSwitchScreen.setBackgroundResource(R.drawable.icon_fullscreen);
		mVideoSearchIV.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected String getScreenName() {
		return this.getClass().getSimpleName() + "#" + filename;
	}

	private void setSeekBarProgress() {
		seekbarTimer = new MyTimer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_UPDATE_CURRENT_TIME;
				handler.sendMessage(msg);
			}
		};
		seekbarTimer.innerTask = timerTask;
		seekbarTimer.schedule(timerTask, 0, 1000);
	}
	
	Timer t;
	Timer tt;
	//Timer tAnalyticDelay;
	private void setUpdateAnalytics() {
		if(t == null) t= new MyTimer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_UPDATE_ANALYTICS;
				handler.sendMessage(msg);
			}
		};
		t.schedule(timerTask, 0, 5000);
		
		if(tt == null) tt= new MyTimer();
		TimerTask timerTask2 = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_POST_ANALYTICS;
				handler.sendMessage(msg);
			}
		};
		tt.schedule(timerTask2, 0, 60000);
		/*
		//zy: to get start delay, seek delay and lag
		if(tAnalyticDelay == null) tAnalyticDelay= new MyTimer();
		TimerTask timerTask3 = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_DELAY_ANALYTICS;
				handler.sendMessage(msg);
			}
		};
		tAnalyticDelay.schedule(timerTask3, 0, 5000);
		//PA.changePlayerState(1); //started
*/
	}
	

	private void hideUpView() {
		mHideContainer.setVisibility(View.INVISIBLE);
	}

	private void automaticHideUpView() {
		Timer time = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				msg.what = MSG_HIDE_MASKVIEW;
				handler.sendMessage(msg);
			}
		};
		time.schedule(timerTask, 3000);
	}
	
	void showWhenFinished(){
		//PA.changePlayerState(0);
		if (seekbarTimer != null)
			seekbarTimer.cancel();
		bottom.setVisibility(View.INVISIBLE);
		if (!isPortraitOrientation) {
			if(isLive){
//				exitActivity();
				return;
			}
			else mPlayerEndView.showRecommentView();
		} else {
			if(isLive) {
//				exitActivity();
				return;
			}
			mVideoCenterPlayImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.icon_replay));
			mVideoCenterPlayImageView.setVisibility(View.VISIBLE);
			mVideoImageView.setImageDrawable(null);
			mVideoImageView.setBackgroundColor(this.getResources().getColor(R.color.mask_background));
			mVideoImageView.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * 重新刷新player的数据
	 * @param vod
	 */
	public void resetData(VOD vod){
		//根据vod加载数据
		mPlayerEndView.mVod = vod;
		changeScreenDisplay();
	}
	
	/**
	 * 获得视频的名字
	 * 
	 * @param position
	 * @return
	 */
	private String getVideoName(int position) {
		if(mPlayerEndView.mVideoContent == null || mPlayerEndView.mVideoContent.size() == 0)  return null;
		VOD vod = mPlayerEndView.mVideoContent.get(position);
		title = vod.getName();
		return title;
	}

	/**
	 * 获得视频图片的地址
	 * 
	 * @param position
	 * @return
	 */
	private String getVideoImageViewUrl(int position) {
		Content poster = mPlayerEndView.mVideoContent.get(position).getPoster();
		List<Resource> resposter = poster.getResources();
		return resposter.get(0).getUrl();
	}

	void transferHorizontalListView(int position, boolean isPortraitOrientation) {
		clear();
		try {
			mPlayerEndView.mVod = mPlayerEndView.mRecommentsVideo.get(position);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		getVideoInfo(mPlayerEndView.mVod);

		setVideoName(title);
		initVideoView();

		try {
			mWatchRecommendCount = mPlayerEndView.mRecommentsVideo.get(position).getVideo().getSelCount();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		if (!isLive) {
			mPlayerEndView.refreshVideoData();
		}

		if (mWatchRecommendCount != 0) {
			mHorizontalVideoPlayInfoView.setWatchCount(mWatchRecommendCount + 1);
		}	

		if(!isPortraitOrientation) {
			setLandscape();
		}
	}

	private void clear() {
		if (seekbarTimer != null)
			seekbarTimer.cancel();
		if(mSdlActivity != null) 
			mSdlActivity.onDestroy();
	}


	/**
	 * 视频播放
	 * @param vod
	 */
	public void settingVideoPlay(VOD vod){
		clear();
		getVideoInfo(vod);
		setVideoName(title);
		initVideoView();
	}

	/**
	 * 获得视频信息
	 * @param vod
	 */
	private void getVideoInfo(VOD vod) {
		Content video = vod.getVideo();
		List<Resource> resvideo = video.getResources();
		filename = resvideo.get(0).getUrl();
		title = vod.getName();
		contentId = vod.getVideo().getId();

	}
	
	/**
	 * 设置视频的播放
	 * @param position
	 * @param type
	 */
	public void settingVideoPlay(int position){
		transferHorizontalListView(position,false);
	}

	private void hideBottomBar(){
		mSeekBar.setVisibility(View.GONE);
		currentTime.setVisibility(View.GONE);
		tTime.setVisibility(View.GONE);
	}
	
	private void showBottomBar(){
		mSeekBar.setVisibility(View.VISIBLE);
		currentTime.setVisibility(View.VISIBLE);
		tTime.setVisibility(View.VISIBLE);
	}
	
	public void findViews() {
		mSeekBar = (VideoSeekBar) findViewById(R.id.sdl_pb_play_progress);
		mSeekBar.setEnabled(true);
		currentTime = (TextView) findViewById(R.id.sdl_tv_video_start_time);
		tTime = (TextView) findViewById(R.id.sdl_tv_video_end_time);

		frameContainer = (FrameLayout) findViewById(R.id.framecontainer);
		wholeFrameContainer = (FrameLayout) findViewById(R.id.video_vertical_player);
		videoPlayerRL = (RelativeLayout) findViewById(R.id.video_player_rl);
		mVideoImageView = (com.star.ui.ImageView) findViewById(R.id.video_more_imageview);

		playerPB = (ProgressBar) findViewById(R.id.video_progressbar);
		progressMsg = (TextView)  findViewById(R.id.video_progress_dialog_msg);

		mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
		mOperationBg = (ImageView) findViewById(R.id.operation_bg);
		mOperationPercent = (ImageView) findViewById(R.id.operation_percent);

		ivBreak = (ImageView) findViewById(R.id.sdl_iv_break);
		mVideoSearchIV = (ImageView) findViewById(R.id.sdl_iv_up_linetwo);
		mVideoSearchIV.setOnClickListener(this);
		mVideoSwitchScreenRL = (RelativeLayout) findViewById(R.id.sdl_rl_switch_screen);
		mVideoSwitchScreenRL.setOnClickListener(imgPlayListener);
		mVideoSwitchScreenRL.setVisibility(View.INVISIBLE);
		ivSwitchScreen = (ImageView) findViewById(R.id.sdl_iv_switch_screen);
		mVideoSettingRL = (RelativeLayout) findViewById(R.id.video_rl_setting_iv);
		mVideoSettingRL.setOnClickListener(this);
		mVideoSettingRL.setVisibility(View.GONE);
//		showVideoSettingRL();
		ivBreak.setOnClickListener(imgPlayListener);
		videoTitle = (TextView) findViewById(R.id.sdl_tv_epg_name);
		//点播页面
		mVideoDemandContolLL = (LinearLayout) findViewById(R.id.demand_control_ll);
		mVideoDemandContolLL.setVisibility(View.GONE);
		//直播页面
		mVideoLiveViewControl = (VideoLiveViewControl) findViewById(R.id.video_live_view_control);
		mVideoLiveViewControl.setVisibility(View.GONE);
		mVideoPlayRL = (RelativeLayout) findViewById(R.id.sdl_rl_play);
		imgPlay = (ImageView) findViewById(R.id.sdl_iv_play);
		bottom = (RelativeLayout) findViewById(R.id.sdl_rl_buttom);
		mHideContainer = findViewById(R.id.hidecontainer);
		
		mVideoPlayRL.setOnClickListener(imgPlayListener);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		mHideContainer.setOnClickListener(mVisibleListener);

		mVideoCenterPlayImageView = (ImageView) findViewById(R.id.video_center_play);
		mVideoCenterPlayImageView.setOnClickListener(imgPlayListener);
		mVideoMoreInfoListview = (com.star.mobile.video.view.ListView) findViewById(R.id.horizontal_video_list);
		mVideoMoreInfoListview.setVisibility(View.GONE);
		mNoneMoreVideoTextView = (TextView) findViewById(R.id.none_more_video_textview);
		mHorizontalVideoPlayInfoView = (HorizontalVideoPlayInfoView) findViewById(R.id.hv_playinfoview);
		
		mVideoQualitySettingView = (VideoQualitySettingView) findViewById(R.id.video_quality_setting_view);
		setVideoQualityData(mVideoQualitySettingView);
		mPortraitVideoQualitySettingView = (VideoQualitySettingView) findViewById(R.id.portrait_video_quality_setting_view);
		setVideoQualityData(mPortraitVideoQualitySettingView);
		getVideoName(mPosition);

		popupWindowContainer = (FrameLayout) findViewById(R.id.popup_window_container);
		
		if(isDejia == null) {
			String url = getVideoImageViewUrl(mPosition);
			mVideoImageView.setUrl(url);// 设置电影图片url
		}
		
		mVideoQualityGuideRL = (RelativeLayout) findViewById(R.id.video_quality_guide);
		mVideoQualityGuideRL.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mVideoQualityGuideOkIV = (ImageView) findViewById(R.id.video_quality_guide_setting_ok);
		mVideoQualityGuideOkIV.setOnClickListener(this);
		mHorizontalVideoPlayInfoView.setChannel(mChannel);
	}

	private void showVideoSettingRL() {
		SharedPreferences sharedPreferences = SharedPreferencesUtil.getVideoGuideSharedPreferences(Player.this);
		boolean isShowGuide = sharedPreferences.getBoolean("isShowGuide", false);
		if (isShowGuide) {
			mVideoSettingRL.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置视频质量的数据
	 * @param view
	 */
	private void setVideoQualityData(VideoQualitySettingView view) {
		view.setVisibility(View.GONE);
		view.setOnClickListener(this);
		view.setHandler(handler);
		view.setPlayer(this);
	}
	/**
	 * 显示视频设置引导
	 */
	private void showVideoQualityGuide(){
		SharedPreferences sharedPreferences = SharedPreferencesUtil.getVideoGuideSharedPreferences(Player.this);
		boolean isShowGuide = sharedPreferences.getBoolean("isShowGuide", false);
		if (!isShowGuide) {
			//显示视频引导
			mVideoQualityGuideRL.setVisibility(View.VISIBLE);
			Editor edit = sharedPreferences.edit();
			edit.putBoolean("isShowGuide", true);
			edit.apply();
		}
	}
	
	// Play and Pause
	OnClickListener imgPlayListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			PlayerUtil mPlayerUtil = mSdlActivity;
			if(mPlayerUtil == null) {
				if (v.getId() == R.id.sdl_iv_break) {
					onBackPressed();
				}else{
					initVideoView();
				}
			}
			if (mPlayerUtil != null && v.getId() == R.id.sdl_rl_play) {
				if (mPlayerUtil.isPlaying()) {
					imgPlay.setBackgroundResource(R.drawable.icon_play_arrow);
					mPlayerUtil.stop();
					//PA.changePlayerState(3); //pause
					mPlayerPauseView.showPauseView();
				} else {
					imgPlay.setBackgroundResource(R.drawable.vp_pause);
					mPlayerUtil.start();
					mPlayerPauseView.dismissPauseView();
				}
			} else if (mPlayerUtil != null && v.getId() == R.id.sdl_iv_break) {
				//如果为竖屏退出
				if (isPortraitOrientation) {
					onBackPressed();
				}
				if(mPlayerEndView.mVideoEndView != null) mPlayerEndView.mVideoEndView.hideVideoEndView();
				//恢复到当前频道
				if (mChannel != null && mPlayerEndView.mCurrentChannelId != mChannel.getId() && mPlayerEndView.mCurrentChannelType != mChannel.getType()) {
					mPlayerEndView.mCurrentChannelId = mChannel.getId();
					mPlayerEndView.mCurrentChannelType = mChannel.getType();
				}
				setPortrait();
			} else if (mPlayerUtil != null && v.getId() == R.id.sdl_rl_switch_screen) {
				changeScreenDisplay();
			} else if (mPlayerUtil != null && v.getId() == R.id.video_center_play) {
				if(PlayerUtil.isPlayFinish() == 1 && isStarted) {
					clear();
					initVideoView();
//					replayMsg.setVisibility(View.GONE);
				}
			}
		}
	};

	private void initVideoView()
	{
		mVideoCenterPlayImageView.setVisibility(View.GONE);
		mVideoImageView.setVisibility(View.GONE);
		if(isLive) hideBottomBar();
		else showBottomBar();
		
		if(mPlayerEndView.mVideoEndView != null) videoPlayerRL.removeView(mPlayerEndView.mVideoEndView);;
		playerPB.setVisibility(View.VISIBLE);
		progressMsg.setVisibility(View.VISIBLE);
		// mSdlActivity.onDestroy();
		frameContainer.removeAllViews();
		isStarted = false;
		mVideoSwitchScreenRL.setVisibility(View.INVISIBLE);

		mSdlActivity = new PlayerUtil(this, handler);
		mVideoView = mSdlActivity.getSDLSurface();
		frameContainer.addView(mVideoView);
		if(contentId != -1) mPlayerEndView.videoSerivce.takeCount(contentId);
		setSeekBarProgress();
//		filename = "http://s3-ap-southeast-1.amazonaws.com/tenbresing/cms_back/html/vod/test/LET_271/live.m3u8";
		if (isLive)
			mVideoView.setLiveVideoUrl(Uri.parse(filename));
		else
			mVideoView.setVideoURI(Uri.parse(filename));
		
//		String[] tsElems = filename.split("/");
//		if(tsElems[tsElems.length - 1].startsWith("live")) {
//			liveRsID = tsElems[tsElems.length - 2];
//		}
		
		PlayerUtil.SetBuffer(1500*1024);
		PlayerUtil.handleResume();
		automaticHideUpView();
//		if(toSend != null)
//			toSend.clear();
		
		//播放数据统计
//		if(isLive) {
			if(t!=null){
				t.cancel();
				t=null;
			}
			if(tt!=null){
				tt.cancel();
				tt=null;
			}
			
			//if(tAnalyticDelay!=null){
			//	tAnalyticDelay.cancel();
			//	tAnalyticDelay=null;
			//}
			PA = new PlayerAnalytic(Player.this);
			PA.postBasicInfo();
			setUpdateAnalytics();
//		}
	}
	
	private void stopAnalytic(){
		if(PA!=null){
			PA.clearCachedTask();
			PA=null;
		}
		if(t!=null){
			t.cancel();
		}
		if(tt!=null){
			tt.cancel();
		}
		/*
		if(tAnalyticDelay!=null) {
			tAnalyticDelay.cancel();
		}
		*/
	}
	
	public int getCurrentPosition(){
		return mSdlActivity.getCurrentPosition();
	}
	public boolean CheckIfPlayStart(){
        return mSdlActivity.CheckIfPlayStart();
    }

	/**
	 * 改变屏幕的显示
	 */
	private void changeScreenDisplay() {
		if (isPortraitOrientation) {
			videoLP = (LayoutParams) mVideoView.getLayoutParams();
			wholeFrameContainerLP = (RelativeLayout.LayoutParams) wholeFrameContainer.getLayoutParams();
			videoPlayerRLLP = (LinearLayout.LayoutParams) videoPlayerRL.getLayoutParams();
			setLandscape();
		} else{
			setPortrait();
		}
	}
	/**
	 * 横屏显示
	 */
	private void setLandscape() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		ivSwitchScreen.setBackgroundResource(R.drawable.icon_fullscreen_exit);
		mVideoSearchIV.setVisibility(View.GONE);
	}

	// Seek Function
	OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			int totalTime, seekTo = 0;
			int progress = seekBar.getProgress();
			PlayerUtil mPlayerUtil = mSdlActivity;

			if (mPlayerUtil != null) {
				totalTime = mPlayerUtil.getDuration();
				if(totalTime==0) {
					seekBar.setProgress(0);
					Log.w(TAG, "zy setProgress before player init");
					return;
				}
					
				mPlayerUtil.seekTo(progress);
				//PA.changePlayerState(4);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {
			Log.w(TAG, "onStartTrackingTouch ");
		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		}
	};

	OnClickListener mVisibleListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick mVisibleListener");
//			if (v.getId() == R.id.iv_actionbar_back) {
//				// exitActivity();
//				onBackPressed();
//			}
			if (mPlayerPauseView.popupView.getVisibility() == View.VISIBLE) {
				mPlayerPauseView.dismissPauseView();
				return;
			}
			if ((mHideContainer.getVisibility() == View.GONE) || (mHideContainer.getVisibility() == View.INVISIBLE)) {
				mHideContainer.setVisibility(View.VISIBLE);
			} else {
				mHideContainer.setVisibility(View.INVISIBLE);
			}
		}
	};

	// Events
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (PlayerUtil.mBrokenLibraries) {
			return false;
		}

		int keyCode = event.getKeyCode();
		// Ignore certain special keys so they're handled by Android
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP
				|| keyCode == KeyEvent.KEYCODE_CAMERA || keyCode == 168
				|| /* API 11: KeyEvent.KEYCODE_ZOOM_IN */
				keyCode == 169 /* API 11: KeyEvent.KEYCODE_ZOOM_OUT */
				) {
			return false;
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG, "onStart");
		mHideContainer.setVisibility(View.VISIBLE);
		try {
			if(isDejia == null) {
				int size = mChannel.getLogo().getResources().size();
				String logoUrl = mChannel.getLogo().getResources().get(size - 1).getUrl();
				try {
					String[] strs = logoUrl.split("/");
					Resources resources = this.getResources();
					String logoName = strs[strs.length - 1];
					StringBuffer sb = new StringBuffer("c_");
					sb.append(logoName.substring(logoName.charAt(2) == '-' ? 3 : 2, logoName.length()-4));
//					Log.d(TAG, "Logo ID: " + logoName.substring(logoName.charAt(2) == '-' ? 3 : 2, logoName.length()-4));
					int resourceId = resources.getIdentifier(sb.toString(), "drawable",  this.getPackageName());
//					channelIcon.setImageDrawable( resources.getDrawable(resourceId));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Resources resources = this.getResources();
			}
			
		}catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.disableKeyguard();
		clear();
		initVideoView();
		if (!isLive) {
			mPlayerEndView.refreshVideoData();
		}
		if (isDejia == null && !isLive) {
			mPlayerEndView.refreshVideoData();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
		//if (isStarted) {
		if(PlayerUtil.isInitStart()==1?true:false){
			PlayerUtil.handlePause();
			PlayerUtil mPlayerUtil = mSdlActivity;
			if (mPlayerUtil != null && mPlayerUtil.isPlaying()) {
				imgPlay.setBackgroundResource(R.drawable.vp_play);
				mPlayerUtil.stop();
				//PA.changePlayerState(3); //pause
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		//if (isStarted) {
		if (PlayerUtil.isInitStart()==1?true:false) {	
			PlayerUtil.handleResume(); // dedicated for pressing close screen  button
		}
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
		KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
		lock.reenableKeyguard();
		if(PA!=null){
			PA.sendfinishPlayerDelay();
			PA.postLog();
		}
//		if(isLive) {
		if(t!=null)
			t.cancel();
		if(tt!=null)
			tt.cancel();
		//if(tAnalyticDelay!=null)
			//tAnalyticDelay.cancel();
		//PA.changePlayerState(0);

//		}
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (isStarted) {
			PlayerUtil.mHasFocus = hasFocus;
			if (hasFocus) {
				PlayerUtil.handleResume();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event))
			return true;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_UP:
			mPlayerGesture.endGesture();
			break;
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		PlayerUtil.nativeLowMemory();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		// Now wait for the SDL thread to quit
		//if (PlayerUtil.mSDLThread != null && isStarted) {
		if (PlayerUtil.mSDLThread != null && (PlayerUtil.isInitStart()==1?true:false) ) {
			
			// Send a quit message to the application
			PlayerUtil.mExitCalledFromJava = true;
			PlayerUtil.nativeQuit();
			try {
				PlayerUtil.mSDLThread.join();
			} catch (Exception e) {
				Log.e(TAG, "Problem stopping thread: " + e);
			}
			PlayerUtil.mSDLThread = null;
			Log.w(TAG, "Finished waiting for SDL thread");
		}
		if (seekbarTimer != null)
			seekbarTimer.cancel();
		if (mHomeWatcher != null)
			mHomeWatcher.stopWatch();
		super.onDestroy();
		PlayerUtil.initialize();
		if (mVideoLiveViewControl != null)
			mVideoLiveViewControl.removeTimerAndTask();
	}

	public void exitActivity() {
		if(isDejia == null){
			clear();
			this.finish();
		} else {
			onBackPressed();
		} 
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sdl_iv_up_linetwo:
			CommonUtil.startActivity(this, SearchActivity.class);
			break;
		case R.id.video_quality_guide_setting_ok:
			mVideoQualityGuideRL.setVisibility(View.GONE);
			break;
		case R.id.video_rl_setting_iv:
			//全屏时点击设置
			if (!isPortraitOrientation) {
				//获得屏幕分辨率
				DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
				int height = displayMetrics.heightPixels;
				android.view.ViewGroup.LayoutParams layutParams = mPortraitVideoQualitySettingView.getLayoutParams();
				layutParams.height = height*2/3;
				layutParams.width = android.widget.RelativeLayout.LayoutParams.MATCH_PARENT;
				mPortraitVideoQualitySettingView.setLayoutParams(layutParams);
				int visibility = mPortraitVideoQualitySettingView.getVisibility();
				if (visibility == View.GONE) {
					//显示设置页面
					mPortraitVideoQualitySettingView.setVisibility(View.VISIBLE);
					mPortraitVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(Player.this, R.anim.tran_down_up));
				}else {
					//隐藏设置页面
					mPortraitVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(Player.this, R.anim.tran_up_down));
					mPortraitVideoQualitySettingView.setVisibility(View.GONE);
				}
			}else {
				portraitOrientationSetting();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 屏幕为竖屏的时候，视频质量的设置
	 */
	private void portraitOrientationSetting() {
		int visibility = mVideoQualitySettingView.getVisibility();
		if (visibility == View.GONE) {
			//显示设置页面
			mVideoQualitySettingView.setVisibility(View.VISIBLE);
			mVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(Player.this, R.anim.tran_down_up));
		}else {
			//隐藏设置页面
			mVideoQualitySettingView.startAnimation(AnimationUtils.loadAnimation(Player.this, R.anim.tran_up_down));
			mVideoQualitySettingView.setVisibility(View.GONE);
		}
	}
	
	public void alertCannotPlay(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				CommonUtil.getInstance().showPromptDialog(Player.this,null,"This video is not available in your country.","OK",null,new PromptDialogClickListener(){
					
					@Override
					public void onConfirmClick() {
						exitActivity();
					}
					
					@Override
					public void onCancelClick() {
					}
					
				});
			}
		});
	}
}