package com.star.mobile.video.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.libsdl.app.Player;

import com.star.cms.model.Content;
import com.star.cms.model.vo.ChannelVO;
import com.star.cms.model.vo.VOD;
import com.star.mobile.video.R;
import com.star.mobile.video.adapter.VideoLiveMoreVideosAdatpter;
import com.star.mobile.video.service.ChannelService;
import com.star.mobile.video.service.VideoService;
import com.star.mobile.video.util.LoadingDataTask;
import com.star.mobile.video.view.PullToRefreshLayout.OnRefreshListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 直播页面的视图控制
 * 
 * @author Lee @version1.0 2015/08/24
 *
 */
public class VideoLiveViewControlMoreVideos extends LinearLayout implements OnRefreshListener{
	private static final String TAG = VideoLiveViewControlMoreVideos.class.getSimpleName();
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	private com.star.mobile.video.view.PullableGridView mVideoLiveMoreVideosLV;
	private TextView mVideoLiveNoneMoreVideoTV;
	private VideoService videoSerivce;
	private long mCurrentChannelId;
	private int mCurrentChannelType;
	private VOD mVod;
	private int mPosition;
	private ChannelVO mChannel;
	private VideoLiveMoreVideosAdatpter mVideoPlayAdapter;
	private List<VOD> mRecommentsVideo = new ArrayList<VOD>();// 推荐视频
	private Player mPlayer;
	private VideoLiveViewControl mVideoLiveViewControl;
	private boolean isFirstLoadBundesliga = true;
	private int Offset = 0;
	private int COUNT = 6;
	public VideoLiveViewControlMoreVideos(Context context) {
		super(context);
		initView(context);
		initData();
	}

	public VideoLiveViewControlMoreVideos(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		initData();
	}

	private void initView(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		View view = mLayoutInflater.inflate(R.layout.view_video_live_control_more_videos, this);
		((PullToRefreshLayout) findViewById(R.id.refresh_view))
		.setOnRefreshListener(this);
		mVideoLiveMoreVideosLV = (com.star.mobile.video.view.PullableGridView) view.findViewById(R.id.video_live_more_videos_list);
		mVideoLiveNoneMoreVideoTV = (TextView) view.findViewById(R.id.video_live_none_more_video_textview);
		
	}

	private void initData() {
		getChannel();
		// 需要获得channelID和channel type
		if (mChannel != null) {
			mCurrentChannelType = mChannel.getType();
		}else {
			mCurrentChannelType = 1;
		}
		videoSerivce = new VideoService(mContext);
		mVideoPlayAdapter = new VideoLiveMoreVideosAdatpter(mContext, mRecommentsVideo);
		mVideoLiveMoreVideosLV.setAdapter(mVideoPlayAdapter);
		mVideoLiveMoreVideosLV.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 刷新数据，根据position播放视频
				setCurrentVideoData(position);
				refreshListViewData();
			}
		});
	}

	public void setPlayer(Player p){
		this.mPlayer = p;
	}
	public void setVideoLiveViewControl(VideoLiveViewControl viewControl){
		this.mVideoLiveViewControl = viewControl;
	}
	public void setChannelID(long channelID) {
		this.mCurrentChannelId = channelID;
	}

	public void setChannelType(int channelType) {
		this.mCurrentChannelType = channelType;
	}

	public void setVod(VOD vod) {
		this.mVod = vod;
	}
	public void setPosition(int position){
		this.mPosition = position;
	}

	/**
	 * 根据channelID获得channel
	 */
	public void getChannel(){
		final ChannelService cs = new ChannelService(mContext);
		new LoadingDataTask() {
			@Override
			public void onPreExecute() {
			}
			@Override
			public void onPostExecute() {
			}
			@Override
			public void doInBackground() {
				mChannel = cs.getChannelById(mCurrentChannelId);
			}
		}.execute();
	}
	
	private void getChannelVideo() {
		getChannelVideo(null);
	}
	private void getChannelVideo(final PullToRefreshLayout pullToRefreshLayout) {
		new LoadingDataTask() {
			private List<VOD> content;
			@Override
			public void onPreExecute() {
			}
			
			@Override
			public void onPostExecute() {
				if (content != null && content.size() > 0) {
					if(content.size()<COUNT){
						mVideoLiveMoreVideosLV.setCanPlull(false);
					}
					if (isFirstLoadBundesliga) {
						isFirstLoadBundesliga = false;
						mVod = content.get(mPosition);
					}else {
						Iterator<VOD> iterator = content.iterator();
						while (iterator.hasNext()) {
							long id = iterator.next().getId();
							if (mVod.getId() == id) {
								iterator.remove();
								break;
							}
						}
					}
					mRecommentsVideo.addAll(content);
					mVideoPlayAdapter.setRecommentVideo(mRecommentsVideo);
					
					showNoneMoreVideo(content);
					if (pullToRefreshLayout != null) {
						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
					}
				}else {
					if (pullToRefreshLayout != null) {
						pullToRefreshLayout.setLoadDoneText(getResources().getString(R.string.load_no_videos));
						pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.NOMOREDATA);
					}
				}
			}
			
			@Override
			public void doInBackground() {
				content = videoSerivce.getRecommendVideo(mCurrentChannelId, Offset, COUNT, mCurrentChannelType);
			}
		}.execute();	
	}
	
	/**
	 * 显示没有morevideo
	 * 
	 * @param responseDatas
	 */
	private void showNoneMoreVideo(List<VOD> responseDatas) {
		if (responseDatas.size() == 0) {
			mVideoLiveNoneMoreVideoTV.setVisibility(View.VISIBLE);
		} else {
			mVideoLiveNoneMoreVideoTV.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置当前视频所需的数据
	 * 
	 * @param position
	 */
	private void setCurrentVideoData(int position) {
		mVod = mRecommentsVideo.get(position);
		setVideoLiveWatchCount(mVod);
		//根据vod设置视频的播放，视频的名字
		mPlayer.setVideoName(mVod.getName());
		mPlayer.settingVideoPlay(mVod);
	}

	/**
	 * 更新listview里的数据
	 */
	private void refreshListViewData() {
		refreshVideoData();
	}

	/**
	 * 设置视频的观看次数
	 * 
	 * @param vod
	 */
	private void setVideoLiveWatchCount(VOD vod) {
		if (vod != null) {
			Content video = vod.getVideo();
			if (video != null) {
				Long watchCount = video.getSelCount();
				if (watchCount != 0) {
					mVideoLiveViewControl.setWatchCount(watchCount + 1);
				}
			}
		}
	}

	
	/**
	 * 刷新视频数据
	 */
	public void refreshVideoData() {
		Offset = 0;
		mRecommentsVideo.clear();
		mVideoPlayAdapter.setRecommentVideo(mRecommentsVideo);
		mVideoLiveMoreVideosLV.setCanPlull(true);
		getChannelVideo();
	}

	@Override
	public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
		// TODO 下拉刷新操作
		
	}

	@Override
	public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
		// 加载数据
		Offset += COUNT;
		getChannelVideo(pullToRefreshLayout);
	}

}
