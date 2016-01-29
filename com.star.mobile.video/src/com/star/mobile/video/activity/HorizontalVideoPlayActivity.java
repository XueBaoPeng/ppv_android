package com.star.mobile.video.activity;

import java.util.ArrayList;
import java.util.List;

import org.libsdl.app.Player;
import org.libsdl.app.PlayerSurface;
import org.libsdl.app.PlayerUtil;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.star.cms.model.Content;
import com.star.cms.model.Resource;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.VerticalVideoPlayAdapter;
import com.star.mobile.video.base.BaseActivity;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.util.CommonUtil;
import com.star.mobile.video.util.Constant;
import com.star.mobile.video.util.DateFormat;
import com.star.mobile.video.view.HorizontalVideoPlayInfoView;
import com.star.mobile.video.view.ListView.LoadingListener;

/**
 * 横向播放视频以及视频信息
 * 
 * @author Lee @version1.0 2015.07.26
 *
 */
public class HorizontalVideoPlayActivity extends BaseActivity implements OnClickListener {
	private static final int MSG_INIT_VIDEO_DATA = 10;
	protected static final int MSG_REFRESH_VIDEO_SEEKBAR_DURATION = 11;
	private RelativeLayout mVerticalVideoRelativelayout;
	private ImageView mVideoBackImageView;
	private TextView mVideoName;
	// private com.star.ui.ImageView mVideoImageView;

	private RelativeLayout mVideoPlayControlRL;
	private ImageView mVideoCenterPlayImageView;
	private TextView mStartTime;
	private TextView mEndTime;
	private SeekBar mVideoSeekBar;
	private ImageView mVideoPauseImageView;
	private ImageView mVideoFullScreenImageView;
	private boolean isHideVideoControl;
	private boolean isLoadVideo = true;
	private com.star.mobile.video.view.ListView mVideoMoreInfoListview;
	private PlayerUtil mPlayUtil;
	private VerticalVideoPlayAdapter mVideoPlayAdapter;
	private List<VOD> mVideoContent = new ArrayList<VOD>();// 挂载视频
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private VideoService videoSerivce;
	private String video_url_one;
	private String video_name_one;
	private Long contentId_one;
	private ChannelVO mChannel;
	private int MOUNT_VIDEO = 1;// 挂载视频
	private int RECOMMENT_VIDEO = 0;// 推荐视频
	private int VIDEO_TYPE = RECOMMENT_VIDEO;
	private int mPosition;
	private HorizontalVideoPlayInfoView mHorizontalVideoPlayInfoView;
	int position;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_INIT_VIDEO_DATA: 
				// initVideoData();
				break;
			case MSG_REFRESH_VIDEO_SEEKBAR_DURATION:
//				int currentPosition = mPlayUtil.getCurrentPosition();
				position+=1;
				mVideoSeekBar.setProgress(position);
				//TODO 设置当前时间
				mStartTime.setText(DateFormat.formatTime((long)position));
				sendEmptyMessageDelayed(MSG_REFRESH_VIDEO_SEEKBAR_DURATION, 1000);
				break;
			default:
				break;
			}
		};
	};
	private PlayerSurface mVideoView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vertical_video_play);
		initView();
		initData();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
//		mVerticalVideoRelativelayout = (RelativeLayout) findViewById(R.id.vertical_video_relativelayout);
		mVideoBackImageView = ((ImageView) findViewById(R.id.iv_actionbar_back));
		mVideoName = (TextView) findViewById(R.id.tv_actionbar_title);
		// mVideoImageView = (com.star.ui.ImageView)
		// findViewById(R.id.video_more_imageview);
		mVideoMoreInfoListview = (com.star.mobile.video.view.ListView) findViewById(R.id.horizontal_video_list);

		// 视频控制区域图标
		mVideoPlayControlRL = (RelativeLayout) findViewById(R.id.vertical_video_bottom_rl);
//		mVideoCenterPlayImageView = (ImageView) findViewById(R.id.video_center_play);
		mStartTime = (TextView) findViewById(R.id.vertical_video_start_time);
		mEndTime = (TextView) findViewById(R.id.vertical_video_end_time);
		mVideoSeekBar = (SeekBar) findViewById(R.id.vertical_video_seekbar);
		mVideoPauseImageView = (ImageView) findViewById(R.id.vertical_video_pause_play_button);
		mVideoFullScreenImageView = (ImageView) findViewById(R.id.vertical_video_full_screen_button);

		// 设置播放视频控件的宽高比为16:9
		android.view.ViewGroup.LayoutParams params = mVerticalVideoRelativelayout.getLayoutParams();
		params.height = Constant.WINDOW_WIDTH * 9 / 16;
		mVerticalVideoRelativelayout.setLayoutParams(params);
		// mVideoImageView.setLayoutParams(new
		// RelativeLayout.LayoutParams(Constant.WINDOW_WIDTH,
		// Constant.WINDOW_WIDTH*9/16));
	}

	/**
	 * 初始化数据
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			mVideoContent = (List<VOD>) bundle.getSerializable("videocontent");
			mChannel = (ChannelVO) bundle.getSerializable("channel");
			mPosition = bundle.getInt("position");
		}
		// 设置电影信息
		setVideoInfo();
		mVideoBackImageView.setOnClickListener(this);
		mVerticalVideoRelativelayout.setOnClickListener(this);
		// mVerticalVideoRelativelayout.setOnClickListener(new OnClickListener()
		// {
		//
		// @Override
		// public void onClick(View v) {
		// transferHorizontalListView(mPosition, MOUNT_VIDEO);
		// }
		// });

		videoSerivce = new VideoService(this);
		
		// mVideoView = mPlayUtil.getSDLSurface();
		// mVerticalVideoRelativelayout.addView(mVideoView);
		mVideoMoreInfoListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				position = position - 1;
				if (position != -1) {
					transferHorizontalListView(position, RECOMMENT_VIDEO);
				}
			}
		});

		mVideoMoreInfoListview.setRequestCount(6);
		mVideoMoreInfoListview.setLoadingListener(new MyLoadingListener());
//		mHorizontalVideoPlayInfoView = new HorizontalVideoPlayInfoView(this, mChannel, mPosition);
		mVideoMoreInfoListview.addHeaderView(mHorizontalVideoPlayInfoView);
		mVideoPlayAdapter = new VerticalVideoPlayAdapter(this, mRecommentsVideo);
		mVideoMoreInfoListview.setAdapter(mVideoPlayAdapter);

		// // 视频控制区域数据初始化
		// mEndTime.setText(mPlayUtil.getDuration() + "");
		 mVideoSeekBar.setOnSeekBarChangeListener(new
		 MySeekBarChangeListener());
		// mVideoSeekBar.setMax(mPlayUtil.getDuration());
		 mVideoPauseImageView.setOnClickListener(this);
		 mVideoFullScreenImageView.setOnClickListener(this);
	}

	/**
	 * 设置电影信息
	 */
	private void setVideoInfo() {
		mVideoName.setText(getVideoName(mPosition));// 设置电影名字
		String url = getVideoImageViewUrl(mPosition);
//		 mVideoImageView.setUrl(url);// 设置电影图片url
	}

	/**
	 * 获得视频的名字
	 * 
	 * @param position
	 * @return
	 */
	private String getVideoName(int position) {
		VOD vod = mVideoContent.get(position);
		return vod.getName();
	}

	/**
	 * 获得视频图片的地址
	 * 
	 * @param position
	 * @return
	 */
	private String getVideoImageViewUrl(int position) {
		Content poster = mVideoContent.get(position).getPoster();
		List<Resource> resposter = poster.getResources();
		return resposter.get(0).getUrl();
	}

	/**
	 * 点击item播放视频
	 * 
	 * @param position
	 */
	private void transferHorizontalListView(int position, int type) {
		VOD vod = null;
		if (type == RECOMMENT_VIDEO) {
			vod = mRecommentsVideo.get(position);
		} else {
			vod = mVideoContent.get(position);
		}
		Content video = vod.getVideo();
		List<Resource> resvideo = video.getResources();
		String video_url = resvideo.get(0).getUrl();
		String video_name = vod.getName();
//		contentId = vod.getVideo().getId();

		Intent intent = new Intent(this, Player.class);
		intent.putExtra("filename", video_url);
		intent.putExtra("epgname", video_name);
		intent.putExtra("duration", mPlayUtil.getCurrentPosition());
		intent.putExtra("channel", mChannel);
		startActivity(intent);
		
		finish();
		// videoSerivce.play(video_url_one, video_name_one, contentId_one);
	}

	/**
	 * 视频的url
	 * 
	 * @return
	 */
	private String getVideoUrl() {
		VOD vod = mVideoContent.get(mPosition);
		Content video = vod.getVideo();
		List<Resource> resvideo = video.getResources();
		return resvideo.get(0).getUrl();
	}

	private void initVideoData() {
		mPlayUtil=null;
		mPlayUtil = new PlayerUtil(this, mHandler);
		mVideoView = null;
		mVideoView = mPlayUtil.getSDLSurface();
		
		// 视频控制区域数据初始化
		
		mVideoView.setVideoURI(Uri.parse(getVideoUrl()));
		mVerticalVideoRelativelayout.removeAllViews();
		mVerticalVideoRelativelayout.addView(mVideoView);
		
		mEndTime.setText(DateFormat.formatTime((long)mPlayUtil.getDuration()));
//		mVideoSeekBar.setOnSeekBarChangeListener(new MySeekBarChangeListener());
//		mVideoSeekBar.setMax(mPlayUtil.getDuration());
		mVideoSeekBar.setMax(100);
		mStartTime.setText(DateFormat.formatTime((long)mPlayUtil.getCurrentPosition()));
		mHandler.sendEmptyMessage(MSG_REFRESH_VIDEO_SEEKBAR_DURATION);
//		mVideoPauseImageView.setOnClickListener(this);
//		mVideoFullScreenImageView.setOnClickListener(this);
	}

	
	/**
	 * 加载数据接口
	 * 
	 * @author Lee
	 *
	 */
	private class MyLoadingListener implements LoadingListener<VOD> {

		@Override
		public List<VOD> loadingS(int offset, int requestCount) {
			return videoSerivce.getRecommendVideo(mChannel.getId(), offset, requestCount, VIDEO_TYPE);
		}

		@Override
		public void loadPost(List<VOD> responseDatas) {
			CommonUtil.closeProgressDialog();
			mRecommentsVideo.addAll(responseDatas);
			if (mRecommentsVideo != null && mRecommentsVideo.size() > 0) {
				mVideoPlayAdapter.setDatas(mRecommentsVideo);
			}
		}

		// 加载本地数据
		@Override
		public List<VOD> loadingL(int offset, int requestCount) {
			return null;
		}

		@Override
		public List<VOD> getFillList() {
			return mRecommentsVideo;
		}

		@Override
		public void onNoMoreData() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * seekber的改变事件
	 * 
	 * @author Owen
	 *
	 */
	private class MySeekBarChangeListener implements OnSeekBarChangeListener {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			mPlayUtil.seekTo(seekBar.getProgress());
			mHandler.sendEmptyMessage(MSG_REFRESH_VIDEO_SEEKBAR_DURATION);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_actionbar_back:
			// 点击返回键
			CommonUtil.finishActivityAnimation(this);
			break;
//		case R.id.vertical_video_relativelayout:
//			mVideoCenterPlayImageView.setVisibility(View.GONE);
//			
//			if (isHideVideoControl) {
//				isHideVideoControl = false;
//				mVideoPlayControlRL.setVisibility(View.VISIBLE);
//			} else {
//				if (isLoadVideo) {
//					isLoadVideo = false;
//	//				initVideoData();
//				}
//				isHideVideoControl = true;
//				mVideoPlayControlRL.setVisibility(View.GONE);
//			}
//			break;
		case R.id.vertical_video_pause_play_button:
			if (mPlayUtil.isPlaying()) {
				mVideoPauseImageView.setBackgroundResource(R.drawable.vp_play);
				mPlayUtil.stop();
				mHandler.sendEmptyMessage(MSG_REFRESH_VIDEO_SEEKBAR_DURATION);
			} else {
				mVideoPauseImageView.setBackgroundResource(R.drawable.vp_pause);
				mPlayUtil.start();
				mHandler.removeMessages(MSG_REFRESH_VIDEO_SEEKBAR_DURATION);
			}
			break;
		case R.id.vertical_video_full_screen_button:
			// 点击全屏，当前视频暂停，给全屏传递播放的时长
			if (mPlayUtil != null && mPlayUtil.isPlaying()) {
				mVideoPauseImageView.setBackgroundResource(R.drawable.vp_play);
				mPlayUtil.stop();
			}
//			if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//			}else if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//			}
			transferHorizontalListView(mPosition, MOUNT_VIDEO);

			break;

		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//TODO 暂停
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mRecommentsVideo.clear();
		mVideoPlayAdapter.notifyDataSetChanged();
		mVideoMoreInfoListview.setLoadingListener(new MyLoadingListener());
		mVideoMoreInfoListview.loadingData(false);
//		mHorizontalVideoPlayInfoView.getChannelVideo();
		Message msg = mHandler.obtainMessage();
		msg.what = MSG_INIT_VIDEO_DATA;
		mHandler.sendMessageDelayed(msg, 1000);
		if (!isLoadVideo) {
		}
		initVideoData();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 100) {
			System.out.println("返回到");
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		getWindow().setFlags(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);// 设置全屏
		// 检测屏幕的方向：纵向或横向
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 当前为横屏
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int mSurfaceViewWidth = dm.widthPixels;
			int mSurfaceViewHeight = dm.heightPixels;
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.width = mSurfaceViewWidth;
			lp.height = mSurfaceViewHeight;
			mVerticalVideoRelativelayout.setLayoutParams(lp);

		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			int mSurfaceViewWidth = dm.widthPixels;
			int mSurfaceViewHeight = dm.heightPixels;
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);
			lp.width = mSurfaceViewWidth;
			lp.height = mSurfaceViewHeight/3;
			mVerticalVideoRelativelayout.setLayoutParams(lp);
		}

		super.onConfigurationChanged(newConfig);
	}

}
